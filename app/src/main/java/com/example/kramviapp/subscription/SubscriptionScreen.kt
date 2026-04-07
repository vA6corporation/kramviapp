package com.example.kramviapp.subscription

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kramviapp.navigation.NavigationViewModel

@Composable
fun SubscriptionScreen(
    navigationViewModel: NavigationViewModel,
) {
    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Renueve la suscripcion")
    }
    Column(modifier = Modifier
        .padding(12.dp)
        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Warning, modifier = Modifier.size(48.dp), contentDescription = "Renueve la suscripcion")
        Text(
            text = "Es necesario renovar la suscripcion",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(text = "Contacte al soporte tecnico 930 430 929", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = "Medios de pago", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "BCP")
                    Text(text = "191-93002668-0-87")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Interbank")
                    Text(text = "8983151207750")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "BBVA")
                    Text(text = "0011-0284-0200643262")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Yape / Plin")
                    Text(text = "930 430 929")
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Todas las cuentas a estan a nombre del administrador", textAlign = TextAlign.Center)
                    Text(text = "KELVIN EDGAR ROMANI OLLERO", style = MaterialTheme.typography.titleMedium)
                }
            }

        }
    }
}