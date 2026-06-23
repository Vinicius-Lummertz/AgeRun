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

private enum class AuthFlow { Entry, StudentOnboarding, TrainerOnboarding, InviteOnboarding }

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
    onCompleteStudent: (String, String, String, Uri?, List<Int>, Int, String) -> Unit,
    onCompleteInviteStudent: (String, String, String, String, Uri?, List<Int>, Int) -> Unit = { _, _, _, _, _, _, _ -> },
    initialStudentPhone: String? = null,
    initialStudentAccessToken: String? = null,
    initialInviteCode: String? = null
) {
    var flow by remember(initialStudentPhone, initialInviteCode) {
        mutableStateOf(
            when {
                !initialStudentPhone.isNullOrBlank() -> AuthFlow.StudentOnboarding
                !initialInviteCode.isNullOrBlank() -> AuthFlow.InviteOnboarding
                else -> AuthFlow.Entry
            }
        )
    }
    var studentPhone by remember(initialStudentPhone) { mutableStateOf(initialStudentPhone.orEmpty()) }

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
                    when (flow) {
                        AuthFlow.Entry -> EntryPane(
                            loading = loading,
                            onStartLogin = onStartLoginResult,
                            onVerifyLogin = onVerifyLogin,
                            onNewStudent = { phone ->
                                studentPhone = phone
                                flow = AuthFlow.StudentOnboarding
                            },
                            onNewTrainer = { flow = AuthFlow.TrainerOnboarding }
                        )
                        AuthFlow.StudentOnboarding -> StudentOnboardingPane(
                            phone = studentPhone,
                            loading = loading,
                            onBack = { flow = AuthFlow.Entry },
                            onCompleteStudent = onCompleteStudent,
                            initialToken = initialStudentAccessToken.orEmpty()
                        )
                        AuthFlow.InviteOnboarding -> InviteStudentOnboardingPane(
                            inviteCode = initialInviteCode.orEmpty(),
                            loading = loading,
                            onBack = { flow = AuthFlow.Entry },
                            onCompleteInviteStudent = onCompleteInviteStudent
                        )
                        AuthFlow.TrainerOnboarding -> TrainerOnboardingPane(
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
    onNewStudent: (String) -> Unit,
    onNewTrainer: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var accessToken by remember { mutableStateOf("") }
    var tokenRequested by remember { mutableStateOf(false) }

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
                    if (nextStep == "student_first_access") onNewStudent(identifier)
                    else tokenRequested = true
                }
            }
        }

        TextButton(onClick = onNewTrainer, modifier = Modifier.fillMaxWidth()) {
            Text("Novo treinador?", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StudentOnboardingPane(
    phone: String,
    loading: Boolean,
    onBack: () -> Unit,
    onCompleteStudent: (String, String, String, Uri?, List<Int>, Int, String) -> Unit,
    initialToken: String = ""
) {
    var step by remember { mutableIntStateOf(0) }
    var email by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedDays by remember { mutableStateOf(setOf<Int>()) }
    var billingDay by remember { mutableIntStateOf(5) }
    var token by remember { mutableStateOf(initialToken) }
    val titles = listOf("Qual é o seu e-mail?", "Monte seu perfil", "Escolha seus dias", "Dia de pagamento", "PIN do professor")

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (step == 0) onBack() else step -= 1 }) {
                Icon(Icons.Outlined.ArrowBack, "Voltar", tint = Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text("Você é uma aluna nova", color = Lime, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(titles[step], color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text("${step + 1}/5", color = Lime, fontWeight = FontWeight.Bold)
        }
        Text(
            if (step == 0) "Reconhecemos o número $phone. Complete seu cadastro para acessar o treino preparado pelo seu professor."
            else "Seu cadastro está vinculado ao número $phone.",
            color = Color.White.copy(alpha = .62f),
            fontSize = 12.sp
        )

        when (step) {
            0 -> {
                AuthField(email, { email = it }, "E-mail", KeyboardType.Email, Icons.Outlined.AlternateEmail)
                AuthButton("Continuar", loading, email.contains("@") && email.substringAfter("@").contains(".")) { step = 1 }
            }
            1 -> {
                AuthField(nickname, { nickname = it }, "Nome ou apelido", KeyboardType.Text, Icons.Outlined.AccountCircle)
                PhotoPickerRow(photoUri = photoUri, onPhotoSelected = { photoUri = it })
                AuthButton("Continuar", loading, nickname.trim().length > 1) { step = 2 }
            }
            2 -> {
                Text("Em quais dias você pretende seguir seu treino?", color = Color.White.copy(alpha = .78f), fontSize = 13.sp)
                WeekDaySelector(selectedDays) { day ->
                    selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                }
                Text("Você poderá ajustar essa preferência depois.", color = Color.White.copy(alpha = .52f), fontSize = 12.sp)
                AuthButton("Continuar", loading, selectedDays.isNotEmpty()) { step = 3 }
            }
            3 -> {
                Text("Escolha o dia do mes em que prefere pagar a mensalidade.", color = Color.White.copy(alpha = .78f), fontSize = 13.sp)
                BillingDayPicker(billingDay) { billingDay = it }
                Text("Voce podera mudar essa data depois, no financeiro do app.", color = Color.White.copy(alpha = .52f), fontSize = 12.sp)
                AuthButton("Continuar", loading, true) { step = 4 }
            }
            4 -> {
                Text("Digite o PIN de 6 dígitos exibido no cadastro feito pelo seu professor. O PIN vale por 24 horas.", color = Color.White.copy(alpha = .72f), fontSize = 13.sp)
                AuthField(token, { token = it.filter(Char::isDigit).take(6) }, "PIN de 6 dígitos", KeyboardType.Number, Icons.Outlined.Phone)
                AuthButton("Concluir e entrar", loading, token.length == 6) {
                    onCompleteStudent(phone, email.trim(), nickname.trim(), photoUri, selectedDays.sorted(), billingDay, token)
                }
            }
        }
    }
}

@Composable
private fun InviteStudentOnboardingPane(
    inviteCode: String,
    loading: Boolean,
    onBack: () -> Unit,
    onCompleteInviteStudent: (String, String, String, String, Uri?, List<Int>, Int) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedDays by remember { mutableStateOf(setOf<Int>()) }
    var billingDay by remember { mutableIntStateOf(5) }
    val titles = listOf("Seu telefone", "Qual é o seu e-mail?", "Monte seu perfil", "Escolha seus dias", "Dia de pagamento")

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (step == 0) onBack() else step -= 1 }) {
                Icon(Icons.Outlined.ArrowBack, "Voltar", tint = Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text("Você foi convidada pelo professor", color = Lime, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(titles[step], color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text("${step + 1}/5", color = Lime, fontWeight = FontWeight.Bold)
        }
        Text(
            "Complete seu cadastro para acessar o treino preparado pelo seu professor.",
            color = Color.White.copy(alpha = .62f),
            fontSize = 12.sp
        )

        when (step) {
            0 -> {
                AuthField(phone, { phone = it.filter(Char::isDigit).take(15) }, "Telefone", KeyboardType.Phone, Icons.Outlined.Phone)
                AuthButton("Continuar", loading, phone.length >= 8) { step = 1 }
            }
            1 -> {
                AuthField(email, { email = it }, "E-mail", KeyboardType.Email, Icons.Outlined.AlternateEmail)
                AuthButton("Continuar", loading, email.contains("@") && email.substringAfter("@").contains(".")) { step = 2 }
            }
            2 -> {
                AuthField(nickname, { nickname = it }, "Nome ou apelido", KeyboardType.Text, Icons.Outlined.AccountCircle)
                PhotoPickerRow(photoUri = photoUri, onPhotoSelected = { photoUri = it })
                AuthButton("Continuar", loading, nickname.trim().length > 1) { step = 3 }
            }
            3 -> {
                Text("Em quais dias você pretende seguir seu treino?", color = Color.White.copy(alpha = .78f), fontSize = 13.sp)
                WeekDaySelector(selectedDays) { day ->
                    selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                }
                Text("Você poderá ajustar essa preferência depois.", color = Color.White.copy(alpha = .52f), fontSize = 12.sp)
                AuthButton("Continuar", loading, selectedDays.isNotEmpty()) { step = 4 }
            }
            4 -> {
                Text("Escolha o dia do mes em que prefere pagar a mensalidade.", color = Color.White.copy(alpha = .78f), fontSize = 13.sp)
                BillingDayPicker(billingDay) { billingDay = it }
                Text("Voce podera mudar essa data depois, no financeiro do app.", color = Color.White.copy(alpha = .52f), fontSize = 12.sp)
                AuthButton("Concluir e entrar", loading, true) {
                    onCompleteInviteStudent(inviteCode, phone.trim(), email.trim(), nickname.trim(), photoUri, selectedDays.sorted(), billingDay)
                }
            }
        }
    }
}

@Composable
private fun WeekDaySelector(selectedDays: Set<Int>, onToggle: (Int) -> Unit) {
    val days = listOf(1 to "Seg", 2 to "Ter", 3 to "Qua", 4 to "Qui", 5 to "Sex", 6 to "Sáb", 7 to "Dom")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        days.chunked(4).forEach { rowDays ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowDays.forEach { (value, label) ->
                    val selected = value in selectedDays
                    Surface(
                        modifier = Modifier.weight(1f).clickable { onToggle(value) },
                        color = if (selected) Lime else PurpleSurface,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            label,
                            modifier = Modifier.padding(vertical = 13.dp),
                            color = if (selected) PurpleDeep else Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                repeat(4 - rowDays.size) { Spacer(Modifier.weight(1f)) }
            }
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    maxLength: Int = when (keyboardType) {
        KeyboardType.Email -> 254
        KeyboardType.Phone -> 20
        KeyboardType.Number -> 6
        else -> 80
    }
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.take(maxLength)) },
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
