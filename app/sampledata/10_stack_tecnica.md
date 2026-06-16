# 10 — Stack Técnica e Arquitetura

## 10.1 Stack Principal

| Camada | Tecnologia | Justificativa |
|---|---|---|
| **Mobile** | Kotlin + Android Studio | Linguagem oficial Android, performance nativa, ecossistema maduro |
| **UI Framework** | Jetpack Compose ou XML Views + ViewBinding | XML para compatibilidade ampla; Compose para novos módulos |
| **Arquitetura Android** | MVVM + Clean Architecture | Testabilidade, separação de responsabilidades, padrão Android |
| **Navegação** | Jetpack Navigation Component | Single Activity, deep links, back stack gerenciado |
| **Injeção de Dependência** | Hilt (Dagger2) | Padrão Google, integrado com Jetpack |
| **Async** | Kotlin Coroutines + Flow | Nativo Kotlin, integração com Supabase SDK |
| **Backend** | Node.js (Express ou Fastify) | Familiaridade da equipe, ecossistema npm |
| **Banco de Dados** | Supabase (PostgreSQL) | BaaS completo: auth, realtime, storage, RLS |
| **Cache Local** | Room Database | ORM oficial Android, integração com Coroutines |
| **Notificações** | Firebase Cloud Messaging (FCM) | Padrão de mercado para Android |

---

## 10.2 Supabase — Configuração

### Serviços utilizados

| Serviço Supabase | Uso no app |
|---|---|
| **Auth** | Autenticação de usuários (email/senha) |
| **Database** | Todas as tabelas relacionais |
| **Row Level Security** | Isolamento de dados por usuário |
| **Storage** | Armazenamento de avatares e imagens |
| **Realtime** | Feed de comunicados em tempo real |
| **Edge Functions** | Lógica de negócio complexa (cobranças, notificações) |

### Configuração no Android (Supabase Kotlin SDK)

```kotlin
// build.gradle (app)
implementation(platform("io.github.jan-tennert.supabase:bom:2.0.0"))
implementation("io.github.jan-tennert.supabase:postgrest-kt")
implementation("io.github.jan-tennert.supabase:auth-kt")
implementation("io.github.jan-tennert.supabase:realtime-kt")
implementation("io.github.jan-tennert.supabase:storage-kt")
implementation("io.ktor:ktor-client-android:2.3.7")

// SupabaseModule.kt (Hilt)
@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
            install(Storage)
        }
    }
}
```

---

## 10.3 Estrutura de Pastas (Android)

```
app/src/main/
├── java/com/agego/app/
│   ├── AgeGoApplication.kt          # Application class (Hilt)
│   ├── MainActivity.kt              # Single Activity
│   │
│   ├── data/
│   │   ├── local/
│   │   │   ├── database/
│   │   │   │   ├── AgeGoDatabase.kt
│   │   │   │   └── dao/
│   │   │   │       ├── WorkoutDao.kt
│   │   │   │       └── WorkoutLogDao.kt
│   │   │   └── entity/              # Room entities
│   │   ├── remote/
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   └── datasource/          # Supabase calls
│   │   │       ├── AuthDataSource.kt
│   │   │       ├── StudentDataSource.kt
│   │   │       ├── WorkoutDataSource.kt
│   │   │       └── AnnouncementDataSource.kt
│   │   └── repository/
│   │       ├── AuthRepositoryImpl.kt
│   │       ├── StudentRepositoryImpl.kt
│   │       ├── WorkoutRepositoryImpl.kt
│   │       └── AnnouncementRepositoryImpl.kt
│   │
│   ├── domain/
│   │   ├── model/                   # Entidades de domínio puras
│   │   │   ├── User.kt
│   │   │   ├── Student.kt
│   │   │   ├── Workout.kt
│   │   │   ├── WorkoutActivity.kt
│   │   │   ├── Plan.kt
│   │   │   ├── Group.kt
│   │   │   └── Announcement.kt
│   │   ├── repository/              # Interfaces
│   │   │   ├── AuthRepository.kt
│   │   │   ├── StudentRepository.kt
│   │   │   └── WorkoutRepository.kt
│   │   └── usecase/
│   │       ├── auth/
│   │       │   ├── LoginUseCase.kt
│   │       │   └── RegisterUseCase.kt
│   │       ├── student/
│   │       │   ├── GetStudentsUseCase.kt
│   │       │   ├── CreateStudentUseCase.kt
│   │       │   └── UpdateStudentStatusUseCase.kt
│   │       ├── workout/
│   │       │   ├── CreateWorkoutUseCase.kt
│   │       │   ├── GetWorkoutsUseCase.kt
│   │       │   └── CompleteWorkoutUseCase.kt
│   │       └── announcement/
│   │           └── PublishAnnouncementUseCase.kt
│   │
│   ├── presentation/
│   │   ├── auth/
│   │   │   ├── LoginFragment.kt
│   │   │   ├── LoginViewModel.kt
│   │   │   ├── RegisterFragment.kt
│   │   │   └── RegisterViewModel.kt
│   │   ├── home/
│   │   │   ├── HomeInstructorFragment.kt
│   │   │   ├── HomeInstructorViewModel.kt
│   │   │   ├── HomeStudentFragment.kt
│   │   │   └── HomeStudentViewModel.kt
│   │   ├── students/
│   │   │   ├── list/
│   │   │   ├── profile/
│   │   │   └── create/
│   │   ├── workouts/
│   │   │   ├── list/
│   │   │   ├── detail/
│   │   │   └── create/
│   │   ├── groups/
│   │   ├── announcements/
│   │   ├── profile/
│   │   └── running/               # Fase 2
│   │
│   └── utils/
│       ├── extensions/
│       │   ├── ViewExtensions.kt
│       │   └── StringExtensions.kt
│       ├── Result.kt               # sealed class Result<T>
│       ├── Constants.kt
│       └── DateUtils.kt
│
└── res/
    ├── layout/
    ├── drawable/
    ├── values/
    │   ├── colors.xml
    │   ├── themes.xml
    │   ├── strings.xml
    │   ├── dimens.xml
    │   └── typography.xml
    └── navigation/
        ├── nav_main.xml
        └── nav_auth.xml
```

