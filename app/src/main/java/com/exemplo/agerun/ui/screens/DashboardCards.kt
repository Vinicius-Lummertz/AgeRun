package com.exemplo.agerun.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.agerun.model.NoticeEntry
import com.exemplo.agerun.model.PaymentEntry
import com.exemplo.agerun.state.AgeRunAppState
import com.exemplo.agerun.ui.components.AuthField
import com.exemplo.agerun.ui.components.DetailCard
import com.exemplo.agerun.ui.components.SelectionChip
import com.exemplo.agerun.ui.components.SmallActionButton
import com.exemplo.agerun.ui.theme.CardPurple
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NoticeComposerCard(appState: AgeRunAppState) {
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    var pinned by rememberSaveable { mutableStateOf(false) }

    DetailCard(title = "Publicar comunicado") {
        AuthField(title, { title = it }, "Titulo", KeyboardType.Text)
        Spacer(modifier = Modifier.height(10.dp))
        com.exemplo.agerun.ui.components.MultilineField(body, { body = it }, "Mensagem para a turma")
        Spacer(modifier = Modifier.height(12.dp))
        SelectionChip(
            text = if (pinned) "Fixado no topo" else "Marcar como importante",
            selected = pinned,
            onClick = { pinned = !pinned },
        )
        Spacer(modifier = Modifier.height(12.dp))
        SmallActionButton(
            text = "Publicar para todos",
            onClick = {
                if (title.isNotBlank() && body.isNotBlank()) {
                    appState.createNotice(title.trim(), body.trim(), pinned)
                    title = ""
                    body = ""
                    pinned = false
                }
            },
        )
    }
}

@Composable
fun NoticeCard(notice: NoticeEntry) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        color = CardPurple.copy(alpha = 0.96f),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = notice.title,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (notice.pinned) {
                    com.exemplo.agerun.ui.components.TagChip(text = "Fixado")
                }
            }
            Text(
                text = notice.body,
                color = TextMuted,
                fontSize = 14.sp,
                lineHeight = 21.sp,
            )
            Text(
                text = notice.date,
                color = Lime,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun PaymentOverviewCard(
    studentName: String,
    payment: PaymentEntry,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = CardPurple.copy(alpha = 0.94f),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = studentName,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${payment.label} • ${payment.amount}",
                color = TextMuted,
                fontSize = 13.sp,
            )
            Text(
                text = "Vence em ${payment.dueDate}",
                color = Lime,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun WorkoutListItem(workout: com.exemplo.agerun.model.WorkoutEntry) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        color = CardPurple.copy(alpha = 0.88f),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = workout.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${workout.date} • ${workout.distanceKm}km • ${workout.focus}",
                color = Lime,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Pace ${workout.pace}  •  ${workout.notes}",
                color = TextMuted,
                fontSize = 12.sp,
            )
        }
    }
}
