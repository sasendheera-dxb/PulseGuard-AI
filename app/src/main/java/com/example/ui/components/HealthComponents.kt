package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlertSeverity
import com.example.model.ConnectionStatus
import com.example.model.HealthRiskAlert
import com.example.ui.theme.*

@Composable
fun MetricCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    iconTint: Color,
    sourceDevice: String,
    confidence: Float,
    isNormal: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("metric_card_${title.lowercase().replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Confidence pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurfaceVariant
                ) {
                    Text(
                        text = "${(confidence * 100).toInt()}% conf",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (confidence > 0.9f) TealNormal else WarningAmber,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Source Attribution Badge (CUJ 10 requirement)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(NavySurfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Watch,
                    contentDescription = "Source",
                    tint = GoldPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = sourceDevice,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun HeartRateWaveformCanvas(
    modifier: Modifier = Modifier,
    lineColor: Color = RubyEmergency
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxWidth()) {
        val width = size.width
        val height = size.height
        val midY = height / 2

        val path = Path()
        path.moveTo(0f, midY)

        val points = listOf(
            0.05f to 0f,
            0.15f to 0f,
            0.20f to -8f,
            0.23f to 10f,
            0.28f to -40f, // R wave spike
            0.32f to 25f,  // S wave dip
            0.38f to -12f, // T wave
            0.45f to 0f,
            0.60f to 0f,
            0.68f to -10f,
            0.71f to -45f,
            0.75f to 28f,
            0.82f to -14f,
            0.90f to 0f,
            1.0f to 0f
        )

        for ((xRatio, yDelta) in points) {
            val px = width * xRatio
            val py = midY + yDelta
            path.lineTo(px, py)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
fun StepConsolidationBanner(
    consolidatedSteps: Int,
    rawStepsUnconsolidated: Int,
    duplicatesPrevented: Int,
    modifier: Modifier = Modifier,
    onInspectClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .testTag("step_consolidation_banner"),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Intelligent Duplicate Prevention",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                }

                TextButton(
                    onClick = onInspectClick,
                    modifier = Modifier.testTag("inspect_dedup_button")
                ) {
                    Text("Audit", color = GoldPrimary, fontSize = 13.sp)
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Inspect",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Consolidated Daily Steps", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        "$consolidatedSteps",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Raw Ingested Sum", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        "$rawStepsUnconsolidated",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextTertiary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Duplicates Filtered", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        "-$duplicatesPrevented steps",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TealNormal
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = {
                    if (rawStepsUnconsolidated > 0) {
                        (consolidatedSteps.toFloat() / rawStepsUnconsolidated.toFloat()).coerceIn(0f, 1f)
                    } else 1f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GoldPrimary,
                trackColor = NavySurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Multi-device fusion active. Wearables and phone overlapping walks were deduplicated to prevent false inflation.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun HealthAlertCard(
    alert: HealthRiskAlert,
    onAcknowledge: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (badgeBg, badgeTextColor, icon) = when (alert.severity) {
        AlertSeverity.POTENTIAL_EMERGENCY -> Triple(RubyEmergency, TextPrimary, Icons.Default.Warning)
        AlertSeverity.URGENT -> Triple(RubyEmergencyDark, TextPrimary, Icons.Default.ReportProblem)
        AlertSeverity.NON_URGENT -> Triple(WarningAmber, NavyDarkBackground, Icons.Default.Info)
        AlertSeverity.INFORMATIONAL -> Triple(InfoSky, TextPrimary, Icons.Default.Lightbulb)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, badgeBg.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("health_alert_${alert.id}"),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = badgeTextColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = alert.severity.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor
                        )
                    }
                }

                if (alert.triggerValue.isNotEmpty()) {
                    Text(
                        text = "Reading: ${alert.triggerValue}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = alert.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Clinical guidance box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NavySurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Clinical Context & Recommendation:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = alert.recommendedAction,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
