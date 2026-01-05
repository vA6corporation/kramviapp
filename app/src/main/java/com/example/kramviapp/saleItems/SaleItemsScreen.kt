package com.example.kramviapp.saleItems

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.ui.theme.DarkGreen

@SuppressLint("DefaultLocale")
@Composable
fun SaleItemsScreen(
    loginViewModel: LoginViewModel,
    saleItemsViewModel: SaleItemsViewModel,
) {
    val setting by loginViewModel.setting.collectAsState()
    val saleItems by saleItemsViewModel.saleItems.collectAsState()
    var saleItemIndex by remember { mutableIntStateOf(0) }
    var showSaleItemsDialog by remember { mutableStateOf(false) }

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

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        saleItems.forEachIndexed { index, saleItem ->
            ListItem(
                modifier = Modifier.clickable {
                    saleItemIndex = index
                    showSaleItemsDialog = true
                },
                headlineContent = { Text(saleItem.fullName) },
                supportingContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if  ((saleItem.quantity % 1).toFloat() == 0f) {
                            Text(text = "x${String.format("%.0f", saleItem.quantity)}")
                        } else {
                            Text(text = "x${String.format("%.2f", saleItem.quantity)}")
                        }
                        if (saleItem.igvCode == IgvCodeType.BONIFICACION) {
                            Text(text = "Bonificacion", color = DarkGreen)
                        }
                        Text(text = String.format("%.2f", saleItem.price * saleItem.quantity))
                    }
                },
            )
        }
    }
}