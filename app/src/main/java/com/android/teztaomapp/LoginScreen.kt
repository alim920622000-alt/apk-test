package com.android.teztaomapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.teztaomapp.network.Api
import com.android.teztaomapp.network.AuthClient
import com.android.teztaomapp.network.LoginRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit
) {
    var telegramUserId by remember { mutableStateOf("123456") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("Вход") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = telegramUserId,
                onValueChange = { telegramUserId = it },
                label = { Text("telegram_user_id") },
                singleLine = true
            )

            Button(
                onClick = {
                    scope.launch {
                        try {
                            loading = true
                            error = null

                            val resp = AuthClient.api.login(LoginRequest(telegramUserId.toLong()))
                            val expiresAt = System.currentTimeMillis() + resp.expires_in * 1000L

                            Api.tokenStorage().saveTokens(
                                access = resp.access_token,
                                refresh = resp.refresh_token ?: "",
                                expiresAtMillis = expiresAt
                            )

                            onLoggedIn()
                        } catch (e: HttpException) {
                            val body = e.response()?.errorBody()?.string()
                            error = "HTTP ${e.code()} ${e.message()}\n$body"
                        } catch (e: Exception) {
                            error = e.message
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading
            ) {
                Text(if (loading) "Вход..." else "Войти")
            }

            if (error != null) {
                Text("Ошибка: $error")
            }
        }
    }
}
