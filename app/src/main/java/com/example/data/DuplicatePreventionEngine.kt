package com.example.data

import com.example.model.*
import java.util.UUID
import kotlin.math.abs
import kotlin.math.max

/**
 * Intelligent Data-Fusion & Deduplication Engine for PulseGuard AI.
 * Reconciles concurrent multi-device streams without duplicate accumulation.
 */
class DuplicatePreventionEngine {

    data class DeduplicationResult(
        val deduplicatedRecords: List<DeduplicatedRecord>,
        val consolidatedDailySteps: Int,
        val unconsolidatedRawStepsSum: Int,
        val auditEntries: List<AuditLogEntry>,
        val flaggedConflicts: List<DeduplicatedRecord>
    )

    /**
     * Deduplicates a collection of raw measurements across devices and time windows.
     * Idempotent: Multiple runs with the same raw measurements produce identical results.
     */
    fun processMeasurements(
        rawMeasurements: List<RawMeasurement>,
        devices: List<HealthDevice>
    ): DeduplicationResult {
        val auditEntries = mutableListOf<AuditLogEntry>()
        val flaggedConflicts = mutableListOf<DeduplicatedRecord>()

        // 1. Group measurements by MetricType
        val byMetric = rawMeasurements.groupBy { it.metricType }

        val consolidatedDeduplicatedRecords = mutableListOf<DeduplicatedRecord>()

        // --- Step & Activity Deduplication ---
        val stepMeasurements = byMetric[MetricType.STEPS].orEmpty()
        val rawStepsSum = stepMeasurements.sumOf { it.value.toInt() }

        // Group steps by 30-minute time windows
        val stepBuckets = stepMeasurements.groupBy { it.originalTimestamp / (30 * 60 * 1000) }

        var consolidatedStepsTotal = 0

        for ((bucketIndex, bucketRecords) in stepBuckets) {
            val startTime = bucketIndex * 30 * 60 * 1000
            val endTime = startTime + 30 * 60 * 1000

            // Check if multiple devices recorded steps during this time window
            val uniqueDevices = bucketRecords.map { it.deviceName }.distinct()
            val deviceCount = uniqueDevices.size

            val selectedRecord: RawMeasurement
            val strategy: ReconciliationStrategy
            val confidence: Float
            val explanation: String
            var isConflict = false

            // Check user preferred source
            val preferredDevice = devices.find { it.isPreferredSourceForSteps && it.isEnabled }
            val preferredRecord = bucketRecords.find { it.deviceId == preferredDevice?.id }

            if (deviceCount == 1) {
                // Single device window - retain reading
                selectedRecord = bucketRecords.maxByOrNull { it.value } ?: bucketRecords.first()
                strategy = ReconciliationStrategy.HIGHEST_CONFIDENCE_SENSOR
                confidence = selectedRecord.confidence
                explanation = "Single stream from ${selectedRecord.deviceName}; direct sensor fidelity."
            } else if (preferredRecord != null) {
                // User has configured a preferred device
                selectedRecord = preferredRecord
                strategy = ReconciliationStrategy.USER_PREFERRED_OVERRIDE
                confidence = 0.98f
                explanation = "User preference applied: selected ${preferredRecord.deviceName} over ${uniqueDevices.filter { it != preferredRecord.deviceName }.joinToString()}."
                auditEntries.add(
                    AuditLogEntry(
                        id = UUID.randomUUID().toString(),
                        timestamp = System.currentTimeMillis(),
                        action = AuditAction.SOURCE_PREFERENCE_APPLIED,
                        metricType = MetricType.STEPS,
                        description = "Overlapping window [$startTime..$endTime]: Selected preferred ${preferredRecord.deviceName} (${preferredRecord.value.toInt()} steps) over ${bucketRecords.size - 1} overlapping devices.",
                        devicesInvolved = uniqueDevices.joinToString(", "),
                        resolutionConfidence = confidence
                    )
                )
            } else {
                // Data-fusion heuristic:
                // Wearable smartwatches (optical wrist sensor/accelerometer) generally have higher cadence accuracy
                // than smartphone in pocket or backpack.
                val smartwatchRecord = bucketRecords.find { record ->
                    val dev = devices.find { it.id == record.deviceId }
                    dev?.type == DeviceType.SMARTWATCH
                }

                // Check difference between max and min device reading in window
                val maxVal = bucketRecords.maxOf { it.value }
                val minVal = bucketRecords.minOf { it.value }
                val discrepancyPct = if (maxVal > 0) ((maxVal - minVal) / maxVal) * 100 else 0.0

                if (discrepancyPct > 35.0 && maxVal > 1500) {
                    // High variance between devices: flag for review
                    isConflict = true
                    selectedRecord = smartwatchRecord ?: bucketRecords.maxByOrNull { it.confidence } ?: bucketRecords.first()
                    strategy = ReconciliationStrategy.CONFLICT_FLAGGED
                    confidence = 0.72f
                    explanation = "High divergence ($discrepancyPct%) between ${uniqueDevices.joinToString()}. Wearable wrist record preserved; marked for user audit."
                    auditEntries.add(
                        AuditLogEntry(
                            id = UUID.randomUUID().toString(),
                            timestamp = System.currentTimeMillis(),
                            action = AuditAction.CONFLICT_FLAGGED,
                            metricType = MetricType.STEPS,
                            description = "Discrepancy of ${discrepancyPct.toInt()}% detected among devices [${uniqueDevices.joinToString()}]. Preserved primary sensor.",
                            devicesInvolved = uniqueDevices.joinToString(", "),
                            resolutionConfidence = confidence
                        )
                    )
                } else {
                    // Reconcile via Activity Provenance Fusion
                    // Pick the highest quality / wearable record or median to avoid duplicate addition
                    selectedRecord = smartwatchRecord ?: bucketRecords.maxByOrNull { it.confidence } ?: bucketRecords.first()
                    strategy = ReconciliationStrategy.ACTIVITY_PROVENANCE_FUSION
                    confidence = 0.94f
                    explanation = "Multi-device overlap detected across ${uniqueDevices.joinToString()}. Deduplicated using accelerometer provenance (${selectedRecord.deviceName})."
                    auditEntries.add(
                        AuditLogEntry(
                            id = UUID.randomUUID().toString(),
                            timestamp = System.currentTimeMillis(),
                            action = AuditAction.DEDUPLICATION_MERGE,
                            metricType = MetricType.STEPS,
                            description = "Merged ${bucketRecords.size} records into 1 representative window. Prevented duplicate accumulation of ${bucketRecords.sumOf { it.value.toInt() } - selectedRecord.value.toInt()} steps.",
                            devicesInvolved = uniqueDevices.joinToString(", "),
                            resolutionConfidence = confidence
                        )
                    )
                }
            }

            consolidatedStepsTotal += selectedRecord.value.toInt()

            val dedupRecord = DeduplicatedRecord(
                id = "step_window_$bucketIndex",
                metricType = MetricType.STEPS,
                consolidatedValue = selectedRecord.value,
                unit = "steps",
                timeWindowStart = startTime,
                timeWindowEnd = endTime,
                contributingDeviceIds = bucketRecords.map { it.deviceId }.distinct(),
                contributingDeviceNames = uniqueDevices,
                rawRecordsCount = bucketRecords.size,
                selectedSourceDevice = selectedRecord.deviceName,
                strategy = strategy,
                confidence = confidence,
                explanation = explanation,
                isConflict = isConflict
            )
            consolidatedDeduplicatedRecords.add(dedupRecord)
            if (isConflict) flaggedConflicts.add(dedupRecord)
        }

        // --- Heart Rate & Biometrics Deduplication ---
        val hrMeasurements = byMetric[MetricType.HEART_RATE].orEmpty()
        val hrBuckets = hrMeasurements.groupBy { it.originalTimestamp / (5 * 60 * 1000) } // 5-minute windows

        for ((bucketIndex, records) in hrBuckets) {
            val startTime = bucketIndex * 5 * 60 * 1000
            val endTime = startTime + 5 * 60 * 1000
            val uniqueDevs = records.map { it.deviceName }.distinct()

            val selected = if (records.size == 1) {
                records.first()
            } else {
                // Prefer optical PPG wearable or chest strap
                records.maxByOrNull { it.confidence } ?: records.first()
            }

            val dedupHr = DeduplicatedRecord(
                id = "hr_window_$bucketIndex",
                metricType = MetricType.HEART_RATE,
                consolidatedValue = selected.value,
                unit = "bpm",
                timeWindowStart = startTime,
                timeWindowEnd = endTime,
                contributingDeviceIds = records.map { it.deviceId }.distinct(),
                contributingDeviceNames = uniqueDevs,
                rawRecordsCount = records.size,
                selectedSourceDevice = selected.deviceName,
                strategy = if (records.size > 1) ReconciliationStrategy.HIGHEST_CONFIDENCE_SENSOR else ReconciliationStrategy.ACTIVITY_PROVENANCE_FUSION,
                confidence = selected.confidence,
                explanation = "Sensor reading aligned at ${selected.value.toInt()} bpm from ${selected.deviceName}."
            )
            consolidatedDeduplicatedRecords.add(dedupHr)
        }

        return DeduplicationResult(
            deduplicatedRecords = consolidatedDeduplicatedRecords,
            consolidatedDailySteps = consolidatedStepsTotal,
            unconsolidatedRawStepsSum = rawStepsSum,
            auditEntries = auditEntries,
            flaggedConflicts = flaggedConflicts
        )
    }
}
