package com.example.kramviapp.biller

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.models.BillerItemModel
import com.example.kramviapp.ui.theme.DarkGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillerItemBottomSheet(
    billerItems: List<BillerItemModel>,
    onDismissRequest: (saleItemIndex: Int?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismissRequest(null)
            }
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            billerItems.forEachIndexed { index, billerItem ->
                ListItem(
                    colors = ListItemDefaults.colors(BottomSheetDefaults.ContainerColor),
                    modifier = Modifier.clickable {
                        onDismissRequest(index)
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
        }
    }
}