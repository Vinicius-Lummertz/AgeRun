package com.exemplo.agerun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.ui.theme.CardPurple
import com.exemplo.agerun.ui.theme.DeepPurple
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.Radius
import com.exemplo.agerun.ui.theme.Spacing
import com.exemplo.agerun.ui.theme.Success
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary
import com.exemplo.agerun.ui.theme.Warning

@Composable
fun AgeCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        color = CardPurple,
        tonalElevation = 6.dp,
    ) {
        Column(modifier = Modifier.padding(Spacing.lg), content = { content() })
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
        if (action != null && onAction != null) {
            Text(
                text = action, color = Lime, style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(Lime.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .clickableNoRipple(onAction),
            )
        }
    }
}

@Composable
fun StatBadge(modifier: Modifier = Modifier, label: String, value: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Radius.md),
        color = DeepPurple,
        tonalElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(text = value, color = Lime, style = MaterialTheme.typography.headlineMedium)
            Text(text = label, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun StatusBadge(text: String) {
    val color = when (text) {
        "Pago", "Ativo", "Concluído" -> Success
        "Pendente", "Inativo" -> Warning
        else -> Lime
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.sm))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(text = text, color = color, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(Spacing.xxl),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = message, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
    }
}

fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = onClick,
    ),
)
