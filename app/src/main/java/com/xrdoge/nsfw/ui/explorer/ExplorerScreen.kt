package com.xrdoge.nsfw.ui.explorer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.data.CreatorProfile
import com.xrdoge.nsfw.ui.UiState
import com.xrdoge.nsfw.ui.components.KeyValue
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.theme.Mist

@Composable
fun ExplorerScreen(
    state: UiState,
    creators: List<CreatorProfile>,
    onQueryChange: (String) -> Unit,
    onCreatorInterest: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Creator Hub", style = MaterialTheme.typography.headlineMedium)
            Text("Creatorinnen für die Adult-Plattform finden und kuratieren.", color = Mist)
        }
        item {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Suche") },
                placeholder = { Text("Name oder Nische") },
            )
        }

        if (creators.isEmpty()) {
            item {
                NeonCard {
                    Text("Keine Creatorinnen für den Suchbegriff gefunden.", color = Mist)
                }
            }
        } else {
            items(creators, key = { it.id }) { creator ->
                NeonCard {
                    SectionLabel(creator.name)
                    KeyValue("Nische", creator.niche)
                    KeyValue("Subscriber", creator.monthlySubscribers.toString())
                    KeyValue("Status", creator.verification)
                    Button(onClick = { onCreatorInterest(creator.id) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Kontakt vorbereiten")
                    }
                }
            }
        }
    }
}
