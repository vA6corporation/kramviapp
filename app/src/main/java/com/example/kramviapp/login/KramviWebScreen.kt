package com.example.kramviapp.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.example.kramviapp.navigation.NavigationViewModel
import com.va6corporation.kramviapp.BuildConfig
import kotlinx.coroutines.launch

private const val BASE_FRONT_URL: String = BuildConfig.BASE_FRONT_URL

@Composable
fun KramviWebScreen(
    navigationViewModel: NavigationViewModel,
    loginViewModel: LoginViewModel,
) {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("KramviWeb")
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {
        Column {
            Button(
                onClick = {
                    scope.launch {
                        val accessToken = loginViewModel.getAccessToken()
                        uriHandler.openUri("$BASE_FRONT_URL?kvtoken=$accessToken")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("IR A KRAMVI WEB") }
        }
    }
}