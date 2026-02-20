package com.android.teztaomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.android.teztaomapp.network.Api

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var authed by remember {
                mutableStateOf(!Api.tokenStorage().accessToken().isNullOrBlank())
            }

            if (!authed) {
                LoginScreen(onLoggedIn = { authed = true })
            } else {
                MerchantsScreen()
            }
        }
    }
}

class MerchantsScreen {

}
