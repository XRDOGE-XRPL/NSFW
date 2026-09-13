package com.xrdoge.nsfw.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.BuildConfig
import com.xrdoge.nsfw.ui.UiState
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.theme.Mist

@Composable
fun SettingsScreen(
    state: UiState,
    onSave: (creatorAlias: String, platformMode: String, allowDm: Boolean, showPreview: Boolean) -> Unit,
) {
    var alias by remember(state.settings.creatorAlias) { mutableStateOf(state.settings.creatorAlias) }
    var mode by remember(state.settings.platformMode) { mutableStateOf(state.settings.platformMode) }
    var allowDm by remember(state.settings.allowDirectMessages) { mutableStateOf(state.settings.allowDirectMessages) }
    var showPreview by remember(state.settings.showExplicitPreview) { mutableStateOf(state.settings.showExplicitPreview) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Einstellungen", style = MaterialTheme.typography.headlineMedium)
        Text("Plattform-Profile und NSFW-Policy-Optionen verwalten.", color = Mist)

        NeonCard {
            SectionLabel("Profil")
            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Creator Alias") },
                singleLine = true,
            )
            OutlinedTextField(
                value = mode,
                onValueChange = { mode = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Plattformmodus") },
                singleLine = true,
            )
        }

        NeonCard {
            SectionLabel("Policy")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = allowDm,
                        role = Role.Switch,
                        onValueChange = { allowDm = it },
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Direktnachrichten erlauben")
                Switch(checked = allowDm, onCheckedChange = { allowDm = it })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = showPreview,
                        role = Role.Switch,
                        onValueChange = { showPreview = it },
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Explizite Preview anzeigen")
                Switch(checked = showPreview, onCheckedChange = { showPreview = it })
            }
        }

        Button(onClick = { onSave(alias, mode, allowDm, showPreview) }, modifier = Modifier.fillMaxWidth()) {
            Text("Speichern")
        }

        NeonCard {
            SectionLabel("About")
            Text("NSFW ${BuildConfig.VERSION_NAME} (${BuildConfig.BUILD_TYPE})")
            Text("Creatorin/Adult/Plattform")
            Text("Kein XRPL-Ledger-Companion aktiv.", color = Mist)
        }
    }
}
