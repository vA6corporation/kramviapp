package com.example.kramviapp.charge

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.requiredWidthIn
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kramviapp.models.CreatePaymentModel
import com.example.kramviapp.models.PaymentMethodModel
import java.math.BigDecimal
import java.math.RoundingMode

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitPaymentsDialog(
    paymentMethods: List<PaymentMethodModel>,
    payments: List<CreatePaymentModel>,
    charge: Double,
    turnId: String,
    onDismissRequest: (List<CreatePaymentModel>?) -> Unit,
) {
    var localPayments: MutableList<CreatePaymentModel> by remember { mutableStateOf(mutableListOf()) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        if (payments.isNotEmpty()) {
            localPayments = payments.toMutableList()
        } else {
            val copyList = localPayments.toMutableList()
            copyList.add(CreatePaymentModel(charge = 0.0, paymentMethodId = paymentMethods[0]._id, turnId))
            localPayments = copyList
        }
    }

    LaunchedEffect(Unit) {
        scrollState.animateScrollBy(10000f)
    }

    Dialog(onDismissRequest = { }) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        Card(
            modifier = Modifier.fillMaxWidth().onSizeChanged { size = it },
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Monto a cuenta",
                    style = MaterialTheme.typography.titleMedium
                )
                for (payment in localPayments) {
                    var localCharge by remember { mutableStateOf(if (payment.charge > 0) payment.charge.toString() else "") }

                    val isValidCharge by remember { mutableStateOf(true) }
                    var expandedPaymentMethod by remember { mutableStateOf(false) }

                    Row {
                        TextField(
                            modifier = Modifier.fillMaxWidth(0.5f),
                            value = localCharge,
                            onValueChange = {
                                localCharge = it
                                if (it.isNotEmpty()) {
                                    payment.charge = it.toDouble()
                                }
                            },
                            label = { Text("Monto") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            maxLines = 1,
                            isError = !isValidCharge
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedPaymentMethod,
                            onExpandedChange = {
                                expandedPaymentMethod = !expandedPaymentMethod
                            }
                        ) {
                            paymentMethods.find { it._id == payment.paymentMethodId }?.let {
                                TextField(
                                    readOnly = true,
                                    value = it.name,
                                    onValueChange = { },
                                    label = { Text("M. de pago") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandedPaymentMethod
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                )
                            }
                            ExposedDropdownMenu(
                                expanded = expandedPaymentMethod,
                                onDismissRequest = { expandedPaymentMethod = false },
                            ) {
                                for (paymentMethod in paymentMethods) {
                                    DropdownMenuItem(
                                        onClick = {
                                            payment.paymentMethodId = paymentMethod._id
                                            expandedPaymentMethod = false
                                        },
                                        text = {
                                            Text(paymentMethod.name.uppercase())
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            onDismissRequest(listOf())
                        },
                    ) {
                        Text(text = "CANCELAR")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (localPayments.find { it.charge <= 0 } == null) {
                                onDismissRequest(localPayments)
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