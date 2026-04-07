package com.example.kramviapp.posBoard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kramviapp.models.ProductModel

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SelectAnnotationsDialog(
    product: ProductModel,
    onDismissRequest: (String?) -> Unit
) {
    val selectedAnnotations: MutableList<String> by remember { mutableStateOf(mutableListOf()) }
    val scrollState = rememberScrollState()
    Dialog(onDismissRequest = { onDismissRequest(null) }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(12.dp)) {
                Column(modifier = Modifier
                    .verticalScroll(scrollState)
                    .weight(1f)) {
                    for (annotation in product.annotations) {
                        val (checkedState, onStateChange) = remember { mutableStateOf(false) }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .toggleable(
                                    value = checkedState,
                                    onValueChange = {
                                        onStateChange(!checkedState)
                                        if (!checkedState) {
                                            selectedAnnotations.add(annotation)
                                        } else {
                                            val selectedIndex = selectedAnnotations.indexOf(annotation)
                                            selectedAnnotations.removeAt(selectedIndex)
                                        }
                                    },
                                    role = Role.Checkbox
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checkedState,
                                onCheckedChange = null // null recommended for accessibility with screenreaders
                            )
                            Text(
                                text = annotation,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
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
                            val result = selectedAnnotations.joinToString(separator = ", ")
                            onDismissRequest(result)
                        },
                    ) {
                        Text(text = "GUARDAR")
                    }
                }
            }
        }
    }
}