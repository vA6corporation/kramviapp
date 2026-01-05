package com.example.kramviapp.posBoard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.models.BoardItemModel
import com.example.kramviapp.models.SaleItemModel
import com.example.kramviapp.ui.theme.DarkGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardItemBottomSheet(
    boardItems: List<BoardItemModel>,
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
            boardItems.forEachIndexed { index, boardItem ->
                ListItem(
                    modifier = Modifier.clickable {
                        onDismissRequest(index)
                    },
                    headlineContent = { Text(boardItem.fullName) },
                    supportingContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if  ((boardItem.quantity % 1).toFloat() == 0f) {
                                Text(text = "x${String.format("%.0f", boardItem.quantity)}")
                            } else {
                                Text(text = "x${String.format("%.2f", boardItem.quantity)}")
                            }
                            if (boardItem.igvCode == IgvCodeType.BONIFICACION) {
                                Text(text = "Bonificacion", color = DarkGreen)
                            }
                            Text(text = boardItem.observations)
                            Text(text = String.format("%.2f", boardItem.price))
                        }
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}