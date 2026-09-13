package com.xrdoge.nsfw.ui.explorer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.ui.UiState
import com.xrdoge.nsfw.ui.components.KeyValue
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.theme.Mist
import com.xrdoge.nsfw.util.Drops
import com.xrdoge.nsfw.util.XrplAddress

@Composable
fun ExplorerScreen(
    state: UiState,
    onQueryChange: (String) -> Unit,
    onLookup: () -> Unit,
    onSave: () -> Unit,
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Explorer", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
            Text("Classic Address nachschlagen – ohne Keys, nur öffentliche Ledger-Daten.", color = Mist)
        }
        item {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("r-Address") },
                placeholder = { Text("rXXXXXXXXXXXXXXXXXXXXXXXXXXXX") },
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onLookup, modifier = Modifier.weight(1f), enabled = !state.loadingAccount) {
                    Text("Lookup")
                }
                OutlinedButton(onClick = onSave, modifier = Modifier.weight(1f)) { Text("Speichern") }
            }
        }
        if (state.loadingAccount) {
            item { CircularProgressIndicator() }
        }
        state.account?.let { account ->
            item {
                NeonCard {
                    SectionLabel("Konto")
                    Text(account.address)
                    KeyValue("XRP", Drops.formatXrp(account.balanceDrops))
                    KeyValue("Sequence", account.sequence.toString())
                    KeyValue("OwnerCount", account.ownerCount.toString())
                    KeyValue("Flags", account.flags.toString())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {
                            clipboard.setText(AnnotatedString(account.address))
                        }) { Text("Kopieren") }
                        OutlinedButton(onClick = {
                            val uri = Uri.parse("https://xrpscan.com/account/${account.address}")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }) { Text("XRPScan") }
                    }
                }
            }
        }
        if (state.txs.isNotEmpty()) {
            item { SectionLabel("Letzte Transaktionen") }
            items(state.txs, key = { it.hash.ifBlank { it.type + it.account } }) { tx ->
                NeonCard {
                    Text(tx.type)
                    Text(XrplAddress.shorten(tx.hash.ifBlank { tx.account }, 10, 8), color = Mist)
                    KeyValue("Amount", tx.amountLabel)
                    tx.destination?.let { KeyValue("To", XrplAddress.shorten(it)) }
                }
            }
        }
    }
}
