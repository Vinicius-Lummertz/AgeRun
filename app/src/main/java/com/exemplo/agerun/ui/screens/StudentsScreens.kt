package com.exemplo.agerun.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.agerun.state.AgeRunAppState
import com.exemplo.agerun.ui.components.AuthField
import com.exemplo.agerun.ui.components.DetailCard
import com.exemplo.agerun.ui.components.HeroSection
import com.exemplo.agerun.ui.components.InfoLine
import com.exemplo.agerun.ui.components.SectionTitle
import com.exemplo.agerun.ui.components.SelectionChip
import com.exemplo.agerun.ui.components.SmallActionButton
import com.exemplo.agerun.ui.components.StatusMiniCard
import com.exemplo.agerun.ui.components.TagChip
import com.exemplo.agerun.ui.components.MultilineField
import com.exemplo.agerun.model.Student
import com.exemplo.agerun.ui.theme.CardPurple
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun StudentsModuleScreen(appState: AgeRunAppState) {
    HeroSection(
        title = "Alunos",
        subtitle = "Organize a base, abra perfis individuais e gerencie tudo por atleta.",
        actionLabel = "Novo",
        onActionClick = appState::openCreateStudentSheet,
    )
    Spacer(modifier = Modifier.height(20.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appState.students.forEach { student ->
            StudentCard(
                student = student,
                onClick = { appState.selectStudent(student.id) },
            )
        }
    }
}

@Composable
fun StudentProfileScreen(appState: AgeRunAppState) {
    val student = appState.selectedStudent ?: return
    val workouts = appState.workouts.filter { student.id in it.assignedStudentIds }
    var worksheetDraft by remember(student.id, student.activeWorksheet) { mutableStateOf(student.activeWorksheet) }
    var noteDraft by remember(student.id, student.coachNote) { mutableStateOf(student.coachNote) }
    var paymentLabel by remember(student.id) { mutableStateOf("") }
    var paymentAmount by remember(student.id) { mutableStateOf("") }
    var paymentDueDate by remember(student.id) { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = appState::clearSelectedStudent),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "< Voltar",
            color = Lime,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
    Spacer(modifier = Modifier.height(14.dp))
    HeroSection(
        title = student.name,
        subtitle = "${student.plan}  •  ${student.status}",
        actionLabel = if (student.status == "Ativo") "Inativar" else "Ativar",
        onActionClick = { appState.toggleStudentStatus(student.id) },
    )
    Spacer(modifier = Modifier.height(18.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        StatusMiniCard(
            modifier = Modifier.fillMaxWidth(),
            title = "KM no mes",
            value = student.monthlyKm.toString(),
        )
        StatusMiniCard(
            modifier = Modifier.fillMaxWidth(),
            title = "Treinos no perfil",
            value = workouts.size.toString(),
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    DetailCard(title = "Informacoes") {
        InfoLine("E-mail", student.email)
        InfoLine("Telefone", student.phone)
        InfoLine("Inicio", student.startDate)
    }
    Spacer(modifier = Modifier.height(16.dp))
    DetailCard(title = "Planilha ativa") {
        AuthField(
            value = worksheetDraft,
            onValueChange = { worksheetDraft = it },
            label = "Nome da planilha",
            keyboardType = KeyboardType.Text,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SmallActionButton(
            text = "Salvar planilha",
            onClick = { appState.updateStudentWorksheet(student.id, worksheetDraft) },
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    DetailCard(title = "Observacoes do professor") {
        MultilineField(
            value = noteDraft,
            onValueChange = { noteDraft = it },
            label = "Anotacoes",
        )
        Spacer(modifier = Modifier.height(12.dp))
        SmallActionButton(
            text = "Salvar observacao",
            onClick = { appState.updateStudentNote(student.id, noteDraft) },
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    DetailCard(title = "Pagamentos") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            student.payments.forEach { payment ->
                PaymentRow(
                    payment = payment,
                    onToggle = { appState.togglePaymentStatus(student.id, payment.id) },
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AuthField(paymentLabel, { paymentLabel = it }, "Descricao da cobranca", KeyboardType.Text)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AuthField(
                value = paymentAmount,
                onValueChange = { paymentAmount = it },
                label = "Valor",
                keyboardType = KeyboardType.Text,
                modifier = Modifier.weight(1f),
            )
            AuthField(
                value = paymentDueDate,
                onValueChange = { paymentDueDate = it },
                label = "Venc.",
                keyboardType = KeyboardType.Text,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SmallActionButton(
            text = "Adicionar mensalidade",
            onClick = {
                if (paymentLabel.isNotBlank()) {
                    appState.addStudentPayment(
                        student.id,
                        paymentLabel.trim(),
                        paymentAmount.ifBlank { "R$ 0" }.trim(),
                        paymentDueDate.ifBlank { "--/--" }.trim(),
                    )
                    paymentLabel = ""
                    paymentAmount = ""
                    paymentDueDate = ""
                }
            },
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    DetailCard(title = "Treinos atribuídos") {
        if (workouts.isEmpty()) {
            Text(
                text = "Ainda nao ha treinos vinculados a este aluno.",
                color = TextMuted,
                fontSize = 14.sp,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                workouts.forEach { workout ->
                    WorkoutListItem(workout = workout)
                }
            }
        }
    }
}

@Composable
private fun StudentCard(
    student: Student,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        color = CardPurple.copy(alpha = 0.95f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = student.name,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = student.plan,
                    color = TextMuted,
                    fontSize = 13.sp,
                )
                Text(
                    text = student.activeWorksheet,
                    color = Lime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            TagChip(text = student.status)
        }
    }
}

@Composable
private fun PaymentRow(
    payment: com.exemplo.agerun.model.PaymentEntry,
    onToggle: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        color = CardPurple.copy(alpha = 0.82f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.label,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${payment.amount} • ${payment.dueDate}",
                    color = TextMuted,
                    fontSize = 12.sp,
                )
            }
            SelectionChip(
                text = payment.status,
                selected = payment.status == "Pago",
                onClick = onToggle,
            )
        }
    }
}
