package com.example.kramviapp.biller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.kramviapp.charge.ChargeBottonSheet
import com.example.kramviapp.charge.ChargeViewModel
import com.example.kramviapp.charge.SplitPaymentsDialog
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
import com.example.kramviapp.models.SaleItemModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.navigation.ConfirmDialog
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.openTurn.OpenTurnDialog
import com.example.kramviapp.openTurn.OpenTurnViewModel
import com.example.kramviapp.printers.PrinterInvoice58
import com.example.kramviapp.printers.PrinterInvoice80
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.PrinterModel
import com.example.kramviapp.ui.theme.DarkGreen
import com.example.kramviapp.utils.BuildInvoiceSharePdf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargeBillerLandscapeScreen(
    database: AppDatabase,
    loginViewModel: LoginViewModel,
    billerViewModel: BillerViewModel,
    navigationViewModel: NavigationViewModel,
    customersViewModel: CustomersViewModel,
    openTurnViewModel: OpenTurnViewModel,
    chargeViewModel: ChargeViewModel,
) {
    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val clickMenu by navigationViewModel.clickMenu.collectAsState()
    val billerItems by billerViewModel.billerItems.collectAsState()
    val paymentMethods by chargeViewModel.paymentMethods.collectAsState()
    val workers by chargeViewModel.workers.collectAsState()
    val payments by chargeViewModel.payments.collectAsState()
    val setting by loginViewModel.setting.collectAsState()
    val office by loginViewModel.office.collectAsState()
    val business by loginViewModel.business.collectAsState()
    val turn by openTurnViewModel.turn.collectAsState()
    val customer by customersViewModel.customer.collectAsState()

    var showCreateBillerItemDialog by remember { mutableStateOf(false) }
    var showEditBillerItemDialog by remember { mutableStateOf(false) }
    var showChargeBottomSheet by remember { mutableStateOf(false) }
    var showSearchCustomerDialog by remember { mutableStateOf(false) }
    var showCreateCustomerDialog by remember { mutableStateOf(false) }
    var showEditCustomerDialog by remember { mutableStateOf(false) }
    var showOpenTurnDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSplitPaymentsDialog by remember { mutableStateOf(false) }
    var printers: List<PrinterModel> by remember { mutableStateOf(listOf()) }

    var billerItemIndex by remember { mutableIntStateOf(0) }
    var invoiceType by remember { mutableStateOf(InvoiceType.BOLETA) }
    var paymentMethodId by remember { mutableStateOf("") }
    var currencyCode by remember { mutableStateOf(setting.defaultCurrencyCode) }
    var workerId by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var cash by remember { mutableStateOf("") }
    var cashChange by remember { mutableStateOf("") }
    var savedSale: SaleModel? by remember { mutableStateOf(null) }

    var expandedInvoice by remember { mutableStateOf(false) }
    var expandedPaymentMethod by remember { mutableStateOf(false) }
    var expandedCurrencyCode by remember { mutableStateOf(false) }
    var expandedWorker by remember { mutableStateOf(false) }
    var isEnabledSave by remember { mutableStateOf(false) }

    var charge = 0.0
    for (billerItem in billerItems) {
        if (billerItem.igvCode != IgvCodeType.BONIFICACION) {
            charge += billerItem.price * billerItem.quantity
        }
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
                    val saleItems: MutableList<SaleItemModel> = mutableListOf()
                    for (billerItem in billerItems) {
                        val saleItem = SaleItemModel(
                            fullName = billerItem.fullName,
                            price = billerItem.price,
                            onModel = "Product",
                            quantity = billerItem.quantity,
                            igvCode = billerItem.igvCode,
                            preIgvCode = billerItem.igvCode,
                            unitCode = "NIU",
                            productId = "",
                            prices = listOf(),
                            isTrackStock = false,
                            observations = ""
                        )
                        saleItems.add(saleItem)
                    }
                    val printerInvoice58 = PrinterInvoice58(
                        sale,
                        saleItems.toList(),
                        customer,
                        office,
                        setting,
                        business
                    )
                    val printerInvoice80 = PrinterInvoice80(
                        sale,
                        saleItems.toList(),
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
                    customersViewModel.setCustomer(null)
                    chargeViewModel.setPayments(listOf())
                    billerViewModel.removeAllBillerItems()
                },
                onShareRequest = {
                    val saleItems: MutableList<SaleItemModel> = mutableListOf()
                    for (billerItem in billerItems) {
                        val saleItem = SaleItemModel(
                            fullName = billerItem.fullName,
                            price = billerItem.price,
                            onModel = "Product",
                            quantity = billerItem.quantity,
                            igvCode = billerItem.igvCode,
                            preIgvCode = billerItem.igvCode,
                            unitCode = "NIU",
                            productId = "",
                            prices = listOf(),
                            isTrackStock = false,
                            observations = ""
                        )
                        saleItems.add(saleItem)
                    }
                    val buildSharePdf = BuildInvoiceSharePdf(sale, saleItems, customer, office, business, setting, context)
                    buildSharePdf.sharePdf()
                    showChargeBottomSheet = false
                    customersViewModel.setCustomer(null)
                    chargeViewModel.setPayments(listOf())
                    billerViewModel.removeAllBillerItems()
                },
                onDismissRequest = {
                    showChargeBottomSheet = false
                    customersViewModel.setCustomer(null)
                    chargeViewModel.setPayments(listOf())
                    billerViewModel.removeAllBillerItems()
                }
            )
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
                    })
            }
        }
    }
    if (showCreateBillerItemDialog) {
        CreateBillerItemDialog(
            loginViewModel,
            onDismissRequest = { billerItem ->
                billerItem?.let {
                    billerViewModel.addBillerItem(it)
                }
                showCreateBillerItemDialog = false
            }
        )
    }
    if (showEditBillerItemDialog) {
        EditBillerItemDialog(
            billerItems[billerItemIndex],
            onDismissRequest = { billerItem ->
                billerItem?.let {
                    billerViewModel.updateBillerItem(billerItemIndex, it)
                }
                showEditBillerItemDialog = false
            },
            onDeleteRequest = {
                billerViewModel.removeBillerItem(billerItemIndex)
                showEditBillerItemDialog = false
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
                        },
                    )
                }
                showOpenTurnDialog = false
            }
        )
    }
    if (showConfirmDialog) {
        ConfirmDialog(
            onDismissRequest = {
                showConfirmDialog = false
            },
            onConfirmation = {
                showConfirmDialog = false
                billerViewModel.removeAllBillerItems()
                customersViewModel.setCustomer(null)
            },
            dialogText = "Esta seguro de cancelar la venta?..."
        )
    }
    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Emitir al contado")
        val actions: MutableList<ActionModel> = mutableListOf()
        actions.add(ActionModel("add_payment", "AddPayment", Icons.Default.AddCard, false))
        actions.add(ActionModel("add_customer", "AddCustomer", Icons.Default.PersonAdd, false))
        navigationViewModel.setActions(actions)
        customersViewModel.setCustomer(null)
        chargeViewModel.setPayments(listOf())
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
                                billerViewModel.createSale(
                                    createdSale,
                                    billerItems,
                                    payments,
                                    onResponse = {
                                        savedSale = it
                                        navigationViewModel.loadSpinnerFinish()
                                        showChargeBottomSheet = true
                                        isEnabledSave = true
                                    },
                                    onFailure = {
                                        navigationViewModel.loadSpinnerFinish()
                                        navigationViewModel.showMessage(it)
                                        isEnabledSave = true
                                    }
                                )
                            }
                        },
                    ) {
                        Text(text = "GUARDAR")
                    }
                }
            }
        }
        Column(Modifier.fillMaxHeight().background(Color.White)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(billerItems) { index, billerItem ->
                    ListItem(
                        modifier = Modifier.clickable {
                            billerItemIndex = index
                            showEditBillerItemDialog = true
                        },
                        headlineContent = { Text(billerItem.fullName) },
                        supportingContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if  ((billerItem.quantity % 1).toFloat() == 0f) {
                                    Text(text = "x${String.format("%.0f", billerItem.quantity)}")
                                } else {
                                    Text(text = "x${String.format("%.2f", billerItem.quantity)}")
                                }
                                if (billerItem.igvCode == IgvCodeType.BONIFICACION) {
                                    Text(text = "Bonificacion", color = DarkGreen)
                                }
                                Text(text = String.format("%.2f", billerItem.price))
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = {
                        showConfirmDialog = true
                    },
                ) {
                    Text(text = "CANCELAR")
                }
                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    onClick = {
                              showCreateBillerItemDialog = true
                    },
                ) {
                    Text(text = "AGREGAR PRODUCTO")
                }
            }
        }
    }
}