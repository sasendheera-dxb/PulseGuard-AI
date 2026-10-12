package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.ai.GeminiHealthService
import com.example.data.HealthRepository
import com.example.model.HealthRiskAlert
import com.example.ui.components.HealthAlertCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiInsightsScreen(
    repository: HealthRepository,
    geminiService: GeminiHealthService,
    modifier: Modifier = Modifier
) {
    val stats by repository.derivedStats.collectAsState()
    val alerts by repository.activeAlerts.collectAsState()
    val devices by repository.devices.collectAsState()

    var userQuery by remember { mutableStateOf("") }
    var chatResponse by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val suggestedQuestions = listOf(
        "Why was my resting heart rate higher today?",
        "Which device recorded these steps?",
        "Why were duplicate measurements excluded?",
        "How has my sleep and HRV changed?",
        "What does my active health alert mean?",
        "Find nearest emergency medical center"
    )

    fun sendQuestion(query: String) {
        userQuery = query
        isLoading = true
        coroutineScope.launch {
            val deviceSummary = devices.filter { it.connectionStatus.name == "CONNECTED" }
                .joinToString(", ") { "${it.name} (${it.manufacturer.displayName})" }
            val dedupSummary = "Consolidated: ${stats.dailySteps} steps from ${stats.rawStepsSumUnconsolidated} raw steps across devices; ${stats.deduplicationSavingsSteps} duplicate steps filtered."

            val resp = geminiService.askAssistant(query, stats, alerts, deviceSummary, dedupSummary)
            chatResponse = resp
            isLoading = false
        }
    }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GEMINI INTELLIGENCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "AI Health Assistant & Insights",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Personalized baseline analysis and sensor trend explanations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Active Alerts Section
        if (alerts.isNotEmpty()) {
            item {
                Text(
                    text = "Clinical Risk Alerts (${alerts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = RubyEmergency
                )
            }

            items(alerts) { alert ->
                HealthAlertCard(alert = alert, onAcknowledge = {})
            }
        }

        // Personalized Baseline Comparison Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Personal Baseline vs Today's Vitals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    BaselineComparisonRow(
                        metric = "Resting Heart Rate",
                        current = "${stats.restingHeartRate} bpm",
                        baseline = "${stats.baselineRestingHr} bpm",
                        status = if (stats.restingHeartRate > stats.baselineRestingHr + 10) "Elevated" else "Normal",
                        statusColor = if (stats.restingHeartRate > stats.baselineRestingHr + 10) WarningAmber else TealNormal
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    BaselineComparisonRow(
                        metric = "Heart Rate Variability (HRV)",
                        current = "${stats.hrvScore} ms",
                        baseline = "${stats.baselineHrv} ms",
                        status = if (stats.hrvScore < 30) "Low Recovery" else "Optimal",
                        statusColor = if (stats.hrvScore < 30) WarningAmber else TealNormal
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    BaselineComparisonRow(
                        metric = "Blood Oxygen (SpO2)",
                        current = "${stats.spO2Percentage}%",
                        baseline = "97-99%",
                        status = if (stats.spO2Percentage < 94) "Sub-optimal" else "Healthy",
                        statusColor = if (stats.spO2Percentage < 94) RubyEmergency else TealNormal
                    )
                }
            }
        }

        // Interactive AI Assistant Box
        item {
            Text(
                text = "Ask PulseGuard AI Assistant",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Suggested Prompt Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestedQuestions) { q ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = NavySurfaceVariant,
                        modifier = Modifier
                            .clickable { sendQuestion(q) }
                            .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                    ) {
                        Text(
                            text = q,
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Query Input Field
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userQuery,
                    onValueChange = { userQuery = it },
                    placeholder = { Text("Ask about heart rate, steps, devices, alerts...", color = TextTertiary, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_assistant_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NavySurfaceCard,
                        unfocusedContainerColor = NavySurfaceCard,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (userQuery.isNotBlank()) sendQuestion(userQuery)
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GoldPrimary)
                        .testTag("ai_assistant_send_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NavyDarkBackground)
                }
            }
        }

        // Loading or Response Display
        if (isLoading) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Analyzing physiological patterns and sensors...", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        } else if (chatResponse != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .testTag("ai_assistant_response_card"),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NavyDarkBackground, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PulseGuard AI Clinical Analysis", fontWeight = FontWeight.Bold, color = GoldLight, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = chatResponse.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 20.sp,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavySurfaceVariant
                        ) {
                            Text(
                                text = "Medical Disclaimer: PulseGuard AI provides physiological tracking and early risk detection, not a diagnostic medical evaluation. In life-threatening emergencies, dial 911 or visit an emergency room.",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BaselineComparisonRow(
    metric: String,
    current: String,
    baseline: String,
    status: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(metric, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("Baseline: $baseline", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(current, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(status, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = statusColor)
        }
    }
}
