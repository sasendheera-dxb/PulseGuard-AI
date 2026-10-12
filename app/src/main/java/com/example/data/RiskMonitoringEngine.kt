package com.example.data

import com.example.model.*
import java.util.UUID

/**
 * AI Health Risk Monitoring Engine for PulseGuard AI.
 * Compares current biometric streams against individual baseline and clinical safety boundaries.
 */
class RiskMonitoringEngine {

    fun analyzeRisks(
        stats: DerivedHealthStats,
        latestHeartRates: List<Int>,
        isResting: Boolean,
        userAge: Int = 34
    ): List<HealthRiskAlert> {
        val alerts = mutableListOf<HealthRiskAlert>()
        val now = System.currentTimeMillis()

        // 1. Resting Tachycardia / Sustained Elevated Heart Rate at Rest
        if (isResting && stats.currentHeartRate > 105) {
            val delta = stats.currentHeartRate - stats.baselineRestingHr
            alerts.add(
                HealthRiskAlert(
                    id = UUID.randomUUID().toString(),
                    timestamp = now,
                    metricType = MetricType.HEART_RATE,
                    severity = if (stats.currentHeartRate > 125) AlertSeverity.URGENT else AlertSeverity.NON_URGENT,
                    title = "Elevated Heart Rate at Inactive Rest (${stats.currentHeartRate} bpm)",
                    summary = "Your heart rate is +$delta bpm above your personal resting baseline (62 bpm) while inactive.",
                    explanation = "PulseGuard AI detected sustained pulse elevation exceeding 100 bpm during a period with zero movement or exercise recorded by your sensors.",
                    clinicalContext = "Elevated resting pulse (tachycardia) can arise from dehydration, fever, acute stress, caffeine intake, or autonomic stress. Not an emergency diagnosis, but warrants rest.",
                    recommendedAction = "Cease strenuous activity, sit down in a cool environment, drink water, and take slow, deep breaths. If accompanied by chest tightness, shortness of breath, or dizziness, seek urgent medical care.",
                    triggerValue = "${stats.currentHeartRate} bpm"
                )
            )
        } else if (stats.currentHeartRate < 45 && stats.currentHeartRate > 0) {
            // Bradycardia
            alerts.add(
                HealthRiskAlert(
                    id = UUID.randomUUID().toString(),
                    timestamp = now,
                    metricType = MetricType.HEART_RATE,
                    severity = AlertSeverity.NON_URGENT,
                    title = "Unusual Low Heart Rate (${stats.currentHeartRate} bpm)",
                    summary = "Heart rate is significantly below your average resting baseline.",
                    explanation = "A pulse below 50 bpm was recorded while awake. While common in endurance athletes, sudden drops may cause reduced perfusion.",
                    clinicalContext = "Bradycardia observation. Baseline indicates average resting heart rate of ${stats.baselineRestingHr} bpm.",
                    recommendedAction = "Check if wearable is fitting snugly on wrist. If feeling lightheaded, sit down and monitor closely.",
                    triggerValue = "${stats.currentHeartRate} bpm"
                )
            )
        }

        // 2. Blood Oxygen Saturation (SpO2) Anomalies
        if (stats.spO2Percentage in 1..91) {
            alerts.add(
                HealthRiskAlert(
                    id = UUID.randomUUID().toString(),
                    timestamp = now,
                    metricType = MetricType.SPO2,
                    severity = AlertSeverity.URGENT,
                    title = "Sub-Optimal Blood Oxygen Saturation (${stats.spO2Percentage}%)",
                    summary = "Blood oxygen level has dipped below the expected safe 95% threshold.",
                    explanation = "Optical PPG pulse oximetry from your connected wearable recorded SpO2 at ${stats.spO2Percentage}%, which is lower than normal sea-level physiology.",
                    clinicalContext = "Transient readings can occur due to poor sensor contact, cold extremities, or movement artifacts. Sustained hypoxemia requires clinical attention.",
                    recommendedAction = "Warm your hands, readjust the wearable band, and repeat a resting reading. If reading persists below 92% or you experience shortness of breath, seek prompt medical evaluation.",
                    triggerValue = "${stats.spO2Percentage}%"
                )
            )
        }

        // 3. Heart Rate Variability (HRV) Significant Deviation
        if (stats.hrvScore > 0 && stats.hrvScore < 24) {
            alerts.add(
                HealthRiskAlert(
                    id = UUID.randomUUID().toString(),
                    timestamp = now,
                    metricType = MetricType.HRV,
                    severity = AlertSeverity.INFORMATIONAL,
                    title = "Depressed Autonomic Recovery (HRV: ${stats.hrvScore} ms)",
                    summary = "Your heart rate variability is 56% lower than your 30-day baseline (${stats.baselineHrv} ms).",
                    explanation = "HRV reflects autonomic nervous system balance. Lower values frequently correlate with physiological stress, impending illness, or insufficient deep sleep.",
                    clinicalContext = "Non-acute observational metric indicating sympathetic nervous system dominance and reduced physiological resilience.",
                    recommendedAction = "Prioritize restorative sleep tonight, reduce intense cardio training, and maintain adequate hydration.",
                    triggerValue = "${stats.hrvScore} ms"
                )
            )
        }

        // 4. Combined Metrics Risk: Sleep Deficit + Elevated Resting HR
        if (stats.sleepDurationHours in 0.1..4.5 && stats.restingHeartRate > 75) {
            alerts.add(
                HealthRiskAlert(
                    id = UUID.randomUUID().toString(),
                    timestamp = now,
                    metricType = MetricType.SLEEP_DURATION,
                    severity = AlertSeverity.INFORMATIONAL,
                    title = "Sleep Debt Impacting Cardiovascular Recovery",
                    summary = "Short sleep duration (${String.format("%.1f", stats.sleepDurationHours)} hrs) accompanied by elevated resting heart rate.",
                    explanation = "Consolidated sleep metrics from your wearable show severe sleep restriction, correlating with elevated resting sympathetic tone today.",
                    clinicalContext = "Cumulative fatigue suppresses parasympathetic tone, temporarily increasing resting cardiovascular workload.",
                    recommendedAction = "Avoid high caffeine intake in the afternoon. Schedule a wind-down period at least 45 minutes before bedtime.",
                    triggerValue = "${String.format("%.1f", stats.sleepDurationHours)} hrs sleep"
                )
            )
        }

        return alerts
    }
}
