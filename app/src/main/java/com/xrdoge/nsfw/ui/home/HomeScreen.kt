package com.xrdoge.nsfw.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.ui.UiState
import com.xrdoge.nsfw.ui.components.GradientTitle
import com.xrdoge.nsfw.ui.components.KeyValue
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.components.StatChip
import com.xrdoge.nsfw.ui.theme.Mist
import com.xrdoge.nsfw.ui.theme.Success
import com.xrdoge.nsfw.util.CurrencyCodec
import com.xrdoge.nsfw.util.Drops
import com.xrdoge.nsfw.util.XrplAddress
import java.util.Locale

@Composable
fun HomeScreen(
    state: UiState,
    onRefresh: () -> Unit,
    onOpenExplorer: () -> Unit,
    onLookupSaved: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GradientTitle("NSFW")
        Text("XRPL Companion für das XRDOGE-Ökosystem.", color = Mist)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            val net = state.network
            StatChip(
                label = "Netzwerk",
                value = net?.serverState?.replaceFirstChar { it.titlecase(Locale.ROOT) } ?: "…",
                modifier = Modifier.weight(1f),
            )
            StatChip(
                label = "Ledger",
                value = net?.ledgerIndex?.toString() ?: "…",
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatChip(
                label = "XRP/USD",
                value = state.price?.let { Drops.formatFiat(it.usd) } ?: "—",
                modifier = Modifier.weight(1f),
            )
            StatChip(
                label = "XRP/EUR",
                value = state.price?.let { Drops.formatFiat(it.eur, "EUR") } ?: "—",
                modifier = Modifier.weight(1f),
            )
        }

        NeonCard {
            SectionLabel("Token")
            Text("${state.settings.tokenCurrency} on XRPL")
            KeyValue(
                "Währung",
                CurrencyCodec.display(CurrencyCodec.toXrplCode(state.settings.tokenCurrency)),
            )
            KeyValue(
                "Issuer",
                if (state.settings.tokenIssuer.isBlank()) {
                    "in Einstellungen setzen"
                } else {
                    XrplAddress.shorten(state.settings.tokenIssuer)
                },
            )
            val matches = state.lines.filter { line ->
                CurrencyCodec.display(line.currency).equals(state.settings.tokenCurrency, true)
            }
            if (matches.isNotEmpty()) {
                KeyValue("Balance", matches.first().balance)
            }
        }

        NeonCard {
            SectionLabel("Gespeichertes Konto")
            val saved = state.settings.savedAccount
            if (saved.isBlank()) {
                Text("Noch kein Konto gespeichert. Lookup im Explorer, dann speichern.", color = Mist)
            } else {
                Text(XrplAddress.shorten(saved, 10, 8))
                state.account?.takeIf { it.address == saved }?.let { account ->
                    Text("${Drops.formatXrp(account.balanceDrops)} XRP", color = Success)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onLookupSaved, enabled = saved.isNotBlank()) { Text("Laden") }
                OutlinedButton(onClick = onOpenExplorer) { Text("Explorer") }
            }
        }

        if (state.loadingNetwork) {
            CircularProgressIndicator()
        }
        OutlinedButton(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) {
            Text("Netzwerk aktualisieren")
        }
    }
}
