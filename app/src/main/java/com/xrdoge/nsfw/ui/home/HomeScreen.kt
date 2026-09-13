package com.xrdoge.nsfw.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.data.ConversationDraft
import com.xrdoge.nsfw.data.CreatorProfile
import com.xrdoge.nsfw.data.SubscriptionTier
import com.xrdoge.nsfw.ui.UiState
import com.xrdoge.nsfw.ui.components.GradientTitle
import com.xrdoge.nsfw.ui.components.InfoPill
import com.xrdoge.nsfw.ui.components.KeyValue
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.components.StatChip
import com.xrdoge.nsfw.ui.theme.Mist

@Composable
fun HomeScreen(
    state: UiState,
    favoriteCreators: List<CreatorProfile>,
    activeSubscription: SubscriptionTier?,
    drafts: List<ConversationDraft>,
    onRefresh: () -> Unit,
    onOpenCreatorHub: () -> Unit,
    onOpenContentHub: () -> Unit,
    onDismissDraft: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GradientTitle("NSFW")
        Text("Favoriten, Story-Modes und Voice-Intros wie bei modernen Companion-Plattformen.", color = Mist)

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
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatChip(
                label = "Favoriten",
                value = favoriteCreators.size.toString(),
                modifier = Modifier.weight(1f),
            )
            StatChip(
                label = "DM Drafts",
                value = drafts.size.toString(),
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
            SectionLabel("Companion Flow")
            KeyValue("Aktiver Tier", activeSubscription?.name ?: "Kein Tier")
            activeSubscription?.let { tier ->
                Text(tier.tagline, color = Mist)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    tier.highlights.take(2).forEach { highlight ->
                        InfoPill(highlight)
                    }
                }
            } ?: Text("Wähle im Content-Bereich ein Subscription-Tier für passende Unlocks.", color = Mist)
        }

        NeonCard {
            SectionLabel("Favoritinnen")
            if (favoriteCreators.isEmpty()) {
                Text("Markiere Creatorinnen im Hub, um sie hier schnell wiederzufinden.", color = Mist)
            } else {
                favoriteCreators.take(3).forEach { creator ->
                    Text(creator.name)
                    Text("${creator.niche} • ${creator.chemistryTags.joinToString(" • ")}", color = Mist)
                    KeyValue("Nächstes Erlebnis", creator.experiences.firstOrNull() ?: "Persönliches Companion")
                }
            }
        }

        NeonCard {
            SectionLabel("DM Drafts")
            if (drafts.isEmpty()) {
                Text("Lege im Creator Hub direkte Story- oder Voice-Intro-Anfragen an.", color = Mist)
            } else {
                drafts.forEach { draft ->
                    Text(draft.creatorName, color = NeonPink)
                    Text(draft.opener, color = Mist)
                    KeyValue("Status", draft.status)
                    OutlinedButton(onClick = { onDismissDraft(draft.creatorId) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Entwurf entfernen")
                    }
                }
            }
        }

        NeonCard {
            SectionLabel("Schnellzugriff")
            Text("Creatorinnen verwalten, DM-Flows starten und Subscription-Erlebnisse steuern.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onOpenCreatorHub, modifier = Modifier.weight(1f)) { Text("Creator Hub") }
                OutlinedButton(onClick = onOpenContentHub, modifier = Modifier.weight(1f)) { Text("Content") }
            }
        }

        OutlinedButton(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) {
            Text("Plattformdaten aktualisieren")
        }
    }
}
