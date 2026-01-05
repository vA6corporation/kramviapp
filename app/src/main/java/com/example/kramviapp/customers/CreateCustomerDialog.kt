package com.example.kramviapp.customers

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
import androidx.compose.material3.NavigationBarItemColors
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
import com.example.kramviapp.enums.DocumentType
import com.example.kramviapp.models.CreateCustomerModel
import com.example.kramviapp.models.CustomerModel
import com.example.kramviapp.navigation.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomerDialog(
    customersViewModel: CustomersViewModel,
    navigationViewModel: NavigationViewModel,
    onDismissRequest: () -> Unit,
) {
    var documentType by remember { mutableStateOf(DocumentType.DNI) }
    var document by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var isValidName by remember { mutableStateOf(true) }
    var isValidDocument by remember { mutableStateOf(true) }
    var isEnabledSave by remember { mutableStateOf(true) }

    var expandedDocumentType by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Nuevo cliente",
                    style = MaterialTheme.typography.titleMedium
                )
                ExposedDropdownMenuBox(
                    expanded = expandedDocumentType,
                    onExpandedChange = {
                        expandedDocumentType = !expandedDocumentType
                    }
                ) {
                    TextField(
                        readOnly = true,
                        value = documentType.toString(),
                        onValueChange = { },
                        label = { Text("Tipo de documento") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedDocumentType
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDocumentType,
                        onDismissRequest = { expandedDocumentType = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                documentType = DocumentType.RUC
                                expandedDocumentType = false
                            },
                            text = {
                                Text("RUC")
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                documentType = DocumentType.DNI
                                expandedDocumentType = false
                            },
                            text = {
                                Text("DNI")
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                documentType = DocumentType.CE
                                expandedDocumentType = false
                            },
                            text = {
                                Text("CE")
                            }
                        )
                    }
                }
                TextField(
                    value = document,
                    onValueChange = { document = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Documento") },
                    singleLine = true,
                    maxLines = 1,
                    isError = !isValidDocument
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombres/Razon social") },
                    singleLine = true,
                    maxLines = 1,
                    isError = !isValidName
                )
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Direccion") },
                    singleLine = true,
                    maxLines = 1,
//                    isError = !isValid
                )
                TextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Celular") },
                    singleLine = true,
                    maxLines = 1,
//                    isError = !isValid
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    singleLine = true,
                    maxLines = 1,
//                    isError = !isValid
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            onDismissRequest()
                        },
                    ) {
                        Text(text = "CANCELAR")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        enabled = isEnabledSave,
                        onClick = {
                            if (documentType == DocumentType.RUC) {
                                if (document.isEmpty()) {
                                    isValidDocument = false
                                }
                                if (name.isEmpty()) {
                                    isValidName = false
                                }
                                if (document.isNotEmpty() && name.isNotEmpty()) {
                                    val createdCustomer = CreateCustomerModel(
                                        documentType,
                                        document,
                                        name,
                                        address,
                                        mobileNumber,
                                        email,
                                    )
                                    isEnabledSave = false
                                    customersViewModel.createCustomer(
                                        createdCustomer,
                                        onResponse = {
                                            navigationViewModel.showMessage("Registrado correctamente")
                                            onDismissRequest()
                                        },
                                        onFailure = { message ->
                                            navigationViewModel.showMessage(message)
                                            isEnabledSave = true
                                        }
                                    )
                                }
                            } else {
                                if (name.isEmpty()) {
                                    isValidName = false
                                }
                                if (name.isNotEmpty()) {
                                    val createdCustomer = CreateCustomerModel(
                                        documentType,
                                        document,
                                        name,
                                        address,
                                        mobileNumber,
                                        email,
                                    )
                                    isEnabledSave = false
                                    customersViewModel.createCustomer(
                                        createdCustomer,
                                        onResponse = {
                                            navigationViewModel.showMessage("Registrado correctamente")
                                            onDismissRequest()
                                        },
                                        onFailure = { message ->
                                            navigationViewModel.showMessage(message)
                                            isEnabledSave = true
                                        }
                                    )
                                }
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