package com.example.kramviapp.charge

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kramviapp.customers.CreateCustomerDialog
import com.example.kramviapp.customers.CustomersViewModel
import com.example.kramviapp.customers.EditCustomerDialog
import com.example.kramviapp.customers.SearchCustomerDialog
import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.DocumentType
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.InvoiceType
import com.example.kramviapp.enums.PrinterType
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.ActionModel
import com.example.kramviapp.models.CreateSaleModel
import com.example.kramviapp.models.CreateTurnModel
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.models.OutStockModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.openTurn.OpenTurnDialog
import com.example.kramviapp.openTurn.OpenTurnViewModel
import com.example.kramviapp.printers.PrinterInvoice58
import com.example.kramviapp.printers.PrinterInvoice80
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.PrinterModel
import com.example.kramviapp.saleItems.SaleItemsDialog
import com.example.kramviapp.saleItems.SaleItemsViewModel
import com.example.kramviapp.utils.BuildInvoiceSharePdf

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargePortraitScreen(
    navigateTo: String,
    boardId: String?,
    database: AppDatabase,
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
    saleItemsViewModel: SaleItemsViewModel,
    customersViewModel: CustomersViewModel,
    openTurnViewModel: OpenTurnViewModel,
    chargeViewModel: ChargeViewModel,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val clickMenu by navigationViewModel.clickMenu.collectAsState()
    val saleItems by saleItemsViewModel.saleItems.collectAsState()
    val paymentMethods by chargeViewModel.paymentMethods.collectAsState()
    val workers by chargeViewModel.workers.collectAsState()
    val payments by chargeViewModel.payments.collectAsState()
    val setting by loginViewModel.setting.collectAsState()
    val office by loginViewModel.office.collectAsState()
    val business by loginViewModel.business.collectAsState()
    val turn by openTurnViewModel.turn.collectAsState()
    val customer by customersViewModel.customer.collectAsState()

    var showSaleItemsDialog by remember { mutableStateOf(false) }
    var showSaleItemBottomSheet by remember { mutableStateOf(false) }
    var showChargeBottomSheet by remember { mutableStateOf(false) }
    var showSearchCustomerDialog by remember { mutableStateOf(false) }
    var showCreateCustomerDialog by remember { mutableStateOf(false) }
    var showEditCustomerDialog by remember { mutableStateOf(false) }
    var showOpenTurnDialog by remember { mutableStateOf(false) }
    var showSplitPaymentsDialog by remember { mutableStateOf(false) }
    var showOutStockDialog by remember { mutableStateOf(false) }
    var outStocks: List<OutStockModel> by remember { mutableStateOf(listOf()) }
    var printers: List<PrinterModel> by remember { mutableStateOf(listOf()) }

    var saleItemIndex by remember { mutableIntStateOf(0) }
    var invoiceType by remember { mutableStateOf(InvoiceType.BOLETA) }
    var paymentMethodId by remember { mutableStateOf("") }
    var currencyCode by remember { mutableStateOf(setting.defaultCurrencyCode) }
    var workerId by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var cash by remember { mutableStateOf("") }
//    var cashChange by remember { mutableStateOf("") }
    var savedSale: SaleModel? by remember { mutableStateOf(null) }

    var expandedInvoice by remember { mutableStateOf(false) }
    var expandedPaymentMethod by remember { mutableStateOf(false) }
    var expandedCurrencyCode by remember { mutableStateOf(false) }
    var expandedWorker by remember { mutableStateOf(false) }
    var isEnabledSave by remember { mutableStateOf(false) }

    var charge = 0.0
    var countProducts = 0.0
    for (saleItem in saleItems) {
        if (saleItem.igvCode != IgvCodeType.BONIFICACION) {
            charge += saleItem.price * saleItem.quantity
        }
        countProducts += saleItem.quantity
    }
    charge -= if (discount.isNotEmpty()) discount.toDouble() else 0.0

    clickMenu?.let {
        navigationViewModel.setClickMenu(null)
        if (it == "add_customer") {
            showSearchCustomerDialog = true
        }
        if (it == "add_payment") {
            showSplitPaymentsDialog = true
        }
    }

    if (showChargeBottomSheet) {
        savedSale?.let { sale ->
            val invoiceSerial = "${sale.invoicePrefix}${office.serialPrefix}-${sale.invoiceNumber}"
            ChargeBottonSheet(
                invoiceSerial = invoiceSerial,
                onPrintRequest = {
                    val printerInvoice58 = PrinterInvoice58(
                        sale,
                        saleItems,
                        customer,
                        office,
                        setting,
                        business
                    )
                    val printerInvoice80 = PrinterInvoice80(
                        sale,
                        saleItems,
                        customer,
                        office,
                        setting,
                        business
                    )
                    for (printer in printers) {
                        if (printer.printInvoice) {
                            when (printer.printerType) {
                                PrinterType.BLUETOOTH58 -> {
                                    printerInvoice58.printBluetooth()
                                }
                                PrinterType.BLUETOOTH80 -> {
                                    printerInvoice80.printBluetooth()
                                }
                                PrinterType.ETHERNET58 -> {
                                    printerInvoice58.printEthernet(printer.ipAddress)
                                }
                                PrinterType.ETHERNET80 -> {
                                    printerInvoice80.printEthernet(printer.ipAddress)
                                }

                            }
                        }
                    }
                    showChargeBottomSheet = false
                    saleItemsViewModel.removeAllSaleItems()
                    navigationViewModel.onNavigateTo(NavigateTo(navigateTo, true))

                },
                onShareRequest = {
                    val buildSharePdf = BuildInvoiceSharePdf(sale, saleItems, customer, office, business, setting, context)
                    buildSharePdf.sharePdf()
                    showChargeBottomSheet = false
                    saleItemsViewModel.removeAllSaleItems()
                    navigationViewModel.onNavigateTo(NavigateTo(navigateTo, true))

                },
                onDismissRequest = {
                    showChargeBottomSheet = false
                    saleItemsViewModel.removeAllSaleItems()
                    navigationViewModel.onNavigateTo(NavigateTo(navigateTo, true))
                }
            )
        }
    }
    if (showOutStockDialog) {
        OutStockDialog(outStocks = outStocks) {
            showOutStockDialog = false
        }
    }
    if (showSplitPaymentsDialog) {
        paymentMethods?.let { paymentMethods ->
            turn?.let { turn ->
                SplitPaymentsDialog(
                    paymentMethods,
                    payments,
                    charge,
                    turnId = turn._id,
                    onDismissRequest = { payments ->
                        payments?.let {
                            chargeViewModel.setPayments(it)
                        }
                        showSplitPaymentsDialog = false
                    }
                )
            }
        }
    }
    if (showSaleItemsDialog) {
        SaleItemsDialog(
            saleItems[saleItemIndex],
            setting,
            onDeleteRequest = {
                saleItemsViewModel.removeSaleItem(saleItemIndex)
                showSaleItemsDialog = false
            },
            onDismissRequest = { saleItem ->
                if (saleItem != null) {
                    saleItemsViewModel.updateSaleItem(saleItemIndex, saleItem)
                }
                showSaleItemsDialog = false
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
    if (showOpenTurnDialog) {
        OpenTurnDialog(
            onDismissRequest = {
                it?.let {
                    navigationViewModel.loadBarStart()
                    val createdTurn = CreateTurnModel(it)
                    openTurnViewModel.createTurnUser(
                        createdTurn,
                        onResponse = {
                            navigationViewModel.showMessage("Caja aperturada correctamente")
                            navigationViewModel.loadBarFinish()
                        },
                        onFailure = { message ->
                            navigationViewModel.showMessage(message)
                            navigationViewModel.loadBarFinish()
                        }
                    )
                }
                showOpenTurnDialog = false
            }
        )
    }
    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Cobrar")
        customersViewModel.setCustomer(null)
        chargeViewModel.setPayments(listOf())
        val actions: MutableList<ActionModel> = mutableListOf()
        actions.add(ActionModel("add_payment", "AddPayment", Icons.Default.AddCard, false))
        actions.add(ActionModel("add_customer", "AddCustomer", Icons.Default.PersonAdd, false))
        navigationViewModel.setActions(actions)
        scrollState.animateScrollBy(10000f)
        printers = database.printerDao().getAll()
        if (paymentMethods == null) {
            chargeViewModel.loadPaymentMethods(onResponse = {
                isEnabledSave = true
            })
        } else {
            isEnabledSave = true
        }
        if (workers == null) {
            chargeViewModel.loadWorkers()
        }
        if (turn == null) {
            showOpenTurnDialog = true
        }
    }
    Column {
        Row(modifier = Modifier.clickable {
            showSaleItemBottomSheet = true
        }) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Row {
                    Icon(Icons.Default.ShoppingBasket, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    if  ((countProducts % 1).toFloat() == 0f) {
                        Text(text = String.format("%.0f", countProducts))
                    } else {
                        Text(text = String.format("%.2f", countProducts))
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Row {
                    Icon(Icons.Default.Payments, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = String.format("%.2f", charge))
                }
            }
        }
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

            ExposedDropdownMenuBox(
                expanded = expandedInvoice,
                onExpandedChange = {
                    expandedInvoice = !expandedInvoice
                }
            ) {
                TextField(
                    readOnly = true,
                    value = invoiceType.toString(),
                    onValueChange = { },
                    label = { Text("Comprobante") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedInvoice
                        )
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedInvoice,
                    onDismissRequest = { expandedInvoice = false },
                ) {
                    DropdownMenuItem(
                        onClick = {
                            invoiceType = InvoiceType.BOLETA
                            expandedInvoice = false
                        },
                        text = {
                            Text(InvoiceType.BOLETA.toString())
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            invoiceType = InvoiceType.FACTURA
                            expandedInvoice = false
                        },
                        text = {
                            Text(InvoiceType.FACTURA.toString())
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            invoiceType = InvoiceType.NOTA_DE_VENTA
                            expandedInvoice = false
                        },
                        text = {
                            Text(InvoiceType.NOTA_DE_VENTA.toString())
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

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
                        enabled = payments.isEmpty(),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    if (payments.isEmpty()) {
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
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

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

            workers?.let { workers ->
                if (setting.showWorker) {
                    ExposedDropdownMenuBox(
                        expanded = expandedWorker,
                        onExpandedChange = {
                            expandedWorker = !expandedWorker
                        }
                    ) {
                        TextField(
                            readOnly = true,
                            value = workers.find { it._id == workerId }?.name?.uppercase() ?: "",
                            onValueChange = { },
                            label = { Text("Personal a cargo") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandedWorker
                                )
                            },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedWorker,
                            onDismissRequest = { expandedWorker = false }
                        ) {
                            for (worker in workers) {
                                DropdownMenuItem(
                                    onClick = {
                                        workerId = worker._id
                                        expandedWorker = false
                                    },
                                    text = {
                                        Text(worker.name.uppercase())
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
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
                    turn.let { turn ->
                        if (turn == null) {
                            showOpenTurnDialog = true
                            navigationViewModel.showMessage("Debes aperturar una caja")
                            return@Button
                        }
                        if (invoiceType == InvoiceType.FACTURA) {
                            customer.let {
                                if (it == null) {
                                    navigationViewModel.showMessage("Agrega un cliente")
                                    return@Button
                                } else {
                                    if (it.documentType != DocumentType.RUC) {
                                        navigationViewModel.showMessage("El cliente debe tener un N° de RUC")
                                        return@Button
                                    }
                                }
                            }
                        }
                        val createdSale = CreateSaleModel(
                            invoiceType = invoiceType,
                            paymentMethodId = paymentMethodId,
                            discount = if (discount.isNotEmpty()) discount.toDouble() else 0.0,
                            cash = if (cash.isNotEmpty()) cash.toDouble() else 0.0,
                            igvPercent = setting.defaultIgvPercent,
                            currencyCode = currencyCode,
                            observations = observations,
                            workerId = workerId.ifEmpty { null },
                            customerId = customer?._id,
                            turnId = turn._id,
                            isCredit = false,
                        )
                        navigationViewModel.loadSpinnerStart()
                        isEnabledSave = false
                        if (setting.allowFreeStock) {
                            chargeViewModel.createSale(
                                createdSale,
                                saleItems,
                                payments,
                                boardId,
                                onResponse = {
                                    savedSale = it
                                    navigationViewModel.loadSpinnerFinish()
                                    showChargeBottomSheet = true
                                },
                                onFailure = {
                                    navigationViewModel.loadSpinnerFinish()
                                    navigationViewModel.showMessage(it)
                                    isEnabledSave = true
                                }
                            )
                        } else {
                            chargeViewModel.createSaleWithStock(
                                createdSale,
                                saleItems,
                                payments,
                                boardId,
                                onResponse = {
                                    if (it.sale != null) {
                                        savedSale = it.sale
                                        showChargeBottomSheet = true
                                    } else {
                                        outStocks = it.outStocks
                                        showOutStockDialog = true
                                        isEnabledSave = true
                                    }
                                    navigationViewModel.loadSpinnerFinish()
                                },
                                onFailure = {
                                    navigationViewModel.loadSpinnerFinish()
                                    navigationViewModel.showMessage(it)
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