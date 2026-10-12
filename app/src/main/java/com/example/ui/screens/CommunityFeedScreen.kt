package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.model.*
import com.example.ui.components.PostCard
import com.example.ui.theme.*

@Composable
fun CommunityFeedScreen(
    socialRepository: SocialRepository,
    modifier: Modifier = Modifier
) {
    val posts by socialRepository.posts.collectAsState()
    val eligibleAchievements by socialRepository.eligibleAchievements.collectAsState()

    var newPostText by remember { mutableStateOf("") }
    var selectedVisibility by remember { mutableStateOf(PostVisibility.PUBLIC) }
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: All, 1: Friends, 2: Achievements, 3: Challenges

    var achievementToShare by remember { mutableStateOf<EligibleHealthAchievement?>(null) }
    var customShareMessage by remember { mutableStateOf("") }
    var shareAudience by remember { mutableStateOf(PostVisibility.PUBLIC) }

    val filteredPosts = when (selectedFilter) {
        1 -> posts.filter { it.visibility == PostVisibility.FRIENDS || it.isCurrentUser }
        2 -> posts.filter { it.achievementMeta != null }
        3 -> posts.filter { it.pollMeta != null || it.groupName != null }
        else -> posts
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
                Text(
                    text = "PULSEGUARD COMMUNITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Social Health Feed",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Connect with friends, celebrate verified milestones, and join community discussions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Create Post Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                    .testTag("create_post_card"),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = newPostText,
                        onValueChange = { newPostText = it },
                        placeholder = { Text("Share an update or fitness tip with your community...", color = TextTertiary, fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_post_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NavySurface,
                            unfocusedContainerColor = NavySurface,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Audience Selector Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavySurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = GoldLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(selectedVisibility.label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Button(
                            onClick = {
                                if (newPostText.isNotBlank()) {
                                    socialRepository.createPost(
                                        content = newPostText,
                                        visibility = selectedVisibility
                                    )
                                    newPostText = ""
                                }
                            },
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("publish_post_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                        ) {
                            Text("Post", color = NavyDarkBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Pending Unshared Achievements Banner (Requirement 8: Explicit Confirmation)
        val unshared = eligibleAchievements.filter { !it.isShared }
        if (unshared.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("New Milestone Unlocked!", fontWeight = FontWeight.Bold, color = GoldLight, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${unshared.first().title}: ${unshared.first().verifiedMetric}. PulseGuard AI never auto-publishes health data without your explicit approval.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                achievementToShare = unshared.first()
                                customShareMessage = "Proud to reach my ${unshared.first().title}!"
                            },
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("open_share_achievement_dialog"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Review & Share Milestone", color = NavyDarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Feed Filter Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedFilter,
                containerColor = NavySurfaceCard,
                contentColor = GoldPrimary,
                edgePadding = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            ) {
                listOf("All Posts", "Friends Only", "Achievements", "Communities").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        text = { Text(title, fontSize = 12.sp) }
                    )
                }
            }
        }

        // Posts List
        items(filteredPosts) { post ->
            PostCard(
                post = post,
                onReactionClick = { reaction -> socialRepository.toggleReaction(post.id, reaction) },
                onAddComment = { text -> socialRepository.addComment(post.id, text) },
                onDeletePost = { socialRepository.deletePost(post.id) },
                onVotePoll = { optId -> socialRepository.votePoll(post.id, optId) },
                onToggleBookmark = { socialRepository.toggleBookmark(post.id) }
            )
        }
    }

    // Explicit Health Achievement Share Dialog (Requirement 8)
    if (achievementToShare != null) {
        val ach = achievementToShare!!
        AlertDialog(
            onDismissRequest = { achievementToShare = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Health Achievement", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Preview of achievement to be shared:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(ach.title, fontWeight = FontWeight.Bold, color = GoldLight, fontSize = 13.sp)
                            Text(ach.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Verified Value: ${ach.verifiedMetric}", fontWeight = FontWeight.Bold, color = TealNormal, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Custom Message:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customShareMessage,
                        onValueChange = { customShareMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NavySurface,
                            unfocusedContainerColor = NavySurface,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Audience / Visibility:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(PostVisibility.PUBLIC, PostVisibility.FRIENDS, PostVisibility.ONLY_ME).forEach { vis ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (shareAudience == vis) GoldPrimary else NavySurfaceVariant,
                                modifier = Modifier
                                    .clickable { shareAudience = vis }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = vis.label,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = if (shareAudience == vis) NavyDarkBackground else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NavySurfaceVariant
                    ) {
                        Text(
                            text = "Privacy Safeguard: Private medical data (heart rate, SpO2, sleep, medical alerts) will NEVER be shared.",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        socialRepository.shareHealthAchievement(
                            achievement = ach,
                            customMessage = customShareMessage,
                            visibility = shareAudience
                        )
                        achievementToShare = null
                    },
                    modifier = Modifier.testTag("confirm_share_achievement_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Confirm & Publish", color = NavyDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { achievementToShare = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavySurfaceCard
        )
    }
}
