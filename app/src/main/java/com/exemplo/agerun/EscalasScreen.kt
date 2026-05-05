package com.exemplo.agerun

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.ui.theme.AgeRunTheme
import kotlinx.coroutines.launch

private val EscalasBackground = Color(0xFFF6F7F2)
private val EscalasInk = Color(0xFF10221C)
private val EscalasMuted = Color(0xFF587069)
private val EscalasGreen = Color(0xFF0B6B3A)
private val EscalasSurface = Color.White

@Composable
fun EscalasScreen(
    authResponse: AuthResponse,
    modifier: Modifier = Modifier,
) {
    val accessToken = authResponse.session?.accessToken.orEmpty()
    val isProfessor = authResponse.user.role == "professor"
    val coroutineScope = rememberCoroutineScope()
    var escalas by remember { mutableStateOf<List<Escala>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    fun loadEscalas() {
        if (accessToken.isBlank()) return

        isLoading = true
        coroutineScope.launch {
            AuthApi.listarEscalas(accessToken)
                .onSuccess {
                    escalas = it
                    feedback = null
                }
                .onFailure {
                    feedback = it.message ?: "Nao foi possivel carregar as escalas."
                }
            isLoading = false
        }
    }

    LaunchedEffect(accessToken) {
        loadEscalas()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = EscalasBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Escalas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = EscalasInk,
                    )
                    Text(
                        text = if (isProfessor) "Planeje e acompanhe os treinos." else "Veja os proximos encontros da turma.",
                        color = EscalasMuted,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                if (isProfessor) {
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = { showCreateDialog = true },
                    ) {
                        Text(text = "Nova", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${escalas.size} ${if (escalas.size == 1) "escala" else "escalas"}",
                    color = EscalasMuted,
                    fontWeight = FontWeight.Bold,
                )
                TextButton(onClick = { loadEscalas() }) {
                    Text(text = "Atualizar", color = EscalasGreen, fontWeight = FontWeight.Bold)
                }
            }

            feedback?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            }

            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text(text = "Carregando escalas...", color = EscalasMuted)
                }
            } else if (escalas.isEmpty()) {
                EmptyEscalasScreenCard(isProfessor = isProfessor)
            } else {
                escalas.forEach { escala ->
                    EscalaCard(escala = escala)
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateEscalaDialog(
            accessToken = accessToken,
            onDismiss = { showCreateDialog = false },
            onCreated = {
                showCreateDialog = false
                loadEscalas()
            },
        )
    }
}

@Composable
private fun EmptyEscalasScreenCard(isProfessor: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = EscalasSurface),
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = if (isProfessor) "Crie a primeira escala para a turma." else "Nenhuma escala publicada ainda.",
            color = EscalasMuted,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun EscalaCard(escala: Escala) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = EscalasSurface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = escala.titulo,
                color = EscalasInk,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
            )
            Text(text = formatDateTime(escala.inicioAt), color = EscalasMuted, fontWeight = FontWeight.SemiBold)
            escala.fimAt?.let {
                Text(text = "Termina ${formatDateTime(it)}", color = EscalasMuted, style = MaterialTheme.typography.bodyMedium)
            }
            escala.local?.let {
                Text(text = it, color = EscalasInk, style = MaterialTheme.typography.bodyMedium)
            }
            escala.descricao?.let {
                Text(text = it, color = EscalasMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EscalasScreenPreview() {
    AgeRunTheme {
        EscalasScreen(
            authResponse = AuthResponse(
                user = AuthUser("1", "Vinicius", "professor@agerun.com", "professor"),
                session = AuthSession("preview", "preview"),
            ),
        )
    }
}
