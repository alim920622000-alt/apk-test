package com.android.teztaomapp

import androidx.compose.runtime.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

                setContent {
                    var authed by remember { mutableStateOf(false) }

                    if (!authed) {
                        LoginScreen(onLoggedIn = { authed = true })
                    } else {
                        MerchantsScreen()
                    }
                }
    }
}