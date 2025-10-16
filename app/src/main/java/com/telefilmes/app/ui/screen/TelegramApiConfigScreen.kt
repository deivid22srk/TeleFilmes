package com.telefilmes.app.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramApiConfigScreen(
    currentApiId: Int?,
    currentApiHash: String?,
    hasCustomCredentials: Boolean,
    onSave: (apiId: Int, apiHash: String) -> Unit,
    onBackClick: () -> Unit
) {
    var apiId by remember { mutableStateOf(currentApiId?.toString() ?: "") }
    var apiHash by remember { mutableStateOf(currentApiHash ?: "") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar API do Telegram") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alerta informativo
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Por que preciso disso?",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Para se conectar ao Telegram, você precisa de credenciais próprias da API. Isso é gratuito e seguro!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // Status atual
            if (hasCustomCredentials) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "✅ Credenciais configuradas",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Você pode atualizar suas credenciais abaixo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚠️ Credenciais não configuradas",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Configure suas credenciais para usar o Telegram",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Instruções
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Como obter suas credenciais:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("1. Acesse o site do Telegram", style = MaterialTheme.typography.bodyMedium)
                    Text("2. Faça login com seu número", style = MaterialTheme.typography.bodyMedium)
                    Text("3. Clique em 'API development tools'", style = MaterialTheme.typography.bodyMedium)
                    Text("4. Preencha o formulário e crie sua aplicação", style = MaterialTheme.typography.bodyMedium)
                    Text("5. Copie o api_id e api_hash", style = MaterialTheme.typography.bodyMedium)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://my.telegram.org/apps"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abrir https://my.telegram.org/apps")
                    }
                }
            }
            
            // Campos de entrada
            Text(
                text = "Insira suas credenciais:",
                style = MaterialTheme.typography.titleMedium
            )
            
            OutlinedTextField(
                value = apiId,
                onValueChange = { 
                    if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                        apiId = it
                        showError = false
                    }
                },
                label = { Text("API ID") },
                placeholder = { Text("Ex: 12345678") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = showError && apiId.isEmpty(),
                supportingText = {
                    if (showError && apiId.isEmpty()) {
                        Text("Campo obrigatório")
                    } else {
                        Text("Número que você recebeu ao criar a aplicação")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = apiHash,
                onValueChange = { 
                    apiHash = it
                    showError = false
                },
                label = { Text("API Hash") },
                placeholder = { Text("Ex: a1b2c3d4e5f6g7h8...") },
                singleLine = true,
                isError = showError && apiHash.isEmpty(),
                supportingText = {
                    if (showError && apiHash.isEmpty()) {
                        Text("Campo obrigatório")
                    } else {
                        Text("Texto alfanumérico que você recebeu")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Mensagem de erro
            if (showError && errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            // Botão salvar
            Button(
                onClick = {
                    val apiIdInt = apiId.toIntOrNull()
                    if (apiIdInt == null || apiHash.isEmpty()) {
                        showError = true
                        errorMessage = "Preencha todos os campos corretamente"
                    } else if (apiHash.length < 32) {
                        showError = true
                        errorMessage = "API Hash parece estar incompleto (deve ter pelo menos 32 caracteres)"
                    } else {
                        onSave(apiIdInt, apiHash)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Credenciais")
            }
            
            // Aviso de segurança
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔒 Segurança",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Suas credenciais são armazenadas apenas localmente no seu dispositivo e nunca são compartilhadas.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Justify
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
