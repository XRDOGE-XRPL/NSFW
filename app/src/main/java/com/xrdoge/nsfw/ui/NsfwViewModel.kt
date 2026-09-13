package com.xrdoge.nsfw.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xrdoge.nsfw.data.AppSettings
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
    val loading: Boolean = false,
    val error: String? = null,
    val notice: String? = null,
)

class NsfwViewModel(application: Application) : AndroidViewModel(application) {
    private val store = SettingsStore(application)

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
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
                loading = false,
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
        if (query.isBlank()) return _state.value.creators
        return _state.value.creators.filter {
            it.name.contains(query, ignoreCase = true) || it.niche.contains(query, ignoreCase = true)
        }
    }

    fun filteredPosts(): List<ContentPost> {
        val query = _state.value.query.trim()
        if (query.isBlank()) return _state.value.feed
        return _state.value.feed.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.creatorName.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
        }
    }

    fun registerCreatorInterest(creatorId: String) {
        val creatorName = _state.value.creators.firstOrNull { it.id == creatorId }?.name ?: creatorId
        _state.update { it.copy(notice = "Kontaktanfrage an $creatorName vorbereitet") }
    }

    private fun sampleCreators() = listOf(
        CreatorProfile("c1", "Luna Velvet", "Exclusive Clips", 1280, "Verified"),
        CreatorProfile("c2", "Nox Ember", "Live Sessions", 940, "Verified"),
        CreatorProfile("c3", "Mira Nova", "Premium Storysets", 610, "Rising"),
    )

    private fun samplePosts() = listOf(
        ContentPost("p1", "After Dark Set", "Luna Velvet", "Photo Set", "€24.99", true),
        ContentPost("p2", "Private Live Replay", "Nox Ember", "Video", "€18.50", true),
        ContentPost("p3", "Teaser Collection", "Mira Nova", "Preview", "Free", false),
    )

    private fun sampleSubscriptions() = listOf(
        SubscriptionTier("Starter", "€9.99", listOf("Weekly Preview Content", "Creator Feed Access")),
        SubscriptionTier("Insider", "€19.99", listOf("Exclusive Media", "Priority DM Queue")),
        SubscriptionTier("VIP", "€49.99", listOf("Custom Requests", "Monthly 1:1 Session")),
    )
}
