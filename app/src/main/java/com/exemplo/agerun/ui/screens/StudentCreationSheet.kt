package com.exemplo.agerun.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.exemplo.agerun.model.StudentDraft
import com.exemplo.agerun.ui.components.AuthField
import com.exemplo.agerun.ui.components.FilledActionButton
import com.exemplo.agerun.ui.components.OutlineActionButton
import com.exemplo.agerun.ui.theme.DeepPurple
import com.exemplo.agerun.ui.theme.TextPrimary
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun StudentCreationSheet(
    onDismiss: () -> Unit,
    onCreateStudent: (StudentDraft) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var plan by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.44f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(enabled = false) {},
            shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
            color = DeepPurple,
            tonalElevation = 14.dp,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Novo aluno",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                AuthField(name, { name = it }, "Nome completo", KeyboardType.Text)
                AuthField(email, { email = it }, "E-mail", KeyboardType.Email)
                AuthField(phone, { phone = it }, "Telefone", KeyboardType.Phone)
                AuthField(plan, { plan = it }, "Plano ou turma", KeyboardType.Text)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlineActionButton(
                        modifier = Modifier.weight(1f),
                        text = "Cancelar",
                        onClick = onDismiss,
                    )
                    FilledActionButton(
                        modifier = Modifier.weight(1f),
                        text = "Criar",
                        onClick = {
                            if (name.isNotBlank()) {
                                onCreateStudent(
                                    StudentDraft(
                                        name = name.trim(),
                                        email = email.trim(),
                                        phone = phone.trim(),
                                        plan = plan.trim(),
                                    ),
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}
