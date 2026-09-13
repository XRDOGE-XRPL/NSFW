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
import com.xrdoge.nsfw.data.ContentPost
import com.xrdoge.nsfw.data.CreatorProfile
import com.xrdoge.nsfw.ui.UiState
import com.xrdoge.nsfw.ui.components.KeyValue
import com.xrdoge.nsfw.ui.components.NeonCard
import com.xrdoge.nsfw.ui.components.SectionLabel
import com.xrdoge.nsfw.ui.theme.Mist
import com.xrdoge.nsfw.ui.theme.NeonPink

@Composable
fun TokensScreen(state: UiState, feed: List<ContentPost>, creators: List<CreatorProfile>) {
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
                    NeonCard {
                        Text(tier.name, color = NeonPink, style = MaterialTheme.typography.titleMedium)
                        KeyValue("Preis", tier.monthlyPrice)
                        tier.highlights.forEach { highlight ->
                            Text("• $highlight", color = Mist)
                        }
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
                    KeyValue("Preis", post.priceLabel)
                    KeyValue("Zugang", if (post.isPremium) "Premium" else "Free")
                }
            }
        }

        item {
            SectionLabel("Creator Snapshot")
            Text("Aktuell gelistet: ${creators.size} Creatorinnen", color = Mist)
        }
    }
}
