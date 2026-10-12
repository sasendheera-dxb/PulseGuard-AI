package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.SocialRepository
import com.example.model.SocialGroup
import com.example.ui.components.GroupCard
import com.example.ui.theme.*

@Composable
fun GroupsScreen(
    socialRepository: SocialRepository,
    modifier: Modifier = Modifier
) {
    val groups by socialRepository.groups.collectAsState()
    var showCreateGroupDialog by remember { mutableStateOf(false) }

    var newGroupName by remember { mutableStateOf("") }
    var newGroupDesc by remember { mutableStateOf("") }
    var newGroupCategory by remember { mutableStateOf("General Fitness") }
    var isNewGroupPrivate by remember { mutableStateOf(false) }

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
                        text = "COMMUNITIES & CLUBS",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Groups & Communities",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Join special interest health circles and team challenges.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showCreateGroupDialog = true },
                    modifier = Modifier.testTag("create_group_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = NavyDarkBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Group", color = NavyDarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Active Groups List
        items(groups) { group ->
            GroupCard(
                group = group,
                onToggleJoin = { socialRepository.toggleGroupMembership(group.id) }
            )
        }
    }

    // Create Group Dialog
    if (showCreateGroupDialog) {
        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            title = { Text("Create New Community Group", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newGroupName,
                        onValueChange = { newGroupName = it },
                        label = { Text("Group Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newGroupDesc,
                        onValueChange = { newGroupDesc = it },
                        label = { Text("Description & Mission") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newGroupCategory,
                        onValueChange = { newGroupCategory = it },
                        label = { Text("Category (e.g. Walking, Running, Cycling)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Private Group (Requires approval)", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        Switch(
                            checked = isNewGroupPrivate,
                            onCheckedChange = { isNewGroupPrivate = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGroupName.isNotBlank()) {
                            socialRepository.toggleGroupMembership(newGroupName)
                            showCreateGroupDialog = false
                            newGroupName = ""
                            newGroupDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Create Group", color = NavyDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGroupDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavySurfaceCard
        )
    }
}
