package com.xrdoge.nsfw.ui.explorer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.data.ConversationDraft
import com.xrdoge.nsfw.data.CreatorProfile
import com.xrdoge.nsfw.ui.components.InfoPill
import com.xrdoge.nsfw.ui.components.KeyValue
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.theme.Mist

@Composable
fun ExplorerScreen(
    query: String,
    creators: List<CreatorProfile>,
    favoriteCreatorIds: Set<String>,
    drafts: List<ConversationDraft>,
    onQueryChange: (String) -> Unit,
    onCreatorInterest: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Creator Hub", style = MaterialTheme.typography.headlineMedium)
            Text("Creatorinnen entdecken, Favoriten speichern und Story-/Voice-Drafts anlegen.", color = Mist)
        }
        item {
            OutlinedTextField(
                value = query,
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
                val isFavorite = creator.id in favoriteCreatorIds
                val draftCount = drafts.count { it.creatorId == creator.id }
                NeonCard {
                    SectionLabel(creator.name)
                    Text("${creator.niche} • ${creator.chemistryTags.joinToString(" • ")}", color = Mist)
                    KeyValue("Nische", creator.niche)
                    KeyValue("Subscriber", creator.monthlySubscribers.toString())
                    KeyValue("Status", creator.verification)
                    KeyValue("Erlebnisse", creator.experiences.joinToString())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        if (creator.voiceIntroAvailable) {
                            InfoPill("Voice Intro")
                        }
                        InfoPill("Story Mode")
                        if (draftCount > 0) {
                            InfoPill("$draftCount Draft")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { onToggleFavorite(creator.id) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(if (isFavorite) "Favorit ✓" else "Favorit")
                        }
                        Button(
                            onClick = { onCreatorInterest(creator.id) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(if (draftCount > 0) "Draft updaten" else "DM-Draft")
                        }
                    }
                }
            }
        }
    }
}
