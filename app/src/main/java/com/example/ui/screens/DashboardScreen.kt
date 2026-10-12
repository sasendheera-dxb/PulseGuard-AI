package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.HealthRepository
import com.example.data.SocialRepository
import com.example.model.HealthRiskAlert
import com.example.ui.components.HealthAlertCard
import com.example.ui.components.HeartRateWaveformCanvas
import com.example.ui.components.MetricCard
import com.example.ui.components.StepConsolidationBanner
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    repository: HealthRepository,
    socialRepository: SocialRepository? = null,
    onNavigateToActivity: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToDevices: () -> Unit,
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToChallenges: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stats by repository.derivedStats.collectAsState()
    val alerts by repository.activeAlerts.collectAsState()
    val devices by repository.devices.collectAsState()
    val lastAction by repository.lastSimulatedAction.collectAsState()

    val challenges = socialRepository?.challenges?.collectAsState()?.value.orEmpty()
    val posts = socialRepository?.posts?.collectAsState()?.value.orEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header & Sync Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(TealNormal)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PULSEGUARD AI ECOSYSTEM",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TealNormal,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Text(
                        text = "Unified Health Vitals",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NavySurfaceCard,
                    modifier = Modifier.border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync",
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${devices.count { it.connectionStatus.name == "CONNECTED" }} Devices",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Hero Biometric Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.pulseguard_banner_1791785189733)
                            .crossfade(true)
                            .build(),
                        contentDescription = "PulseGuard AI Ecosystem Hero",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Subtle gradient overlay for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        NavyDarkBackground.copy(alpha = 0.2f),
                                        NavyDarkBackground.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldPrimary
                        ) {
                            Text(
                                text = "CROSS-PLATFORM DATA FUSION",
                                style = MaterialTheme.typography.labelSmall,
                                color = NavyDarkBackground,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Zero-Duplicate Health Intelligence",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Wearables • HealthKit • Health Connect • Cloud",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Recent action feedback pill (if simulated action happened)
        if (lastAction != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = lastAction.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Duplicate Prevention Step Consolidation Banner (CUJ 1 & 2 Core)
        item {
            StepConsolidationBanner(
                consolidatedSteps = stats.dailySteps,
                rawStepsUnconsolidated = stats.rawStepsSumUnconsolidated,
                duplicatesPrevented = stats.deduplicationSavingsSteps,
                onInspectClick = onNavigateToActivity
            )
        }

        // Live Heart Rate Card with Real-time Waveform
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                    .testTag("heart_rate_live_card"),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .background(RubyEmergency.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Pulse",
                                    tint = RubyEmergency,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Heart Rate Stream",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Resting: ${stats.restingHeartRate} bpm • Baseline: ${stats.baselineRestingHr} bpm",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${stats.currentHeartRate}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = RubyEmergency
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "bpm",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Animated ECG waveform
                    HeartRateWaveformCanvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        lineColor = if (stats.currentHeartRate > 105) RubyEmergency else TealNormal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Source: Google Pixel Watch 3 (PPG Continuous)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Confidence: 96%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TealNormal,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Active Health Risk Alerts
        if (alerts.isNotEmpty()) {
            item {
                Text(
                    text = "AI Health Risk Warnings (${alerts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = RubyEmergency
                )
            }
            items(alerts.size) { index ->
                HealthAlertCard(
                    alert = alerts[index],
                    onAcknowledge = { }
                )
            }
        }

        // Biometrics Grid
        item {
            Text(
                text = "Consolidated Biometrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Blood Oxygen",
                    value = "${stats.spO2Percentage}",
                    unit = "%",
                    icon = Icons.Default.Air,
                    iconTint = BlueBiometric,
                    sourceDevice = "Apple Watch Ultra 2",
                    confidence = 0.94f,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "HRV",
                    value = "${stats.hrvScore}",
                    unit = "ms",
                    icon = Icons.Default.Timeline,
                    iconTint = PurpleHRV,
                    sourceDevice = "Pixel Watch 3",
                    confidence = 0.92f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Sleep Duration",
                    value = String.format("%.1f", stats.sleepDurationHours),
                    unit = "hrs",
                    icon = Icons.Default.Bedtime,
                    iconTint = GoldLight,
                    sourceDevice = "Apple Watch Ultra 2",
                    confidence = 0.98f,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Active Time",
                    value = "${stats.activeMinutes}",
                    unit = "min",
                    icon = Icons.Default.FitnessCenter,
                    iconTint = TealNormal,
                    sourceDevice = "Pixel Watch 3 + Phone",
                    confidence = 0.95f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Respiration",
                    value = "${stats.respiratoryRate}",
                    unit = "brpm",
                    icon = Icons.Default.Waves,
                    iconTint = BlueBiometric,
                    sourceDevice = "Apple Watch Ultra 2",
                    confidence = 0.90f,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Skin Temp",
                    value = String.format("%.1f", stats.skinTemperature),
                    unit = "°C",
                    icon = Icons.Default.Thermostat,
                    iconTint = WarningAmber,
                    sourceDevice = "Pixel Watch 3",
                    confidence = 0.91f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Active Group Challenges Section (Integrated Social Health)
        if (challenges.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Group Challenges",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    TextButton(onClick = onNavigateToChallenges) {
                        Text("View All (${challenges.size})", color = GoldPrimary, fontSize = 12.sp)
                    }
                }
            }

            val topChallenge = challenges.first()
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(topChallenge.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Surface(shape = RoundedCornerShape(6.dp), color = GoldPrimary.copy(alpha = 0.15f)) {
                                Text("${topChallenge.daysRemaining}d left", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = GoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Group: ${topChallenge.groupName} • Goal: ${topChallenge.targetSteps} steps", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        val userPart = topChallenge.participants.find { it.isUser }
                        val userSteps = userPart?.consolidatedSteps ?: stats.dailySteps
                        val progress = (userSteps.toFloat() / topChallenge.targetSteps.toFloat()).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = GoldPrimary,
                            trackColor = NavySurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Your Verified Progress: $userSteps steps", style = MaterialTheme.typography.labelSmall, color = TealNormal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = GoldLight, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Friends' Shared Achievements & Community Highlights
        if (posts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Community Highlights",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = onNavigateToCommunity) {
                        Text("Community Feed", color = GoldPrimary, fontSize = 12.sp)
                    }
                }
            }

            val achievementPost = posts.find { it.achievementMeta != null } ?: posts.first()
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(GoldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(achievementPost.authorAvatar, fontWeight = FontWeight.Bold, color = GoldLight, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(achievementPost.authorName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                Text("@${achievementPost.authorUsername}", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(achievementPost.content, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 12.sp)
                        if (achievementPost.achievementMeta != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(shape = RoundedCornerShape(8.dp), color = NavySurface, modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${achievementPost.achievementMeta.title}: ${achievementPost.achievementMeta.verifiedConsolidatedValue}", style = MaterialTheme.typography.labelSmall, color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Emergency Bar
        item {
            Button(
                onClick = onNavigateToEmergency,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("dashboard_sos_button"),
                colors = ButtonDefaults.buttonColors(containerColor = RubyEmergency),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Emergency, contentDescription = null, tint = TextPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Emergency Assistance & Fall Shield",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
            }
        }
    }
}
