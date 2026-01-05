package com.example.kramviapp.products

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import com.example.kramviapp.charge.ChargeViewModel
import com.example.kramviapp.models.PurchaseStockModel
import com.example.kramviapp.models.StockModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseStockDialog(
    chargeViewModel: ChargeViewModel,
    onDismissRequest: (PurchaseStockModel?) -> Unit
) {
    val paymentMethods by chargeViewModel.paymentMethods.collectAsState()
    Dialog(onDismissRequest = { onDismissRequest(null) }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            var quantity by remember { mutableStateOf("") }
            var cost by remember { mutableStateOf("") }
            var paymentMethodId by remember { mutableStateOf("") }
            var observations by remember { mutableStateOf("") }

            var isEnabledSave by remember { mutableStateOf(false) }
            var isValidQuantity by remember { mutableStateOf(true) }
            var isValidCost by remember { mutableStateOf(true) }
            var expandedPaymentMethod by remember { mutableStateOf(false) }

            if (paymentMethods == null) {
                chargeViewModel.loadPaymentMethods(onResponse = {
                    isEnabledSave = true
                })
            } else {
                isEnabledSave = true
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Nueva compra",
                    style = MaterialTheme.typography.titleMedium
                )
                TextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Cantidad") },
                    singleLine = true,
                    isError = !isValidQuantity,
                    maxLines = 1,
                )
                TextField(
                    value = cost,
                    onValueChange = { cost = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Costo") },
                    singleLine = true,
                    isError = !isValidCost,
                    maxLines = 1,
                )
                paymentMethods?.let { paymentMethods ->
                    if (paymentMethodId.isEmpty()) {
                        paymentMethodId = paymentMethods[0]._id
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedPaymentMethod,
                        onExpandedChange = {
                            expandedPaymentMethod = !expandedPaymentMethod
                        }
                    ) {
                        TextField(
                            readOnly = true,
                            value = paymentMethods.find { it._id == paymentMethodId }?.name ?: "",
                            onValueChange = { },
                            label = { Text("Medio de pago") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandedPaymentMethod
                                )
                            },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPaymentMethod,
                            onDismissRequest = { expandedPaymentMethod = false }
                        ) {
                            for (paymentMethod in paymentMethods) {
                                DropdownMenuItem(
                                    onClick = {
                                        paymentMethodId = paymentMethod._id
                                        expandedPaymentMethod = false
                                    },
                                    text = {
                                        Text(text = paymentMethod.name)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                TextField(
                    value = observations,
                    onValueChange = { observations = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Observaciones") },
                    singleLine = true,
                    maxLines = 1,
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
                        enabled = isEnabledSave,
                        onClick = {
                            if (quantity.isEmpty()) {
                                isValidQuantity = false
                                return@Button
                            }
                            if (cost.isEmpty()) {
                                isValidCost = false
                                return@Button
                            }
                            val stock = PurchaseStockModel(
                                quantity.toDouble(),
                                cost.toDouble(),
                                paymentMethodId,
                                observations
                            )
                            onDismissRequest(stock)
                        },
                    ) {
                        Text(text = "GUARDAR")
                    }
                }
            }
        }
    }
}