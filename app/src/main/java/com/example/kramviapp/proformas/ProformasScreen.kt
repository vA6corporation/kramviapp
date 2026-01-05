package com.example.kramviapp.proformas

import android.annotation.SuppressLint
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
import com.example.kramviapp.chargeProforma.ChargeProformaBottonSheet
import com.example.kramviapp.enums.InvoiceType
import com.example.kramviapp.enums.PrinterType
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.ProformaModel
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.printers.PrinterProforma58
import com.example.kramviapp.printers.PrinterProforma80
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.PrinterModel
import com.example.kramviapp.ui.theme.KramviRed
import com.example.kramviapp.utils.BuildProformaSharePdf
import com.example.kramviapp.utils.formatDate

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProformasScreen(
    database: AppDatabase,
    proformasViewModel: ProformasViewModel,
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
) {
    val setting by loginViewModel.setting.collectAsState()
    val office by loginViewModel.office.collectAsState()
    val business by loginViewModel.business.collectAsState()
    val proformasOfTheDay by proformasViewModel.proformasOfTheDay.collectAsState()
    val isRefreshing by proformasViewModel.isRefreshing.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var selectedProforma: ProformaModel? by remember { mutableStateOf(null) }
    var printers: List<PrinterModel> by remember { mutableStateOf(listOf()) }
    val context = LocalContext.current

    if (showBottomSheet) {
        selectedProforma?.let { proforma ->
            val proformaSerial = "P${office.serialPrefix}-${proforma.proformaNumber}"
            ChargeProformaBottonSheet(
                proformaSerial = proformaSerial,
                onPrintRequest = {
                    navigationViewModel.loadBarStart()
                    proformasViewModel.loadProformaById(
                        proformaId = proforma._id,
                        onResponse = { proforma ->
                            navigationViewModel.loadBarFinish()
                            val printerProforma58 = PrinterProforma58(
                                proforma,
                                proforma.proformaItems,
                                proforma.customer,
                                office,
                                setting,
                                business
                            )
                            val printerProforma80 = PrinterProforma80(
                                proforma,
                                proforma.proformaItems,
                                proforma.customer,
                                office,
                                setting,
                                business
                            )
                            for (printer in printers) {
                                if (printer.printInvoice) {
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
                    proformasViewModel.loadProformaById(
                        proformaId = proforma._id,
                        onResponse = {
                            navigationViewModel.loadBarFinish()
                            val buildSharePdf = BuildProformaSharePdf(
                                it,
                                it.proformaItems,
                                it.customer,
                                office,
                                business,
                                setting,
                                context
                            )
                            buildSharePdf.sharePdf()
                        },
                        onFailure = { message ->
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage(message)
                        }
                    )
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
        proformasViewModel.loadProformasOfTheDay(
            onReponse = {
                navigationViewModel.loadBarFinish()
            },
            onFailure = { message ->
                navigationViewModel.showMessage(message)
                navigationViewModel.loadBarFinish()
            }
        )
        navigationViewModel.setTitle("Proformas")
        printers = database.printerDao().getAll()
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            navigationViewModel.loadBarStart()
            proformasViewModel.setIsRefreshing(true)
            proformasViewModel.loadProformasOfTheDay(
                onReponse = {
                    navigationViewModel.loadBarFinish()
                    proformasViewModel.setIsRefreshing(false)
                },
                onFailure = { message ->
                    navigationViewModel.showMessage(message)
                    navigationViewModel.loadBarFinish()
                    proformasViewModel.setIsRefreshing(false)
                }
            )
        },
    ) {
        Column(modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            for (proforma in proformasOfTheDay) {
                var color = Color.White
                if (proforma.deletedAt != null) {
                    color = KramviRed
                }
                ListItem(
                    colors = ListItemDefaults.colors(color),
                    modifier = Modifier.clickable {
                        showBottomSheet = true
                        selectedProforma = proforma
                    },
                    headlineContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "P${office.serialPrefix}-${proforma.proformaNumber}")
                            Text(text = proforma.user.name.uppercase())
                        }
                    },
                    supportingContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = String.format("%.2f", proforma.charge))
                            Text(text = formatDate(proforma.createdAt))
                        }
                    }
                )
            }
        }
    }

}