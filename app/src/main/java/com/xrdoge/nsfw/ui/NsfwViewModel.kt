package com.xrdoge.nsfw.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xrdoge.nsfw.data.AccountSnapshot
import com.xrdoge.nsfw.data.AppSettings
import com.xrdoge.nsfw.data.LedgerTx
import com.xrdoge.nsfw.data.NetworkStatus
import com.xrdoge.nsfw.data.SettingsStore
import com.xrdoge.nsfw.data.TrustLine
import com.xrdoge.nsfw.data.XrplClient
import com.xrdoge.nsfw.data.XrpPrice
import com.xrdoge.nsfw.util.CurrencyCodec
import com.xrdoge.nsfw.util.XrplAddress
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val settings: AppSettings = AppSettings(),
    val query: String = "",
    val network: NetworkStatus? = null,
    val price: XrpPrice? = null,
    val account: AccountSnapshot? = null,
    val lines: List<TrustLine> = emptyList(),
    val txs: List<LedgerTx> = emptyList(),
    val loadingNetwork: Boolean = false,
    val loadingAccount: Boolean = false,
    val error: String? = null,
    val notice: String? = null,
)

class NsfwViewModel(application: Application) : AndroidViewModel(application) {
    private val store = SettingsStore(application)
    private val client = XrplClient()

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            store.settings.collect { settings ->
                _state.update { it.copy(settings = settings, query = it.query.ifBlank { settings.savedAccount }) }
            }
        }
        refreshNetwork()
    }

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value, error = null) }
    }

    fun dismissMessage() {
        _state.update { it.copy(error = null, notice = null) }
    }

    fun refreshNetwork() {
        viewModelScope.launch {
            _state.update { it.copy(loadingNetwork = true, error = null) }
            try {
                coroutineScope {
                    val net = async { client.serverInfo(_state.value.settings.rpcUrl) }
                    val price = async {
                        runCatching { client.xrpPrice() }.getOrNull()
                    }
                    _state.update {
                        it.copy(
                            network = net.await(),
                            price = price.await(),
                            loadingNetwork = false,
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(loadingNetwork = false, error = e.message ?: "Netzwerkfehler") }
            }
        }
    }

    fun lookup(address: String = _state.value.query) {
        val normalized = XrplAddress.normalize(address)
        if (!XrplAddress.isClassicAddress(normalized)) {
            _state.update { it.copy(error = "Ungültige XRPL Classic Address (r…)") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loadingAccount = true, error = null, query = normalized) }
            try {
                val rpc = _state.value.settings.rpcUrl
                coroutineScope {
                    val info = async { client.accountInfo(rpc, normalized) }
                    val lines = async { client.accountLines(rpc, normalized) }
                    val txs = async { client.accountTx(rpc, normalized) }
                    _state.update {
                        it.copy(
                            account = info.await(),
                            lines = lines.await(),
                            txs = txs.await(),
                            loadingAccount = false,
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(loadingAccount = false, error = e.message ?: "Lookup fehlgeschlagen") }
            }
        }
    }

    fun saveCurrentAccount() {
        val address = _state.value.account?.address ?: _state.value.query
        if (!XrplAddress.isClassicAddress(address)) {
            _state.update { it.copy(error = "Kein gültiges Konto zum Speichern") }
            return
        }
        viewModelScope.launch {
            store.update { it.copy(savedAccount = XrplAddress.normalize(address)) }
            _state.update { it.copy(notice = "Konto gespeichert") }
        }
    }

    fun updateSettings(
        rpcUrl: String? = null,
        savedAccount: String? = null,
        tokenCurrency: String? = null,
        tokenIssuer: String? = null,
    ) {
        viewModelScope.launch {
            store.update { current ->
                current.copy(
                    rpcUrl = rpcUrl ?: current.rpcUrl,
                    savedAccount = savedAccount ?: current.savedAccount,
                    tokenCurrency = tokenCurrency ?: current.tokenCurrency,
                    tokenIssuer = tokenIssuer ?: current.tokenIssuer,
                )
            }
            _state.update { it.copy(notice = "Einstellungen gespeichert") }
            refreshNetwork()
        }
    }

    fun matchingTokenLines(): List<TrustLine> {
        val settings = _state.value.settings
        val wanted = CurrencyCodec.toXrplCode(settings.tokenCurrency)
        val human = settings.tokenCurrency.trim().uppercase()
        return _state.value.lines.filter { line ->
            val display = CurrencyCodec.display(line.currency).uppercase()
            val currencyMatch = line.currency.equals(wanted, true) ||
                display == human ||
                line.currency.equals(human, true)
            val issuerMatch = settings.tokenIssuer.isBlank() ||
                line.issuer.equals(settings.tokenIssuer.trim(), true)
            currencyMatch && issuerMatch
        }
    }
}
