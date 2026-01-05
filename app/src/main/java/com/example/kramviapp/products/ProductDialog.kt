package com.example.kramviapp.products

import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kramviapp.models.FavoriteModel
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.models.SettingModel
import com.example.kramviapp.navigation.NavigationViewModel

@SuppressLint("DefaultLocale")
@Composable
fun ProductDialog(
    product: ProductModel,
    favorites: List<FavoriteModel>,
    setting: SettingModel,
    productsViewModel: ProductsViewModel,
    navigationViewModel: NavigationViewModel,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            val scrollState = rememberScrollState()
            var stock by remember { mutableDoubleStateOf(0.0) }
            LaunchedEffect(Unit) {
                if (product.isTrackStock) {
                    navigationViewModel.loadBarStart()
                    productsViewModel.getStock(
                        product._id,
                        onResponse = {
                            stock = it
                            navigationViewModel.loadBarFinish()
                        },
                        onFailure = { message ->
                            navigationViewModel.showMessage(message)
                            navigationViewModel.loadBarFinish()
                        }
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.fullName,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (product.isTrackStock) {
                    if (stock <= 0) {
                        Row {
                            Text(text = "Stock: ")
                            Text(text = "Agotado", color = Color.Red)
                        }
                    } else {
                        Text(text = "Stock: $stock")
                    }
                } else {
                    Text(text = "Stock: Venta libre")
                }
                if (setting.showCost) {
                    Text(text = "Costo: ${String.format("%.2f", product.cost)}")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            onDismissRequest()
                        },
                    ) {
                        Text(text = "VOLVER")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    if (favorites.find { it.product._id == product._id } == null) {
                        Button(
                            onClick = {
                                productsViewModel.createFavorite(
                                    product._id,
                                    onResponse = {
                                        productsViewModel.getFavorites()
                                    },
                                    onFailure = { message ->
                                        navigationViewModel.showMessage(message)
                                    }
                                )
                            },
                        ) {
                            Text(text = "AGREGAR A FAVORITOS")
                        }
                    } else {
                        Button(
                            onClick = {
                                productsViewModel.deleteFavorite(
                                    product._id,
                                    onResponse = {
                                        productsViewModel.getFavorites()
                                    },
                                    onFailure = { message ->
                                        navigationViewModel.showMessage(message)
                                    }
                                )
                            },
                        ) {
                            Text(text = "REMOVER DE FAVORITOS")
                        }
                    }
                }
            }
        }
    }
}