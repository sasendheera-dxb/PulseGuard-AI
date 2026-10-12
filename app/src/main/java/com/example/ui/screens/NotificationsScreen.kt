package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.NotificationType
import com.example.model.SocialNotification
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    socialRepository: SocialRepository,
    modifier: Modifier = Modifier
) {
    val notifications by socialRepository.notifications.collectAsState()

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
                    text = "ACTIVITY & ALERTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Social & Health Notifications",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Updates from friends, group challenges, and verified fitness milestones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        items(notifications) { notif ->
            NotificationCard(
                notification = notif,
                onClick = { socialRepository.markNotificationRead(notif.id) }
            )
        }
    }
}

@Composable
fun NotificationCard(
    notification: SocialNotification,
    onClick: () -> Unit
) {
    val (icon, iconColor) = when (notification.type) {
        NotificationType.ACHIEVEMENT_UNLOCKED -> Pair(Icons.Default.MilitaryTech, GoldPrimary)
        NotificationType.CHALLENGE_MILESTONE -> Pair(Icons.Default.EmojiEvents, TealNormal)
        NotificationType.POST_REACTION -> Pair(Icons.Default.Favorite, RubyEmergency)
        NotificationType.COMMENT -> Pair(Icons.Default.ChatBubble, BlueBiometric)
        NotificationType.FRIEND_REQUEST -> Pair(Icons.Default.PersonAdd, GoldLight)
        NotificationType.GROUP_INVITE -> Pair(Icons.Default.Group, PurpleHRV)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, if (!notification.isRead) GoldPrimary.copy(alpha = 0.5f) else NavyBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("notification_card_${notification.id}"),
        colors = CardDefaults.cardColors(containerColor = if (!notification.isRead) NavySurface else NavySurfaceCard)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(notification.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                    Text(
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(notification.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(notification.message, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 12.sp)
            }

            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                )
            }
        }
    }
}
