package com.exemplo.agerun

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.ui.theme.AgeRunTheme
import kotlinx.coroutines.launch

private val AuthBackground = Color(0xFFF6F7F2)
private val AuthInk = Color(0xFF10221C)
private val AuthMuted = Color(0xFF587069)
private val AuthGreen = Color(0xFF0B6B3A)
private val AuthFieldBorder = Color(0xFFB8C7BF)
private val AuthFieldFill = Color(0xFFFBFCF8)

private enum class AuthMode {
    Login,
    Cadastro,
}

@Composable
fun AuthScreen(
    onAuthenticated: (AuthResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    var mode by remember { mutableStateOf(AuthMode.Login) }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    fun submit() {
        isLoading = true
        message = null
        isError = false

        coroutineScope.launch {
            val result = when (mode) {
                AuthMode.Login -> AuthApi.login(email.trim(), senha)
                AuthMode.Cadastro -> AuthApi.cadastro(nome.trim(), email.trim(), senha)
            }

            isLoading = false

            result
                .onSuccess { response ->
                    if (mode == AuthMode.Cadastro) {
                        mode = AuthMode.Login
                        senha = ""
                        message = "Cadastro criado. Agora entre com email e senha."
                        isError = false
                    } else {
                        onAuthenticated(response)
                    }
                }
                .onFailure { throwable ->
                    message = throwable.message ?: "Nao foi possivel completar a operacao."
                    isError = true
                }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AuthBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "AgeRun",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = AuthInk,
            )
            Text(
                text = if (mode == AuthMode.Login) "Entre para ver suas escalas." else "Crie sua conta de aluno.",
                style = MaterialTheme.typography.titleMedium,
                color = AuthMuted,
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (mode == AuthMode.Cadastro) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = nome,
                            onValueChange = { nome = it },
                            label = { Text("Nome") },
                            colors = authTextFieldColors(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        )
                    }

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        colors = authTextFieldColors(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = senha,
                        onValueChange = { senha = it },
                        label = { Text("Senha") },
                        colors = authTextFieldColors(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    )

                    message?.let {
                        Text(
                            text = it,
                            color = if (isError) MaterialTheme.colorScheme.error else AuthGreen,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isLoading,
                        onClick = { submit() },
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .height(22.dp)
                                    .background(Color.Transparent),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = if (mode == AuthMode.Login) "entrar" else "cadastrar",
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        onClick = {
                            mode = if (mode == AuthMode.Login) AuthMode.Cadastro else AuthMode.Login
                            message = null
                            isError = false
                        },
                    ) {
                        Text(
                            color = AuthGreen,
                            fontWeight = FontWeight.Bold,
                            text = if (mode == AuthMode.Login) {
                                "Criar uma conta"
                            } else {
                                "Ja tenho conta"
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = AuthInk,
    unfocusedTextColor = AuthInk,
    disabledTextColor = AuthMuted,
    cursorColor = AuthGreen,
    focusedBorderColor = AuthGreen,
    unfocusedBorderColor = AuthFieldBorder,
    focusedLabelColor = AuthGreen,
    unfocusedLabelColor = AuthMuted,
    focusedContainerColor = AuthFieldFill,
    unfocusedContainerColor = AuthFieldFill,
)

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    AgeRunTheme {
        AuthScreen(onAuthenticated = {})
    }
}
