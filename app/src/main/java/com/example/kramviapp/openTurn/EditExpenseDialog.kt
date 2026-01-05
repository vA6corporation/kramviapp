package com.example.kramviapp.openTurn

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kramviapp.models.CreateExpenseModel
import com.example.kramviapp.models.ExpenseModel

@Composable
fun EditExpenseDialog(
    turnId: String,
    expense: ExpenseModel,
    onDismissRequest: (CreateExpenseModel?) -> Unit,
    onDeleteRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest(null) }) {
        Card(modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            var concept by remember { mutableStateOf(expense.concept) }
            var charge by remember { mutableStateOf(expense.charge.toString()) }
            var isValidConcept by remember { mutableStateOf(true) }
            var isValidCharge by remember { mutableStateOf(true) }
            val scrollState = rememberScrollState()
            LaunchedEffect(Unit) {
                scrollState.animateScrollBy(10000f)
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Editar gasto",
                    style = MaterialTheme.typography.titleMedium
                )
                TextField(
                    value = concept,
                    onValueChange = { concept = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Concepto del gasto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    maxLines = 1,
                    isError = !isValidConcept
                )
                TextField(
                    value = charge,
                    onValueChange = { charge = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    maxLines = 1,
                    isError = !isValidCharge
                )
                Spacer(modifier = Modifier.height(20.dp))
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(text = "ELIMINAR")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
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
                            if (concept.isEmpty()) {
                                isValidConcept = false
                            }
                            if (charge.isEmpty()) {
                                isValidCharge = false
                            }
                            if (concept.isNotEmpty() && charge.isNotEmpty()) {
                                val createdExpense = CreateExpenseModel(concept, charge.toDouble(), turnId)
                                onDismissRequest(createdExpense)
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