package com.example.kramviapp.charge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kramviapp.models.OutStockModel

@Composable
fun OutStockDialog(
    outStocks: List<OutStockModel>,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Productos agotados",
                    style = MaterialTheme.typography.titleMedium
                )
                outStocks.forEach { outStock ->
                    ListItem(headlineContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = outStock.fullName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (outStock.stock <= 0) {
                                Row {
                                    Text(text = "Stock: ")
                                    Text(text = "Agotado", color = Color.Red)
                                }
                            } else {
                                Text(text = "Stock: ${outStock.stock}")
                            }
                        }
                    })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            onDismissRequest()
                        },
                    ) {
                        Text(text = "VOLVER")
                    }
                }
            }
        }
    }
}