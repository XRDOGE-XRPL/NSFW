package com.xrdoge.nsfw.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xrdoge.nsfw.ui.theme.CardStroke
import com.xrdoge.nsfw.ui.theme.Elevated
import com.xrdoge.nsfw.ui.theme.Mist
import com.xrdoge.nsfw.ui.theme.NeonMagenta
import com.xrdoge.nsfw.ui.theme.NeonPink
import com.xrdoge.nsfw.ui.theme.NeonPurple

@Composable
fun NeonCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Elevated)
            .border(1.dp, CardStroke, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

@Composable
fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1D1D2C))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Mist)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun GradientTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.displayLarge.copy(
            brush = Brush.linearGradient(listOf(NeonPink, NeonMagenta, NeonPurple)),
        ),
    )
}

@Composable
fun KeyValue(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = Mist, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SectionLabel(text: String) {
    Box(Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = NeonPink)
    }
}

@Composable
fun InfoPill(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardStroke,
    contentColor: Color = Color.White,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .border(1.dp, contentColor.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = contentColor)
    }
}
