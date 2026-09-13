package com.xrdoge.nsfw.data

data class CreatorProfile(
    val id: String,
    val name: String,
    val niche: String,
    val monthlySubscribers: Int,
    val verification: String,
    val chemistryTags: List<String>,
    val experiences: List<String>,
    val voiceIntroAvailable: Boolean,
)

data class ContentPost(
    val id: String,
    val title: String,
    val creatorName: String,
    val category: String,
    val format: String,
    val priceLabel: String,
    val isPremium: Boolean,
    val storyHook: String,
)

data class SubscriptionTier(
    val id: String,
    val name: String,
    val tagline: String,
    val monthlyPrice: String,
    val yearlyPrice: String,
    val highlights: List<String>,
)

data class ConversationDraft(
    val creatorId: String,
    val creatorName: String,
    val opener: String,
    val status: String,
)

data class AppSettings(
    val creatorAlias: String = "Creatorin",
    val platformMode: String = "Adult Plattform",
    val allowDirectMessages: Boolean = true,
    val showExplicitPreview: Boolean = false,
    val favoriteCreatorIds: Set<String> = emptySet(),
    val activeSubscriptionTierId: String = "insider",
)
