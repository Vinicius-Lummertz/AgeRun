package com.exemplo.agerun

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

private val RecadosBackground = Color(0xFFF6F7F2)
private val RecadosInk = Color(0xFF10221C)
private val RecadosMuted = Color(0xFF587069)
private val RecadosGreen = Color(0xFF0B6B3A)
private val RecadosSurface = Color.White
private val RecadosLine = Color(0xFFD7E3DA)

@Composable
fun RecadosScreen(
    authResponse: AuthResponse,
    modifier: Modifier = Modifier,
) {
    val accessToken = authResponse.session?.accessToken.orEmpty()
    val isProfessor = authResponse.user.role == "professor"
    val coroutineScope = rememberCoroutineScope()
    var recados by remember { mutableStateOf<List<Recado>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    fun loadRecados() {
        if (accessToken.isBlank()) return

        isLoading = true
        coroutineScope.launch {
            AuthApi.listarRecados(accessToken)
                .onSuccess {
                    recados = it
                    feedback = null
                }
                .onFailure {
                    feedback = it.message ?: "Nao foi possivel carregar os recados."
                }
            isLoading = false
        }
    }

    LaunchedEffect(accessToken) {
        loadRecados()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = RecadosBackground,
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
                Column {
                    Text(
                        text = "Recados",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = RecadosInk,
                    )
                    Text(
                        text = if (isProfessor) "Comunicados para a turma." else "Avisos enviados pelos professores.",
                        color = RecadosMuted,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                if (isProfessor) {
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = { showCreateDialog = true },
                    ) {
                        Text(text = "Novo", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${recados.size} ${if (recados.size == 1) "recado" else "recados"}",
                    color = RecadosMuted,
                    fontWeight = FontWeight.Bold,
                )
                TextButton(onClick = { loadRecados() }) {
                    Text(text = "Atualizar", color = RecadosGreen, fontWeight = FontWeight.Bold)
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
                    Text(text = "Carregando recados...", color = RecadosMuted)
                }
            } else if (recados.isEmpty()) {
                EmptyRecadosCard(isProfessor = isProfessor)
            } else {
                recados.forEach { recado ->
                    RecadoCard(
                        recado = recado,
                        isProfessor = isProfessor,
                        onArchive = {
                            coroutineScope.launch {
                                AuthApi.arquivarRecado(accessToken, recado.id)
                                    .onSuccess { loadRecados() }
                                    .onFailure { feedback = it.message ?: "Nao foi possivel arquivar o recado." }
                            }
                        },
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateRecadoDialog(
            accessToken = accessToken,
            onDismiss = { showCreateDialog = false },
            onCreated = {
                showCreateDialog = false
                loadRecados()
            },
        )
    }
}

@Composable
private fun EmptyRecadosCard(isProfessor: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = RecadosSurface),
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = if (isProfessor) "Nenhum recado criado ainda." else "Nenhum recado novo por enquanto.",
            color = RecadosMuted,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun RecadoCard(
    recado: Recado,
    isProfessor: Boolean,
    onArchive: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = RecadosSurface),
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
                    modifier = Modifier.weight(1f),
                    text = recado.titulo,
                    color = RecadosInk,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium,
                )
                PriorityPill(prioridade = recado.prioridade)
            }
            Text(text = recado.mensagem, color = RecadosInk)
            Text(
                text = formatDateTime(recado.createdAt),
                color = RecadosMuted,
                style = MaterialTheme.typography.bodySmall,
            )
            if (recado.fixado) {
                Text(
                    text = "Fixado no topo",
                    color = RecadosGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (isProfessor && recado.ativo) {
                TextButton(onClick = onArchive) {
                    Text(text = "Arquivar", color = RecadosGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PriorityPill(prioridade: String) {
    val color = when (prioridade) {
        "urgente" -> Color(0xFFB42318)
        "importante" -> Color(0xFFB54708)
        else -> RecadosGreen
    }

    Surface(
        color = color.copy(alpha = 0.11f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = prioridade,
            color = color,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun CreateRecadoDialog(
    accessToken: String,
    onDismiss: () -> Unit,
    onCreated: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var titulo by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var prioridade by remember { mutableStateOf("normal") }
    var fixado by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) onDismiss()
        },
        title = {
            Text(text = "Novo recado", color = RecadosInk, fontWeight = FontWeight.Black)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Titulo") },
                    singleLine = true,
                    colors = recadoTextFieldColors(),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = mensagem,
                    onValueChange = { mensagem = it },
                    label = { Text("Mensagem") },
                    minLines = 4,
                    colors = recadoTextFieldColors(),
                )
                PrioritySelector(
                    selected = prioridade,
                    onSelected = { prioridade = it },
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { fixado = !fixado },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (fixado) RecadosGreen else Color(0xFFE8EFEA),
                        contentColor = if (fixado) Color.White else RecadosInk,
                    ),
                ) {
                    Text(
                        text = if (fixado) "Fixado no topo" else "Fixar no topo",
                        fontWeight = FontWeight.Bold,
                    )
                }

                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving,
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    isSaving = true
                    error = null
                    coroutineScope.launch {
                        AuthApi.criarRecado(
                            accessToken = accessToken,
                            titulo = titulo,
                            mensagem = mensagem,
                            prioridade = prioridade,
                            fixado = fixado,
                        )
                            .onSuccess { onCreated() }
                            .onFailure { error = it.message ?: "Nao foi possivel criar o recado." }
                        isSaving = false
                    }
                },
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(text = "Publicar", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(enabled = !isSaving, onClick = onDismiss) {
                Text(text = "Cancelar", color = RecadosGreen, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = RecadosSurface,
        shape = RoundedCornerShape(8.dp),
    )
}

@Composable
private fun PrioritySelector(
    selected: String,
    onSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Prioridade",
            color = RecadosMuted,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("normal", "importante", "urgente").forEach { option ->
                val isSelected = selected == option
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { onSelected(option) },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) RecadosGreen else Color(0xFFE8EFEA),
                        contentColor = if (isSelected) Color.White else RecadosInk,
                    ),
                ) {
                    Text(text = option, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun recadoTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = RecadosInk,
    unfocusedTextColor = RecadosInk,
    cursorColor = RecadosGreen,
    focusedBorderColor = RecadosGreen,
    unfocusedBorderColor = RecadosLine,
    focusedLabelColor = RecadosGreen,
    unfocusedLabelColor = RecadosMuted,
    focusedContainerColor = Color(0xFFFBFCF8),
    unfocusedContainerColor = Color(0xFFFBFCF8),
)

@Preview(showBackground = true)
@Composable
private fun RecadosScreenPreview() {
    AgeRunTheme {
        RecadosScreen(
            authResponse = AuthResponse(
                user = AuthUser("1", "Vinicius", "professor@agerun.com", "professor"),
                session = AuthSession("preview", "preview"),
            ),
        )
    }
}
