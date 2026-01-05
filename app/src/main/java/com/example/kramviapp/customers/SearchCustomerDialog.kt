package com.example.kramviapp.customers

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kramviapp.enums.SearchCustomerType
import com.example.kramviapp.models.SettingModel
import com.example.kramviapp.navigation.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCustomerDialog(
    customersViewModel: CustomersViewModel,
    navigationViewModel: NavigationViewModel,
    setting: SettingModel,
    onDismissRequest: () -> Unit,
    onCreateRequest: () -> Unit,
) {
    val customers by customersViewModel.customers.collectAsState()
    var isValidSearch by remember { mutableStateOf(true) }
    var isEnabledSearch by remember { mutableStateOf(true) }
    var isEnabledCreateButton by remember { mutableStateOf(true) }
    var expandedSearchType by remember { mutableStateOf(false) }

    var searchType: SearchCustomerType by remember { mutableStateOf(SearchCustomerType.RUC) }
    var key by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    searchType = setting.defaultSearchCustomer

    val onRequestSuccess = {
        isEnabledSearch = true
        isEnabledCreateButton = true
    }
    val onRequestFail = { message: String ->
        navigationViewModel.showMessage(message)
        isEnabledSearch = true
        isEnabledCreateButton = true
    }
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Buscar cliente",
                    style = MaterialTheme.typography.titleMedium
                )
                ExposedDropdownMenuBox(
                    expanded = expandedSearchType,
                    onExpandedChange = {
                        expandedSearchType = !expandedSearchType
                    },
                ) {
                    TextField(
                        readOnly = true,
                        value = searchType.toString(),
                        onValueChange = {},
                        label = { Text("Tipo de busqueda") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedSearchType
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSearchType,
                        onDismissRequest = { expandedSearchType = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                searchType = SearchCustomerType.RUC
                                expandedSearchType = false
                                key = ""
                            },
                            text = {
                                Text("RUC")
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                searchType = SearchCustomerType.DNI
                                expandedSearchType = false
                                key = ""
                            },
                            text = {
                                Text("DNI")
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                searchType = SearchCustomerType.CE
                                expandedSearchType = false
                                key = ""
                            },
                            text = {
                                Text("CE")
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                searchType = SearchCustomerType.MOBILE
                                expandedSearchType = false
                                key = ""
                            },
                            text = {
                                Text("CELULAR")
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                searchType = SearchCustomerType.NAME
                                expandedSearchType = false
                                key = ""
                            },
                            text = {
                                Text("NOMBRES")
                            }
                        )
                    }
                }
                TextField(
                    value = key,
                    onValueChange = {
                        key = it
                        if (searchType == SearchCustomerType.RUC && key.length == 11) {
                            isEnabledSearch = false
                            isEnabledCreateButton = false
                            customersViewModel.getCustomersByRuc(key, onRequestSuccess, onRequestFail)
                        }

                        if (searchType == SearchCustomerType.DNI && key.length == 8) {
                            isEnabledSearch = false
                            isEnabledCreateButton = false
                            customersViewModel.getCustomersByDni(key, onRequestSuccess, onRequestFail)
                        }

                        if (searchType == SearchCustomerType.CE && key.length == 9) {
                            isEnabledSearch = false
                            isEnabledCreateButton = false
                            customersViewModel.getCustomersByCe(key, onRequestSuccess, onRequestFail)
                        }

                        if (searchType == SearchCustomerType.MOBILE && key.length == 9) {
                            isEnabledSearch = false
                            isEnabledCreateButton = false
                            customersViewModel.getCustomersByMobileNumber(key, onRequestSuccess, onRequestFail)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    label = { Text("Busqueda") },
                    enabled = isEnabledSearch,
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (key.isNotEmpty()) {
                                if (searchType == SearchCustomerType.NAME && key.length >= 2) {
                                    isEnabledSearch = false
                                    isEnabledCreateButton = false
                                    customersViewModel.getCustomersByKey(key, onRequestSuccess, onRequestFail)
                                } else {
                                    isValidSearch = false
                                }
                            }
                        },
                    ),
                    isError = !isValidSearch
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    customers.forEach { customer ->
                        ListItem(
                            modifier = Modifier.clickable {
                                customersViewModel.setCustomer(customer)
                                onDismissRequest()
                            },
                            headlineContent = { Text(customer.name) },
                            supportingContent = {
                                Text("${customer.documentType}:${customer.document}")
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
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
                        enabled = isEnabledCreateButton,
                        onClick = {
                            onCreateRequest()
                        },
                    ) {
                        Text(text = "NUEVO CLIENTE")
                    }
                }
            }
        }
    }
}