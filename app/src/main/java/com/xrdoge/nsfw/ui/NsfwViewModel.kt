package com.xrdoge.nsfw.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xrdoge.nsfw.data.AppSettings
import com.xrdoge.nsfw.data.ConversationDraft
import com.xrdoge.nsfw.data.ContentPost
import com.xrdoge.nsfw.data.CreatorProfile
import com.xrdoge.nsfw.data.SettingsStore
import com.xrdoge.nsfw.data.SubscriptionTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val settings: AppSettings = AppSettings(),
    val query: String = "",
    val creators: List<CreatorProfile> = emptyList(),
    val feed: List<ContentPost> = emptyList(),
    val subscriptions: List<SubscriptionTier> = emptyList(),
    val drafts: List<ConversationDraft> = emptyList(),
    val error: String? = null,
    val notice: String? = null,
)

class NsfwViewModel(application: Application) : AndroidViewModel(application) {
    private val store = SettingsStore(application)

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            store.migrateLegacySettings()
        }
        viewModelScope.launch {
            store.settings.collect { settings ->
                _state.update { it.copy(settings = settings) }
            }
        }
        refreshPlatform()
    }

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value, error = null) }
    }

    fun dismissMessage() {
        _state.update { it.copy(error = null, notice = null) }
    }

    fun refreshPlatform() {
        _state.update {
            it.copy(
                creators = sampleCreators(),
                feed = samplePosts(),
                subscriptions = sampleSubscriptions(),
                error = null,
            )
        }
    }

    fun updateSettings(
        creatorAlias: String,
        platformMode: String,
        allowDirectMessages: Boolean,
        showExplicitPreview: Boolean,
    ) {
        viewModelScope.launch {
            store.update { current ->
                current.copy(
                    creatorAlias = creatorAlias.trim().ifBlank { "Creatorin" },
                    platformMode = platformMode.trim().ifBlank { "Adult Plattform" },
                    allowDirectMessages = allowDirectMessages,
                    showExplicitPreview = showExplicitPreview,
                )
            }
            _state.update { it.copy(notice = "Plattform-Einstellungen gespeichert") }
        }
    }

    fun filteredCreators(): List<CreatorProfile> {
        val query = _state.value.query.trim()
        val favorites = _state.value.settings.favoriteCreatorIds
        val creators = if (query.isBlank()) {
            _state.value.creators
        } else {
            _state.value.creators.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.niche.contains(query, ignoreCase = true) ||
                    it.chemistryTags.any { tag -> tag.contains(query, ignoreCase = true) } ||
                    it.experiences.any { experience -> experience.contains(query, ignoreCase = true) }
            }
        }
        return creators.sortedWith(
            compareByDescending<CreatorProfile> { it.id in favorites }
                .thenByDescending { it.monthlySubscribers },
        )
    }

    fun favoriteCreators(): List<CreatorProfile> {
        val favorites = _state.value.settings.favoriteCreatorIds
        return _state.value.creators
            .filter { it.id in favorites }
            .sortedByDescending { it.monthlySubscribers }
    }

    fun activeSubscription(): SubscriptionTier? {
        val activeId = _state.value.settings.activeSubscriptionTierId
        return _state.value.subscriptions.firstOrNull { it.id == activeId }
    }

    fun toggleFavoriteCreator(creatorId: String) {
        val creator = _state.value.creators.firstOrNull { it.id == creatorId }
        if (creator == null) {
            _state.update { it.copy(error = "Creatorin nicht gefunden") }
            return
        }
        viewModelScope.launch {
            val isFavorite = creatorId in _state.value.settings.favoriteCreatorIds
            val nextFavorites = _state.value.settings.favoriteCreatorIds.toMutableSet().apply {
                if (isFavorite) remove(creatorId) else add(creatorId)
            }.toSet()
            store.update { current ->
                current.copy(favoriteCreatorIds = nextFavorites)
            }
            _state.update {
                it.copy(
                    settings = it.settings.copy(favoriteCreatorIds = nextFavorites),
                    error = null,
                    notice = if (isFavorite) "${creator.name} aus Favoriten entfernt" else "${creator.name} als Favoritin gespeichert",
                )
            }
        }
    }

    fun selectSubscriptionTier(tierId: String) {
        val tier = _state.value.subscriptions.firstOrNull { it.id == tierId }
        if (tier == null) {
            _state.update { it.copy(error = "Subscription-Tier nicht gefunden") }
            return
        }
        viewModelScope.launch {
            store.update { current ->
                current.copy(activeSubscriptionTierId = tierId)
            }
            _state.update {
                it.copy(
                    settings = it.settings.copy(activeSubscriptionTierId = tierId),
                    error = null,
                    notice = "${tier.name} aktiviert",
                )
            }
        }
    }

    fun filteredPosts(): List<ContentPost> {
        val query = _state.value.query.trim()
        if (query.isBlank()) return _state.value.feed
        return _state.value.feed.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.creatorName.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.format.contains(query, ignoreCase = true) ||
                it.storyHook.contains(query, ignoreCase = true)
        }
    }

    fun registerCreatorInterest(creatorId: String) {
        val creator = _state.value.creators.firstOrNull { it.id == creatorId }
        if (creator == null) {
            _state.update { it.copy(error = "Creatorin nicht gefunden") }
            return
        }
        val primaryExperience = creator.experiences.firstOrNull() ?: "persönliches Companion"
        val opener = buildString {
            append("Hi ${creator.name}, ")
            append("ich möchte ein $primaryExperience Erlebnis starten")
            if (creator.voiceIntroAvailable) {
                append(" und danach eine Voice-Intro freischalten.")
            } else {
                append(".")
            }
        }
        val status = if (creator.voiceIntroAvailable) "Voice Intro bereit" else "Story Mode bereit"
        val draft = ConversationDraft(
            creatorId = creator.id,
            creatorName = creator.name,
            opener = opener,
            status = status,
        )
        _state.update { current ->
            current.copy(
                drafts = current.drafts.filterNot { it.creatorId == creatorId } + draft,
                error = null,
                notice = "DM-Entwurf an ${creator.name} vorbereitet",
            )
        }
    }

    fun dismissDraft(creatorId: String) {
        val creatorName = _state.value.drafts.firstOrNull { it.creatorId == creatorId }?.creatorName ?: return
        _state.update {
            it.copy(
                drafts = it.drafts.filterNot { draft -> draft.creatorId == creatorId },
                error = null,
                notice = "Entwurf für $creatorName entfernt",
            )
        }
    }

    private fun sampleCreators() = listOf(
        CreatorProfile(
            "c1",
            "Luna Velvet",
            "Exclusive Clips",
            1280,
            "Verified",
            listOf("Slow Burn", "Luxury", "Aftercare"),
            listOf("Story Mode", "Custom Reel", "Private Preview"),
            true,
        ),
        CreatorProfile(
            "c2",
            "Nox Ember",
            "Live Sessions",
            940,
            "Verified",
            listOf("Dominant", "Playful", "Night Owl"),
            listOf("Live Fantasy", "Priority DM", "Series Episodes"),
            true,
        ),
        CreatorProfile(
            "c3",
            "Mira Nova",
            "Premium Storysets",
            610,
            "Rising",
            listOf("Cosplay", "Romance", "Creator POV"),
            listOf("Candy Shorts", "Photo Story", "Teaser Drops"),
            false,
        ),
    )

    private fun samplePosts() = listOf(
        ContentPost(
            "p1",
            "After Dark Set",
            "Luna Velvet",
            "Photo Set",
            "Carousel",
            "€24.99",
            true,
            "Luxury tease mit 3-teiligem Story Hook",
        ),
        ContentPost(
            "p2",
            "Private Live Replay",
            "Nox Ember",
            "Video",
            "Live Clip",
            "€18.50",
            true,
            "Fantasy Session mit Voice Intro und Replay Access",
        ),
        ContentPost(
            "p3",
            "Teaser Collection",
            "Mira Nova",
            "Preview",
            "Candy Short",
            "Free",
            false,
            "Kurzserie für neue Fans mit episodischem Cliffhanger",
        ),
    )

    private fun sampleSubscriptions() = listOf(
        SubscriptionTier(
            "starter",
            "Starter",
            "Schneller Einstieg mit Preview-Zugang",
            "€9.99",
            "€96/Jahr",
            listOf("Weekly Preview Content", "Creator Feed Access", "1 gespeicherter DM-Entwurf"),
        ),
        SubscriptionTier(
            "insider",
            "Insider",
            "Der beliebte Mix aus Feed, DM und Story Mode",
            "€19.99",
            "€180/Jahr",
            listOf("Exclusive Media", "Priority DM Queue", "Story Mode Unlocks"),
        ),
        SubscriptionTier(
            "vip",
            "VIP",
            "Für Custom Requests und engere Creator-Bindung",
            "€49.99",
            "€468/Jahr",
            listOf("Custom Requests", "Monthly 1:1 Session", "Voice Intro Drops"),
        ),
    )
}
