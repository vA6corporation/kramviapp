package com.example.kramviapp.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
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
import com.example.kramviapp.models.ProductModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsBottonSheet(
    product: ProductModel,
    onPurchaseStock: () -> Unit,
    onAddStock: () -> Unit,
    onRemoveStock: () -> Unit,
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
            Text(text = product.fullName)
        }
        ListItem(
            colors = ListItemDefaults.colors(BottomSheetDefaults.ContainerColor),
            modifier = Modifier.clickable {
                onPurchaseStock()
            },
            headlineContent = { Text("Ingresar compra") },
            leadingContent = {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Purchase stock",
                )
            }
        )
        ListItem(
            colors = ListItemDefaults.colors(BottomSheetDefaults.ContainerColor),
            modifier = Modifier.clickable {
                onAddStock()
            },
            headlineContent = { Text("Aumentar stock") },
            leadingContent = {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add stock",
                )
            }
        )
        ListItem(
            colors = ListItemDefaults.colors(BottomSheetDefaults.ContainerColor),
            modifier = Modifier.clickable {
                onRemoveStock()
            },
            headlineContent = { Text("Reducir stock") },
            leadingContent = {
                Icon(
                    Icons.Filled.Remove,
                    contentDescription = "Remove stock",
                )
            }
        )

    }
}