package com.xrdoge.nsfw.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "nsfw_settings")

class SettingsStore(private val context: Context) {
    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        prefs.toAppSettings()
    }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = prefs.toAppSettings()
            val next = transform(current)
            prefs[CREATOR_ALIAS] = next.creatorAlias
            prefs[PLATFORM_MODE] = next.platformMode
            prefs[ALLOW_DIRECT_MESSAGES] = next.allowDirectMessages
            prefs[SHOW_EXPLICIT_PREVIEW] = next.showExplicitPreview
        }
    }

    private fun androidx.datastore.preferences.core.Preferences.toAppSettings(): AppSettings {
        return AppSettings(
            creatorAlias = this[CREATOR_ALIAS].orEmpty().ifBlank { "Creatorin" },
            platformMode = this[PLATFORM_MODE].orEmpty().ifBlank { "Adult Plattform" },
            allowDirectMessages = this[ALLOW_DIRECT_MESSAGES] ?: true,
            showExplicitPreview = this[SHOW_EXPLICIT_PREVIEW] ?: false,
        )
    }

    private companion object {
        val CREATOR_ALIAS = stringPreferencesKey("creator_alias")
        val PLATFORM_MODE = stringPreferencesKey("platform_mode")
        val ALLOW_DIRECT_MESSAGES = booleanPreferencesKey("allow_direct_messages")
        val SHOW_EXPLICIT_PREVIEW = booleanPreferencesKey("show_explicit_preview")
    }
}
