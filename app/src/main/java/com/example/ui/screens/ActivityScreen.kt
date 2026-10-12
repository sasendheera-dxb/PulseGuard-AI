package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HealthRepository
import com.example.model.AuditAction
import com.example.model.DeduplicatedRecord
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActivityScreen(
    repository: HealthRepository,
    modifier: Modifier = Modifier
) {
    val stats by repository.derivedStats.collectAsState()
    val dedupRecords by repository.deduplicatedRecords.collectAsState()
    val auditLogs by repository.auditLogs.collectAsState()
    val rawMeasurements by repository.rawMeasurements.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overlapping Windows, 1: Raw Records, 2: Audit Trail

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "DATA-FUSION ENGINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Intelligent Duplicate Prevention",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Eliminates multi-device double counting across concurrent smartwatches and phones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Metrics Summary Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Consolidated Daily Total", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(
                                "${stats.dailySteps} steps",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Raw Multi-Device Total", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(
                                "${stats.rawStepsSumUnconsolidated} steps",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TealNormalSubtle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TealNormal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Deduplication Savings: ${stats.deduplicationSavingsSteps} phantom steps prevented from false accumulation.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TealNormal,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = NavySurfaceCard,
                contentColor = GoldPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Reconciled Windows", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Raw Streams (${rawMeasurements.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Audit Trail", fontSize = 12.sp) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Reconciled Windows List
                item {
                    Text(
                        text = "Time-Window Activity Merges",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(dedupRecords) { record ->
                    ReconciledWindowCard(record = record)
                }
            }

            1 -> {
                // Raw Ingested Measurements
                item {
                    Text(
                        text = "Original Ingested Sensor Readings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(rawMeasurements) { raw ->
                    RawMeasurementCard(raw = raw)
                }
            }

            2 -> {
                // Audit Trail
                item {
                    Text(
                        text = "Provenance & Fusion Audit Log",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(auditLogs) { log ->
                    AuditLogCard(log = log)
                }
            }
        }
    }
}

@Composable
fun ReconciledWindowCard(record: DeduplicatedRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, if (record.isConflict) WarningAmber else NavyBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (record.isConflict) WarningAmber.copy(alpha = 0.2f) else NavySurfaceVariant
                ) {
                    Text(
                        text = record.strategy.name.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (record.isConflict) WarningAmber else GoldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${(record.confidence * 100).toInt()}% Confidence",
                    style = MaterialTheme.typography.labelSmall,
                    color = TealNormal,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${record.consolidatedValue.toInt()} ${record.unit}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Selected Source: ${record.selectedSourceDevice}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldLight,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NavySurfaceVariant
                ) {
                    Text(
                        text = "${record.rawRecordsCount} Devices Overlapped",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = record.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Contributing devices: ${record.contributingDeviceNames.joinToString(", ")}",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun RawMeasurementCard(raw: com.example.model.RawMeasurement) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${raw.metricType.displayName}: ${raw.value.toInt()} ${raw.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Source: ${raw.deviceName} (${raw.sourceApp})",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "Timestamp: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(raw.originalTimestamp))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = NavySurfaceVariant
            ) {
                Text(
                    text = "${raw.dataQuality} Quality",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (raw.dataQuality == com.example.model.DataQuality.HIGH) TealNormal else WarningAmber,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun AuditLogCard(log: com.example.model.AuditLogEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = log.action.name.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = when (log.action) {
                        AuditAction.CONFLICT_FLAGGED -> WarningAmber
                        AuditAction.DATA_CLEARED -> RubyEmergency
                        else -> GoldPrimary
                    }
                )
                Text(
                    text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Devices: ${log.devicesInvolved}",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                fontSize = 10.sp
            )
        }
    }
}
