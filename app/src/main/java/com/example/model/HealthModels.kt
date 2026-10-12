package com.example.model

enum class MetricType(val displayName: String, val unit: String) {
    STEPS("Steps", "steps"),
    DISTANCE("Distance", "km"),
    HEART_RATE("Heart Rate", "bpm"),
    RESTING_HEART_RATE("Resting Heart Rate", "bpm"),
    HRV("Heart Rate Variability", "ms"),
    SPO2("Blood Oxygen (SpO2)", "%"),
    SLEEP_DURATION("Sleep Duration", "hrs"),
    ACTIVE_MINUTES("Active Minutes", "min"),
    CALORIES_BURNED("Calories Burned", "kcal"),
    RESPIRATORY_RATE("Respiratory Rate", "brpm"),
    SKIN_TEMPERATURE("Skin Temperature", "°C"),
    FALL_DETECTION("Fall Detection", "event")
}

enum class DataQuality {
    HIGH,
    MEDIUM,
    LOW,
    SUSPICIOUS
}

enum class DeviceType(val label: String) {
    SMARTWATCH("Smartwatch"),
    FITNESS_BAND("Fitness Band"),
    SMARTPHONE("Smartphone"),
    TABLET("Tablet"),
    CHEST_STRAP("ECG Chest Strap")
}

enum class Manufacturer(val displayName: String) {
    APPLE("Apple"),
    GOOGLE("Google Wear OS"),
    SAMSUNG("Samsung Health"),
    GARMIN("Garmin"),
    FITBIT("Fitbit"),
    HEALTH_CONNECT("Android Health Connect"),
    HEALTHKIT("Apple HealthKit")
}

enum class ConnectionStatus {
    CONNECTED,
    DISCONNECTED,
    SYNCING,
    ERROR
}

data class HealthDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val manufacturer: Manufacturer,
    var connectionStatus: ConnectionStatus,
    val batteryPercent: Int?,
    var lastSyncTimestamp: Long,
    val supportedMetrics: List<MetricType>,
    val nativeApiSupport: String,
    var isEnabled: Boolean = true,
    var isPreferredSourceForSteps: Boolean = false,
    var isPreferredSourceForHeartRate: Boolean = false,
    var lastSyncError: String? = null,
    val totalMeasurementsContributed: Int = 0
)

data class RawMeasurement(
    val id: String,
    val metricType: MetricType,
    val value: Double,
    val unit: String,
    val originalTimestamp: Long,
    val deviceId: String,
    val deviceName: String,
    val sourceApp: String,
    val dataQuality: DataQuality = DataQuality.HIGH,
    val confidence: Float = 0.95f,
    val recordFingerprint: String = "",
    val timeBucket: Long = originalTimestamp / (15 * 60 * 1000) // 15-min bucket
)

enum class ReconciliationStrategy {
    HIGHEST_CONFIDENCE_SENSOR,
    ACTIVITY_PROVENANCE_FUSION,
    USER_PREFERRED_OVERRIDE,
    DISAMBIGUATED_OVERLAP,
    CONFLICT_FLAGGED
}

data class DeduplicatedRecord(
    val id: String,
    val metricType: MetricType,
    val consolidatedValue: Double,
    val unit: String,
    val timeWindowStart: Long,
    val timeWindowEnd: Long,
    val contributingDeviceIds: List<String>,
    val contributingDeviceNames: List<String>,
    val rawRecordsCount: Int,
    val selectedSourceDevice: String,
    val strategy: ReconciliationStrategy,
    val confidence: Float,
    val explanation: String,
    val isConflict: Boolean = false
)

data class DerivedHealthStats(
    val dailySteps: Int,
    val rawStepsSumUnconsolidated: Int, // e.g. 11,700 before deduplication!
    val currentHeartRate: Int,
    val restingHeartRate: Int,
    val hrvScore: Int,
    val spO2Percentage: Int,
    val sleepDurationHours: Double,
    val activeMinutes: Int,
    val caloriesBurned: Int,
    val respiratoryRate: Int,
    val skinTemperature: Double,
    val deduplicationSavingsSteps: Int,
    val activeAlertsCount: Int,
    val connectedDevicesCount: Int,
    val lastSyncFormatted: String,
    val baselineRestingHr: Int = 62,
    val baselineHrv: Int = 55
)

enum class AlertSeverity(val label: String) {
    INFORMATIONAL("Observation"),
    NON_URGENT("Moderate Warning"),
    URGENT("Urgent Health Warning"),
    POTENTIAL_EMERGENCY("Critical Alert")
}

data class HealthRiskAlert(
    val id: String,
    val timestamp: Long,
    val metricType: MetricType,
    val severity: AlertSeverity,
    val title: String,
    val summary: String,
    val explanation: String,
    val clinicalContext: String,
    val recommendedAction: String,
    var isAcknowledged: Boolean = false,
    val isSimulated: Boolean = false,
    val triggerValue: String = ""
)

enum class EmergencyTrigger {
    FALL_DETECTED,
    IMMOBILITY_POST_FALL,
    MANUAL_SOS,
    CRITICAL_VITALS_ANOMALY
}

enum class EmergencyStatus {
    IDLE,
    COUNTDOWN_ACTIVE,
    CANCELLED_BY_USER,
    ESCALATED_CONTACTS_NOTIFIED,
    SIMULATED_SERVICES_DISPATCHED
}

data class EmergencyContact(
    val id: String,
    val name: String,
    val relationship: String,
    val phone: String,
    val isVerified: Boolean = true,
    var notifyOnFall: Boolean = true,
    var notifyOnSos: Boolean = true
)

data class EmergencyEvent(
    val id: String,
    val timestamp: Long,
    val trigger: EmergencyTrigger,
    var status: EmergencyStatus,
    val locationDescription: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val simulatedContactsNotified: List<String> = emptyList(),
    val outcomeNotes: String = ""
)

enum class AuditAction {
    DEDUPLICATION_MERGE,
    IDENTICAL_RECORD_DISCARD,
    SOURCE_PREFERENCE_APPLIED,
    CONFLICT_FLAGGED,
    RAW_RECORD_INGESTED,
    DEVICE_DISCONNECTED,
    DATA_CLEARED
}

data class AuditLogEntry(
    val id: String,
    val timestamp: Long,
    val action: AuditAction,
    val metricType: MetricType,
    val description: String,
    val devicesInvolved: String,
    val resolutionConfidence: Float
)
