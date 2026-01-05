package com.example.kramviapp.openTurn

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.emptyPreferences
import com.example.kramviapp.enums.PrinterType
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.ActionModel
import com.example.kramviapp.models.CreateTurnModel
import com.example.kramviapp.models.ExpenseModel
import com.example.kramviapp.models.SummarySaleItemModel
import com.example.kramviapp.navigation.ConfirmDialog
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.printers.PrinterTurn58
import com.example.kramviapp.printers.PrinterTurn80
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.PrinterModel
import com.example.kramviapp.ui.theme.DarkGreen
import com.example.kramviapp.ui.theme.KramviRed
import com.example.kramviapp.utils.formatDate
import com.example.kramviapp.utils.formatTime
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Gray
import kotlinx.coroutines.delay

@Composable
fun OpenTurnScreen(
    database: AppDatabase,
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
    openTurnViewModel: OpenTurnViewModel,
) {
    val user by loginViewModel.user.collectAsState()
    val turn by openTurnViewModel.turn.collectAsState()
    val summaryPayments by openTurnViewModel.summaryPayments.collectAsState()
    var summarySaleItems: List<SummarySaleItemModel> by remember { mutableStateOf(listOf()) }
    val expenses by openTurnViewModel.expenses.collectAsState()
    val clickMenu by navigationViewModel.clickMenu.collectAsState()

    var showOpenTurnDialog by remember { mutableStateOf(false) }
    var showEditCashDialog by remember { mutableStateOf(false) }
    var showAddObservationsDialog by remember { mutableStateOf(false) }
    var showCloseTurnDialog by remember { mutableStateOf(false) }
    var showCreateExpenseDialog by remember { mutableStateOf(false) }
    var showEditExpenseDialog by remember { mutableStateOf(false) }
    var selectedExpense: ExpenseModel? by remember { mutableStateOf(null) }
    var cashCollected by remember { mutableDoubleStateOf(0.0) }
    var totalCollected by remember { mutableDoubleStateOf(0.0) }
    var totalExpenses by remember { mutableDoubleStateOf(0.0) }
    var printers: List<PrinterModel> by remember { mutableStateOf(listOf()) }

    var sumCollected = 0.0
    var sumCash = 0.0
    var sumExpenses = 0.0

    summaryPayments.forEach {
        sumCollected += it.totalCharge
        if (it.paymentMethod.name == "EFECTIVO") {
            sumCash += it.totalCharge
        }
    }

    expenses.forEach {
        sumExpenses += it.charge
    }

    cashCollected = sumCash
    totalCollected = sumCollected
    totalExpenses = sumExpenses

    clickMenu?.let {
        navigationViewModel.setClickMenu(null)
        if (it == "edit_cash") {
            showEditCashDialog = true
        }
        if (it == "add_observations") {
            showAddObservationsDialog = true
        }
        if (it == "print_box") {
            turn?.let { turn ->
                navigationViewModel.loadBarStart()
                openTurnViewModel.loadSummarySaleItemsByTurn(turn._id, onResponse = { summarySaleItems ->
                    navigationViewModel.loadBarFinish()
                    val printerTurn58 = PrinterTurn58(
                        turn,
                        expenses,
                        summaryPayments,
                        summarySaleItems,
                        user
                    )
                    val printerTurn80 = PrinterTurn80(
                        turn,
                        expenses,
                        summaryPayments,
                        summarySaleItems,
                        user
                    )
                    for (printer in printers) {
                        if (printer.printInvoice) {
                            when (printer.printerType) {
                                PrinterType.BLUETOOTH58 -> {
                                    printerTurn58.printBluetooth()
                                }

                                PrinterType.BLUETOOTH80 -> {
                                    printerTurn80.printBluetooth()
                                }

                                PrinterType.ETHERNET58 -> {
                                    printerTurn58.printEthernet(printer.ipAddress)
                                }

                                PrinterType.ETHERNET80 -> {
                                    printerTurn80.printEthernet(printer.ipAddress)
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Estado de caja")
        val actions: MutableList<ActionModel> = mutableListOf()
        actions.add(ActionModel("print_box", "Imprimir caja", Icons.Default.Print))
        actions.add(ActionModel("edit_cash", "Modificar apertura", Icons.Default.Info))
        actions.add(ActionModel("add_observations", "Agregar observaciones", Icons.Default.Info))
        navigationViewModel.setActions(actions)
        printers = database.printerDao().getAll()
    }

    if (showOpenTurnDialog) {
        OpenTurnDialog { openCash ->
            showOpenTurnDialog = false
            openCash?.let {
                navigationViewModel.loadBarStart()
                openTurnViewModel.createTurnUser(
                    CreateTurnModel(openCash = it),
                    onResponse = {
                        navigationViewModel.loadBarFinish()
                        navigationViewModel.showMessage("Caja aperturada correctamente")
                    },
                    onFailure = { message ->
                        navigationViewModel.loadBarFinish()
                        navigationViewModel.showMessage(message)
                    }
                )
            }
        }
    }

    if (showEditCashDialog) {
        EditCashTurnDialog(onDismissRequest = { openCash ->
            showEditCashDialog = false
            openCash?.let {
                turn?.let { turn ->
                    turn.openCash = openCash
                    navigationViewModel.loadBarStart()
                    openTurnViewModel.updateTurn(
                        turn._id,
                        turn,
                        onResponse = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage("Se han guardado los cambios")
                        },
                        onFailure = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage(it)
                        },
                    )
                }
            }
        })
    }

    if (showAddObservationsDialog) {
        AddObservationsDialog(onDismissRequest = { observations ->
            showAddObservationsDialog = false
            observations?.let {
                turn?.let { turn ->
                    turn.observations = it
                    navigationViewModel.loadBarStart()
                    openTurnViewModel.updateTurn(
                        turn._id,
                        turn,
                        onResponse = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage("Se han guardado los cambios")
                        },
                        onFailure = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage(it)
                        },
                    )
                }
            }
        })
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        turn.let { turn ->
            if (turn == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "La caja esta cerrada", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = {
                            showOpenTurnDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("APERTURAR CAJA") }
                }
            } else {
                LaunchedEffect(Unit) {
                    navigationViewModel.loadBarStart()
                    openTurnViewModel.loadSummaryPaymentsByTurn(turn._id)
                    openTurnViewModel.loadExpensesByTurn(turn._id)
                    navigationViewModel.loadBarFinish()
                }
                if (showCloseTurnDialog) {
                    ConfirmDialog(
                        onDismissRequest = { showCloseTurnDialog = false },
                        onConfirmation = {
                            showCloseTurnDialog = false
                            navigationViewModel.loadBarStart()
                            openTurnViewModel.loadCloseTurn(
                                turn._id,
                                onResponse = {
                                    navigationViewModel.loadBarFinish()
                                    navigationViewModel.showMessage("Caja cerrada correctamente")
                                },
                                onFailure = {
                                    navigationViewModel.loadBarFinish()
                                    navigationViewModel.showMessage(it)
                                }
                            )
                        },
                        dialogText = "Esta seguro de cerrar la caja?...",
                    )
                }
                if (showCreateExpenseDialog) {
                    CreateExpenseDialog(turn._id) { expense ->
                        showCreateExpenseDialog = false
                        expense?.let {
                            navigationViewModel.loadBarStart()
                            openTurnViewModel.createExpense(
                                expense,
                                onResponse = {
                                    navigationViewModel.loadBarFinish()
                                    openTurnViewModel.addExpense(it)
                                    navigationViewModel.showMessage("Gasto agregado correctamente")
                                },
                                onFailure = { message ->
                                    navigationViewModel.loadBarFinish()
                                    navigationViewModel.showMessage(message)
                                }
                            )
                        }
                    }
                }
                if (showEditExpenseDialog) {
                    selectedExpense?.let { selectedExpense ->
                        EditExpenseDialog(
                            turn._id,
                            selectedExpense,
                            onDismissRequest = { expense ->
                                showEditExpenseDialog = false
                                expense?.let {
                                    navigationViewModel.loadBarStart()
                                    openTurnViewModel.updateExpense(
                                        selectedExpense._id,
                                        expense,
                                        onResponse = {
                                            navigationViewModel.loadBarFinish()
                                            navigationViewModel.showMessage("Se han guardado los cambios")
                                            openTurnViewModel.loadExpensesByTurn(turn._id)
                                        },
                                        onFailure = { message ->
                                            navigationViewModel.loadBarFinish()
                                            navigationViewModel.showMessage(message)
                                        }
                                    )
                                }
                            },
                            onDeleteRequest = {
                                showEditExpenseDialog = false
                                navigationViewModel.loadBarStart()
                                openTurnViewModel.deleteExpense(
                                    selectedExpense._id,
                                    onResponse = {
                                        navigationViewModel.loadBarFinish()
                                        navigationViewModel.showMessage("Eliminado correctamente")
                                        openTurnViewModel.loadExpensesByTurn(turn._id)
                                    },
                                    onFailure = {
                                        navigationViewModel.loadBarFinish()
                                        navigationViewModel.showMessage(it)
                                    }
                                )
                            }
                        )
                    }
                }
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Resumen de caja",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "F/H de apertura")
                                Text(text = "${formatDate(turn.createdAt)} ${formatTime(turn.createdAt)}")
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Monto de apertura")
                                Text(text = String.format("%.2f", turn.openCash))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total recaudado")
                                Text(text = String.format("%.2f", totalCollected))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total gastos")
                                Text(text = String.format("%.2f", totalExpenses))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Efectivo final", color = DarkGreen)
                                Text(text = String.format("%.2f", cashCollected + turn.openCash - totalExpenses), color = DarkGreen)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Observaciones")
                                if (turn.observations.isEmpty()) {
                                    Text(text = "NINGUNO", color = Color.Gray)
                                } else {
                                    Text(text = turn.observations)
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Divider(color = Color.Gray, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(5.dp))
                            for (summaryPayment in summaryPayments) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "${summaryPayment.paymentMethod.name} (${summaryPayment.totalQuantity})")
                                    Text(text = String.format("%.2f", summaryPayment.totalCharge))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            showCloseTurnDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("CERRAR CAJA") }
                    Spacer(modifier = Modifier.height(10.dp))
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Gastos",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            for (expense in expenses) {
                                ListItem(
                                    modifier = Modifier.clickable {
                                        selectedExpense = expense
                                        showEditExpenseDialog = true
                                    },
                                    headlineContent = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(text = expense.concept.lowercase())
                                            Text(text = String.format("%.2f", expense.charge))
                                        }
                                    },
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            showCreateExpenseDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("AGREGAR GASTO") }
                    Spacer(modifier = Modifier.height(10.dp))
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Productos vendidos",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            for (summarySaleItem in summarySaleItems) {
                                ListItem(
                                    headlineContent = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            if  ((summarySaleItem.totalQuantity % 1).toFloat() == 0f) {
                                                Text(text = "${summarySaleItem.fullName} (${String.format("%.0f", summarySaleItem.totalQuantity)})")
                                            } else {
                                                Text(text = "${summarySaleItem.fullName} (${String.format("%.2f", summarySaleItem.totalQuantity)})")
                                            }
                                            Text(text = String.format("%.2f", summarySaleItem.totalSale))
                                        }
                                    },
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            navigationViewModel.loadBarStart()
                            openTurnViewModel.loadSummarySaleItemsByTurn(turn._id, onResponse = {
                                summarySaleItems = it
                                navigationViewModel.loadBarFinish()
                            })
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("MOSTRAR PRODUCTOS VENDIDOS") }
                }
            }
        }
    }
}