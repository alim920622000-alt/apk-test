package com.android.teztaomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.teztaomapp.model.Merchant
import com.android.teztaomapp.network.Api
import retrofit2.HttpException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MerchantsScreen() }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MerchantsScreen() {
    var merchants by remember { mutableStateOf<List<Merchant>>(emptyList()) }
    var nextCursor by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var loadingMore by remember { mutableStateOf(false) }

    suspend fun loadPage(cursor: String?) {
        val resp = Api.service.getMerchants(type = "shop", cursor = cursor)
        nextCursor = resp.nextCursor
        merchants = if (cursor == null) resp.items else (merchants + resp.items)
    }

    LaunchedEffect(Unit) {
        try {
            loading = true
            error = null
            loadPage(cursor = null)
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()
            error = "HTTP ${e.code()} ${e.message()}\n$body"
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    LaunchedEffect(loadingMore) {
        if (!loadingMore) return@LaunchedEffect
        try {
            error = null
            val cursor = nextCursor
            if (cursor != null) loadPage(cursor)
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()
            error = "HTTP ${e.code()} ${e.message()}\n$body"
        } catch (e: Exception) {
            error = e.message
        } finally {
            loadingMore = false
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Список") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            when {
                loading -> CircularProgressIndicator()
                error != null -> Text("Ошибка: $error")
                else -> LazyColumn {
                    items(merchants) { m ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(m.name)
                                Text("id=${m.id} type=${m.business_type}")
                            }
                        }
                    }

                    item {
                        if (nextCursor != null) {
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { loadingMore = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !loadingMore
                            ) {
                                Text(if (loadingMore) "Загрузка..." else "Ещё")
                            }
                        }
                    }
                }
            }
        }
    }
}
