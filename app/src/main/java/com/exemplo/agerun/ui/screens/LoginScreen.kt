package com.exemplo.agerun.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.agerun.model.Role
import com.exemplo.agerun.ui.components.AppBackground
import com.exemplo.agerun.ui.components.AuthField
import com.exemplo.agerun.ui.components.DecorativeGlow
import com.exemplo.agerun.ui.components.LogoHero
import com.exemplo.agerun.ui.components.OutlinedActionButton
import com.exemplo.agerun.ui.components.StatusMiniCard
import com.exemplo.agerun.ui.theme.CardPurple
import com.exemplo.agerun.ui.theme.DeepPurple
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.NightPurple
import com.exemplo.agerun.ui.theme.Radius
import com.exemplo.agerun.ui.theme.SoftPurple
import com.exemplo.agerun.ui.theme.StrokeSoft
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.Row

@Composable
fun LoginScreen(onLogin: (Role) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableStateOf(Role.Professor) }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(horizontal = 24.dp, vertical = 18.dp),
            ) {
                DecorativeGlow(
                    modifier = Modifier
                        .size(240.dp)
                        .align(Alignment.TopEnd),
                    color = Lime.copy(alpha = 0.12f),
                )
                DecorativeGlow(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.BottomStart),
                    color = SoftPurple.copy(alpha = 0.18f),
                )
                LogoHero(modifier = Modifier.align(Alignment.Center))
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(
                    topStart = 30.dp,
                    topEnd = 30.dp,
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp,
                ),
                color = DeepPurple.copy(alpha = 0.96f),
                tonalElevation = 10.dp,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        text = "Controle sua assessoria com leveza e clareza",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Treinos, alunos, pagamentos, planilhas e comunicados em uma experiencia simples e elegante.",
                        color = TextMuted,
                        fontSize = 15.sp,
                        lineHeight = 23.sp,
                    )
                    AuthField(
                        value = email,
                        onValueChange = { email = it },
                        label = "E-mail",
                        keyboardType = KeyboardType.Email,
                    )
                    AuthField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Senha",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                    )
                    Text(
                        text = "Entrar como",
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        RolePill(
                            label = "Sou professor",
                            selected = selectedRole == Role.Professor,
                            onClick = { selectedRole = Role.Professor },
                            modifier = Modifier.weight(1f),
                        )
                        RolePill(
                            label = "Sou aluno",
                            selected = selectedRole == Role.Aluno,
                            onClick = { selectedRole = Role.Aluno },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Button(
                        onClick = { onLogin(selectedRole) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Lime,
                            contentColor = NightPurple,
                        ),
                    ) {
                        Text(
                            text = "Entrar na plataforma",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    OutlinedActionButton(text = "Criar conta")
                    StatusMiniCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Tudo centralizado para a assessoria",
                        value = "Coach mode",
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RolePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(Radius.md))
            .background(if (selected) Lime else CardPurple)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent else StrokeSoft,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(Radius.md),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) NightPurple else TextPrimary,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
