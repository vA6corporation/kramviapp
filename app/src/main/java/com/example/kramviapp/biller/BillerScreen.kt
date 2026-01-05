package com.example.kramviapp.biller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.navigation.NavigationViewModel

@Composable
fun BillerScreen(
    navigationViewModel: NavigationViewModel,
    loginViewModel: LoginViewModel,
) {
    val business by loginViewModel.business.collectAsState()

    if (business.isDebtorCancel) {
        navigationViewModel.onNavigateTo(NavigateTo("subscription"))
    }

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Facturador")
    }
    Column(modifier = Modifier.padding(12.dp)) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navigationViewModel.onNavigateTo(NavigateTo("chargeBiller"))
            },
        ) {
            Text(text = "EMITIR AL CONTADO")
        }
        Spacer(modifier = Modifier.height(5.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navigationViewModel.onNavigateTo(NavigateTo("creditBiller"))
            },
        ) {
            Text(text = "EMITIR AL CREDITO")
        }
    }
}