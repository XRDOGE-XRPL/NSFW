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

@Composable
fun HomeScreen(
    state: UiState,
    onRefresh: () -> Unit,
    onOpenCreatorHub: () -> Unit,
    onOpenContentHub: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GradientTitle("NSFW")
        Text("Creatorinnen-zentrierte Adult-Plattform ohne XRPL-Funktionen.", color = Mist)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatChip(
                label = "Creator",
                value = state.creators.size.toString(),
                modifier = Modifier.weight(1f),
            )
            StatChip(
                label = "Posts",
                value = state.feed.size.toString(),
                modifier = Modifier.weight(1f),
            )
        }

        NeonCard {
            SectionLabel("Plattform")
            KeyValue("Modus", state.settings.platformMode)
            KeyValue("Profil", state.settings.creatorAlias)
            KeyValue("DM", if (state.settings.allowDirectMessages) "Aktiv" else "Aus")
            KeyValue("Preview", if (state.settings.showExplicitPreview) "Sichtbar" else "Verdeckt")
        }

        NeonCard {
            SectionLabel("Schnellzugriff")
            Text("Creatorinnen verwalten, Content kuratieren und Subscriptions steuern.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onOpenCreatorHub, modifier = Modifier.weight(1f)) { Text("Creator Hub") }
                OutlinedButton(onClick = onOpenContentHub, modifier = Modifier.weight(1f)) { Text("Content") }
            }
        }

        if (state.loading) {
            CircularProgressIndicator()
        }
        OutlinedButton(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) {
            Text("Plattformdaten aktualisieren")
        }
    }
}
