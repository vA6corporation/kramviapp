package com.example.kramviapp.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kramviapp.navigation.ConfirmDialog
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.PrinterModel
import kotlinx.coroutines.launch

const val BLUETOOTH_PERMISSION_CODE = 7766;

@SuppressLint("ContextCastToActivity")
@Composable
fun SettingsScreen(
    database: AppDatabase,
    navigationViewModel: NavigationViewModel,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_DENIED &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            ),
            BLUETOOTH_PERMISSION_CODE
        )
    }

    val scope = rememberCoroutineScope()
    var showCreatePrinterDialog by remember { mutableStateOf(false) }
    var printers: List<PrinterModel> by remember { mutableStateOf(listOf()) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var indexPrinter by remember { mutableIntStateOf(0) }
    if (showCreatePrinterDialog) {
        CreatePrinterDialog(
            onDismissRequest = { printer ->
                showCreatePrinterDialog = false
                printer?.let {
                    scope.launch {
                        database.printerDao().insert(printer)
                        printers = database.printerDao().getAll()
                    }
                }
            }
        )
    }
    if (showConfirmDialog) {
        ConfirmDialog(
            onDismissRequest = {
                showConfirmDialog = false
            },
            onConfirmation = {
                scope.launch {
                    showConfirmDialog = false
                    database.printerDao().delete(printers[indexPrinter])
                    database.printerDao().getAll().let {
                        printers = it
                    }
                }
            },
            dialogText = "Estas seguro de eliminar?..."
        )
    }
    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Ajustes")
        printers = database.printerDao().getAll()
    }
    Column(
        Modifier
            .padding(10.dp)
            .fillMaxSize()) {
        printers.forEachIndexed { index, printer ->
            ListItem(
                headlineContent = {
                    Row {
                        Text(printer.name)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(printer.ipAddress)
                    }
                },
                supportingContent = {
                    Column {
                        if (printer.printInvoice) {
                            Text("COMPROBANTES")
                        }
                        if (printer.printAccount) {
                            Text("PRECUENTAS")
                        }
                        if (printer.printKitchen) {
                            Text("C.COCINA")
                        }
                        if (printer.printBar) {
                            Text("C.BARRA")
                        }
                        if (printer.printOven) {
                            Text("C.HORNO")
                        }
                        if (printer.printBox) {
                            Text("C.CAJA")
                        }
                    }
                },
                trailingContent = {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Localized description",
                        modifier = Modifier.clickable {
                            showConfirmDialog = true
                            indexPrinter = index
                        }
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
              showCreatePrinterDialog = true
            },
        ) {
            Text(text = "AGREGAR IMPRESORA")
        }
    }
}