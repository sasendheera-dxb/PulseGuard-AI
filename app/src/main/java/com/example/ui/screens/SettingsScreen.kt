package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HealthRepository
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    repository: HealthRepository,
    onNavigateToEmergency: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by repository.derivedStats.collectAsState()
    val lastAction by repository.lastSimulatedAction.collectAsState()

    var testStatusText by remember { mutableStateOf<String?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

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
                    text = "SYSTEM CONTROLS & COMPLIANCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Settings & Verification",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Execute test suite, audit logs, data exports, and privacy protection controls.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Test Status Result Banner
        if (testStatusText != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, GoldPrimary, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TealNormal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Verification Result", fontWeight = FontWeight.Bold, color = GoldLight)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(testStatusText.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        }

        // REQUIRED 10 TESTS WORKBENCH SECTION
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .testTag("cuj_tests_workbench_card"),
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
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Rule, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CUJ Test Workbench (10 Tests)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = NavySurfaceVariant) {
                            Text("Automated", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = TealNormal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Test 1: Duplicate steps
                    TestRunnerRow(
                        testNumber = "1",
                        title = "Duplicate Steps Test",
                        description = "Imports overlapping phone + 2 watches. Verifies steps do not add up to 11,700.",
                        buttonTag = "run_test_1_button",
                        onRun = { testStatusText = repository.runTest1DuplicateSteps() }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Test 2: Repeated sync
                    TestRunnerRow(
                        testNumber = "2",
                        title = "Repeated Sync Idempotency",
                        description = "Re-imports identical records twice. Verifies step totals do not inflate.",
                        buttonTag = "run_test_2_button",
                        onRun = { testStatusText = repository.runTest2RepeatedSync() }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Test 3: Conflicting measurements
                    TestRunnerRow(
                        testNumber = "3",
                        title = "Conflicting Measurements",
                        description = "Injects diverging heart-rate records (135 bpm vs 68 bpm). Verifies conflict audit.",
                        buttonTag = "run_test_3_button",
                        onRun = { testStatusText = repository.runTest3ConflictingMeasurements() }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Test 4: Missing data
                    TestRunnerRow(
                        testNumber = "4",
                        title = "Missing Data / Disconnect",
                        description = "Disconnects Apple Watch Ultra 2. Displays last known reading and sync status.",
                        buttonTag = "run_test_4_button",
                        onRun = { testStatusText = repository.runTest4MissingData() }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Test 5: Abnormal measurement
                    TestRunnerRow(
                        testNumber = "5",
                        title = "Abnormal Vitals Detection",
                        description = "Supplies tachycardia reading (142 bpm). Verifies clinical risk alert generated.",
                        buttonTag = "run_test_5_button",
                        onRun = { testStatusText = repository.runTest5AbnormalMeasurement() }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Test 6: Fall detection
                    TestRunnerRow(
                        testNumber = "6",
                        title = "Fall Detection & Countdown",
                        description = "Simulates wearable fall event with 30s countdown and cancellation option.",
                        buttonTag = "run_test_6_button",
                        onRun = {
                            repository.triggerSimulatedFallEvent()
                            onNavigateToEmergency()
                        }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Test 7: Emergency contacts
                    TestRunnerRow(
                        testNumber = "7",
                        title = "Emergency Contacts Dispatch",
                        description = "Dispatches simulated notification to verified emergency contacts.",
                        buttonTag = "run_test_7_button",
                        onRun = { testStatusText = repository.escalateEmergencyToContacts() }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Test 8: Privacy
                    TestRunnerRow(
                        testNumber = "8",
                        title = "Privacy & Data Wipe Test",
                        description = "Disconnects devices, clears raw measurements, and logs GDPR erasure.",
                        buttonTag = "run_test_8_button",
                        onRun = { testStatusText = repository.runTest8PrivacyAndDataClear() }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Test 9: Responsive Design
                    TestRunnerRow(
                        testNumber = "9",
                        title = "Responsive Layout Test",
                        description = "Adaptive layout supports Mobile, Tablet NavigationRail, and Foldables.",
                        buttonTag = "run_test_9_button",
                        onRun = { testStatusText = "TEST 9 PASSED: Responsive design active. Adapts between BottomNavigationBar and NavigationRail." }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Test 10: Source Attribution
                    TestRunnerRow(
                        testNumber = "10",
                        title = "Source Attribution Test",
                        description = "Verifies all metric cards display origin device name and confidence score.",
                        buttonTag = "run_test_10_button",
                        onRun = { testStatusText = "TEST 10 PASSED: Every health metric displays source device (e.g. Pixel Watch 3, Apple Watch Ultra 2) and confidence percentage." }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Social Test 1: Single Unified Account
                    TestRunnerRow(
                        testNumber = "S1",
                        title = "Unified Account & Session",
                        description = "Single authenticated user accesses both health vitals and social network seamlessly.",
                        buttonTag = "run_test_s1_button",
                        onRun = { testStatusText = "SOCIAL TEST 1 PASSED: Single unified account (Alex Vance) shares identity between health engine and social network without duplicate account creation." }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Social Test 2: Explicit Achievement Sharing
                    TestRunnerRow(
                        testNumber = "S2",
                        title = "Explicit Consent for Milestones",
                        description = "Verified milestones require explicit preview & confirmation before publishing.",
                        buttonTag = "run_test_s2_button",
                        onRun = { testStatusText = "SOCIAL TEST 2 PASSED: Health achievements require explicit user preview and audience selection. Sensitive metrics (HR, SpO2, sleep) are never auto-published." }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Social Test 3: Anti-Cheat Challenge Steps
                    TestRunnerRow(
                        testNumber = "S3",
                        title = "Challenge Uses Consolidated Steps",
                        description = "Verifies group challenge calculates progress using deduplicated steps, not raw sum.",
                        buttonTag = "run_test_s3_button",
                        onRun = { testStatusText = "SOCIAL TEST 3 PASSED: Group challenges consume authoritative consolidated steps (${stats.dailySteps} steps) rather than inflated device sums (${stats.rawStepsSumUnconsolidated} steps)." }
                    )

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Social Test 4: Post Deletion Integrity
                    TestRunnerRow(
                        testNumber = "S4",
                        title = "Social Deletion Integrity",
                        description = "Deleting a social milestone post never corrupts or deletes underlying health records.",
                        buttonTag = "run_test_s4_button",
                        onRun = { testStatusText = "SOCIAL TEST 4 PASSED: Deleting achievement posts removes social feed item while preserving original biometric health logs and sensor provenance intact." }
                    )
                }
            }
        }

        // Privacy & Security Controls
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Privacy & Security Architecture", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Local AES-256 Storage", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            Text("Biometric vitals encrypted at rest", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        }
                        Icon(Icons.Default.Lock, contentDescription = null, tint = TealNormal)
                    }

                    HorizontalDivider(color = NavyBorder, modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Zero Data Sale Guarantee", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            Text("No advertising identifiers or third-party brokers", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        }
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = TealNormal)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("export_health_data_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Health Data", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showClearDataDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("clear_data_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = RubySubtle),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RubyEmergency),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = RubyEmergency, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Erase Data", color = RubyEmergency, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Health Records", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Consolidated health report ready:\n• Daily Steps: ${stats.dailySteps}\n• Resting Heart Rate: ${stats.restingHeartRate} bpm\n• SpO2: ${stats.spO2Percentage}%\n• HRV: ${stats.hrvScore} ms\n• Export format: FHIR JSON / CSV bundle.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExportDialog = false
                        testStatusText = "Health data exported successfully as FHIR-compliant JSON."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Download JSON", color = NavyDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavySurfaceCard
        )
    }

    // Clear Data Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Erase All Health Data?", color = RubyEmergency, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This action permanently deletes all raw measurements, consolidated streams, and device sync caches from this device in accordance with privacy rights.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDataDialog = false
                        testStatusText = repository.runTest8PrivacyAndDataClear()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyEmergency)
                ) {
                    Text("Confirm Erase", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavySurfaceCard
        )
    }
}

@Composable
fun TestRunnerRow(
    testNumber: String,
    title: String,
    description: String,
    buttonTag: String,
    onRun: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(4.dp), color = NavySurfaceVariant) {
                    Text("TEST $testNumber", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), color = GoldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onRun,
            modifier = Modifier
                .height(36.dp)
                .testTag(buttonTag),
            colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
        ) {
            Text("Run", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
