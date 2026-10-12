package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import com.example.model.ConnectionStatus
import com.example.model.HealthDevice
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DevicesScreen(
    repository: HealthRepository,
    modifier: Modifier = Modifier
) {
    val devices by repository.devices.collectAsState()
    var showCompatibilityInfo by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ECOSYSTEM HARDWARE",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Device Management",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = { showCompatibilityInfo = !showCompatibilityInfo },
                    modifier = Modifier.testTag("toggle_compatibility_info")
                ) {
                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Compatibility", tint = GoldLight)
                }
            }
        }

        // Action Buttons: Reconnect All & Trigger Sync
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { repository.reconnectAllDevices() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("reconnect_devices_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sync All Streams", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { repository.runTest4MissingData() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("simulate_disconnect_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LinkOff, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Disconnect", color = TextSecondary, fontSize = 13.sp)
                }
            }
        }

        // Compatibility Notice Panel
        if (showCompatibilityInfo) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Platform Integration Architecture",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Android Health Connect: Native system permission sync for steps, HR, sleep.\n" +
                                    "• Apple HealthKit: Supported via Apple Companion Sync bridge API.\n" +
                                    "• Google Wear OS: Direct sensor hub streaming with accelerometer & PPG.\n" +
                                    "• Garmin / Fitbit: Cloud OAuth2 REST webhooks and local Bluetooth LE.\n" +
                                    "Note: Direct hardware sensor access requires native OS permissions. Where restricted, simulated verified adapters are maintained.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Connected Wearables & Phones (${devices.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(devices) { device ->
            DeviceItemCard(
                device = device,
                onToggleEnabled = { repository.toggleDeviceEnabled(device.id) },
                onSetPreferredForSteps = { repository.setPreferredSourceForSteps(device.id) }
            )
        }
    }
}

@Composable
fun DeviceItemCard(
    device: HealthDevice,
    onToggleEnabled: () -> Unit,
    onSetPreferredForSteps: () -> Unit
) {
    val isConnected = device.connectionStatus == ConnectionStatus.CONNECTED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, if (isConnected) NavyBorder else RubyEmergency.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .testTag("device_card_${device.id}"),
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
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) TealNormal else RubyEmergency)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isConnected) NavySurfaceVariant else RubySubtle
                ) {
                    Text(
                        text = device.connectionStatus.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isConnected) TealNormal else RubyEmergency,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Manufacturer & API Info
            Text(
                text = "${device.manufacturer.displayName} • ${device.nativeApiSupport}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Battery: ${device.batteryPercent ?: "--"}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                Text(
                    text = "Last Synced: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(device.lastSyncTimestamp))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }

            if (device.lastSyncError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RubySubtle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sync Status: ${device.lastSyncError}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = RubyEmergency,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Preferred source toggle & status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (device.isPreferredSourceForSteps) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldPrimary.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Preferred Step Source", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = onSetPreferredForSteps,
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Set as Preferred Steps", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Switch(
                    checked = device.isEnabled,
                    onCheckedChange = { onToggleEnabled() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GoldPrimary,
                        checkedTrackColor = NavySurfaceVariant,
                        uncheckedThumbColor = TextTertiary,
                        uncheckedTrackColor = NavyDarkBackground
                    )
                )
            }
        }
    }
}
