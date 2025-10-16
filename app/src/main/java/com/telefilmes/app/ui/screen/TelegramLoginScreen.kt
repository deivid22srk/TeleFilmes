package com.telefilmes.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.telefilmes.app.R
import com.telefilmes.app.telegram.TelegramAuthState
import com.telefilmes.app.ui.viewmodel.TelegramViewModel

@Composable
fun TelegramLoginScreen(
    viewModel: TelegramViewModel,
    onLoginSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    
    LaunchedEffect(authState) {
        if (authState is TelegramAuthState.Authenticated) {
            onLoginSuccess()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val state = authState) {
            is TelegramAuthState.WaitingForPhoneNumber,
            is TelegramAuthState.Idle -> {
                PhoneNumberInput(
                    onSendCode = { phoneNumber ->
                        viewModel.setPhoneNumber(phoneNumber)
                    }
                )
            }
            is TelegramAuthState.WaitingForCode -> {
                CodeInput(
                    onVerifyCode = { code ->
                        viewModel.checkCode(code)
                    }
                )
            }
            is TelegramAuthState.WaitingForPassword -> {
                PasswordInput(
                    onVerifyPassword = { password ->
                        viewModel.checkPassword(password)
                    }
                )
            }
            is TelegramAuthState.Authenticated -> {
                CircularProgressIndicator()
            }
            is TelegramAuthState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = {}
                )
            }
        }
    }
}

@Composable
private fun PhoneNumberInput(
    onSendCode: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium
        )
        
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(stringResource(R.string.phone_number)) },
            placeholder = { Text(stringResource(R.string.phone_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = { 
                if (!isLoading) {
                    isLoading = true
                    onSendCode(phoneNumber)
                }
            },
            enabled = phoneNumber.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.send_code))
            }
        }
    }
}

@Composable
private fun CodeInput(
    onVerifyCode: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = stringResource(R.string.verification_code),
            style = MaterialTheme.typography.headlineMedium
        )
        
        OutlinedTextField(
            value = code,
            onValueChange = { if (it.all { char -> char.isDigit() }) code = it },
            label = { Text(stringResource(R.string.verification_code)) },
            placeholder = { Text(stringResource(R.string.code_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = { 
                if (!isLoading) {
                    isLoading = true
                    onVerifyCode(code)
                }
            },
            enabled = code.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.verify))
            }
        }
    }
}

@Composable
private fun PasswordInput(
    onVerifyPassword: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = stringResource(R.string.password),
            style = MaterialTheme.typography.headlineMedium
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            placeholder = { Text(stringResource(R.string.password_hint)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = { 
                if (!isLoading) {
                    isLoading = true
                    onVerifyPassword(password)
                }
            },
            enabled = password.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.login))
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.error_occurred),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Button(onClick = onRetry) {
            Text("Tentar Novamente")
        }
    }
}
