package com.example.kramviapp.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kramviapp.models.CategoryModel
import com.example.kramviapp.navigation.NavigationViewModel

@Composable
fun CreateCategoriesDialog(
    categoriesViewModel: CategoriesViewModel,
    navigationViewModel: NavigationViewModel,
    onDismissRequest: (CategoryModel?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isEnableSave by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = { onDismissRequest(null) }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Nueva categoria",
                    style = MaterialTheme.typography.titleMedium
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nombre de categoria") },
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
                        enabled = isEnableSave,
                        onClick = {
                            isEnableSave = false
                            categoriesViewModel.create(
                                name,
                                onResponse = {
                                    onDismissRequest(it)
                                },
                                onFailure = {
                                    isEnableSave = true
                                    navigationViewModel.showMessage(it)
                                }
                            )
                        },
                    ) {
                        Text(text = "GUARDAR")
                    }
                }
            }
        }
    }
}