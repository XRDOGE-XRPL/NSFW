package com.xrdoge.nsfw.ui.tokens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.data.ContentPost
import com.xrdoge.nsfw.data.CreatorProfile
import com.xrdoge.nsfw.ui.UiState
import com.xrdoge.nsfw.ui.components.InfoPill
import com.xrdoge.nsfw.ui.components.KeyValue
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.theme.Mist
import com.xrdoge.nsfw.ui.theme.NeonPink

@Composable
fun TokensScreen(
    state: UiState,
    feed: List<ContentPost>,
    creators: List<CreatorProfile>,
    onSelectTier: (String) -> Unit,
) {
    val featureCoverage = state.subscriptions
        .flatMap { tier -> tier.highlights.map { highlight -> highlight to tier.name } }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Content", style = MaterialTheme.typography.headlineMedium)
            Text("Content- und Subscription-Übersicht für die NSFW-Plattform.", color = Mist)
        }

        item {
            SectionLabel("Subscription Tiers")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.subscriptions.forEach { tier ->
                    val isActive = tier.id == state.settings.activeSubscriptionTierId
                    NeonCard {
                        Text(tier.name, color = NeonPink, style = MaterialTheme.typography.titleMedium)
                        Text(tier.tagline, color = Mist)
                        KeyValue("Preis", tier.monthlyPrice)
                        KeyValue("Jahr", tier.yearlyPrice)
                        tier.highlights.forEach { highlight ->
                            Text("• $highlight", color = Mist)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            if (isActive) {
                                InfoPill("Aktiv", modifier = Modifier.weight(1f))
                                OutlinedButton(onClick = { }, enabled = false, modifier = Modifier.weight(1f)) {
                                    Text("Aktiver Tier")
                                }
                            } else {
                                OutlinedButton(onClick = { onSelectTier(tier.id) }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Als aktiven Tier setzen")
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionLabel("Feature Vergleich")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                featureCoverage.forEach { (feature, tiers) ->
                    NeonCard {
                        KeyValue(feature, tiers.joinToString(" • "))
                    }
                }
            }
        }

        item { SectionLabel("Aktiver Feed (${feed.size})") }
        if (feed.isEmpty()) {
            item {
                NeonCard {
                    Text("Keine Beiträge für die aktuelle Suche.", color = Mist)
                }
            }
        } else {
            items(feed, key = { it.id }) { post ->
                NeonCard {
                    Text(post.title, style = MaterialTheme.typography.titleMedium)
                    KeyValue("Creatorin", post.creatorName)
                    KeyValue("Kategorie", post.category)
                    KeyValue("Format", post.format)
                    KeyValue("Preis", post.priceLabel)
                    KeyValue("Zugang", if (post.isPremium) "Premium" else "Free")
                    Text(post.storyHook, color = Mist)
                }
            }
        }

        item {
            SectionLabel("Creator Snapshot")
            Text("Aktuell gelistet: ${creators.size} Creatorinnen", color = Mist)
            Text(
                "Mit Voice Intro: ${creators.count { it.voiceIntroAvailable }} • Story Modes: ${creators.sumOf { it.experiences.size }}",
                color = Mist,
            )
        }
    }
}
