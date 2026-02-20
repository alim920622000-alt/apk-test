package com.android.teztaomapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.teztaomapp.model.Merchant
import com.android.teztaomapp.network.Api
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantsScreen() {

    Log.d("SCREEN", "MerchantsScreen composed")

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var items by remember { mutableStateOf<List<Merchant>>(emptyList()) }

    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            Log.d("M", "load start")
            try {
                loading = true
                error = null

                val resp = Api.service.getMerchants(type = "shop", cursor = null)
                Log.d("M", "load ok: items=${resp.items.size}")

                items = resp.items
            } catch (e: Exception) {
                Log.e("M", "load error", e)
                error = e.message ?: "Ошибка загрузки"
            } finally {
                loading = false
                Log.d("M", "load end loading=$loading error=$error")
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Магазины") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ошибка: $error")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { load() }) { Text("Повторить") }
                    }
                }
                items.isEmpty() -> Text("Пусто", Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items) { m ->
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(m.name, style = MaterialTheme.typography.titleMedium)
                                    Text("id: ${m.id} • ${m.business_type}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}