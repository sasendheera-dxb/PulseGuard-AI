package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SocialRepository(
    private val healthRepository: HealthRepository
) {
    // Current Authenticated User Profile (Unified with Health Profile)
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "user_me",
            username = "alex_pulseguard",
            displayName = "Alex Vance",
            bio = "Biometric enthusiast & marathon runner. Tracking consolidated steps across Pixel Watch 3 & Apple Watch Ultra 2! 🏃‍♂️",
            avatarInitials = "AV",
            followersCount = 142,
            followingCount = 89,
            friendsCount = 64,
            privacyLevel = PostVisibility.PUBLIC,
            allowAchievementSharing = true,
            allowChallengeLeaderboard = true
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Social Feed Posts
    private val _posts = MutableStateFlow<List<SocialPost>>(emptyList())
    val posts: StateFlow<List<SocialPost>> = _posts.asStateFlow()

    // Groups & Communities
    private val _groups = MutableStateFlow<List<SocialGroup>>(emptyList())
    val groups: StateFlow<List<SocialGroup>> = _groups.asStateFlow()

    // Group Challenges
    private val _challenges = MutableStateFlow<List<GroupFitnessChallenge>>(emptyList())
    val challenges: StateFlow<List<GroupFitnessChallenge>> = _challenges.asStateFlow()

    // Conversations & Messages
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<SocialNotification>>(emptyList())
    val notifications: StateFlow<List<SocialNotification>> = _notifications.asStateFlow()

    // Eligible Achievements (Detected from health data, awaiting user consent to share)
    private val _eligibleAchievements = MutableStateFlow<List<EligibleHealthAchievement>>(emptyList())
    val eligibleAchievements: StateFlow<List<EligibleHealthAchievement>> = _eligibleAchievements.asStateFlow()

    // Blocked & Reported Users List
    private val _blockedUsers = MutableStateFlow<Set<String>>(emptySet())
    val blockedUsers: StateFlow<Set<String>> = _blockedUsers.asStateFlow()

    init {
        initializeSocialSeedData()
    }

    private fun initializeSocialSeedData() {
        val now = System.currentTimeMillis()

        // Initial Seed Posts
        val initialPosts = listOf(
            SocialPost(
                id = "post_1",
                authorId = "user_dr_elena",
                authorName = "Dr. Elena Vance",
                authorUsername = "elena_md",
                authorAvatar = "EV",
                timestamp = now - (45 * 60 * 1000),
                content = "Great morning community run! Remember to monitor your resting heart rate and recovery HRV after high-intensity interval training. Autonomic recovery is just as crucial as mileage! 💪",
                reactions = mutableMapOf(ReactionType.LIKE to 24, ReactionType.CELEBRATE to 12, ReactionType.LOVE to 8),
                userReactions = mutableSetOf(ReactionType.LIKE),
                commentsCount = 3,
                comments = mutableListOf(
                    PostComment("c_1", "post_1", "Marcus Vance", "marcus_v", now - (30 * 60 * 1000), "Great advice doctor! My HRV stayed at 62ms today."),
                    PostComment("c_2", "post_1", "Sara Chen", "sara_runner", now - (15 * 60 * 1000), "Loved the morning route through the park!")
                ),
                visibility = PostVisibility.PUBLIC,
                groupName = "Global Walking Club"
            ),
            SocialPost(
                id = "post_2",
                authorId = "user_sam_k",
                authorName = "Sam Kowalski",
                authorUsername = "sam_fit",
                authorAvatar = "SK",
                timestamp = now - (3 * 3600 * 1000),
                content = "Just unlocked a major milestone using PulseGuard AI's multi-device fusion! No phantom step double counting between my phone and watch.",
                achievementMeta = AchievementShareMeta(
                    achievementId = "ach_10k",
                    title = "10,000 Verified Steps Milestone",
                    description = "Consolidated across Wear OS and smartphone without duplicate counting.",
                    verifiedConsolidatedValue = "10,240 Verified Steps",
                    badgeIconName = "footprints"
                ),
                reactions = mutableMapOf(ReactionType.CELEBRATE to 45, ReactionType.ENCOURAGE to 18, ReactionType.SUPPORT to 7),
                commentsCount = 4,
                visibility = PostVisibility.PUBLIC
            ),
            SocialPost(
                id = "post_3",
                authorId = "user_jordan_m",
                authorName = "Jordan Miller",
                authorUsername = "jordan_m",
                authorAvatar = "JM",
                timestamp = now - (8 * 3600 * 1000),
                content = "Community poll for tomorrow's weekend group walk:",
                pollMeta = PollMeta(
                    id = "poll_1",
                    question = "Which scenic walking route should we take tomorrow?",
                    options = listOf(
                        PollOption("opt_1", "Bayfront Coastal Trail (5km)", 38),
                        PollOption("opt_2", "Mountain Ridge Loop (7.5km)", 22),
                        PollOption("opt_3", "Botanical Garden Park Walk (3km)", 15)
                    ),
                    totalVotes = 75
                ),
                reactions = mutableMapOf(ReactionType.LIKE to 19, ReactionType.SUPPORT to 5),
                commentsCount = 2,
                visibility = PostVisibility.PUBLIC,
                groupName = "Daily Fitness Challenge"
            )
        )
        _posts.value = initialPosts

        // Initial Groups
        _groups.value = listOf(
            SocialGroup(
                id = "grp_walking_club",
                name = "Global Walking Club",
                description = "Worldwide community dedicated to daily active walking, posture, and cardiovascular wellness.",
                memberCount = 14250,
                isPrivate = false,
                category = "Walking & Cardio",
                rules = "Be encouraging, celebrate verified steps, respect personal recovery days.",
                isMember = true,
                isAdmin = false,
                activeChallengeTitle = "Walk 50,000 Consolidated Steps This Week"
            ),
            SocialGroup(
                id = "grp_daily_fitness",
                name = "Daily Fitness Challenge",
                description = "Daily motivation, habit streaks, and supportive peer accountability.",
                memberCount = 8920,
                isPrivate = false,
                category = "General Fitness",
                rules = "Post your achievements, support others, no medical diagnostic advice.",
                isMember = true,
                isAdmin = false,
                activeChallengeTitle = "7-Day Active Habit Streak"
            ),
            SocialGroup(
                id = "grp_sleep_improvement",
                name = "Sleep & Restorative Recovery",
                description = "Improving sleep hygiene, tracking circadian rhythm, and boosting REM & Deep sleep.",
                memberCount = 5340,
                isPrivate = false,
                category = "Sleep & Recovery",
                rules = "Share sleep routine tips and wind-down practices.",
                isMember = false,
                isAdmin = false
            ),
            SocialGroup(
                id = "grp_cycling",
                name = "Cycling & Velo Enthusiasts",
                description = "Road, gravel, and urban commuting cyclists sharing cadence and distance.",
                memberCount = 3890,
                isPrivate = true,
                category = "Cycling",
                rules = "Helmet safety required, private membership on approval.",
                isMember = false,
                isAdmin = false
            )
        )

        // Seed Eligible Achievements (Grounded in HealthRepository's verified stats!)
        val currentConsolidatedSteps = healthRepository.derivedStats.value.dailySteps
        _eligibleAchievements.value = listOf(
            EligibleHealthAchievement(
                id = "ach_daily_goal",
                title = "Daily Consolidated Steps Goal",
                description = "Successfully reached active milestone using PulseGuard AI zero-duplicate engine.",
                verifiedMetric = "$currentConsolidatedSteps Verified Steps",
                unlockedDate = "Today",
                isShared = false
            ),
            EligibleHealthAchievement(
                id = "ach_weekly_streak",
                title = "Consistent Autonomic Recovery",
                description = "Optimal resting heart rate and positive HRV restoration for 5 consecutive days.",
                verifiedMetric = "62 bpm Resting Baseline",
                unlockedDate = "Yesterday",
                isShared = false
            ),
            EligibleHealthAchievement(
                id = "ach_50k_month",
                title = "50km Cumulative Distance",
                description = "Completed 50 kilometers of walking and running this month.",
                verifiedMetric = "52.4 km Total",
                unlockedDate = "3 days ago",
                isShared = true
            )
        )

        // Seed Group Challenges (Using HealthRepository's real consolidated steps for user's progress!)
        updateChallengesWithAuthorisedHealthData()

        // Seed Conversations
        _conversations.value = listOf(
            Conversation(
                id = "conv_1",
                recipientId = "user_dr_elena",
                recipientName = "Dr. Elena Vance",
                recipientUsername = "elena_md",
                lastMessage = "Your consolidated step count for this week looks fantastic Alex!",
                lastTimestamp = now - (20 * 60 * 1000),
                unreadCount = 1,
                messages = mutableListOf(
                    PrivateMessage("m_1", "user_me", "Alex Vance", now - (40 * 60 * 1000), "Hi Dr. Vance, I noticed my resting HR was a bit elevated yesterday after the run.", true),
                    PrivateMessage("m_2", "user_dr_elena", "Dr. Elena Vance", now - (20 * 60 * 1000), "Your consolidated step count for this week looks fantastic Alex! Make sure to hydrate well and take a rest day.", false)
                )
            ),
            Conversation(
                id = "conv_2",
                recipientId = "user_sam_k",
                recipientName = "Sam Kowalski",
                recipientUsername = "sam_fit",
                lastMessage = "Are you joining the 50,000 steps team challenge this Saturday?",
                lastTimestamp = now - (2 * 3600 * 1000),
                unreadCount = 0,
                messages = mutableListOf(
                    PrivateMessage("m_3", "user_sam_k", "Sam Kowalski", now - (2 * 3600 * 1000), "Are you joining the 50,000 steps team challenge this Saturday?", false)
                )
            )
        )

        // Seed Notifications
        _notifications.value = listOf(
            SocialNotification(
                id = "notif_1",
                type = NotificationType.ACHIEVEMENT_UNLOCKED,
                title = "New Milestone Detected!",
                message = "You completed your Daily Consolidated Steps Goal ($currentConsolidatedSteps steps). Would you like to share it with your friends?",
                timestamp = now - (15 * 60 * 1000),
                isRead = false
            ),
            SocialNotification(
                id = "notif_2",
                type = NotificationType.POST_REACTION,
                title = "Reaction on your post",
                message = "Dr. Elena Vance celebrated your fitness milestone.",
                timestamp = now - (2 * 3600 * 1000),
                isRead = false,
                senderName = "Dr. Elena Vance"
            ),
            SocialNotification(
                id = "notif_3",
                type = NotificationType.FRIEND_REQUEST,
                title = "New Friend Request",
                message = "Sara Chen sent you a friend request.",
                timestamp = now - (5 * 3600 * 1000),
                isRead = true,
                senderName = "Sara Chen"
            ),
            SocialNotification(
                id = "notif_4",
                type = NotificationType.CHALLENGE_MILESTONE,
                title = "Global Walking Club Challenge",
                message = "You reached 75% of your 50,000 weekly step goal!",
                timestamp = now - (24 * 3600 * 1000),
                isRead = true
            )
        )
    }

    /**
     * Connects Group Challenges directly to PulseGuard AI's authorized consolidated health data.
     * Guaranteed: Uses healthRepository.derivedStats.value.dailySteps (consolidated, NOT sum of devices).
     */
    fun updateChallengesWithAuthorisedHealthData() {
        val userConsolidatedSteps = healthRepository.derivedStats.value.dailySteps

        val challenge = GroupFitnessChallenge(
            id = "chal_50k_week",
            groupId = "grp_walking_club",
            groupName = "Global Walking Club",
            title = "Walk 50,000 Consolidated Steps This Week",
            description = "Hit 50,000 verified steps across 7 days. Deduplicated steps from your connected smartwatches and phones count toward your official tally.",
            targetSteps = 50000,
            durationDays = 7,
            daysRemaining = 3,
            isJoined = true,
            participants = listOf(
                ChallengeParticipant("user_me", "Alex Vance (You)", "alex_pulseguard", "AV", userConsolidatedSteps + 34200, 1, isUser = true),
                ChallengeParticipant("user_sam_k", "Sam Kowalski", "sam_fit", "SK", 37500, 2),
                ChallengeParticipant("user_jordan_m", "Jordan Miller", "jordan_m", "JM", 31200, 3),
                ChallengeParticipant("user_sara_c", "Sara Chen", "sara_runner", "SC", 28400, 4)
            ).sortedByDescending { it.consolidatedSteps }
        )

        _challenges.value = listOf(
            challenge,
            GroupFitnessChallenge(
                id = "chal_7day_streak",
                groupId = "grp_daily_fitness",
                groupName = "Daily Fitness Challenge",
                title = "7-Day Active Habit Streak",
                description = "Log at least 6,000 verified consolidated steps every day for one full week.",
                targetSteps = 42000,
                durationDays = 7,
                daysRemaining = 5,
                isJoined = true,
                participants = listOf(
                    ChallengeParticipant("user_me", "Alex Vance (You)", "alex_pulseguard", "AV", userConsolidatedSteps + 18000, 2, isUser = true),
                    ChallengeParticipant("user_dr_elena", "Dr. Elena Vance", "elena_md", "EV", 24500, 1),
                    ChallengeParticipant("user_marcus_v", "Marcus Vance", "marcus_v", "MV", 16800, 3)
                ).sortedByDescending { it.consolidatedSteps }
            )
        )
    }

    // =========================================================================
    // SOCIAL ACTIONS (Feed, Posts, Comments, Reactions)
    // =========================================================================

    fun createPost(
        content: String,
        visibility: PostVisibility = PostVisibility.PUBLIC,
        groupName: String? = null,
        achievementMeta: AchievementShareMeta? = null
    ): SocialPost {
        val user = _currentUser.value
        val newPost = SocialPost(
            id = "post_${UUID.randomUUID()}",
            authorId = user.id,
            authorName = user.displayName,
            authorUsername = user.username,
            authorAvatar = user.avatarInitials,
            timestamp = System.currentTimeMillis(),
            content = content,
            achievementMeta = achievementMeta,
            visibility = visibility,
            groupName = groupName,
            isCurrentUser = true
        )

        _posts.value = listOf(newPost) + _posts.value
        return newPost
    }

    fun toggleReaction(postId: String, reactionType: ReactionType) {
        val updated = _posts.value.map { post ->
            if (post.id == postId) {
                val userReactions = post.userReactions.toMutableSet()
                val reactions = post.reactions.toMutableMap()

                if (userReactions.contains(reactionType)) {
                    userReactions.remove(reactionType)
                    val currentCount = reactions[reactionType] ?: 1
                    if (currentCount <= 1) reactions.remove(reactionType) else reactions[reactionType] = currentCount - 1
                } else {
                    userReactions.add(reactionType)
                    reactions[reactionType] = (reactions[reactionType] ?: 0) + 1
                }

                post.copy(reactions = reactions, userReactions = userReactions)
            } else post
        }
        _posts.value = updated
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        val user = _currentUser.value
        val comment = PostComment(
            id = "comment_${UUID.randomUUID()}",
            postId = postId,
            authorName = user.displayName,
            authorUsername = user.username,
            timestamp = System.currentTimeMillis(),
            text = text
        )

        val updated = _posts.value.map { post ->
            if (post.id == postId) {
                val newComments = (post.comments + comment).toMutableList()
                post.copy(comments = newComments, commentsCount = post.commentsCount + 1)
            } else post
        }
        _posts.value = updated
    }

    fun deletePost(postId: String) {
        _posts.value = _posts.value.filter { it.id != postId }
    }

    fun toggleBookmark(postId: String) {
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(isBookmarked = !it.isBookmarked) else it
        }
    }

    fun votePoll(postId: String, optionId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId && post.pollMeta != null) {
                val poll = post.pollMeta
                val updatedOptions = poll.options.map { opt ->
                    if (opt.id == optionId && !opt.hasVoted) {
                        opt.copy(voteCount = opt.voteCount + 1, hasVoted = true)
                    } else opt
                }
                post.copy(pollMeta = poll.copy(options = updatedOptions, totalVotes = poll.totalVotes + 1))
            } else post
        }
    }

    // =========================================================================
    // EXPLICIT HEALTH ACHIEVEMENT SHARING (Requirement 8)
    // =========================================================================

    /**
     * Publishes a verified health achievement post ONLY after explicit user confirmation.
     * Sensitive vitals (Heart rate, SpO2, sleep, medical alerts) are never published.
     */
    fun shareHealthAchievement(
        achievement: EligibleHealthAchievement,
        customMessage: String,
        visibility: PostVisibility
    ): SocialPost {
        val achievementMeta = AchievementShareMeta(
            achievementId = achievement.id,
            title = achievement.title,
            description = achievement.description,
            verifiedConsolidatedValue = achievement.verifiedMetric,
            badgeIconName = "military_tech",
            verifiedByPulseGuardEngine = true
        )

        val post = createPost(
            content = if (customMessage.isNotBlank()) customMessage else "Proud to reach my milestone: ${achievement.title}!",
            visibility = visibility,
            achievementMeta = achievementMeta
        )

        // Mark as shared
        _eligibleAchievements.value = _eligibleAchievements.value.map {
            if (it.id == achievement.id) it.copy(isShared = true) else it
        }

        return post
    }

    // =========================================================================
    // GROUPS & CHALLENGES MANAGEMENT
    // =========================================================================

    fun toggleGroupMembership(groupId: String) {
        _groups.value = _groups.value.map { group ->
            if (group.id == groupId) {
                val newStatus = !group.isMember
                val newCount = if (newStatus) group.memberCount + 1 else group.memberCount - 1
                group.copy(isMember = newStatus, memberCount = newCount)
            } else group
        }
    }

    fun toggleChallengeParticipation(challengeId: String) {
        _challenges.value = _challenges.value.map { challenge ->
            if (challenge.id == challengeId) {
                challenge.copy(isJoined = !challenge.isJoined)
            } else challenge
        }
    }

    // =========================================================================
    // PRIVATE MESSAGING
    // =========================================================================

    fun sendMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        val user = _currentUser.value
        val now = System.currentTimeMillis()
        val message = PrivateMessage(
            id = "msg_${UUID.randomUUID()}",
            senderId = user.id,
            senderName = user.displayName,
            timestamp = now,
            text = text,
            isFromUser = true
        )

        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                val newMessages = (conv.messages + message).toMutableList()
                conv.copy(
                    lastMessage = text,
                    lastTimestamp = now,
                    messages = newMessages
                )
            } else conv
        }
    }

    // =========================================================================
    // MODERATION, SAFETY, PRIVACY (Requirements 5 & 14)
    // =========================================================================

    fun blockUser(userId: String) {
        _blockedUsers.value = _blockedUsers.value + userId
        // Filter out posts from blocked user
        _posts.value = _posts.value.filter { it.authorId != userId }
    }

    fun updateProfilePrivacy(
        visibility: PostVisibility,
        allowAchievements: Boolean,
        allowLeaderboard: Boolean
    ) {
        _currentUser.value = _currentUser.value.copy(
            privacyLevel = visibility,
            allowAchievementSharing = allowAchievements,
            allowChallengeLeaderboard = allowLeaderboard
        )
    }

    fun markNotificationRead(notificationId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }
}
