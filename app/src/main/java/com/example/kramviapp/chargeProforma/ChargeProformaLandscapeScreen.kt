package com.example.kramviapp.chargeProforma

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kramviapp.customers.CreateCustomerDialog
import com.example.kramviapp.customers.CustomersViewModel
import com.example.kramviapp.customers.EditCustomerDialog
import com.example.kramviapp.customers.SearchCustomerDialog
import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.PrinterType
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.ActionModel
import com.example.kramviapp.models.CreateProformaModel
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.models.ProformaModel
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.printers.PrinterProforma58
import com.example.kramviapp.printers.PrinterProforma80
import com.example.kramviapp.proformaItems.ProformaItemsDialog
import com.example.kramviapp.proformaItems.ProformaItemsViewModel
import com.example.kramviapp.proformas.ProformasViewModel
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.PrinterModel
import com.example.kramviapp.ui.theme.DarkGreen
import com.example.kramviapp.utils.BuildProformaSharePdf

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargeProformaLandscapeScreen(
    database: AppDatabase,
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
    proformaItemsViewModel: ProformaItemsViewModel,
    customersViewModel: CustomersViewModel,
    proformasViewModel: ProformasViewModel,
) {
    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val clickMenu by navigationViewModel.clickMenu.collectAsState()
    val setting by loginViewModel.setting.collectAsState()
    val office by loginViewModel.office.collectAsState()
    val business by loginViewModel.business.collectAsState()
    val customer by customersViewModel.customer.collectAsState()
    val proformaItems by proformaItemsViewModel.proformaItems.collectAsState()

    var showProformaItemsDialog by remember { mutableStateOf(false) }
    var showChargeProformaBottomSheet by remember { mutableStateOf(false) }
    var showSearchCustomerDialog by remember { mutableStateOf(false) }
    var showCreateCustomerDialog by remember { mutableStateOf(false) }
    var showEditCustomerDialog by remember { mutableStateOf(false) }
    var printers: List<PrinterModel> by remember { mutableStateOf(listOf()) }

    var proformaItemIndex by remember { mutableIntStateOf(0) }
    var currencyCode by remember { mutableStateOf(setting.defaultCurrencyCode) }
    var discount by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var savedProforma: ProformaModel? by remember { mutableStateOf(null) }
    var isEnabledSave by remember { mutableStateOf(true) }

    var expandedCurrencyCode by remember { mutableStateOf(false) }

    var charge = 0.0
    var countProducts = 0.0
    for (proformaItem in proformaItems) {
        if (proformaItem.igvCode != IgvCodeType.BONIFICACION) {
            charge += proformaItem.price * proformaItem.quantity
        }
        countProducts += proformaItem.quantity
    }

    try {
        if (discountPercent.isNotEmpty()) {
            val tmpDiscount = (charge / 100) * discountPercent.toDouble()
            charge -= tmpDiscount
            discount = tmpDiscount.toString()
        } else {
            charge -= if (discount.isNotEmpty()) discount.toDouble() else 0.0
        }
    } catch (e: Exception) {

    }

    clickMenu?.let {
        navigationViewModel.setClickMenu(null)
        if (it == "add_customer") {
            showSearchCustomerDialog = true
        }
    }

    if (showChargeProformaBottomSheet) {
        savedProforma?.let { proforma ->
            val proformaSerial = "P${office.serialPrefix}-${proforma.proformaNumber}"
            ChargeProformaBottonSheet(
                proformaSerial = proformaSerial,
                onPrintRequest = {
                    val printerProforma58 = PrinterProforma58(
                        proforma,
                        proformaItems,
                        customer,
                        office,
                        setting,
                        business
                    )
                    val printerProforma80 = PrinterProforma80(
                        proforma,
                        proformaItems,
                        customer,
                        office,
                        setting,
                        business
                    )
                    for (printer in printers) {
                        if (printer.printProforma) {
                            when (printer.printerType) {
                                PrinterType.BLUETOOTH58 -> {
                                    printerProforma58.printBluetooth()
                                }
                                PrinterType.BLUETOOTH80 -> {
                                    printerProforma80.printBluetooth()
                                }
                                PrinterType.ETHERNET58 -> {
                                    printerProforma58.printEthernet(printer.ipAddress)
                                }
                                PrinterType.ETHERNET80 -> {
                                    printerProforma80.printEthernet(printer.ipAddress)
                                }

                            }
                        }
                    }
                    showChargeProformaBottomSheet = false
                    proformaItemsViewModel.removeAllProformaItems()
                    navigationViewModel.onNavigateTo(NavigateTo("proformar", true))
                },
                onShareRequest = {
                    val buildSharePdf = BuildProformaSharePdf(proforma, proformaItems, customer, office, business, setting, context)
                    buildSharePdf.sharePdf()
                    showChargeProformaBottomSheet = false
                    proformaItemsViewModel.removeAllProformaItems()
                    navigationViewModel.onNavigateTo(NavigateTo("proformar", true))
                },
                onDismissRequest = {
                    showChargeProformaBottomSheet = false
                    proformaItemsViewModel.removeAllProformaItems()
                    navigationViewModel.onNavigateTo(NavigateTo("proformar", true))
                }
            )
        }
    }
    if (showProformaItemsDialog) {
        ProformaItemsDialog(
            proformaItems[proformaItemIndex],
            onDeleteRequest = {
                proformaItemsViewModel.removeProformaItem(proformaItemIndex)
                showProformaItemsDialog = false
            },
            onDismissRequest = { proformaItem ->
                if (proformaItem != null) {
                    proformaItemsViewModel.updateProformaItem(proformaItemIndex, proformaItem)
                }
                showProformaItemsDialog = false
            }
        )
    }
    if (showSearchCustomerDialog) {
        SearchCustomerDialog(
            customersViewModel = customersViewModel,
            navigationViewModel = navigationViewModel,
            setting,
            onCreateRequest = {
                showSearchCustomerDialog = false
                showCreateCustomerDialog = true
            },
            onDismissRequest = {
                showSearchCustomerDialog = false
            }
        )
    }
    if (showCreateCustomerDialog) {
        CreateCustomerDialog(
            customersViewModel = customersViewModel,
            navigationViewModel = navigationViewModel,
            onDismissRequest = {
                showCreateCustomerDialog = false
            }
        )
    }
    if (showEditCustomerDialog) {
        EditCustomerDialog(
            customersViewModel = customersViewModel,
            navigationViewModel = navigationViewModel,
            onDismissRequest = {
                showEditCustomerDialog = false
            }
        )
    }
    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Guardar proforma")
        customersViewModel.setCustomer(null)
        val actions: MutableList<ActionModel> = mutableListOf()
        actions.add(ActionModel("add_payment", "AddPayment", Icons.Default.AddCard, false))
        actions.add(ActionModel("add_customer", "AddCustomer", Icons.Default.PersonAdd, false))
        navigationViewModel.setActions(actions)
        scrollState.animateScrollBy(10000f)
        printers = database.printerDao().getAll()
    }
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(Modifier.fillMaxWidth(.7f)) {
            Column {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = String.format("%.2f", charge), style = MaterialTheme.typography.titleLarge)
                    Text(text = "Total a cobrar")
                    Spacer(modifier = Modifier.height(12.dp))

                    customer?.let { customer ->
                        Button(onClick = {
                            showEditCustomerDialog = true
                        }) {
                            Text(text = customer.name, textAlign = TextAlign.Center)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (customer.address.isNotEmpty()) {
                            Text(text = customer.address, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (setting.showCurrencyCode) {
                        ExposedDropdownMenuBox(
                            expanded = expandedCurrencyCode,
                            onExpandedChange = {
                                expandedCurrencyCode = !expandedCurrencyCode
                            }
                        ) {
                            TextField(
                                readOnly = true,
                                value = currencyCode.toString(),
                                onValueChange = { },
                                label = { Text("Moneda") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedCurrencyCode
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                colors = ExposedDropdownMenuDefaults.textFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCurrencyCode,
                                onDismissRequest = { expandedCurrencyCode = false },
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        currencyCode = CurrencyCodeType.SOLES
                                        expandedCurrencyCode = false
                                    },
                                    text = {
                                        Text("SOLES")
                                    }
                                )
                                DropdownMenuItem(
                                    onClick = {
                                        currencyCode = CurrencyCodeType.DOLARES
                                        expandedCurrencyCode = false
                                    },
                                    text = {
                                        Text("DOLARES")
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (setting.showTotalDiscount) {
                        TextField(
                            value = discount,
                            onValueChange = { discount = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(text = "Descuento global") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            maxLines = 1,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (setting.showTotalDiscountPercent) {
                        TextField(
                            value = discountPercent,
                            onValueChange = {
                                discountPercent = it
                                charge = 0.0
                                discount = ""

                                for (proformaItem in proformaItems) {
                                    if (proformaItem.igvCode != IgvCodeType.BONIFICACION) {
                                        charge += proformaItem.price * proformaItem.quantity
                                    }
                                }

                                try {
                                    if (discountPercent.isNotEmpty()) {
                                        val tmpDiscount = (charge / 100) * discountPercent.toDouble()
                                        charge -= tmpDiscount
                                        discount = tmpDiscount.toString()
                                    } else {
                                        charge -= if (discount.isNotEmpty()) discount.toDouble() else 0.0
                                    }
                                } catch (e: Exception) {

                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(text = "Descuento global (Porcentaje)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            maxLines = 1,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    TextField(
                        value = observations,
                        onValueChange = { observations = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Observaciones") },
                        singleLine = true,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        enabled = isEnabledSave,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val createdProforma = CreateProformaModel(
                                discount = if (discount.isNotEmpty()) discount.toDouble() else 0.0,
                                igvPercent = setting.defaultIgvPercent,
                                currencyCode = currencyCode,
                                observations = observations,
                                customerId = customer?._id,
                            )
                            isEnabledSave = false
                            navigationViewModel.loadSpinnerStart()
                            proformasViewModel.createProforma(
                                createdProforma,
                                proformaItems,
                                onResponse = {
                                    savedProforma = it
                                    navigationViewModel.loadSpinnerFinish()
                                    showChargeProformaBottomSheet = true
                                    discount = ""
                                    observations = ""
                                },
                                onFailure = {
                                    navigationViewModel.loadSpinnerFinish()
                                    navigationViewModel.showMessage(it)
                                    isEnabledSave = true
                                }
                            )
                        },
                    ) {
                        Text(text = "GUARDAR")
                    }
                }
            }
        }
        Column(
            Modifier
                .fillMaxHeight()
                .background(Color.White)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(proformaItems) { index, proformaItem ->
                    ListItem(
                        modifier = Modifier.clickable {
                            proformaItemIndex = index
                            showProformaItemsDialog = true
                        },
                        headlineContent = { Text(proformaItem.fullName) },
                        supportingContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if  ((proformaItem.quantity % 1).toFloat() == 0f) {
                                    Text(text = "x${String.format("%.0f", proformaItem.quantity)}")
                                } else {
                                    Text(text = "x${String.format("%.2f", proformaItem.quantity)}")
                                }
                                if (proformaItem.igvCode == IgvCodeType.BONIFICACION) {
                                    Text(text = "Bonificacion", color = DarkGreen)
                                }
                                Text(text = String.format("%.2f", proformaItem.price))
                            }
                        },
                    )
                }
                item {
                    ListItem(
                        headlineContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total", fontWeight = FontWeight.Bold)
                                Text(text = String.format("%.2f", charge), fontWeight = FontWeight.Bold)
                            }
                        },
                    )
                }
            }
        }
    }
}