# Guia: Adicionando TDLib Real ao TeleFilmes

Este documento explica como substituir a implementação mock pelo TDLib real para conectar ao Telegram.

## 📋 Pré-requisitos

- Android NDK instalado
- CMake (para compilar TDLib)
- Python 3 (para scripts de build)
- Git

## 🔧 Opção 1: Compilar TDLib do Zero

### 1. Clone o TDLib

```bash
git clone https://github.com/tdlib/td.git
cd td
```

### 2. Compile para Android

```bash
# Instale as dependências (Linux/Mac)
sudo apt-get install make git zlib1g-dev libssl-dev gperf php-cli cmake clang-14 libc++-14-dev libc++abi-14-dev

# Compile para Android
cd example/android
./build-tdlib.sh

# Os arquivos compilados estarão em:
# example/android/tdlib/
```

### 3. Copie as Bibliotecas Nativas

```bash
# Copie para o projeto TeleFilmes
cp -r td/example/android/tdlib/libs/* TeleFilmes/app/src/main/jniLibs/
```

### 4. Adicione o JAR do TDLib

```bash
# Crie pasta libs se não existir
mkdir -p TeleFilmes/app/libs

# Copie o JAR
cp td/example/android/tdlib/tdlib.jar TeleFilmes/app/libs/
```

### 5. Atualize build.gradle.kts

```kotlin
dependencies {
    // Descomente e adicione:
    implementation(files("libs/tdlib.jar"))
    
    // Remova o comentário da linha original:
    // implementation("org.drinkless:tdlib:1.8.0")
}
```

## 🚀 Opção 2: Usar Binários Pré-compilados

### 1. Baixe Binários

Visite: https://github.com/tdlib/td#using-in-android-projects

Ou use esta biblioteca wrapper:
```kotlin
implementation 'org.telegram:tdlib:1.8.30'
```

### 2. Configure no projeto

Adicione no `build.gradle.kts`:

```kotlin
android {
    // ...
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
}
```

## 📝 Opção 3: Usar Telegram Bot API (Mais Simples)

### 1. Adicione Retrofit

```kotlin
// Em build.gradle.kts
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
```

### 2. Crie Interface da API

```kotlin
interface TelegramBotApi {
    @GET("getMe")
    suspend fun getMe(@Query("token") token: String): Response
    
    @GET("getUpdates")
    suspend fun getUpdates(
        @Query("token") token: String,
        @Query("offset") offset: Long
    ): UpdatesResponse
}
```

### 3. Implemente Cliente

Modifique `TelegramClient.kt` para usar a Bot API ao invés do TDLib.

**Limitações da Bot API:**
- Não tem acesso aos chats pessoais do usuário
- Funciona apenas com bots
- Não pode salvar mensagens de chats privados

## 🔄 Restaurar Implementação Real

### 1. Em TeleFilmesApplication.kt

Descomente:
```kotlin
override fun onCreate() {
    super.onCreate()
    System.loadLibrary("tdjni") // ← Descomente esta linha
}
```

### 2. Em TelegramClient.kt

Substitua a implementação mock pela versão original que usa TDLib. Você pode recuperar do histórico Git:

```bash
git show 6f7de7a:app/src/main/java/com/telefilmes/app/telegram/TelegramClient.kt > TelegramClient_original.kt
```

### 3. Obtenha Credenciais da API

1. Acesse: https://my.telegram.org/apps
2. Crie um aplicativo
3. Anote seu **API ID** e **API Hash**
4. Adicione no código:

```kotlin
parameters.apply {
    apiId = SEU_API_ID_AQUI
    apiHash = "SEU_API_HASH_AQUI"
}
```

## ✅ Verificação

Após configurar o TDLib:

1. Compile o projeto:
```bash
./gradlew assembleDebug
```

2. Instale no dispositivo:
```bash
./gradlew installDebug
```

3. Teste o login com seu número do Telegram

## 🆘 Resolução de Problemas

### Erro: "couldn't find libtdjni.so"

**Solução:** Certifique-se de que os arquivos `.so` estão em:
```
app/src/main/jniLibs/
├── arm64-v8a/
│   └── libtdjni.so
├── armeabi-v7a/
│   └── libtdjni.so
├── x86/
│   └── libtdjni.so
└── x86_64/
    └── libtdjni.so
```

### Erro: "UnsatisfiedLinkError"

**Solução:** Recompile o TDLib ou baixe binários compatíveis com sua versão do NDK.

### Erro de Autenticação

**Solução:** Verifique se API ID e API Hash estão corretos.

## 📚 Recursos

- **TDLib GitHub**: https://github.com/tdlib/td
- **TDLib Docs**: https://core.telegram.org/tdlib
- **Android Example**: https://github.com/tdlib/td/tree/master/example/android
- **Telegram API**: https://my.telegram.org/apps

## 💡 Dica

Para desenvolvimento rápido, recomendo começar com a implementação mock (atual) para testar a UI, e depois adicionar o TDLib real quando o app estiver mais maduro.
