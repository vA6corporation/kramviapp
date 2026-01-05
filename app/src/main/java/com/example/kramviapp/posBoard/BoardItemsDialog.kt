package com.example.kramviapp.posBoard

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.models.BoardItemModel
import com.example.kramviapp.models.SaleItemModel

@Composable
fun BoardItemDialog(
    boardItem: BoardItemModel,
    onDismissRequest: (BoardItemModel?) -> Unit,
    onDeleteRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest(null) }) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            var quantity by remember { mutableStateOf(boardItem.quantity.toString()) }
            var price by remember { mutableStateOf(boardItem.price.toString()) }
            var observations by remember { mutableStateOf(boardItem.observations) }
            var checked by remember { mutableStateOf(boardItem.igvCode == IgvCodeType.BONIFICACION) }
            var isValidQuantity by remember { mutableStateOf(true) }
            var isValidPrice by remember { mutableStateOf(true) }
            val scrollState = rememberScrollState()
            LaunchedEffect(Unit) {
                scrollState.animateScrollBy(10000f)
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = boardItem.fullName,
                    style = MaterialTheme.typography.titleMedium
                )
                TextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    maxLines = 1,
                    isError = !isValidQuantity,
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                )
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    maxLines = 1,
                    isError = !isValidPrice,
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                )
                TextField(
                    value = observations,
                    onValueChange = { observations = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Observaciones") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    maxLines = 1,
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            if (checked) {
                                boardItem.igvCode = IgvCodeType.BONIFICACION
                            } else {
                                boardItem.igvCode = boardItem.preIgvCode
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Bonificacion")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            onDeleteRequest()
                        },
                    ) {
                        Text(text = "ELIMINAR")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    FilledTonalButton(
                        onClick = {
                            onDismissRequest(null)
                        },
                    ) {
                        Text(text = "CANCELAR")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(
                        onClick = {
                            if (quantity.isEmpty()) {
                                isValidQuantity = false
                            }
                            if (price.isEmpty()) {
                                isValidPrice = false
                            }
                            if (quantity.isNotEmpty() && price.isNotEmpty()) {
                                val updatedBoardItem = boardItem.copy()
                                updatedBoardItem.quantity = quantity.toDouble()
                                updatedBoardItem.price = price.toDouble()
                                updatedBoardItem.observations = observations
                                onDismissRequest(updatedBoardItem)
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