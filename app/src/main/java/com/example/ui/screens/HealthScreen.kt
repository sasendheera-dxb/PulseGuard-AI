package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.HeartRateWaveformCanvas
import com.example.ui.components.MetricCard
import com.example.ui.theme.*

@Composable
fun HealthScreen(
    repository: HealthRepository,
    modifier: Modifier = Modifier
) {
    val stats by repository.derivedStats.collectAsState()
    var selectedTimeframe by remember { mutableIntStateOf(0) } // 0: Day, 1: Week, 2: Month

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
                    text = "PHYSIOLOGICAL VITALS",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Cardiovascular & Respiratory",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Consolidated measurements with individual source provenance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Timeframe selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavySurfaceCard)
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Day", "Week", "Month").forEachIndexed { index, label ->
                    val isSelected = selectedTimeframe == index
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) GoldPrimary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Button(
                            onClick = { selectedTimeframe = index },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) GoldPrimary else Color.Transparent,
                                contentColor = if (isSelected) NavyDarkBackground else TextSecondary
                            ),
                            elevation = null,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Heart Rate Deep Dive Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                    .testTag("health_hr_deep_dive"),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Heart Rate & Cardiac Tone", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                        Surface(shape = RoundedCornerShape(6.dp), color = NavySurfaceVariant) {
                            Text("Continuous Optical PPG", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = TealNormal, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Current Rate", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text("${stats.currentHeartRate} bpm", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = RubyEmergency)
                        }
                        Column {
                            Text("Resting Average", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text("${stats.restingHeartRate} bpm", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text("30-Day Baseline", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text("${stats.baselineRestingHr} bpm", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HeartRateWaveformCanvas(modifier = Modifier.fillMaxWidth().height(40.dp))

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Source: Google Pixel Watch 3 (Primary) fused with Apple Watch Ultra 2. Confidence: 95%.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // HRV & Recovery Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Heart Rate Variability (rMSSD)", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${stats.hrvScore} ms", fontWeight = FontWeight.Bold, color = PurpleHRV, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Measures millisecond variations between consecutive heartbeats (autonomic parasympathetic nervous system tone). Higher values indicate stronger physiological recovery and resilience.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (stats.hrvScore.toFloat() / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = PurpleHRV,
                        trackColor = NavySurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Low (<25ms)", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
                        Text("Personal Baseline: ${stats.baselineHrv}ms", style = MaterialTheme.typography.labelSmall, color = GoldLight, fontSize = 10.sp)
                        Text("Optimal (>65ms)", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
                    }
                }
            }
        }

        // Blood Oxygen & Respiratory Rate
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "SpO2 Saturation",
                    value = "${stats.spO2Percentage}",
                    unit = "%",
                    icon = Icons.Default.Air,
                    iconTint = BlueBiometric,
                    sourceDevice = "Apple Watch Ultra 2",
                    confidence = 0.94f,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Respiratory Rate",
                    value = "${stats.respiratoryRate}",
                    unit = "brpm",
                    icon = Icons.Default.Waves,
                    iconTint = TealNormal,
                    sourceDevice = "Apple Watch Ultra 2",
                    confidence = 0.91f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Sleep Architecture Breakdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sleep Architecture Breakdown", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${String.format("%.1f", stats.sleepDurationHours)} hrs", fontWeight = FontWeight.Bold, color = GoldLight)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sleep stages bar
                    Row(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))) {
                        Box(modifier = Modifier.weight(0.18f).fillMaxHeight().background(Color(0xFF818CF8))) // Deep Sleep
                        Box(modifier = Modifier.weight(0.24f).fillMaxHeight().background(Color(0xFF38BDF8))) // REM Sleep
                        Box(modifier = Modifier.weight(0.50f).fillMaxHeight().background(Color(0xFF1E293B))) // Light Sleep
                        Box(modifier = Modifier.weight(0.08f).fillMaxHeight().background(Color(0xFFF43F5E))) // Awake
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SleepStageLegend(label = "Deep", duration = "1h 22m", color = Color(0xFF818CF8))
                        SleepStageLegend(label = "REM", duration = "1h 48m", color = Color(0xFF38BDF8))
                        SleepStageLegend(label = "Light", duration = "3h 42m", color = Color(0xFF94A3B8))
                        SleepStageLegend(label = "Awake", duration = "35m", color = Color(0xFFF43F5E))
                    }
                }
            }
        }
    }
}

@Composable
fun SleepStageLegend(label: String, duration: String, color: Color) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 11.sp)
        }
        Text(duration, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 11.sp)
    }
}
