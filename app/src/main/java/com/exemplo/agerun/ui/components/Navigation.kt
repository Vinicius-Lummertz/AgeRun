package com.exemplo.agerun.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.ui.theme.CardPurple
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.NightPurple
import com.exemplo.agerun.ui.theme.Radius
import com.exemplo.agerun.ui.theme.TextMuted

data class BottomNavItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun AgeBottomBar(
    items: List<BottomNavItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    elevatedKey: String? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.pill),
        color = CardPurple,
        tonalElevation = 12.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                if (item.key == elevatedKey) {
                    ElevatedNavButton(
                        item = item,
                        selected = item.key == selectedKey,
                        onClick = { onSelect(item.key) },
                    )
                } else {
                    NavButton(
                        item = item,
                        selected = item.key == selectedKey,
                        onClick = { onSelect(item.key) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NavButton(item: BottomNavItem, selected: Boolean, onClick: () -> Unit) {
    val tint by animateColorAsState(if (selected) NightPurple else TextMuted, label = "tint")
    val bg by animateColorAsState(if (selected) Lime else Color.Transparent, label = "bg")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = MutableInteractionSource(), indication = null, onClick = onClick,
        ),
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Radius.md))
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(item.icon, contentDescription = item.label, tint = tint)
        }
        if (selected) {
            Text(
                text = item.label, color = Lime,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ElevatedNavButton(item: BottomNavItem, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(if (selected) Lime else NightPurple)
            .clickable(
                interactionSource = MutableInteractionSource(), indication = null, onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            item.icon, contentDescription = item.label,
            tint = if (selected) NightPurple else Lime,
        )
    }
}
