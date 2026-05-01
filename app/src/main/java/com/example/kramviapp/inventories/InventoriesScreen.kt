package com.example.kramviapp.inventories

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.kramviapp.enums.InvoiceCode
import com.example.kramviapp.incidents.IncidentsViewModel
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.ActionModel
import com.example.kramviapp.models.CreateIncidentItemModel
import com.example.kramviapp.models.CreateIncidentModel
import com.example.kramviapp.models.CreatePurchaseItemModel
import com.example.kramviapp.models.CreatePurchaseModel
import com.example.kramviapp.models.GoTo
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.navigation.PasswordDialog
import com.example.kramviapp.products.AddStockDialog
import com.example.kramviapp.products.ProductsBottonSheet
import com.example.kramviapp.products.ProductsViewModel
import com.example.kramviapp.products.PurchaseStockDialog
import com.example.kramviapp.products.RemoveStockDialog
import com.example.kramviapp.products.TrackearProductsBottonSheet
import com.example.kramviapp.ui.theme.KramviRed
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@SuppressLint("SimpleDateFormat", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoriesScreen(
    productsViewModel: ProductsViewModel,
    navigationViewModel: NavigationViewModel,
    incidentsViewModel: IncidentsViewModel,
    loginViewModel: LoginViewModel,
) {
    val context = LocalContext.current
    val setting by loginViewModel.setting.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isFinish by remember { mutableStateOf(false) }
    val onSearch by navigationViewModel.onSearch.collectAsState()
    val clickMenu by navigationViewModel.clickMenu.collectAsState()
    var pageIndex by remember { mutableIntStateOf(0) }
    val pageSize by remember { mutableIntStateOf(20) }
    var products: MutableList<ProductModel> = remember { mutableStateListOf() }
    var upc by remember { mutableStateOf("") }
    var showProductBottomSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showRemoveStockDialog by remember { mutableStateOf(false) }
    var showPurchaseStockDialog by remember { mutableStateOf(false) }
    var showTrackearProductsBottomSheet by remember { mutableStateOf(false) }
    var selectedProduct: ProductModel? by remember { mutableStateOf(null) }
    var selectedProductIndex by remember { mutableIntStateOf(0) }
    var showPasswordPurchaseStockDialog by remember { mutableStateOf(false) }
    var showPasswordAddStockDialog by remember { mutableStateOf(false) }
    var showPasswordRemoveStockDialog by remember { mutableStateOf(false) }

    onSearch?.let {
        navigationViewModel.search(null)
        navigationViewModel.loadBarStart()
        productsViewModel.getProductsByKey(
            it,
            onResponse = { foundProducts ->
                navigationViewModel.loadBarFinish()
                //products = foundProducts.toMutableList()
                isFinish = true
                products.clear()
                products.addAll(foundProducts)
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
                                    products.addAll(foundProducts)
                                    navigationViewModel.loadBarFinish()
                                },
                                onFailure = { message ->
                                    navigationViewModel.loadBarFinish()
                                    navigationViewModel.onGoTo(GoTo("createProducts?upc=${upc}"))
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

    if (showPasswordPurchaseStockDialog) {
        PasswordDialog(
            setting = setting,
            onSuccessRequest = {
                showPurchaseStockDialog = true
                showPasswordPurchaseStockDialog = false
            },
            onDismissRequest = {
                showPasswordPurchaseStockDialog = false
            }
        )
    }

    if (showPasswordAddStockDialog) {
        PasswordDialog(
            setting = setting,
            onSuccessRequest = {
                showAddStockDialog = true
                showPasswordAddStockDialog = false
            },
            onDismissRequest = {
                showPasswordAddStockDialog = false
            }
        )
    }

    if (showPasswordRemoveStockDialog) {
        PasswordDialog(
            setting = setting,
            onSuccessRequest = {
                showRemoveStockDialog = true
                showPasswordRemoveStockDialog = false
            },
            onDismissRequest = {
                showPasswordRemoveStockDialog = false
            }
        )
    }

    if (showProductBottomSheet) {
        selectedProduct?.let {
            ProductsBottonSheet(
                product = it,
                onPurchaseStock = {
                    if (setting.password.isNotEmpty()) {
                        showPasswordPurchaseStockDialog = true
                        showProductBottomSheet = false
                    } else {
                        showPurchaseStockDialog = true
                        showProductBottomSheet = false
                    }
                },
                onAddStock = {
                    if (setting.password.isNotEmpty()) {
                        showPasswordAddStockDialog = true
                        showProductBottomSheet = false
                    } else {
                        showAddStockDialog = true
                        showProductBottomSheet = false
                    }
                },
                onRemoveStock = {
                    if (setting.password.isNotEmpty()) {
                        showPasswordRemoveStockDialog = true
                        showProductBottomSheet = false
                    } else {
                        showRemoveStockDialog = true
                        showProductBottomSheet = false
                    }
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
                    val incident = CreateIncidentModel(stock.observation)
                    val incidentItem = CreateIncidentItemModel(
                        stock.quantity,
                        product.price,
                        product.cost,
                        product.unitCode,
                        product.id
                    )
                    navigationViewModel.loadBarStart()
                    incidentsViewModel.createIn(
                        incident,
                        listOf(incidentItem),
                        onResponse = {
                            products[selectedProductIndex] = product.copy(stock = product.stock + stock.quantity)
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
                    val incident = CreateIncidentModel(stock.observation)
                    val incidentItem = CreateIncidentItemModel(
                        stock.quantity,
                        product.price,
                        product.cost,
                        product.unitCode,
                        product.id
                    )
                    navigationViewModel.loadBarStart()
                    incidentsViewModel.createOut(
                        incident,
                        listOf(incidentItem),
                        onResponse = {
                            products[selectedProductIndex] = product.copy(stock = product.stock - stock.quantity)
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
            PurchaseStockDialog { stock ->
                showPurchaseStockDialog = false
                stock?.let {
                    val purchase = CreatePurchaseModel(
                        InvoiceCode.NOTA_DE_VENTA,
                        stock.observation,
                        null,
                        "",
                        null,
                        null
                    )
                    val purchaseItem = CreatePurchaseItemModel(
                        product.fullName,
                        product.id,
                        product.igvCode,
                        product.unitCode,
                        stock.quantity,
                        stock.cost,
                        product.price,
                    )
                    navigationViewModel.loadBarStart()
                    incidentsViewModel.createPurchase(
                        purchase,
                        listOf(purchaseItem),
                        onResponse = {
                            products[selectedProductIndex] = product.copy(stock = product.stock + stock.quantity)
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
                        product.id,
                        onResponse = {
                            products[selectedProductIndex] = product.copy(isTrackStock = true)
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
        navigationViewModel.setTitle("Inventario")
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
                    pageIndex = 0
                    products.clear()
                    products.addAll(it.toMutableList())
                    navigationViewModel.loadBarFinish()
                },
                onFailure = {
                    isRefreshing = false
                    navigationViewModel.loadBarFinish()
                    navigationViewModel.showMessage(it)
                }
            )
        },
    ) {
        LazyColumn {
            itemsIndexed(products) { index, product ->
                var color = Color.White
                if (product.stock <= 0 && product.isTrackStock) {
                    color = KramviRed
                }
                ListItem(
                    colors = ListItemDefaults.colors(color),
                    modifier = Modifier.clickable {
                        if (product.isTrackStock) {
                            showProductBottomSheet = true
                        } else {
                            showTrackearProductsBottomSheet = true
                        }
                        selectedProduct = product
                        selectedProductIndex = index
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
                                products.addAll(it.toMutableList())
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