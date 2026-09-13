package com.xrdoge.nsfw.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.BuildConfig
import com.xrdoge.nsfw.data.AppSettings
import com.xrdoge.nsfw.ui.UiState
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.theme.Mist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: UiState,
    onSave: (rpcUrl: String, account: String, currency: String, issuer: String) -> Unit,
) {
    var rpc by remember(state.settings.rpcUrl) { mutableStateOf(state.settings.rpcUrl) }
    var account by remember(state.settings.savedAccount) { mutableStateOf(state.settings.savedAccount) }
    var currency by remember(state.settings.tokenCurrency) { mutableStateOf(state.settings.tokenCurrency) }
    var issuer by remember(state.settings.tokenIssuer) { mutableStateOf(state.settings.tokenIssuer) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Einstellungen", style = MaterialTheme.typography.headlineMedium)
        Text("Öffentliche Nodes, kein Seed, kein Signing.", color = Mist)

        NeonCard {
            SectionLabel("XRPL Node")
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = rpc,
                    onValueChange = { rpc = it },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    label = { Text("RPC URL") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    readOnly = true,
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    AppSettings.RPC_OPTIONS.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                rpc = option
                                expanded = false
                            },
                        )
                    }
                }
            }
        }

        NeonCard {
            SectionLabel("Konto & Token")
            OutlinedTextField(
                value = account,
                onValueChange = { account = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Standard-Konto") },
                singleLine = true,
            )
            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Token-Währung") },
                singleLine = true,
            )
            OutlinedTextField(
                value = issuer,
                onValueChange = { issuer = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Token-Issuer") },
                singleLine = true,
            )
        }

        Button(onClick = { onSave(rpc, account, currency, issuer) }, modifier = Modifier.fillMaxWidth()) {
            Text("Speichern")
        }

        NeonCard {
            SectionLabel("About")
            Text("NSFW ${BuildConfig.VERSION_NAME} (${BuildConfig.BUILD_TYPE})")
            Text("XRDOGE-XRPL · Read-only Ledger Companion")
            Text("Diese App speichert keine Secrets und signiert keine Transaktionen.", color = Mist)
        }
    }
}
