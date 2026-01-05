package com.example.kramviapp.invoices


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kramviapp.charge.ChargeViewModel
import com.example.kramviapp.enums.InvoiceType
import com.example.kramviapp.enums.PrinterType
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.navigation.PasswordDialog
import com.example.kramviapp.printers.PrinterInvoice58
import com.example.kramviapp.printers.PrinterInvoice80
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.PrinterModel
import com.example.kramviapp.ui.theme.KramviRed
import com.example.kramviapp.utils.BuildInvoiceSharePdf
import com.example.kramviapp.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    database: AppDatabase,
    invoicesViewModel: InvoicesViewModel,
    chargeViewModel: ChargeViewModel,
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
) {
    val setting by loginViewModel.setting.collectAsState()
    val office by loginViewModel.office.collectAsState()
    val business by loginViewModel.business.collectAsState()
    val salesOfTheDay by invoicesViewModel.salesOfTheDay.collectAsState()
    val isRefreshing by invoicesViewModel.isRefreshing.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showDeleteInvoiceDialog by remember { mutableStateOf(false) }
    var selectedSale: SaleModel? by remember { mutableStateOf(null) }
    var printers: List<PrinterModel> by remember { mutableStateOf(listOf()) }
    val context = LocalContext.current
    if (showPasswordDialog) {
        PasswordDialog(
            setting = setting,
            onSuccessRequest = {
                showPasswordDialog = false
                showDeleteInvoiceDialog = true
            },
            onDismissRequest = {
                showPasswordDialog = false
            }
        )
    }
    if (showDeleteInvoiceDialog) {
        selectedSale?.let { sale ->
            DeleteInvoiceDialog(
                onSuccessRequest = { deletedReason ->
                    showDeleteInvoiceDialog = false
                    navigationViewModel.loadBarStart()
                    if (sale.invoiceType == InvoiceType.NOTA_DE_VENTA || business.certificateId == null) {
                        invoicesViewModel.deleteSale(
                            sale._id,
                            deletedReason,
                            onResponse = {
                                invoicesViewModel.loadSalesOfTheDay(
                                    onReponse = {
                                        navigationViewModel.loadBarFinish()
                                    },
                                    onFailure = { message ->
                                        navigationViewModel.showMessage(message)
                                        navigationViewModel.loadBarFinish()
                                    }
                                )
                            },
                            onFailure = { message ->
                                navigationViewModel.showMessage(message)
                                navigationViewModel.loadBarFinish()
                            }
                        )
                    } else {
                        invoicesViewModel.deleteInvoice(
                            sale._id,
                            deletedReason,
                            onResponse = {
                                invoicesViewModel.loadSalesOfTheDay(
                                    onReponse = {
                                        sale.ticket = it
                                        navigationViewModel.loadBarFinish()
                                    },
                                    onFailure = { message ->
                                        navigationViewModel.showMessage(message)
                                        navigationViewModel.loadBarFinish()
                                    }
                                )
                            },
                            onFailure = { message ->
                                navigationViewModel.showMessage(message)
                                navigationViewModel.loadBarFinish()
                            }
                        )
                    }
                },
                onDismissRequest = {
                    showDeleteInvoiceDialog = false
                }
            )
        }
    }
    if (showBottomSheet) {
        selectedSale?.let { sale ->
            val invoiceSerial = "${sale.invoicePrefix}${office.serialPrefix}-${sale.invoiceNumber}"
            InvoicesBottonSheet(
                invoiceSerial = invoiceSerial,
                onPrintRequest = {
                    navigationViewModel.loadBarStart()
                    chargeViewModel.loadSaleById(
                        saleId = sale._id,
                        onResponse = { sale ->
                            navigationViewModel.loadBarFinish()
                            val printerInvoice58 = PrinterInvoice58(
                                sale,
                                sale.saleItems,
                                sale.customer,
                                office,
                                setting,
                                business
                            )
                            val printerInvoice80 = PrinterInvoice80(
                                sale,
                                sale.saleItems,
                                sale.customer,
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
                        },
                        onFailure = { message ->
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage(message)
                        }
                    )
                    showBottomSheet = false
                },
                onShareRequest = {
                    navigationViewModel.loadBarStart()
                    chargeViewModel.loadSaleById(
                        saleId = sale._id,
                        onResponse = {
                            navigationViewModel.loadBarFinish()
                            val buildSharePdf = BuildInvoiceSharePdf(it, it.saleItems, it.customer, office, business, setting, context)
                            buildSharePdf.sharePdf()
                        },
                        onFailure = { message ->
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage(message)
                        }
                    )
                    showBottomSheet = false
                },
                onDeleteRequest = {
                    if (business.certificateId != null && sale.cdr == null && sale.invoiceType != InvoiceType.NOTA_DE_VENTA) {
                        navigationViewModel.loadBarStart()
                        invoicesViewModel.sendInvoice(sale._id, onResponse = {
                            navigationViewModel.loadBarFinish()
                            sale.cdr = it
                            if (setting.password.isNotEmpty()) {
                                showPasswordDialog = true
                            } else {
                                showDeleteInvoiceDialog = true
                            }
                        }, onFailure = {
                            navigationViewModel.loadBarFinish()
                        })
                    } else {
                        if (setting.password.isNotEmpty()) {
                            showPasswordDialog = true
                        } else {
                            showDeleteInvoiceDialog = true
                        }
                    }
                    showBottomSheet = false
                },
                onDismissRequest = {
                    showBottomSheet = false
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        navigationViewModel.loadBarStart()
        invoicesViewModel.loadSalesOfTheDay(
            onReponse = {
                navigationViewModel.loadBarFinish()
            },
            onFailure = { message ->
                navigationViewModel.showMessage(message)
                navigationViewModel.loadBarFinish()
            }
        )
        navigationViewModel.setTitle("Comprobantes")
        printers = database.printerDao().getAll()
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            navigationViewModel.loadBarStart()
            invoicesViewModel.setIsRefreshing(true)
            invoicesViewModel.loadSalesOfTheDay(
                onReponse = {
                    navigationViewModel.loadBarFinish()
                    invoicesViewModel.setIsRefreshing(false)
                },
                onFailure = { message ->
                    navigationViewModel.showMessage(message)
                    navigationViewModel.loadBarFinish()
                    invoicesViewModel.setIsRefreshing(false)
                }
            )
        },
    ) {
        Column(modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            for (sale in salesOfTheDay) {
                var color = Color.White
                if (sale.cdr != null) {
                    color = Color.Green
                }
                if (sale.ticket != null && sale.ticket?.sunatCode == "0") {
                    color = KramviRed
                }
                ListItem(
                    colors = ListItemDefaults.colors(color),
                    modifier = Modifier.clickable {
                        showBottomSheet = true
                        selectedSale = sale
                    },
                    headlineContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "${sale.invoicePrefix}${office.serialPrefix}-${sale.invoiceNumber}")
                            Text(text = sale.user.name.uppercase())
                        }
                    },
                    supportingContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = String.format("%.2f", sale.charge))
                            Text(text = formatDate(sale.createdAt))
                        }
                    }
                )
            }
        }
    }

}