---

## 10.4 Padrão Result para Chamadas Assíncronas

```kotlin
// utils/Result.kt
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Extensão de uso no ViewModel
fun <T> Flow<Result<T>>.collectState(
    viewModelScope: CoroutineScope,
    onLoading: () -> Unit = {},
    onSuccess: (T) -> Unit,
    onError: (String) -> Unit
) {
    viewModelScope.launch {
        collect { result ->
            when (result) {
                is Result.Loading -> onLoading()
                is Result.Success -> onSuccess(result.data)
                is Result.Error -> onError(result.message ?: "Erro inesperado")
            }
        }
    }
}
```

---

## 10.5 Backend Node.js — Estrutura

```
backend/
├── src/
│   ├── config/
│   │   ├── supabase.ts          # Supabase admin client
│   │   └── env.ts               # Variáveis de ambiente
│   ├── routes/
│   │   ├── auth.ts
│   │   ├── students.ts
│   │   ├── workouts.ts
│   │   ├── groups.ts
│   │   ├── announcements.ts
│   │   └── notifications.ts
│   ├── services/
│   │   ├── notificationService.ts   # FCM
│   │   ├── emailService.ts          # Envio de convites
│   │   └── paymentService.ts        # Fase 3
│   ├── middleware/
│   │   ├── auth.ts                  # Verificação JWT
│   │   └── roleGuard.ts             # Verificação de perfil
│   └── app.ts
└── package.json
```

---

## 10.6 Variáveis de Ambiente

### Android (local.properties / BuildConfig)
```
SUPABASE_URL=https://xxxxxxxxxxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
FCM_SENDER_ID=123456789
```

### Backend (.env)
```
PORT=3000
NODE_ENV=development
SUPABASE_URL=https://xxxxxxxxxxx.supabase.co
SUPABASE_SERVICE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
FCM_SERVER_KEY=AAAA...
SMTP_HOST=smtp.sendgrid.net
SMTP_KEY=SG.xxxx
```

---

## 10.7 Segurança

| Área | Medida |
|---|---|
| Armazenamento de token | EncryptedSharedPreferences (Android Keystore) |
| Comunicação | HTTPS obrigatório, certificate pinning (produção) |
| API Keys | Nunca em repositórios, usar BuildConfig + CI secrets |
| Supabase RLS | Policies ativas em todas as tabelas com dados sensíveis |
| Senhas | Hash via bcrypt (Supabase Auth gerencia) |
| LGPD | Política de privacidade, opt-in para coleta de GPS, exclusão de conta disponível |

---

## 10.8 Performance

| Área | Estratégia |
|---|---|
| Listas | RecyclerView com DiffUtil para atualizações eficientes |
| Imagens | Glide com cache de disco e memória |
| Chamadas de API | Paginação em listas grandes (Paging 3) |
| Dados locais | Room com índices otimizados |
| UI thread | Coroutines com Dispatchers.IO para I/O |
| Build | R8 habilitado, multidex se necessário |

---

## 10.9 Testes

| Tipo | Ferramentas | Cobertura alvo |
|---|---|---|
| Unit Tests | JUnit 5 + MockK | UseCases, ViewModels, Utils |
| Integration Tests | Supabase emulator | Repositórios |
| UI Tests | Espresso | Fluxos críticos (login, cadastro, treino) |
| End-to-End | Manual + Maestro | Jornada completa do usuário |

---

## 10.10 CI/CD Sugerido

```yaml
# .github/workflows/android.yml
name: Android CI

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with: { java-version: '17' }
      - run: ./gradlew test
      - run: ./gradlew lint

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew assembleRelease
      - uses: actions/upload-artifact@v3
        with:
          name: release-apk
          path: app/build/outputs/apk/release/
```
