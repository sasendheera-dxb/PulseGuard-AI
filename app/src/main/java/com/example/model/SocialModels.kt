package com.example.model

enum class ReactionType(val label: String, val emoji: String) {
    LIKE("Like", "👍"),
    LOVE("Love", "❤️"),
    CELEBRATE("Celebrate", "🎉"),
    SUPPORT("Support", "🤝"),
    ENCOURAGE("Encourage", "💪")
}

enum class PostVisibility(val label: String) {
    PUBLIC("Public"),
    FRIENDS("Friends Only"),
    GROUPS("Group Members"),
    ONLY_ME("Only Me")
}

data class UserProfile(
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String,
    val avatarInitials: String = "PG",
    val followersCount: Int = 142,
    val followingCount: Int = 89,
    val friendsCount: Int = 64,
    var privacyLevel: PostVisibility = PostVisibility.PUBLIC,
    var allowAchievementSharing: Boolean = true,
    var allowChallengeLeaderboard: Boolean = true,
    var showStepMilestonesToFriends: Boolean = true,
    val memberSince: String = "October 2026",
    val earnedBadgesCount: Int = 8
)

data class PollOption(
    val id: String,
    val text: String,
    var voteCount: Int,
    var hasVoted: Boolean = false
)

data class PollMeta(
    val id: String,
    val question: String,
    val options: List<PollOption>,
    val totalVotes: Int
)

data class AchievementShareMeta(
    val achievementId: String,
    val title: String,
    val description: String,
    val verifiedConsolidatedValue: String,
    val badgeIconName: String,
    val verifiedByPulseGuardEngine: Boolean = true
)

data class SocialPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String,
    val timestamp: Long,
    val content: String,
    val achievementMeta: AchievementShareMeta? = null,
    val pollMeta: PollMeta? = null,
    val reactions: MutableMap<ReactionType, Int> = mutableMapOf(),
    val userReactions: MutableSet<ReactionType> = mutableSetOf(),
    var commentsCount: Int = 0,
    val comments: MutableList<PostComment> = mutableListOf(),
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val groupName: String? = null,
    var isBookmarked: Boolean = false,
    val isCurrentUser: Boolean = false
)

data class PostComment(
    val id: String,
    val postId: String,
    val authorName: String,
    val authorUsername: String,
    val timestamp: Long,
    val text: String,
    var likesCount: Int = 0
)

data class SocialGroup(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val isPrivate: Boolean,
    val category: String,
    val rules: String,
    var isMember: Boolean = false,
    val isAdmin: Boolean = false,
    val activeChallengeTitle: String? = null
)

data class ChallengeParticipant(
    val userId: String,
    val displayName: String,
    val username: String,
    val avatarInitials: String,
    val consolidatedSteps: Int,
    val rank: Int,
    val isUser: Boolean = false
)

data class GroupFitnessChallenge(
    val id: String,
    val groupId: String?,
    val groupName: String,
    val title: String,
    val description: String,
    val targetSteps: Int,
    val durationDays: Int,
    val daysRemaining: Int,
    var isJoined: Boolean = true,
    val participants: List<ChallengeParticipant>
)

data class PrivateMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val timestamp: Long,
    val text: String,
    val isFromUser: Boolean
)

data class Conversation(
    val id: String,
    val recipientId: String,
    val recipientName: String,
    val recipientUsername: String,
    var lastMessage: String,
    var lastTimestamp: Long,
    var unreadCount: Int = 0,
    val messages: MutableList<PrivateMessage> = mutableListOf()
)

enum class NotificationType {
    FRIEND_REQUEST,
    POST_REACTION,
    COMMENT,
    CHALLENGE_MILESTONE,
    ACHIEVEMENT_UNLOCKED,
    GROUP_INVITE
}

data class SocialNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long,
    var isRead: Boolean = false,
    val senderName: String? = null
)

data class EligibleHealthAchievement(
    val id: String,
    val title: String,
    val description: String,
    val verifiedMetric: String,
    val unlockedDate: String,
    var isShared: Boolean = false
)
