package com.example.kramviapp.posFastFood

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kramviapp.categories.CategoriesViewModel
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.PriceType
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.ActionModel
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.navigation.ConfirmDialog
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.products.ProductsViewModel
import com.example.kramviapp.saleItems.SaleItemsDialog
import com.example.kramviapp.saleItems.SaleItemsViewModel
import com.example.kramviapp.ui.theme.DarkGreen
import com.example.kramviapp.ui.theme.LightBlue
import com.example.kramviapp.ui.theme.LightGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosFastFoodLandscapeScreen(
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
    productsViewModel: ProductsViewModel,
    saleItemsViewModel: SaleItemsViewModel,
    categoriesViewModel: CategoriesViewModel
) {
    val setting by loginViewModel.setting.collectAsState()
    val office by loginViewModel.office.collectAsState()
    val business by loginViewModel.business.collectAsState()
    val clickMenu by navigationViewModel.clickMenu.collectAsState()
    val onSearch by navigationViewModel.onSearch.collectAsState()
    val products by productsViewModel.products.collectAsState()
    val categories by categoriesViewModel.categories.collectAsState()
    val priceLists by productsViewModel.priceLists.collectAsState()
    val saleItems by saleItemsViewModel.saleItems.collectAsState()

    if (business.isDebtorCancel) {
        navigationViewModel.onNavigateTo(NavigateTo("subscription"))
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollState = rememberScrollState()
    var saleItemIndex by remember { mutableIntStateOf(0) }
    var showSaleItemsDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var expandedPriceList by remember { mutableStateOf(false) }
    var priceListId by remember { mutableStateOf(setting.defaultPriceListId) }

    ProductsViewModel.setPrices(products, priceListId, office, setting)

    var charge = 0.0

    for (saleItem in saleItems) {
        if (saleItem.igvCode != IgvCodeType.BONIFICACION) {
            charge += saleItem.price * saleItem.quantity
        }
    }

    onSearch?.let {
        navigationViewModel.search(null)
        navigationViewModel.loadBarStart()
    }
    clickMenu?.let {
        navigationViewModel.setClickMenu(null)
        if (it == "search") {
            navigationViewModel.showSearch()
        }
    }
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
    if (showConfirmDialog) {
        ConfirmDialog(
            onDismissRequest = {
               showConfirmDialog = false
            },
            onConfirmation = {
                showConfirmDialog = false
                saleItemsViewModel.removeAllSaleItems()
            },
            dialogText = "Esta seguro de cancelar la venta?..."
        )
    }
    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Punto de venta")
        val actions: MutableList<ActionModel> = mutableListOf()
        actions.add(ActionModel("search", "Buscar", Icons.Default.Search, false))
        navigationViewModel.setActions(actions)
        scrollState.animateScrollBy(10000f)
        if (categories == null) {
            categoriesViewModel.getCategories()
        }
        if (priceLists == null) {
            productsViewModel.getPriceLists()
        }
    }
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(Modifier.fillMaxWidth(.7f)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        scope.launch {
                            selectedTabIndex = 0
                            pagerState.scrollToPage(0)
                        }
                    }
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Categorias")
                    }
                }
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        scope.launch {
                            selectedTabIndex = 1
                            pagerState.scrollToPage(1)
                        }
                    }
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Productos")
                    }
                }
            }
            HorizontalPager(
                state = pagerState,
            ) { page ->
                if (page == 0) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                    ) {
                        categories?.let { categories ->
                            items(categories) { category ->
                                Box(
                                    modifier = Modifier
                                        .height(100.dp)
                                        .padding(.3.dp)
                                        .background(LightGreen)
                                        .clickable {
                                            scope.launch {
                                                pagerState.scrollToPage(1)
                                                selectedTabIndex = 1
                                                if (category.products == null) {
                                                    navigationViewModel.loadBarStart()
                                                    productsViewModel.getProductsByCategory(
                                                        category._id,
                                                        onResponse = {
                                                            navigationViewModel.loadBarFinish()
                                                            category.products = it
                                                            productsViewModel.setProducts(it)
                                                        },
                                                        onFailure = { message ->
                                                            navigationViewModel.showMessage(message)
                                                            navigationViewModel.loadBarFinish()
                                                        }
                                                    )
                                                } else {
                                                    productsViewModel.setProducts(category.products!!.toMutableList())
                                                }
                                            }
                                        },
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(text = category.name.uppercase(), style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                    ) {
                        items(products) { product ->
                            Box(
                                modifier = Modifier
                                    .height(100.dp)
                                    .padding(.3.dp)
                                    .background(LightBlue)
                                    .clickable {
                                        scope.launch {
                                            saleItemsViewModel.addSaleItem(product)
                                            delay(500)
                                            listState.animateScrollToItem(saleItems.size)
                                        }
                                    },
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(text = product.fullName.uppercase(), style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(text = String.format("%.2f", product.price), modifier = Modifier.offset(x = 5.dp, y = 78.dp), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
        Column(
            Modifier
                .fillMaxHeight()
                .background(Color.White)) {
            if (setting.defaultPrice == PriceType.LISTA || setting.defaultPrice == PriceType.LISTAOFICINA) {
                priceLists?.let { priceLists ->
                    ExposedDropdownMenuBox(
                        expanded = expandedPriceList,
                        onExpandedChange = {
                            expandedPriceList = !expandedPriceList
                        }
                    ) {
                        priceLists.find { it._id == priceListId }?.let {
                            TextField(
                                readOnly = true,
                                value = it.name,
                                onValueChange = { },
                                label = { Text("Lista de precios") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedPriceList
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                colors = ExposedDropdownMenuDefaults.textFieldColors()
                            )
                        }
                        ExposedDropdownMenu(
                            expanded = expandedPriceList,
                            onDismissRequest = { expandedPriceList = false }
                        ) {
                            for (priceList in priceLists) {
                                DropdownMenuItem(
                                    onClick = {
                                        priceListId = priceList._id
                                        expandedPriceList = false
                                    },
                                    text = {
                                        Text(priceList.name.uppercase())
                                    }
                                )
                            }
                        }
                    }
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(saleItems) {index, saleItem ->
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
                                Text(text = String.format("%.2f", saleItem.price * saleItem.quantity))                            }
                        },
                    )
                }
                item {
                    ListItem(
                        headlineContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total", fontWeight = FontWeight.Bold)
                                Text(text = String.format("%.2f", charge), fontWeight = FontWeight.Bold)
                            }
                        },
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = {
                          showConfirmDialog = true
                    },
                ) {
                    Text(text = "CANCELAR")
                }
                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    onClick = {
                        navigationViewModel.onNavigateTo(NavigateTo("charge/posFastFood"))
                    },
                ) {
                    Text(text = "COBRAR")
                }
            }
        }
    }
}