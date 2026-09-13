package com.xrdoge.nsfw.ui.tokens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.ui.UiState
import com.xrdoge.nsfw.ui.components.KeyValue
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.theme.Mist
import com.xrdoge.nsfw.ui.theme.NeonPink
import com.xrdoge.nsfw.util.CurrencyCodec
import com.xrdoge.nsfw.util.XrplAddress

@Composable
fun TokensScreen(state: UiState, featured: List<com.xrdoge.nsfw.data.TrustLine>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Tokens", style = MaterialTheme.typography.headlineMedium)
            Text("Trustlines des geladenen Kontos. NSFW-Filter kommt aus den Einstellungen.", color = Mist)
        }
        if (featured.isNotEmpty()) {
            item { SectionLabel("NSFW Match") }
            items(featured, key = { "f-${it.issuer}-${it.currency}" }) { line ->
                TokenCard(line, highlight = true)
            }
        }
        item { SectionLabel("Alle Trustlines (${state.lines.size})") }
        if (state.lines.isEmpty()) {
            item {
                NeonCard {
                    Text("Kein Konto geladen oder keine Trustlines.", color = Mist)
                    Text("Im Explorer eine Address nachschlagen.", color = Mist)
                }
            }
        } else {
            items(state.lines, key = { "${it.issuer}-${it.currency}" }) { line ->
                TokenCard(line, highlight = false)
            }
        }
    }
}

@Composable
private fun TokenCard(line: com.xrdoge.nsfw.data.TrustLine, highlight: Boolean) {
    NeonCard {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                CurrencyCodec.display(line.currency),
                color = if (highlight) NeonPink else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            KeyValue("Balance", line.balance)
            KeyValue("Limit", line.limit)
            KeyValue("Issuer", XrplAddress.shorten(line.issuer, 8, 6))
        }
    }
}
