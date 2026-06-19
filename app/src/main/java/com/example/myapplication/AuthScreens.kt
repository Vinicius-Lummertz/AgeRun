package com.example.myapplication

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.Lime
import com.example.myapplication.ui.theme.PurpleBackground
import com.example.myapplication.ui.theme.PurpleDeep
import com.example.myapplication.ui.theme.PurpleSurface
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape

private enum class AuthFlow { Entry, TrainerOnboarding }

@Composable
fun RealAuthScreen(
    loading: Boolean,
    message: String?,
    onStartLogin: (String, (String) -> Unit) -> Unit,
    onStartLoginResult: (String, (String, String) -> Unit) -> Unit = { identifier, callback -> onStartLogin(identifier) { callback(it, "login") } },
    onVerifyLogin: (String, String) -> Unit,
    onRegisterInstructor: (String, String, String, (String) -> Unit) -> Unit,
    onVerifyInstructor: (String, String, String, Uri?, () -> Unit, () -> Unit) -> Unit,
    onStartStudent: (String, (String) -> Unit) -> Unit,
    onCompleteStudent: (String, String, String, Uri?, String) -> Unit
) {
    var flow by remember { mutableStateOf(AuthFlow.Entry) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
            .statusBarsPadding()
            .imePadding()
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(34.dp))
            Image(painterResource(R.drawable.logo_sem_fundo), "AgeGo", Modifier.size(94.dp))
            Text("AgeGo", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text("Acesso por token, sem senha", color = Color.White.copy(alpha = .62f), fontSize = 13.sp)
            Spacer(Modifier.height(28.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                color = PurpleDeep,
                shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (flow == AuthFlow.Entry) {
                        EntryPane(
                            loading = loading,
                            onStartLogin = onStartLoginResult,
                            onVerifyLogin = onVerifyLogin,
                            onCompleteStudent = onCompleteStudent,
                            onNewTrainer = { flow = AuthFlow.TrainerOnboarding }
                        )
                    } else {
                        TrainerOnboardingPane(
                            loading = loading,
                            onBack = { flow = AuthFlow.Entry },
                            onRegisterInstructor = onRegisterInstructor,
                            onVerifyInstructor = { email, token, displayName, photoUri, onSuccess, onInvalidToken ->
                                onVerifyInstructor(email, token, displayName, photoUri, onSuccess) {
                                    flow = AuthFlow.Entry
                                    onInvalidToken()
                                }
                            }
                        )
                    }
                    if (!message.isNullOrBlank()) {
                        Text(message, color = Lime, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun EntryPane(
    loading: Boolean,
    onStartLogin: (String, (String, String) -> Unit) -> Unit,
    onVerifyLogin: (String, String) -> Unit,
    onCompleteStudent: (String, String, String, Uri?, String) -> Unit,
    onNewTrainer: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var accessToken by remember { mutableStateOf("") }
    var tokenRequested by remember { mutableStateOf(false) }
    var studentFirstAccess by remember { mutableStateOf(false) }
    var studentStep by remember { mutableIntStateOf(0) }
    var studentEmail by remember { mutableStateOf("") }
    var studentNickname by remember { mutableStateOf("") }
    var studentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var studentToken by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Entrar", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        AuthField(identifier, { identifier = it }, "Email ou telefone", KeyboardType.Email, Icons.Outlined.AlternateEmail)
        if (tokenRequested) {
            AuthField(accessToken, { accessToken = it.filter(Char::isDigit).take(6) }, "Token de 6 digitos", KeyboardType.Number, Icons.Outlined.Phone)
        }
        AuthButton(
            label = if (tokenRequested) "Entrar" else "Acessar",
            loading = loading,
            enabled = identifier.length >= 3 && (!tokenRequested || accessToken.length == 6)
        ) {
            if (tokenRequested) {
                onVerifyLogin(identifier, accessToken)
            } else {
                onStartLogin(identifier) { _, nextStep ->
                    studentFirstAccess = nextStep == "student_first_access"
                    studentStep = 0
                    tokenRequested = nextStep != "student_first_access"
                }
            }
        }

        if (studentFirstAccess) {
            Surface(color = PurpleSurface, shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (studentStep > 0) {
                            IconButton(onClick = { studentStep -= 1 }) {
                                Icon(Icons.Outlined.ArrowBack, "Voltar", tint = Color.White)
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Primeiro acesso do aluno", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                listOf("Seu email", "Seu perfil", "Verifique o token")[studentStep],
                                color = Lime,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text("${studentStep + 1}/3", color = Lime, fontWeight = FontWeight.Bold)
                    }
                    Text("Telefone reconhecido pelo cadastro do professor.", color = Color.White.copy(alpha = .58f), fontSize = 12.sp)
                    when (studentStep) {
                        0 -> {
                            AuthField(studentEmail, { studentEmail = it }, "Email", KeyboardType.Email, Icons.Outlined.AlternateEmail)
                            AuthButton("Continuar", loading, studentEmail.contains("@")) { studentStep = 1 }
                        }
                        1 -> {
                            AuthField(studentNickname, { studentNickname = it }, "Apelido", KeyboardType.Text, Icons.Outlined.AccountCircle)
                            PhotoPickerRow(photoUri = studentPhotoUri, onPhotoSelected = { studentPhotoUri = it })
                            AuthButton("Continuar", loading, studentNickname.length > 1) { studentStep = 2 }
                        }
                        2 -> {
                            Text("Digite o codigo de 6 digitos que seu professor enviou. Ele vale por 24h.", color = Color.White.copy(alpha = .68f), fontSize = 13.sp)
                            AuthField(studentToken, { studentToken = it.filter(Char::isDigit).take(6) }, "Token de 6 digitos", KeyboardType.Number, Icons.Outlined.Phone)
                            AuthButton("Concluir e entrar", loading, studentToken.length == 6) {
                                onCompleteStudent(identifier, studentEmail, studentNickname, studentPhotoUri, studentToken)
                            }
                        }
                    }
                }
            }
        }

        TextButton(onClick = onNewTrainer, modifier = Modifier.fillMaxWidth()) {
            Text("Novo treinador?", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TrainerOnboardingPane(
    loading: Boolean,
    onBack: () -> Unit,
    onRegisterInstructor: (String, String, String, (String) -> Unit) -> Unit,
    onVerifyInstructor: (String, String, String, Uri?, () -> Unit, () -> Unit) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var profileName by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val titles = listOf("Qual seu nome?", "Contato profissional", "Verifique seu email", "Personalize seu perfil")

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (step == 0) onBack() else step -= 1 }) {
                Icon(Icons.Outlined.ArrowBack, "Voltar", tint = Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text("Novo treinador?", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(titles[step], color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text("${step + 1}/4", color = Lime, fontWeight = FontWeight.Bold)
        }

        when (step) {
            0 -> {
                AuthField(name, { name = it }, "Nome completo", KeyboardType.Text, Icons.Outlined.AccountCircle)
                AuthButton("Continuar", loading, name.length > 1) {
                    profileName = name
                    step = 1
                }
            }
            1 -> {
                AuthField(email, { email = it }, "Email", KeyboardType.Email, Icons.Outlined.AlternateEmail)
                AuthField(phone, { phone = it }, "Telefone", KeyboardType.Phone, Icons.Outlined.Phone)
                AuthButton("Gerar token", loading, email.contains("@") && phone.length >= 6) {
                    onRegisterInstructor(name, email, phone) { step = 2 }
                }
            }
            2 -> {
                Text("Enviamos um token de 6 digitos por notificacao local.", color = Color.White.copy(alpha = .68f), fontSize = 13.sp)
                AuthField(token, { token = it.filter(Char::isDigit).take(6) }, "Token", KeyboardType.Number, Icons.Outlined.Phone)
                AuthButton("Continuar", loading, token.length == 6) { step = 3 }
            }
            3 -> {
                AuthField(profileName, { profileName = it }, "Nome exibido", KeyboardType.Text, Icons.Outlined.AccountCircle)
                PhotoPickerRow(photoUri = photoUri, onPhotoSelected = { photoUri = it })
                AuthButton("Entrar na plataforma", loading, profileName.length > 1) {
                    onVerifyInstructor(email, token, profileName, photoUri, {}, {
                        step = 0
                        name = ""
                        email = ""
                        phone = ""
                        token = ""
                        profileName = ""
                        photoUri = null
                    })
                }
            }
        }
    }
}

@Composable
private fun PhotoPickerRow(photoUri: Uri?, onPhotoSelected: (Uri?) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onPhotoSelected(uri)
    }
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { launcher.launch("image/*") },
        color = PurpleSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(PurpleDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Image, contentDescription = null, tint = Lime)
                }
            }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text("Foto de perfil", color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    if (photoUri == null) "Escolher arquivo do dispositivo" else "Imagem selecionada",
                    color = Color.White.copy(alpha = .62f),
                    fontSize = 12.sp
                )
            }
            if (photoUri != null) {
                TextButton(onClick = { onPhotoSelected(null) }) {
                    Text("Trocar", color = Lime, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        leadingIcon = { Icon(icon, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Lime,
            unfocusedBorderColor = Color.White.copy(alpha = .22f),
            focusedLabelColor = Lime,
            unfocusedLabelColor = Color.White.copy(alpha = .62f),
            cursorColor = Lime,
            focusedLeadingIconColor = Lime,
            unfocusedLeadingIconColor = Color.White.copy(alpha = .62f)
        )
    )
}

@Composable
private fun AuthButton(label: String, loading: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PurpleDeep)
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = PurpleDeep)
        else {
            Text(label, fontWeight = FontWeight.Bold)
            if (label.startsWith("Entrar")) Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.padding(start = 8.dp).size(18.dp))
        }
    }
}
