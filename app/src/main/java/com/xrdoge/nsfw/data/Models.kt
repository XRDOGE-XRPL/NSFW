package com.xrdoge.nsfw.data

data class CreatorProfile(
    val id: String,
    val name: String,
    val niche: String,
    val monthlySubscribers: Int,
    val verification: String,
)

data class ContentPost(
    val id: String,
    val title: String,
    val creatorName: String,
    val category: String,
    val priceLabel: String,
    val isPremium: Boolean,
)

data class SubscriptionTier(
    val name: String,
    val monthlyPrice: String,
    val highlights: List<String>,
)

data class AppSettings(
    val creatorAlias: String = "Creatorin",
    val platformMode: String = "Adult Plattform",
    val allowDirectMessages: Boolean = true,
    val showExplicitPreview: Boolean = false,
)
