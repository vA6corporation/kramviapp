package com.example.kramviapp.openTurn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun EditCashTurnDialog(
    onDismissRequest: (Double?) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest(null) }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            var openCash by remember { mutableStateOf("") }
            var isValid by remember { mutableStateOf(true) }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Modificar monto de apertura",
                    style = MaterialTheme.typography.titleMedium
                )
                TextField(
                    value = openCash,
                    onValueChange = { openCash = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Monto de apertura") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    maxLines = 1,
                    isError = !isValid
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            onDismissRequest(null)
                        },
                    ) {
                        Text(text = "CANCELAR")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (openCash.isEmpty()) {
                                isValid = false
                            } else {
                                onDismissRequest(openCash.toDouble())
                            }
                        },
                    ) {
                        Text(text = "GUARDAR")
                    }
                }
            }
        }
    }
}