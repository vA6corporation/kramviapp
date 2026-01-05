package com.example.kramviapp.biller

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import com.example.kramviapp.models.BillerItemModel
import com.example.kramviapp.models.SaleItemModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBillerItemDialog(
    billerItem: BillerItemModel,
    onDismissRequest: (BillerItemModel?) -> Unit,
    onDeleteRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest(null) }) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            var fullName by remember { mutableStateOf(billerItem.fullName) }
            var quantity by remember { mutableStateOf(billerItem.quantity.toString()) }
            var price by remember { mutableStateOf(billerItem.price.toString()) }
            var igvCode by remember { mutableStateOf(billerItem.igvCode) }

            var isValidFullName by remember { mutableStateOf(true) }
            var isValidQuantity by remember { mutableStateOf(true) }
            var isValidPrice by remember { mutableStateOf(true) }
            var expandedIgvCode by remember { mutableStateOf(false) }
            val scrollState = rememberScrollState()
            LaunchedEffect(Unit) {
                scrollState.animateScrollBy(10000f)
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Editar producto",
                    style = MaterialTheme.typography.titleMedium
                )
                TextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre") },
                    singleLine = true,
                    maxLines = 1,
                    isError = !isValidFullName,
                    textStyle = TextStyle(textAlign = TextAlign.Center),
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
                ExposedDropdownMenuBox(
                    expanded = expandedIgvCode,
                    onExpandedChange = {
                        expandedIgvCode = !expandedIgvCode
                    }
                ) {
                    TextField(
                        readOnly = true,
                        value = igvCode.toString(),
                        onValueChange = { },
                        label = { Text("Afectacion al IGV") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedIgvCode
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedIgvCode,
                        onDismissRequest = { expandedIgvCode = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                igvCode = IgvCodeType.GRAVADO
                                expandedIgvCode = false
                            },
                            text = {
                                Text(IgvCodeType.GRAVADO.toString())
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                igvCode = IgvCodeType.BONIFICACION
                                expandedIgvCode = false
                            },
                            text = {
                                Text(IgvCodeType.BONIFICACION.toString())
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                igvCode = IgvCodeType.EXONERADO
                                expandedIgvCode = false
                            },
                            text = {
                                Text(IgvCodeType.EXONERADO.toString())
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                igvCode = IgvCodeType.INAFECTO
                                expandedIgvCode = false
                            },
                            text = {
                                Text(IgvCodeType.INAFECTO.toString())
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.End
                ) {
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
                            onDeleteRequest()
                        },
                    ) {
                        Text(text = "ELIMINAR")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(
                        onClick = {
                            if (fullName.isEmpty()) {
                                isValidFullName = false
                            }
                            if (quantity.isEmpty()) {
                                isValidQuantity = false
                            }
                            if (price.isEmpty()) {
                                isValidPrice = false
                            }
                            if (fullName.isNotEmpty() && quantity.isNotEmpty() && price.isNotEmpty()) {
                                val createBillerItem = BillerItemModel(
                                    fullName = fullName,
                                    price = price.toDouble(),
                                    quantity = quantity.toDouble(),
                                    igvCode = igvCode,
                                )
                                onDismissRequest(createBillerItem)
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