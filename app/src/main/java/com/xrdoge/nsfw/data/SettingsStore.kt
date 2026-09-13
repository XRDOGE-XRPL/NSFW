package com.xrdoge.nsfw.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "nsfw_settings")

class SettingsStore(private val context: Context) {
    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            rpcUrl = prefs[RPC] ?: AppSettings.DEFAULT_RPC,
            savedAccount = prefs[ACCOUNT].orEmpty(),
            tokenCurrency = prefs[CURRENCY] ?: "NSFW",
            tokenIssuer = prefs[ISSUER].orEmpty(),
        )
    }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = AppSettings(
                rpcUrl = prefs[RPC] ?: AppSettings.DEFAULT_RPC,
                savedAccount = prefs[ACCOUNT].orEmpty(),
                tokenCurrency = prefs[CURRENCY] ?: "NSFW",
                tokenIssuer = prefs[ISSUER].orEmpty(),
            )
            val next = transform(current)
            prefs[RPC] = next.rpcUrl
            prefs[ACCOUNT] = next.savedAccount
            prefs[CURRENCY] = next.tokenCurrency
            prefs[ISSUER] = next.tokenIssuer
        }
    }

    private companion object {
        val RPC = stringPreferencesKey("rpc_url")
        val ACCOUNT = stringPreferencesKey("saved_account")
        val CURRENCY = stringPreferencesKey("token_currency")
        val ISSUER = stringPreferencesKey("token_issuer")
    }
}
