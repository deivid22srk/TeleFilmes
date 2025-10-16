# 🔐 Como Configurar suas Credenciais do Telegram

## ⚠️ Problema: UPDATE_APP_TO_LOGIN

O erro **406 - UPDATE_APP_TO_LOGIN** significa que as credenciais da API do Telegram estão desatualizadas ou inválidas.

## ✅ Solução: Obter suas Próprias Credenciais

### Passo 1: Criar Aplicação no Telegram

1. Acesse: **https://my.telegram.org/apps**
2. Faça login com seu número de telefone do Telegram
3. Clique em **"API development tools"**
4. Preencha o formulário:
   - **App title:** `TeleFilmes` (ou qualquer nome)
   - **Short name:** `telefilmes` (sem espaços)
   - **Platform:** `Android`
   - **Description:** `App para gerenciar filmes e séries do Telegram`
5. Clique em **"Create application"**

### Passo 2: Copiar as Credenciais

Você receberá:
- **api_id:** Um número (ex: 12345678)
- **api_hash:** Uma string (ex: "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6")

### Passo 3: Adicionar ao Projeto

1. Abra o arquivo: `app/src/main/java/com/telefilmes/app/telegram/TelegramClient.kt`

2. Substitua as linhas 34-35:
```kotlin
// ANTES (valores antigos/inválidos):
private const val API_ID = 94575
private const val API_HASH = "a3406de8d171bb422bb6ddf3bbd800e2"

// DEPOIS (suas credenciais):
private const val API_ID = SEU_API_ID_AQUI
private const val API_HASH = "SEU_API_HASH_AQUI"
```

### Passo 4: Recompilar e Testar

```bash
./gradlew assembleDebug
```

## 🔒 Importante

- **Nunca** compartilhe suas credenciais publicamente
- **Não** faça commit das credenciais no GitHub
- Se publicar o código, use variáveis de ambiente ou arquivo de configuração local

## 📝 Exemplo de `.gitignore`

Adicione ao `.gitignore` para proteger suas credenciais:

```
# Telegram API Keys
telegram_credentials.properties
local.properties
```

## 🆘 Problemas Comuns

### Erro persiste após adicionar credenciais?

1. Limpe o cache do app:
```bash
./gradlew clean
```

2. Desinstale o app do dispositivo
3. Reinstale e teste novamente

### Não recebe SMS de verificação?

- Verifique se o número está correto (incluindo código do país)
- Use o formato internacional: `+5587996156854`
- Tente usar "Ligar" em vez de SMS na tela de login do Telegram

## 📞 Suporte

Se ainda tiver problemas, verifique os logs com:
```bash
adb logcat -s TelegramClient:D
```
