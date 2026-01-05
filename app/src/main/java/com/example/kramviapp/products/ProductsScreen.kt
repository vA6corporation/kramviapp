package com.example.kramviapp.products

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.kramviapp.charge.ChargeViewModel
import com.example.kramviapp.enums.InvoiceType
import com.example.kramviapp.incidents.IncidentsViewModel
import com.example.kramviapp.models.ActionModel
import com.example.kramviapp.models.CreateIncidentItemModel
import com.example.kramviapp.models.CreateIncidentModel
import com.example.kramviapp.models.CreatePurchaseItemModel
import com.example.kramviapp.models.CreatePurchaseModel
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.ui.theme.KramviRed
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

@SuppressLint("SimpleDateFormat", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    productsViewModel: ProductsViewModel,
    navigationViewModel: NavigationViewModel,
    incidentsViewModel: IncidentsViewModel,
    chargeViewModel: ChargeViewModel,
) {
    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isFinish by remember { mutableStateOf(false) }
    val onSearch by navigationViewModel.onSearch.collectAsState()
    val clickMenu by navigationViewModel.clickMenu.collectAsState()
    var pageIndex by remember { mutableIntStateOf(0) }
    val pageSize by remember { mutableIntStateOf(20) }
    var products: List<ProductModel> by remember { mutableStateOf(listOf()) }
    var upc by remember { mutableStateOf("") }
    var showProductBottomSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showRemoveStockDialog by remember { mutableStateOf(false) }
    var showPurchaseStockDialog by remember { mutableStateOf(false) }
    var showTrackearProductsBottomSheet by remember { mutableStateOf(false) }
    var selectedProduct: ProductModel? by remember { mutableStateOf(null) }

    onSearch?.let {
        navigationViewModel.search(null)
        navigationViewModel.loadBarStart()
        productsViewModel.getProductsByKey(
            it,
            onResponse = { foundProducts ->
                navigationViewModel.loadBarFinish()
                products = foundProducts
            },
            onFailure = { message ->
                navigationViewModel.loadBarFinish()
                navigationViewModel.showMessage(message)
            }
        )
    }

    clickMenu?.let { id ->
        navigationViewModel.setClickMenu(null)
        when (id) {
            "show_search" -> {
                navigationViewModel.showSearch()
            }

            "scan_code" -> {
                val options = GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_EAN_13)
                    .build()
                val scanner = GmsBarcodeScanning.getClient(context, options)
                scanner.startScan()
                    .addOnSuccessListener { barcode ->
                        // Task completed successfully
                        val key: String? = barcode.rawValue
                        key?.let {
                            upc = it
                            navigationViewModel.loadBarStart()
                            productsViewModel.getProductsByKey(
                                it,
                                onResponse = { foundProducts ->
                                    products = foundProducts
                                    navigationViewModel.loadBarFinish()
                                },
                                onFailure = { message ->
                                    navigationViewModel.loadBarFinish()
                                    navigationViewModel.onNavigateTo(NavigateTo("createProducts?upc=${upc}"))
                                }
                            )
                        }
                    }
                    .addOnCanceledListener {
                        // Task canceled
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                    }
            }

            else -> {}
        }
    }

    if (showProductBottomSheet) {
        selectedProduct?.let {
            ProductsBottonSheet(
                product = it,
                onPurchaseStock = {
                    showPurchaseStockDialog = true
                    showProductBottomSheet = false
                },
                onAddStock = {
                    showAddStockDialog = true
                    showProductBottomSheet = false
                },
                onRemoveStock = {
                    showRemoveStockDialog = true
                    showProductBottomSheet = false
                },
                onDismissRequest = {
                    showProductBottomSheet = false
                }
            )
        }
    }

    if (showAddStockDialog) {
        selectedProduct?.let { product ->
            AddStockDialog { stock ->
                showAddStockDialog = false
                stock?.let {
                    val incident = CreateIncidentModel(stock.observations)
                    val incidentItem = CreateIncidentItemModel(
                        stock.quantity,
                        product.cost,
                        product.unitCode,
                        product._id
                    )
                    navigationViewModel.loadBarStart()
                    incidentsViewModel.createIn(
                        incident,
                        listOf(incidentItem),
                        onResponse = {
                            product.stock += stock.quantity
                            navigationViewModel.showMessage("Se han guardado los cambios")
                            navigationViewModel.loadBarFinish()
                        },
                        onFailure = {
                            navigationViewModel.loadBarFinish()
                        }
                    )
                }
            }
        }
    }

    if (showRemoveStockDialog) {
        selectedProduct?.let { product ->
            RemoveStockDialog { stock ->
                showRemoveStockDialog = false
                stock?.let {
                    val incident = CreateIncidentModel(stock.observations)
                    val incidentItem = CreateIncidentItemModel(
                        stock.quantity,
                        product.cost,
                        product.unitCode,
                        product._id
                    )
                    navigationViewModel.loadBarStart()
                    incidentsViewModel.createOut(
                        incident,
                        listOf(incidentItem),
                        onResponse = {
                            product.stock -= stock.quantity
                            navigationViewModel.showMessage("Se han guardado los cambios")
                            navigationViewModel.loadBarFinish()
                        },
                        onFailure = {
                            navigationViewModel.loadBarFinish()
                        }
                    )
                }
            }
        }
    }

    if (showPurchaseStockDialog) {
        selectedProduct?.let { product ->
            PurchaseStockDialog(
                chargeViewModel,
            ) { stock ->
                showPurchaseStockDialog = false
                stock?.let {
                    val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    val currentDate = sdf.format(Date())
                    val purchase = CreatePurchaseModel(
                        InvoiceType.NOTA_DE_VENTA,
                        stock.observations,
                        false,
                        stock.paymentMethodId,
                        currentDate,
                        null,
                        null,
                        null
                    )
                    val purchaseItem = CreatePurchaseItemModel(
                        product.fullName,
                        product._id,
                        product.igvCode,
                        product.unitCode,
                        stock.quantity,
                        stock.cost,
                        product.price,
                        null
                    )
                    navigationViewModel.loadBarStart()
                    incidentsViewModel.createPurchase(
                        purchase,
                        listOf(purchaseItem),
                        onResponse = {
                            product.stock += stock.quantity
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage("Se han guardado los cambios")
                        },
                        onFailure = {
                            navigationViewModel.showMessage(it)
                            navigationViewModel.loadBarFinish()
                        }
                    )
                }
            }
        }
    }

    if (showTrackearProductsBottomSheet) {
        selectedProduct?.let { product ->
            TrackearProductsBottonSheet(
                product,
                onTrackStock = {
                    navigationViewModel.loadBarStart()
                    showTrackearProductsBottomSheet = false
                    productsViewModel.trackStock(
                        product._id,
                        onResponse = {
                            product.isTrackStock = true
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage("Se han guardado los cambios")
                        },
                        onFailure = {
                            showTrackearProductsBottomSheet = false
                            navigationViewModel.loadBarFinish()
                        }
                    )
                }
            ) {
                showTrackearProductsBottomSheet = false
            }
        }
    }

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Productos")
        val actions: MutableList<ActionModel> = mutableListOf()
        actions.add(ActionModel("show_search", "Buscar", Icons.Default.Search, false))
        actions.add(ActionModel("scan_code", "Scanear codigo", Icons.Filled.QrCodeScanner, false))
        navigationViewModel.setActions(actions)
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            navigationViewModel.loadBarStart()
            pageIndex = 0
            productsViewModel.getProductsByPage(
                pageIndex + 1,
                pageSize,
                onResponse = {
                    isRefreshing = false
                    navigationViewModel.loadBarFinish()
                    products = it
                },
                onFailure = {
                    isRefreshing = false
                    navigationViewModel.loadBarFinish()
                    navigationViewModel.showMessage(it)
                }
            )
        },
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigationViewModel.onNavigateTo(NavigateTo("createProducts")) },
                ) {
                    Icon(Icons.Filled.Add, "Floating action button.")
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { innerPadding ->
            LazyColumn(Modifier.padding(innerPadding)) {
                items(products) { product ->
                    var color = Color.White
                    if (product.stock <= 0 && product.isTrackStock) {
                        color = KramviRed
                    }
                    ListItem(
                        colors = ListItemDefaults.colors(color),
                        modifier = Modifier.clickable {
                            //if (product.isTrackStock) {
                            //    showProductBottomSheet = true
                            //} else {
                            //    showTrackearProductsBottomSheet = true
                            //}
                            //selectedProduct = product
                        },
                        headlineContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = product.fullName)
                            }
                        },
                        supportingContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = String.format("%.2f", product.price))
                                if (product.isTrackStock) {
                                    Text(text = "Stock: ${product.stock}")
                                } else {
                                    Text(text = "Venta libre")
                                }
                            }
                        }
                    )
                }
                item {
                    LaunchedEffect(Unit) {
                        if (!isLoading && !isFinish) {
                            isLoading = true
                            navigationViewModel.loadBarStart()
                            productsViewModel.getProductsByPage(
                                pageIndex + 1,
                                pageSize,
                                onResponse = {
                                    if (it.isEmpty()) {
                                        isFinish = true
                                    }
                                    isLoading = false
                                    navigationViewModel.loadBarFinish()
                                    val mutableProducts: MutableList<ProductModel> =
                                        products.toMutableList()
                                    mutableProducts.addAll(it)
                                    products = mutableProducts.toList()
                                    pageIndex++
                                },
                                onFailure = {
                                    isLoading = false
                                    navigationViewModel.loadBarFinish()
                                    navigationViewModel.showMessage(it)
                                }
                            )
                        }
                        //Do something when List end has been reached
                    }
                }
            }
        }
    }
}