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
import com.example.data.HealthRepository
import com.example.data.SocialRepository
import com.example.ui.components.ChallengeCard
import com.example.ui.theme.*

@Composable
fun ChallengesScreen(
    socialRepository: SocialRepository,
    healthRepository: HealthRepository,
    modifier: Modifier = Modifier
) {
    val challenges by socialRepository.challenges.collectAsState()
    val stats by healthRepository.derivedStats.collectAsState()

    // Ensure challenge data stays refreshed with latest consolidated health stats
    LaunchedEffect(stats.dailySteps) {
        socialRepository.updateChallengesWithAuthorisedHealthData()
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
                    text = "COMMUNITY COMPETITIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Group Fitness Challenges",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Powered directly by PulseGuard AI's zero-duplicate health reconciliation engine.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Consolidated Integrity Proof Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Anti-Cheat Deduplication Active",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Text(
                            text = "Your challenge progress uses your ${stats.dailySteps} consolidated steps (not the raw ${stats.rawStepsSumUnconsolidated} multi-device sum). Duplicate wearable & phone readings are eliminated.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Challenges List
        items(challenges) { challenge ->
            ChallengeCard(
                challenge = challenge,
                onToggleJoin = { socialRepository.toggleChallengeParticipation(challenge.id) }
            )
        }
    }
}
