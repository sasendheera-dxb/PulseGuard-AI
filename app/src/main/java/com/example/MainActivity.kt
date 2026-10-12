package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiHealthService
import com.example.data.HealthRepository
import com.example.data.SocialRepository
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class AppDestination(val label: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Dashboard, "nav_home"),
    MY_HEALTH("My Health", Icons.Default.Favorite, "nav_my_health"),
    ACTIVITY("Activity", Icons.AutoMirrored.Filled.DirectionsWalk, "nav_activity"),
    COMMUNITY("Community", Icons.Default.Forum, "nav_community"),
    GROUPS("Groups", Icons.Default.Groups, "nav_groups"),
    CHALLENGES("Challenges", Icons.Default.EmojiEvents, "nav_challenges"),
    MESSAGES("Messages", Icons.AutoMirrored.Filled.Chat, "nav_messages"),
    NOTIFICATIONS("Notifications", Icons.Default.Notifications, "nav_notifications"),
    PROFILE("Profile", Icons.Default.Person, "nav_profile"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_settings"),
    EMERGENCY("Emergency", Icons.Default.Emergency, "nav_emergency")
}

class MainActivity : ComponentActivity() {

    private val healthRepository by lazy { HealthRepository() }
    private val socialRepository by lazy { SocialRepository(healthRepository) }
    private val geminiService by lazy { GeminiHealthService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                PulseGuardApp(
                    healthRepository = healthRepository,
                    socialRepository = socialRepository,
                    geminiService = geminiService
                )
            }
        }
    }
}

@Composable
fun PulseGuardApp(
    healthRepository: HealthRepository,
    socialRepository: SocialRepository,
    geminiService: GeminiHealthService
) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }

    // BackHandler: Returns to Home if on any secondary tab
    if (currentDestination != AppDestination.HOME) {
        BackHandler {
            currentDestination = AppDestination.HOME
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(NavyDarkBackground)) {
        val isTablet = maxWidth >= 760.dp

        if (isTablet) {
            // Adaptive Tablet / Desktop Layout: NavigationRail
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = NavySurface,
                    contentColor = TextSecondary,
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 90.dp)
                        .border(1.dp, NavyBorder)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "PulseGuard AI",
                        tint = GoldPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    AppDestination.values().filter { it != AppDestination.EMERGENCY }.forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label,
                                    tint = if (isSelected) GoldPrimary else TextTertiary
                                )
                            },
                            label = {
                                Text(
                                    destination.label,
                                    fontSize = 10.sp,
                                    color = if (isSelected) TextPrimary else TextTertiary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.testTag(destination.tag)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    IntegratedScreenContent(
                        destination = currentDestination,
                        healthRepository = healthRepository,
                        socialRepository = socialRepository,
                        geminiService = geminiService,
                        onNavigate = { currentDestination = it }
                    )
                }
            }
        } else {
            // Mobile Layout: Scaffold with scrollable bottom NavigationBar
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = NavyDarkBackground,
                contentWindowInsets = WindowInsets.systemBars,
                bottomBar = {
                    Surface(
                        color = NavySurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AppDestination.values().filter { it != AppDestination.EMERGENCY }.forEach { destination ->
                                val isSelected = currentDestination == destination
                                val itemColor = if (isSelected) GoldPrimary else TextTertiary

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) NavySurfaceVariant else androidx.compose.ui.graphics.Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .testTag(destination.tag)
                                ) {
                                    IconButton(
                                        onClick = { currentDestination = destination },
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                            .height(56.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = destination.icon,
                                                contentDescription = destination.label,
                                                tint = itemColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = destination.label,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) TextPrimary else TextTertiary,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    IntegratedScreenContent(
                        destination = currentDestination,
                        healthRepository = healthRepository,
                        socialRepository = socialRepository,
                        geminiService = geminiService,
                        onNavigate = { currentDestination = it }
                    )
                }
            }
        }
    }
}

@Composable
fun IntegratedScreenContent(
    destination: AppDestination,
    healthRepository: HealthRepository,
    socialRepository: SocialRepository,
    geminiService: GeminiHealthService,
    onNavigate: (AppDestination) -> Unit
) {
    when (destination) {
        AppDestination.HOME -> DashboardScreen(
            repository = healthRepository,
            socialRepository = socialRepository,
            onNavigateToActivity = { onNavigate(AppDestination.ACTIVITY) },
            onNavigateToEmergency = { onNavigate(AppDestination.EMERGENCY) },
            onNavigateToDevices = { onNavigate(AppDestination.SETTINGS) },
            onNavigateToCommunity = { onNavigate(AppDestination.COMMUNITY) },
            onNavigateToChallenges = { onNavigate(AppDestination.CHALLENGES) }
        )
        AppDestination.MY_HEALTH -> HealthScreen(
            repository = healthRepository
        )
        AppDestination.ACTIVITY -> ActivityScreen(
            repository = healthRepository
        )
        AppDestination.COMMUNITY -> CommunityFeedScreen(
            socialRepository = socialRepository
        )
        AppDestination.GROUPS -> GroupsScreen(
            socialRepository = socialRepository
        )
        AppDestination.CHALLENGES -> ChallengesScreen(
            socialRepository = socialRepository,
            healthRepository = healthRepository
        )
        AppDestination.MESSAGES -> MessagesScreen(
            socialRepository = socialRepository
        )
        AppDestination.NOTIFICATIONS -> NotificationsScreen(
            socialRepository = socialRepository
        )
        AppDestination.PROFILE -> ProfileScreen(
            socialRepository = socialRepository
        )
        AppDestination.SETTINGS -> SettingsScreen(
            repository = healthRepository,
            onNavigateToEmergency = { onNavigate(AppDestination.EMERGENCY) }
        )
        AppDestination.EMERGENCY -> EmergencyScreen(
            repository = healthRepository
        )
    }
}
