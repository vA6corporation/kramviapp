package com.example.kramviapp.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kramviapp.enums.PrinterType
import com.example.kramviapp.printers.PrinterInvoice58
import com.example.kramviapp.printers.PrinterInvoice80
import com.example.kramviapp.room.PrinterModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePrinterDialog(
    onDismissRequest: (PrinterModel?) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest(null) }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            var printerType by remember { mutableStateOf(PrinterType.BLUETOOTH58) }
            var name by remember { mutableStateOf("") }
            var ipAddress by remember { mutableStateOf("") }
            var printInvoice by remember { mutableStateOf(false) }
            var printProforma by remember { mutableStateOf(false) }
            var printAccount by remember { mutableStateOf(false) }
            var printKitchen by remember { mutableStateOf(false) }
            var printBar by remember { mutableStateOf(false) }
            var printOven by remember { mutableStateOf(false) }
            var printBox by remember { mutableStateOf(false) }

            var expandedPrinterType by remember { mutableStateOf(false) }
            var isValidName by remember { mutableStateOf(true) }
            var isValidIpAddress by remember { mutableStateOf(true) }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Agregar impresora",
                    style = MaterialTheme.typography.titleMedium
                )
                ExposedDropdownMenuBox(
                    expanded = expandedPrinterType,
                    onExpandedChange = {
                        expandedPrinterType = !expandedPrinterType
                    }
                ) {
                    TextField(
                        readOnly = true,
                        value = printerType.toString(),
                        onValueChange = { },
                        label = { Text("Tipo de impresora") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedPrinterType
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPrinterType,
                        onDismissRequest = { expandedPrinterType = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                expandedPrinterType = false
                                printerType = PrinterType.BLUETOOTH58
                            },
                            text = {
                                Text(PrinterType.BLUETOOTH58.toString())
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                expandedPrinterType = false
                                printerType = PrinterType.BLUETOOTH80
                            },
                            text = {
                                Text(PrinterType.BLUETOOTH80.toString())
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                expandedPrinterType = false
                                printerType = PrinterType.BLUETOOTH58
                            },
                            text = {
                                Text(PrinterType.ETHERNET58.toString())
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                expandedPrinterType = false
                                printerType = PrinterType.ETHERNET80
                            },
                            text = {
                                Text(PrinterType.ETHERNET80.toString())
                            }
                        )
                    }
                }
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre de impresora") },
                    singleLine = true,
                    maxLines = 1,
                    isError = !isValidName
                )
                if (printerType == PrinterType.ETHERNET58 || printerType == PrinterType.ETHERNET80) {
                    TextField(
                        value = ipAddress,
                        onValueChange = { ipAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("IP: 192.168.1.220") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        maxLines = 1,
                        isError = !isValidIpAddress
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        when(printerType) {
                            PrinterType.BLUETOOTH58 -> {
                                PrinterInvoice58.printBluetoothTest()
                            }
                            PrinterType.BLUETOOTH80 -> {
                                PrinterInvoice80.printBluetoothTest()
                            }
                            PrinterType.ETHERNET58 -> {
                                if (ipAddress.isNotEmpty()) {
                                    PrinterInvoice58.printEthernetTest(ipAddress)
                                }
                            }
                            PrinterType.ETHERNET80 -> {
                                if (ipAddress.isNotEmpty()) {
                                    PrinterInvoice80.printEthernetTest(ipAddress)
                                }
                            }
                        }
                    },
                ) {
                    Text(text = "PROBAR IMPRESORA")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "¿Que desea imprimir?")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = printInvoice,
                        onCheckedChange = {
                            printInvoice = it
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Comprobantes")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = printProforma,
                        onCheckedChange = {
                            printProforma = it
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Proformas")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = printAccount,
                        onCheckedChange = {
                            printAccount = it
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Precuentas")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = printKitchen,
                        onCheckedChange = {
                            printKitchen = it
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Comandas cocina")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = printBar,
                        onCheckedChange = {
                            printBar = it
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Comandas barra")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = printOven,
                        onCheckedChange = {
                            printOven = it
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Comandas horno")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = printBox,
                        onCheckedChange = {
                            printBox = it
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Comandas caja")
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
                            isValidName = name.isNotEmpty()
                            isValidIpAddress = ipAddress.isNotEmpty()
                            if (!printInvoice && !printProforma && !printAccount && !printKitchen && !printBar && !printOven && !printBox) {
                                return@Button
                            }
                            if (printerType == PrinterType.ETHERNET80 || printerType == PrinterType.ETHERNET58) {
                                if (name.isNotEmpty() && ipAddress.isNotEmpty()) {
                                    val printer = PrinterModel()
                                    printer.printerType = printerType
                                    printer.name = name
                                    printer.ipAddress = ipAddress
                                    printer.printInvoice = printInvoice
                                    printer.printProforma = printProforma
                                    printer.printAccount = printAccount
                                    printer.printKitchen = printKitchen
                                    printer.printBar = printBar
                                    printer.printOven = printOven
                                    printer.printBox = printBox
                                    onDismissRequest(printer)
                                }
                            } else {
                                if (name.isNotEmpty()) {
                                    val printer = PrinterModel()
                                    printer.printerType = printerType
                                    printer.name = name
                                    printer.printInvoice = printInvoice
                                    printer.printProforma = printProforma
                                    printer.printAccount = printAccount
                                    printer.printKitchen = printKitchen
                                    printer.printBar = printBar
                                    printer.printOven = printOven
                                    printer.printBox = printBox
                                    onDismissRequest(printer)
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