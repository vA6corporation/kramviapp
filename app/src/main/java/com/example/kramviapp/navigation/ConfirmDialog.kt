package com.example.kramviapp.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogText: String,
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Example Icon")
        },
        title = {
            Text(text = "Confirmacion")
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("ACEPTAR")
            }
        },
        dismissButton = {
            FilledTonalButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("CANCELAR")
            }
        }
    )
}