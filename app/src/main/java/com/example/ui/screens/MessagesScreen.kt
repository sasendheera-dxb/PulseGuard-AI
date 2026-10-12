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
import com.example.model.Conversation
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesScreen(
    socialRepository: SocialRepository,
    modifier: Modifier = Modifier
) {
    val conversations by socialRepository.conversations.collectAsState()
    var activeConversation by remember { mutableStateOf<Conversation?>(null) }
    var outgoingMessageText by remember { mutableStateOf("") }

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
                    text = "END-TO-END ENCRYPTED",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Private Messaging",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Direct, secure communications with friends, physicians, and running buddies.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Active Conversations List
        items(conversations) { conv ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .clickable { activeConversation = conv }
                    .testTag("conversation_card_${conv.id}"),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conv.recipientName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(conv.recipientName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Text(
                                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(conv.lastTimestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = conv.lastMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            fontSize = 12.sp
                        )
                    }

                    if (conv.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${conv.unreadCount}",
                                color = NavyDarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Active Chat Dialog
    if (activeConversation != null) {
        val currentConv = conversations.find { it.id == activeConversation?.id } ?: activeConversation!!
        AlertDialog(
            onDismissRequest = { activeConversation = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = TealNormal, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(currentConv.recipientName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 340.dp)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentConv.messages) { msg ->
                            val isMe = msg.isFromUser
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isMe) GoldPrimary else NavySurfaceVariant,
                                    modifier = Modifier.widthIn(max = 240.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isMe) NavyDarkBackground else TextPrimary,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isMe) NavyDarkBackground.copy(alpha = 0.7f) else TextTertiary,
                                            fontSize = 9.sp,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = outgoingMessageText,
                            onValueChange = { outgoingMessageText = it },
                            placeholder = { Text("Type message...", color = TextTertiary, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).testTag("message_input_field"),
                            shape = RoundedCornerShape(10.dp),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (outgoingMessageText.isNotBlank()) {
                                    socialRepository.sendMessage(currentConv.id, outgoingMessageText)
                                    outgoingMessageText = ""
                                }
                            },
                            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)).background(GoldPrimary)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NavyDarkBackground, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { activeConversation = null }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = NavySurfaceCard
        )
    }
}
