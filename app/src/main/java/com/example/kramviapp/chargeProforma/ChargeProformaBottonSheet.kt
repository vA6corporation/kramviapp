package com.example.kramviapp.chargeProforma

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargeProformaBottonSheet(
    proformaSerial: String,
    onPrintRequest: () -> Unit,
    onShareRequest: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismissRequest()
            }
        },
        sheetState = sheetState
    ) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text(text = proformaSerial)
        }
        ListItem(
            colors = ListItemDefaults.colors(BottomSheetDefaults.ContainerColor),
            modifier = Modifier.clickable {
                onPrintRequest()
            },
            headlineContent = { Text("Imprimir") },
            leadingContent = {
                Icon(
                    Icons.Filled.Print,
                    contentDescription = "Localized description",
                )
            }
        )
        ListItem(
            colors = ListItemDefaults.colors(BottomSheetDefaults.ContainerColor),
            modifier = Modifier.clickable {
                onShareRequest()
            },
            headlineContent = { Text("Compartir") },
            leadingContent = {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Localized description",
                )
            }
        )
    }
}