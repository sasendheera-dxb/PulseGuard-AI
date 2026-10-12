package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostCard(
    post: SocialPost,
    onReactionClick: (ReactionType) -> Unit,
    onAddComment: (String) -> Unit,
    onDeletePost: () -> Unit,
    onVotePoll: (String) -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCommentInput by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var showReactionPicker by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
            .testTag("post_card_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Post Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f))
                            .border(1.5.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorAvatar,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (post.groupName != null) {
                                Text(
                                    text = " in ${post.groupName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "@${post.authorUsername} • ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(post.timestamp))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NavySurfaceVariant
                            ) {
                                Text(
                                    text = post.visibility.label,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (post.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (post.isBookmarked) GoldPrimary else TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (post.isCurrentUser) {
                        IconButton(
                            onClick = onDeletePost,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = RubyEmergency.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Content Text
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                lineHeight = 21.sp,
                fontSize = 14.sp
            )

            // Verified Health Achievement Share Widget (Requirement 8)
            if (post.achievementMeta != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = "Achievement",
                                tint = GoldPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = post.achievementMeta.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = TealNormalSubtle) {
                                    Text(
                                        text = "VERIFIED",
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TealNormal,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Text(
                                text = post.achievementMeta.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Consolidated Metric: ${post.achievementMeta.verifiedConsolidatedValue}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Interactive Poll Widget
            if (post.pollMeta != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavySurface)
                        .padding(12.dp)
                ) {
                    Text(
                        text = post.pollMeta.question,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    post.pollMeta.options.forEach { opt ->
                        val pct = if (post.pollMeta.totalVotes > 0) (opt.voteCount * 100 / post.pollMeta.totalVotes) else 0
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (opt.hasVoted) GoldPrimary.copy(alpha = 0.2f) else NavySurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onVotePoll(opt.id) }
                                .border(1.dp, if (opt.hasVoted) GoldPrimary else NavyBorder, RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = opt.text,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (opt.hasVoted) GoldLight else TextPrimary,
                                    fontWeight = if (opt.hasVoted) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "$pct% (${opt.voteCount})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${post.pollMeta.totalVotes} total community votes",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reaction Count Badges
            val totalReactions = post.reactions.values.sum()
            if (totalReactions > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    post.reactions.forEach { (type, count) ->
                        if (count > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NavySurfaceVariant,
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(type.emoji, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("$count", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${post.commentsCount} comments",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))

            // Reaction Action Bar (5 reactions: Like, Love, Celebrate, Support, Encourage)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like / Reactions Menu
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ReactionType.values().forEach { reaction ->
                        val hasReacted = post.userReactions.contains(reaction)
                        IconButton(
                            onClick = { onReactionClick(reaction) },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (hasReacted) GoldPrimary.copy(alpha = 0.2f) else Color.Transparent)
                        ) {
                            Text(reaction.emoji, fontSize = 16.sp)
                        }
                    }
                }

                // Comment Button
                TextButton(
                    onClick = { showCommentInput = !showCommentInput }
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comment", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Comment", color = TextSecondary, fontSize = 12.sp)
                }
            }

            // Comments Section
            if (showCommentInput || post.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Existing Comments List
                    post.comments.forEach { comment ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NavySurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(comment.authorName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                                    Text(
                                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(comment.timestamp)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextTertiary,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(comment.text, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }

                    // Add Comment Input Field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Write a comment...", color = TextTertiary, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NavySurface,
                                unfocusedContainerColor = NavySurface,
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
                                if (commentText.isNotBlank()) {
                                    onAddComment(commentText)
                                    commentText = ""
                                }
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldPrimary)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NavyDarkBackground, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: GroupFitnessChallenge,
    onToggleJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .testTag("challenge_card_${challenge.id}"),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = challenge.groupName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NavySurfaceVariant
                ) {
                    Text(
                        text = "${challenge.daysRemaining} days left",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // User's verified progress against target
            val userParticipant = challenge.participants.find { it.isUser }
            val currentSteps = userParticipant?.consolidatedSteps ?: 0
            val progressRatio = (currentSteps.toFloat() / challenge.targetSteps.toFloat()).coerceIn(0f, 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Your Verified Progress: $currentSteps / ${challenge.targetSteps} steps",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldLight,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
                Text(
                    text = "${(progressRatio * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TealNormal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GoldPrimary,
                trackColor = NavySurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Leaderboard Preview
            Text(
                text = "Community Leaderboard (Consolidated Steps)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            challenge.participants.take(3).forEach { participant ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (participant.isUser) GoldPrimary else NavySurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "#${participant.rank}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (participant.isUser) NavyDarkBackground else TextPrimary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = participant.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (participant.isUser) GoldLight else TextPrimary,
                            fontWeight = if (participant.isUser) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "${participant.consolidatedSteps} steps",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TealNormal,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onToggleJoin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("toggle_challenge_join_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (challenge.isJoined) NavySurfaceVariant else GoldPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (challenge.isJoined) "Joined (Active Member)" else "Join Challenge",
                    color = if (challenge.isJoined) TextSecondary else NavyDarkBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun GroupCard(
    group: SocialGroup,
    onToggleJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
            .testTag("group_card_${group.id}"),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${group.category} • ${group.memberCount} members",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (group.isPrivate) RubySubtle else NavySurfaceVariant
                ) {
                    Text(
                        text = if (group.isPrivate) "Private" else "Public",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (group.isPrivate) RubyEmergency else TealNormal,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = group.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp
            )

            if (group.activeChallengeTitle != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NavySurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Active Challenge: ${group.activeChallengeTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rules: ${group.rules}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onToggleJoin,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (group.isMember) NavySurfaceVariant else GoldPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (group.isMember) "Joined" else "Join",
                        color = if (group.isMember) TextPrimary else NavyDarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
