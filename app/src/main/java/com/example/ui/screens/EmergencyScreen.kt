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
import com.example.model.EmergencyContact
import com.example.model.EmergencyStatus
import com.example.ui.theme.*

@Composable
fun EmergencyScreen(
    repository: HealthRepository,
    modifier: Modifier = Modifier
) {
    val emergencyContacts by repository.emergencyContacts.collectAsState()
    val activeEvent by repository.activeEmergencyEvent.collectAsState()
    val lastAction by repository.lastSimulatedAction.collectAsState()

    var showAddContactDialog by remember { mutableStateOf(false) }

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
                    text = "SAFETY & CRISIS INTERVENTION",
                    style = MaterialTheme.typography.labelSmall,
                    color = RubyEmergency,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Emergency & Fall Detection",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Autonomous fall response, SOS dispatch, and verified medical contact escalation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Prominent SOS Button Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, RubyEmergency, RoundedCornerShape(20.dp))
                    .testTag("emergency_sos_card"),
                colors = CardDefaults.cardColors(containerColor = RubySubtle)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MANUAL EMERGENCY ASSISTANCE",
                        style = MaterialTheme.typography.labelMedium,
                        color = RubyEmergency,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { repository.triggerManualSos() },
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .testTag("sos_giant_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = RubyEmergency),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Sos, contentDescription = "SOS", modifier = Modifier.size(40.dp), tint = TextPrimary)
                            Text("HOLD SOS", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pressing triggers a 30-second cancellation countdown before notifying your verified emergency contacts and sharing real-time GPS location.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Test Fall Simulator Bar (CUJ 6 & 7)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonalInjury, contentDescription = null, tint = WarningAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fall Detection Simulator", fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NavySurfaceVariant
                        ) {
                            Text("TEST CUJ 6", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Simulates wearable accelerometer shock spike + rapid barometric altimeter drop + subsequent immobility window.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { repository.triggerSimulatedFallEvent() },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("trigger_fall_sim_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AirlineSeatFlat, contentDescription = null, tint = NavyDarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulate Fall", color = NavyDarkBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { repository.escalateEmergencyToContacts() },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("test_contact_dispatch_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContactPhone, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Contacts", color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Medical ID Card
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
                        Text("Emergency Medical ID", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Surface(shape = RoundedCornerShape(6.dp), color = NavySurfaceVariant) {
                            Text("Encrypted Locally", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = TealNormal, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Blood Type", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            Text("O-Positive (O+)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text("Known Allergies", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            Text("Penicillin, Sulfa", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text("Organ Donor", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            Text("Yes", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Emergency Contacts List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configured Contacts (${emergencyContacts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        items(emergencyContacts) { contact ->
            EmergencyContactCard(contact = contact)
        }

        // Safety Simulation Notice
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NavySurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Development Safety Policy: Live 911/112 cellular dialing is strictly simulated in development mode to prevent accidental public safety dispatch. Background continuous fall monitoring requires compatible wearable hardware motion coprocessors.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
        }
    }

    // Active Emergency Countdown Overlay Dialog (CUJ 6 & 7)
    if (activeEvent != null && activeEvent?.status == EmergencyStatus.COUNTDOWN_ACTIVE) {
        EmergencyActiveDialog(
            event = activeEvent!!,
            onCancel = { repository.cancelEmergencyEvent() },
            onDispatchNow = { repository.escalateEmergencyToContacts() }
        )
    } else if (activeEvent != null && activeEvent?.status == EmergencyStatus.SIMULATED_SERVICES_DISPATCHED) {
        AlertDialog(
            onDismissRequest = { repository.dismissEmergencyModal() },
            title = {
                Text("Emergency Contacts Notified", color = RubyEmergency, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Simulated emergency dispatch completed. The following verified contacts were sent your GPS coordinates and recent biometrics:\n\n${activeEvent?.simulatedContactsNotified?.joinToString("\n")}\n\n${activeEvent?.outcomeNotes}", color = TextSecondary, fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { repository.dismissEmergencyModal() },
                    colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant)
                ) {
                    Text("Dismiss", color = TextPrimary)
                }
            },
            containerColor = NavySurfaceCard
        )
    } else if (activeEvent != null && activeEvent?.status == EmergencyStatus.CANCELLED_BY_USER) {
        AlertDialog(
            onDismissRequest = { repository.dismissEmergencyModal() },
            title = {
                Text("Emergency Cancelled", color = TealNormal, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("You marked yourself safe. No emergency contacts were alerted and no dispatch was executed.", color = TextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = { repository.dismissEmergencyModal() },
                    colors = ButtonDefaults.buttonColors(containerColor = TealNormal)
                ) {
                    Text("OK", color = NavyDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = NavySurfaceCard
        )
    }
}

@Composable
fun EmergencyActiveDialog(
    event: com.example.model.EmergencyEvent,
    onCancel: () -> Unit,
    onDispatchNow: () -> Unit
) {
    var countdownSec by remember { mutableIntStateOf(30) }

    LaunchedEffect(event.id) {
        countdownSec = 30
        while (countdownSec > 0) {
            kotlinx.coroutines.delay(1000)
            countdownSec--
        }
        if (countdownSec == 0) {
            onDispatchNow()
        }
    }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = RubyEmergency, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (event.trigger == com.example.model.EmergencyTrigger.FALL_DETECTED) "FALL DETECTED!" else "SOS ACTIVATED!",
                    color = RubyEmergency,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Are you okay? We detected a possible hard impact and sudden lack of motion.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(RubySubtle)
                        .border(3.dp, RubyEmergency, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$countdownSec",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = RubyEmergency
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Auto-dispatching in $countdownSec seconds if no response.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "GPS: ${event.locationDescription}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("emergency_cancel_button"),
                colors = ButtonDefaults.buttonColors(containerColor = TealNormal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = NavyDarkBackground)
                Spacer(modifier = Modifier.width(8.dp))
                Text("I AM SAFE - CANCEL ALERT", color = NavyDarkBackground, fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDispatchNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(top = 8.dp)
                    .testTag("emergency_dispatch_now_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RubyEmergency),
                border = androidx.compose.foundation.BorderStroke(1.dp, RubyEmergency),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("I NEED HELP NOW - DISPATCH CONTACTS", fontWeight = FontWeight.Bold, color = RubyEmergency, fontSize = 12.sp)
            }
        },
        containerColor = NavySurfaceCard,
        modifier = Modifier.border(2.dp, RubyEmergency, RoundedCornerShape(24.dp))
    )
}

@Composable
fun EmergencyContactCard(contact: EmergencyContact) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(contact.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    if (contact.isVerified) {
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = TealNormal, modifier = Modifier.size(16.dp))
                    }
                }
                Text("${contact.relationship} • ${contact.phone}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (contact.notifyOnFall) {
                        Surface(shape = RoundedCornerShape(4.dp), color = NavySurfaceVariant) {
                            Text("Fall Alert", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), color = WarningAmber, fontSize = 9.sp)
                        }
                    }
                    if (contact.notifyOnSos) {
                        Surface(shape = RoundedCornerShape(4.dp), color = NavySurfaceVariant) {
                            Text("SOS Dispatch", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), color = RubyEmergency, fontSize = 9.sp)
                        }
                    }
                }
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.Phone, contentDescription = "Call", tint = GoldPrimary)
            }
        }
    }
}
