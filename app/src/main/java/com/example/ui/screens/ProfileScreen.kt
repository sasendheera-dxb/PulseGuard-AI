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
import com.example.data.SocialRepository
import com.example.model.PostVisibility
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    socialRepository: SocialRepository,
    modifier: Modifier = Modifier
) {
    val user by socialRepository.currentUser.collectAsState()
    val achievements by socialRepository.eligibleAchievements.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
                    .testTag("user_profile_header"),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.2f))
                                .border(2.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.avatarInitials,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "@${user.username}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Member since ${user.memberSince}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social Stats Row (Friends, Followers, Badges)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatItem(count = "${user.friendsCount}", label = "Friends")
                        ProfileStatItem(count = "${user.followersCount}", label = "Followers")
                        ProfileStatItem(count = "${user.followingCount}", label = "Following")
                        ProfileStatItem(count = "${user.earnedBadgesCount}", label = "Badges")
                    }
                }
            }
        }

        // Granular Social Privacy Controls (Requirement 3 & 14)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                    .testTag("privacy_settings_card"),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Social & Health Privacy Controls", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Icon(Icons.Default.Security, contentDescription = null, tint = TealNormal)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Visibility level selector
                    Text("Profile Visibility:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(PostVisibility.PUBLIC, PostVisibility.FRIENDS, PostVisibility.ONLY_ME).forEach { vis ->
                            val isSelected = user.privacyLevel == vis
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) GoldPrimary else NavySurfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Button(
                                    onClick = {
                                        socialRepository.updateProfilePrivacy(
                                            vis,
                                            user.allowAchievementSharing,
                                            user.allowChallengeLeaderboard
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) GoldPrimary else NavySurfaceVariant,
                                        contentColor = if (isSelected) NavyDarkBackground else TextPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 0.dp)
                                ) {
                                    Text(vis.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Allow Milestone Sharing", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Prompts to share achievements after explicit review", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = user.allowAchievementSharing,
                            onCheckedChange = {
                                socialRepository.updateProfilePrivacy(
                                    user.privacyLevel,
                                    it,
                                    user.allowChallengeLeaderboard
                                )
                            }
                        )
                    }

                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Show On Challenge Leaderboards", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Display verified consolidated steps to competitors", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = user.allowChallengeLeaderboard,
                            onCheckedChange = {
                                socialRepository.updateProfilePrivacy(
                                    user.privacyLevel,
                                    user.allowAchievementSharing,
                                    it
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(6.dp), color = NavySurfaceVariant) {
                        Text(
                            text = "Medical Confidentiality Notice: Heart rate, HRV, SpO2, sleep details, medical alerts, and fall events are strictly segregated and NEVER accessible via social profiles.",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Earned Health Achievements
        item {
            Text(
                text = "Verified Achievements (${achievements.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(achievements) { ach ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(ach.title, fontWeight = FontWeight.Bold, color = GoldLight, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            if (ach.isShared) {
                                Surface(shape = RoundedCornerShape(4.dp), color = TealNormalSubtle) {
                                    Text("SHARED", modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp), color = TealNormal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(ach.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                        Text("Verified: ${ach.verifiedMetric} • ${ach.unlockedDate}", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 11.sp)
    }
}
