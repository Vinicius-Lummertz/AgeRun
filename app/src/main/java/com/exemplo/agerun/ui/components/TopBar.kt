package com.exemplo.agerun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.ui.theme.CardPurple
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.NightPurple
import com.exemplo.agerun.ui.theme.Sizing
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary

@Composable
fun AvatarPhoto(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = Sizing.avatar,
    onClick: (() -> Unit)? = null,
) {
    // M0 usa placeholder com ícone (foto real vem no M1 via Storage).
    val base = modifier
        .size(size)
        .clip(CircleShape)
        .background(CardPurple)
        .border(2.dp, Lime, CircleShape)
    Box(
        modifier = if (onClick != null) base.clickable(onClick = onClick) else base,
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Lime)
    }
}

@Composable
fun AgeTopBar(
    title: String,
    subtitle: String,
    onAvatarClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(text = subtitle, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            Text(text = title, color = TextPrimary, style = MaterialTheme.typography.titleLarge)
        }
        AvatarPhoto(onClick = onAvatarClick)
    }
}
