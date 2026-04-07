package com.example.kramviapp.proformaItems

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
import com.example.kramviapp.saleItems.SaleItemsDialog
import com.example.kramviapp.saleItems.SaleItemsViewModel
import com.example.kramviapp.ui.theme.DarkGreen

@SuppressLint("DefaultLocale")
@Composable
fun ProformaItemsScreen(
    loginViewModel: LoginViewModel,
    proformaItemsViewModel: ProformaItemsViewModel,
) {
    val setting by loginViewModel.setting.collectAsState()
    val proformaItems by proformaItemsViewModel.proformaItems.collectAsState()
    var proformaItemIndex by remember { mutableIntStateOf(0) }
    var showProformaItemsDialog by remember { mutableStateOf(false) }

    if (showProformaItemsDialog) {
        ProformaItemsDialog(
            proformaItems[proformaItemIndex],
            onDeleteRequest = {
                proformaItemsViewModel.removeProformaItem(proformaItemIndex)
                showProformaItemsDialog = false
            },
            onDismissRequest = { proformaItem ->
                if (proformaItem != null) {
                    proformaItemsViewModel.updateProformaItem(proformaItemIndex, proformaItem)
                }
                showProformaItemsDialog = false
            }
        )
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        proformaItems.forEachIndexed { index, saleItem ->
            ListItem(
                modifier = Modifier.clickable {
                    proformaItemIndex = index
                    showProformaItemsDialog = true
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