package com.example.kramviapp.posBoard

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kramviapp.boards.BoardsViewModel
import com.example.kramviapp.boards.TablesViewModel
import com.example.kramviapp.categories.CategoriesViewModel
import com.example.kramviapp.charge.OutStockDialog
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.PriceType
import com.example.kramviapp.enums.PrintZoneType
import com.example.kramviapp.enums.PrinterType
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.ActionModel
import com.example.kramviapp.models.BoardItemModel
import com.example.kramviapp.models.BoardModel
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.models.OutStockModel
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.models.SaleItemModel
import com.example.kramviapp.models.TableModel
import com.example.kramviapp.navigation.ConfirmDialog
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.navigation.PasswordDialog
import com.example.kramviapp.printers.PrinterCommand58
import com.example.kramviapp.printers.PrinterCommand80
import com.example.kramviapp.printers.PrinterPreaccount58
import com.example.kramviapp.printers.PrinterPreaccount80
import com.example.kramviapp.products.ProductsViewModel
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.PrinterModel
import com.example.kramviapp.saleItems.SaleItemsViewModel
import com.example.kramviapp.ui.theme.LightBlue
import com.example.kramviapp.ui.theme.LightGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PosBoardPortraitScreen(
    tableIndex: Int,
    isWaiter: Boolean,
    database: AppDatabase,
    productsViewModel: ProductsViewModel,
    boardsViewModel: BoardsViewModel,
    tablesViewModel: TablesViewModel,
    saleItemsViewModel: SaleItemsViewModel,
    navigationViewModel: NavigationViewModel,
    loginViewModel: LoginViewModel,
    categoriesViewModel: CategoriesViewModel
) {
    val setting by loginViewModel.setting.collectAsState()
    val office by loginViewModel.office.collectAsState()
    val business by loginViewModel.business.collectAsState()
    val clickMenu by navigationViewModel.clickMenu.collectAsState()
    val onSearch by navigationViewModel.onSearch.collectAsState()
    val products by productsViewModel.products.collectAsState()
    val priceLists by productsViewModel.priceLists.collectAsState()
    val categories by categoriesViewModel.categories.collectAsState()
    val boardItems by boardsViewModel.boardItems.collectAsState()
    val tables by tablesViewModel.tables.collectAsState()
    val user by loginViewModel.user.collectAsState()

    if (business.isDebtorCancel) {
        navigationViewModel.onNavigateTo(NavigateTo("subscription"))
    }

    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollState = rememberScrollState()

    var isEnabledOrder by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedProduct: ProductModel? by remember { mutableStateOf(null) }
    var selectedUpdateBoardItem: BoardItemModel? by remember { mutableStateOf(null) }
    var showBoardItemDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showConfirmBoardDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteBoardItemDialog by remember { mutableStateOf(false) }
    var showConfirmUpdateBoardItemDialog by remember { mutableStateOf(false) }
    var boardItemIndex by remember { mutableIntStateOf(0) }
    var table: TableModel? by remember { mutableStateOf(null) }
    var board: BoardModel? by remember { mutableStateOf(null) }
    var expandedPriceList by remember { mutableStateOf(false) }
    var priceListId by remember { mutableStateOf(setting.defaultPriceListId) }
    var showOutStockDialog by remember { mutableStateOf(false) }
    var showPasswordBoardDialog by remember { mutableStateOf(false) }
    var showPasswordBoardItemDialog by remember { mutableStateOf(false) }
    var showPasswordUpdateBoardItemDialog by remember { mutableStateOf(false) }
    var showSelectionAnnotationsDialog by remember { mutableStateOf(false) }
    var outStocks: List<OutStockModel> by remember { mutableStateOf(listOf()) }
    var printers: List<PrinterModel> by remember { mutableStateOf(listOf()) }

    ProductsViewModel.setPrices(products, priceListId, office, setting)

    var charge = 0.0
    var countProducts = 0.0

    for (boardItem in boardItems) {
        if (boardItem.igvCode != IgvCodeType.BONIFICACION) {
            charge += boardItem.price * boardItem.quantity
        }
        countProducts += boardItem.quantity
    }

    onSearch?.let { key ->
        navigationViewModel.search(null)
        navigationViewModel.loadBarStart()
        productsViewModel.getProductsByKey(
            key,
            onResponse = { products ->
                scope.launch {
                    navigationViewModel.loadBarFinish()
                    selectedTabIndex = 1
                    pagerState.scrollToPage(1)
                    productsViewModel.setProducts(products)
                }
            },
            onFailure = {
                navigationViewModel.loadBarFinish()
                navigationViewModel.showMessage(it)
            }
        )
    }

    clickMenu?.let {
        navigationViewModel.setClickMenu(null)
        if (it == "search") {
            navigationViewModel.showSearch()
        }
        if (it == "print_preaccount") {
            table?.let { table ->
                board?.let { board ->
                    val printerPreaccount58 = PrinterPreaccount58(
                        table,
                        board.boardItems,
                        user
                    )
                    val printerPreaccount80 = PrinterPreaccount80(
                        table,
                        board.boardItems,
                        user
                    )
                    for (printer in printers) {
                        if (printer.printAccount) {
                            when (printer.printerType) {
                                PrinterType.BLUETOOTH58 -> {
                                    printerPreaccount58.printBluetooth()
                                }

                                PrinterType.BLUETOOTH80 -> {
                                    printerPreaccount80.printBluetooth()
                                }

                                PrinterType.ETHERNET58 -> {
                                    printerPreaccount58.printEthernet(printer.ipAddress)
                                }

                                PrinterType.ETHERNET80 -> {
                                    printerPreaccount80.printEthernet(printer.ipAddress)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (it == "print_command") {
            table?.let { table ->
                board?.let { board ->
                    BoardsViewModel.printCommand(board, table, setting, user, printers)
                }
            }
        }
        if (it == "print_command_all") {
            table?.let { table ->
                board?.let { board ->
                    val kitchenItems: MutableList<BoardItemModel> = mutableListOf()
                    val barItems: MutableList<BoardItemModel> = mutableListOf()
                    val ovenItems: MutableList<BoardItemModel> = mutableListOf()
                    val boxItems: MutableList<BoardItemModel> = mutableListOf()

                    for (boardItem in board.boardItems) {
                        if (boardItem.printZone == PrintZoneType.COCINA) {
                            kitchenItems.add(boardItem)
                        }
                        if (boardItem.printZone == PrintZoneType.BARRA) {
                            barItems.add(boardItem)
                        }
                        if (boardItem.printZone == PrintZoneType.HORNO) {
                            ovenItems.add(boardItem)
                        }
                        if (boardItem.printZone == PrintZoneType.CAJA) {
                            boxItems.add(boardItem)
                        }
                    }

                    for (printer in printers) {
                        if (printer.printKitchen && kitchenItems.size > 0) {
                            val printerCommand58 = PrinterCommand58(
                                table,
                                board,
                                kitchenItems,
                                setting,
                                user,
                            )
                            val printerCommand80 = PrinterCommand80(
                                table,
                                board,
                                kitchenItems,
                                setting,
                                user
                            )
                            when (printer.printerType) {
                                PrinterType.BLUETOOTH58 -> {
                                    printerCommand58.printBluetooth()
                                }

                                PrinterType.BLUETOOTH80 -> {
                                    printerCommand80.printBluetooth()
                                }

                                PrinterType.ETHERNET58 -> {
                                    printerCommand58.printEthernet(printer.ipAddress)
                                }

                                PrinterType.ETHERNET80 -> {
                                    printerCommand80.printEthernet(printer.ipAddress)
                                }
                            }
                        }
                        if (printer.printBar && barItems.size > 0) {
                            val printerCommand58 = PrinterCommand58(
                                table,
                                board,
                                barItems,
                                setting,
                                user,
                            )
                            val printerCommand80 = PrinterCommand80(
                                table,
                                board,
                                barItems,
                                setting,
                                user,
                            )
                            when (printer.printerType) {
                                PrinterType.BLUETOOTH58 -> {
                                    printerCommand58.printBluetooth()
                                }

                                PrinterType.BLUETOOTH80 -> {
                                    printerCommand80.printBluetooth()
                                }

                                PrinterType.ETHERNET58 -> {
                                    printerCommand58.printEthernet(printer.ipAddress)
                                }

                                PrinterType.ETHERNET80 -> {
                                    printerCommand80.printEthernet(printer.ipAddress)
                                }
                            }
                        }
                        if (printer.printOven && ovenItems.size > 0) {
                            val printerCommand58 = PrinterCommand58(
                                table,
                                board,
                                ovenItems,
                                setting,
                                user,
                            )
                            val printerCommand80 = PrinterCommand80(
                                table,
                                board,
                                ovenItems,
                                setting,
                                user,
                            )
                            when (printer.printerType) {
                                PrinterType.BLUETOOTH58 -> {
                                    printerCommand58.printBluetooth()
                                }

                                PrinterType.BLUETOOTH80 -> {
                                    printerCommand80.printBluetooth()
                                }

                                PrinterType.ETHERNET58 -> {
                                    printerCommand58.printEthernet(printer.ipAddress)
                                }

                                PrinterType.ETHERNET80 -> {
                                    printerCommand80.printEthernet(printer.ipAddress)
                                }
                            }
                        }
                        if (printer.printBox && boxItems.size > 0) {
                            val printerCommand58 = PrinterCommand58(
                                table,
                                board,
                                boxItems,
                                setting,
                                user,
                            )
                            val printerCommand80 = PrinterCommand80(
                                table,
                                board,
                                boxItems,
                                setting,
                                user,
                            )
                            when (printer.printerType) {
                                PrinterType.BLUETOOTH58 -> {
                                    printerCommand58.printBluetooth()
                                }

                                PrinterType.BLUETOOTH80 -> {
                                    printerCommand80.printBluetooth()
                                }

                                PrinterType.ETHERNET58 -> {
                                    printerCommand58.printEthernet(printer.ipAddress)
                                }

                                PrinterType.ETHERNET80 -> {
                                    printerCommand80.printEthernet(printer.ipAddress)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (it == "change_board") {
            board?.let { board ->
                navigationViewModel.onNavigateTo(NavigateTo("changeBoard/${board._id}"))
            }
        }
        if (it == "delete_board") {
            if (setting.password.isEmpty()) {
                showConfirmBoardDialog = true
            } else {
                showPasswordBoardDialog = true
            }
        }
    }

    LaunchedEffect(Unit) {
        tables?.let { tables ->
            table = tables[tableIndex]
            table?.let { table ->
                navigationViewModel.setTitle("Mesa ${table.name}")
                pagerState.scrollToPage(0)
                selectedTabIndex = 0
                navigationViewModel.loadBarStart()
                boardsViewModel.loadActiveBoardByTable(
                    table._id,
                    onResponse = {
                        isEnabledOrder = true
                        navigationViewModel.loadBarFinish()
                        board = it
                        boardsViewModel.setBoardItems(it.boardItems)
                    },
                    onFailure = {
                        isEnabledOrder = true
                        navigationViewModel.loadBarFinish()
                        boardsViewModel.setBoardItems(mutableListOf())
                    }
                )
            }
        }
        val actions: MutableList<ActionModel> = mutableListOf()
        printers = database.printerDao().getAll()
        actions.add(ActionModel("search", "Buscar", Icons.Default.Search, false))
        actions.add(ActionModel("print_preaccount", "Imprimir precuenta", Icons.Default.Print))
        actions.add(ActionModel("print_command", "Imprimir comanda", Icons.Default.Print))
        actions.add(
            ActionModel(
                "print_command_all",
                "Imprimir comanda completa",
                Icons.Default.Print
            )
        )
        if (!isWaiter) {
            actions.add(ActionModel("change_board", "Cambiar mesa", Icons.Default.NorthEast))
        }
        actions.add(ActionModel("delete_board", "Anular mesa", Icons.Default.Delete))
        navigationViewModel.setActions(actions)
        scrollState.animateScrollBy(10000f)
        if (categories == null) {
            categoriesViewModel.getCategories()
        }
        if (priceLists == null) {
            productsViewModel.getPriceLists()
        }
    }

    if (showPasswordBoardDialog) {
        PasswordDialog(
            setting = setting,
            onSuccessRequest = {
                showPasswordBoardDialog = false
                board?.let { board ->
                    navigationViewModel.loadBarStart()
                    boardsViewModel.deleteBoard(
                        board._id,
                        onResponse = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.onNavigateTo(NavigateTo("boards"))
                            navigationViewModel.showMessage("Mesa anulada correctamente")
                        },
                        onFailure = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage(it)
                        }
                    )
                }
            },
            onDismissRequest = {
                showPasswordBoardDialog = false
            }
        )
    }

    if (showPasswordBoardItemDialog) {
        PasswordDialog(
            setting = setting,
            onSuccessRequest = {
                showPasswordBoardItemDialog = false
                val boardItem = boardItems[boardItemIndex]
                navigationViewModel.loadBarStart()
                boardsViewModel.deleteBoardItem(
                    boardItem.boardId,
                    boardItem._id,
                    boardItem.quantity,
                    onResponse = {
                        navigationViewModel.loadBarFinish()
                        navigationViewModel.showMessage("Anulado correctamente")
                        boardsViewModel.removeBoardItem(boardItemIndex)
                    },
                    onFailure = {
                        navigationViewModel.loadBarFinish()
                        navigationViewModel.showMessage(it)
                    }
                )
            },
            onDismissRequest = {
                showPasswordBoardItemDialog = false
            }
        )
    }

    if (showPasswordUpdateBoardItemDialog) {
        selectedUpdateBoardItem?.let { selectedUpdateBoardItem ->
            PasswordDialog(
                setting = setting,
                onSuccessRequest = {
                    showPasswordUpdateBoardItemDialog = false
                    navigationViewModel.loadBarStart()
                    val boardItem = boardItems[boardItemIndex]
                    boardsViewModel.deleteBoardItem(
                        boardItem.boardId,
                        boardItem._id,
                        boardItem.quantity - selectedUpdateBoardItem.quantity,
                        onResponse = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage("Anulado correctamente")
                            boardItem.quantity = selectedUpdateBoardItem.quantity
                            boardItem.price = selectedUpdateBoardItem.price
                            boardItem.observations = selectedUpdateBoardItem.observations
                            boardItem.preQuantity = selectedUpdateBoardItem.quantity
                            if (boardItem.quantity.equals(0.0)) {
                                boardsViewModel.removeBoardItem(boardItemIndex)
                            } else {
                                boardsViewModel.updateBoardItem(boardItemIndex, selectedUpdateBoardItem)
                            }
                        },
                        onFailure = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage(it)
                        }
                    )
                },
                onDismissRequest = {
                    showPasswordUpdateBoardItemDialog = false
                }
            )
        }
    }

    if (showSelectionAnnotationsDialog) {
        selectedProduct?.let { product ->
            SelectAnnotationsDialog(product) { observations ->
                observations?.let {
                    boardsViewModel.addBoardItem(product, observations)
                }
                showSelectionAnnotationsDialog = false
            }
        }
    }

    if (showOutStockDialog) {
        OutStockDialog(outStocks = outStocks) {
            showOutStockDialog = false
        }
    }

    if (showBoardItemDialog) {
        val boardItem = boardItems[boardItemIndex]
        BoardItemDialog(
            boardItem,
            onDeleteRequest = {
                showBoardItemDialog = false
                if (boardItem._id.isNotEmpty()) {
                    if (setting.password.isNotEmpty()) {
                        showPasswordBoardItemDialog = true
                    } else {
                        showConfirmDeleteBoardItemDialog = true
                    }
                } else {
                    boardsViewModel.removeBoardItem(boardItemIndex)
                }
            },
            onDismissRequest = { updatedBoardItem ->
                if (updatedBoardItem != null) {
                    if (boardItem._id.isNotEmpty() && updatedBoardItem.quantity < boardItem.quantity) {
                        selectedUpdateBoardItem = updatedBoardItem
                        showConfirmUpdateBoardItemDialog = true
                    } else {
                        boardsViewModel.updateBoardItem(boardItemIndex, updatedBoardItem)
                    }
                }
                showBoardItemDialog = false
            }
        )
    }

    if (showConfirmBoardDialog) {
        board?.let { board ->
            ConfirmDialog(
                onDismissRequest = {
                    showConfirmBoardDialog = false
                },
                onConfirmation = {
                    showConfirmBoardDialog = false
                    navigationViewModel.loadBarStart()
                    boardsViewModel.deleteBoard(
                        board._id,
                        onResponse = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.onNavigateTo(NavigateTo("boards"))
                            navigationViewModel.showMessage("Mesa anulada correctamente")
                        },
                        onFailure = {
                            navigationViewModel.loadBarFinish()
                            navigationViewModel.showMessage(it)
                        }
                    )
                },
                dialogText = "Esta seguro de anular la mesa?..."
            )
        }
    }

    if (showConfirmDeleteBoardItemDialog) {
        ConfirmDialog(
            onDismissRequest = {
                showConfirmDeleteBoardItemDialog = false
            },
            onConfirmation = {
                showConfirmDeleteBoardItemDialog = false
                val boardItem = boardItems[boardItemIndex]
                navigationViewModel.loadBarStart()
                boardsViewModel.deleteBoardItem(
                    boardItem.boardId,
                    boardItem._id,
                    boardItem.quantity,
                    onResponse = {
                        navigationViewModel.loadBarFinish()
                        navigationViewModel.showMessage("Anulado correctamente")
                        boardsViewModel.removeBoardItem(boardItemIndex)
                    },
                    onFailure = {
                        navigationViewModel.loadBarFinish()
                        navigationViewModel.showMessage(it)
                    }
                )
            },
            dialogText = "Este producto ya fue ordenado, esta seguro de anular?..."
        )
    }

    if (showConfirmUpdateBoardItemDialog) {
        selectedUpdateBoardItem?.let { selectedUpdateBoardItem ->
            ConfirmDialog(
                onDismissRequest = {
                    showConfirmUpdateBoardItemDialog = false
                },
                onConfirmation = {
                    showConfirmUpdateBoardItemDialog = false
                    if (setting.password.isNotEmpty()) {
                        showPasswordUpdateBoardItemDialog = true
                    } else {
                        navigationViewModel.loadBarStart()
                        val boardItem = boardItems[boardItemIndex]
                        boardsViewModel.deleteBoardItem(
                            boardItem.boardId,
                            boardItem._id,
                            boardItem.quantity - selectedUpdateBoardItem.quantity,
                            onResponse = {
                                navigationViewModel.loadBarFinish()
                                navigationViewModel.showMessage("Anulado correctamente")
                                boardItem.quantity = selectedUpdateBoardItem.quantity
                                boardItem.price = selectedUpdateBoardItem.price
                                boardItem.observations = selectedUpdateBoardItem.observations
                                boardItem.preQuantity = selectedUpdateBoardItem.quantity
                                if (boardItem.quantity.equals(0.0)) {
                                    boardsViewModel.removeBoardItem(boardItemIndex)
                                } else {
                                    boardsViewModel.updateBoardItem(boardItemIndex, selectedUpdateBoardItem)
                                }
                            },
                            onFailure = {
                                navigationViewModel.loadBarFinish()
                                navigationViewModel.showMessage(it)
                            }
                        )
                    }
                },
                dialogText = "Este producto ya fue ordenado, esta seguro de anular?..."
            )
        }
    }

    if (showBottomSheet) {
        BoardItemBottomSheet(boardItems) {
            showBottomSheet = false
            it?.let {
                boardItemIndex = it
                showBoardItemDialog = true
            }
        }
    }

    Column {
        if (setting.defaultPrice == PriceType.LISTA || setting.defaultPrice == PriceType.LISTAOFICINA) {
            priceLists?.let { priceLists ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .height(55.dp)
                            .fillMaxWidth(0.5f)
                            .clickable { showBottomSheet = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(Modifier.padding(12.dp, 0.dp)) {
                            Icon(Icons.Default.ShoppingBasket, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            if ((countProducts % 1).toFloat() == 0f) {
                                Text(text = String.format("%.0f", countProducts))
                            } else {
                                Text(text = String.format("%.2f", countProducts))
                            }
                        }
                        Row {
                            Icon(Icons.Default.Payments, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = String.format("%.2f", charge))
                        }
                    }
                    ExposedDropdownMenuBox(
                        expanded = expandedPriceList,
                        onExpandedChange = {
                            expandedPriceList = !expandedPriceList
                        }
                    ) {
                        priceLists.find { it._id == priceListId }?.let {
                            TextField(
                                readOnly = true,
                                singleLine = true,
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
                                    .fillMaxWidth()
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
        } else {
            Row(modifier = Modifier.clickable {
                showBottomSheet = true
            }) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        Icon(Icons.Default.ShoppingBasket, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        if ((countProducts % 1).toFloat() == 0f) {
                            Text(text = String.format("%.0f", countProducts))
                        } else {
                            Text(text = String.format("%.2f", countProducts))
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Row {
                        Icon(Icons.Default.Payments, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = String.format("%.2f", charge))
                    }
                }
            }
        }
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
            modifier = Modifier.weight(1f)
        ) { page ->
            if (page == 0) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = category.name.uppercase(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .padding(.3.dp)
                                .background(LightBlue)
                                .clickable {
                                    scope.launch {
                                        if (product.annotations.isNotEmpty()) {
                                            selectedProduct = product
                                            showSelectionAnnotationsDialog = true
                                        } else {
                                            boardsViewModel.addBoardItem(product)
                                        }
                                        delay(500)
                                        listState.animateScrollToItem(boardItems.size)
                                    }
                                },
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = product.fullName.uppercase(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                )
                            }
                            Text(
                                text = String.format("%.2f", product.price),
                                modifier = Modifier.offset(x = 5.dp, y = 78.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                enabled = isEnabledOrder,
                onClick = {
                    table?.let { table ->
                        navigationViewModel.loadBarStart()
                        isEnabledOrder = false
                        boardsViewModel.createBoard(
                            table._id,
                            boardItems,
                            onResponse = { board ->
                                BoardsViewModel.printCommand(
                                    board,
                                    table,
                                    setting,
                                    user,
                                    printers
                                )
                                navigationViewModel.loadBarFinish()
                                if (isWaiter) {
                                    navigationViewModel.onNavigateTo(NavigateTo("boardsWaiter"))
                                } else {
                                    navigationViewModel.onNavigateTo(NavigateTo("boards"))
                                }
                                navigationViewModel.showMessage("Se han guardado los cambios")
                            },
                            onFailure = {
                                isEnabledOrder = true
                                navigationViewModel.showMessage(it)
                                navigationViewModel.loadBarFinish()
                            }
                        )
                    }
                },
            ) {
                Text(text = "ORDENAR")
            }
            if (!isWaiter) {
                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    enabled = isEnabledOrder,
                    onClick = {
                        board?.let { board ->
                            val saleItems: MutableList<SaleItemModel> = mutableListOf()
                            for (boardItem in boardItems) {
                                val saleItem = SaleItemModel(
                                    fullName = boardItem.fullName,
                                    price = boardItem.price,
                                    onModel = "Product",
                                    quantity = boardItem.quantity,
                                    igvCode = boardItem.igvCode,
                                    preIgvCode = boardItem.igvCode,
                                    unitCode = boardItem.unitCode,
                                    productId = boardItem.productId,
                                    prices = listOf(),
                                    isTrackStock = boardItem.isTrackStock,
                                    observations = boardItem.observations,
                                )
                                saleItems.add(saleItem)
                            }
                            saleItemsViewModel.setSaleItems(saleItems)
                            navigationViewModel.onNavigateTo(NavigateTo("charge/boards?boardId=${board._id}"))
                        }
                    },
                ) {
                    Text(text = "COBRAR")
                }
            }
        }
    }
}