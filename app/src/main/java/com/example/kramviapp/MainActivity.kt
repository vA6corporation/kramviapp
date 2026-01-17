package com.example.kramviapp

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.kramviapp.biller.BillerScreen
import com.example.kramviapp.biller.BillerViewModel
import com.example.kramviapp.biller.ChargeBillerLandscapeScreen
import com.example.kramviapp.biller.ChargeBillerPortraitScreen
import com.example.kramviapp.biller.CreditBillerLandscapeScreen
import com.example.kramviapp.biller.CreditBillerPortraitScreen
import com.example.kramviapp.boards.BoardsScreen
import com.example.kramviapp.boards.BoardsViewModel
import com.example.kramviapp.boards.BoardsWaiterScreen
import com.example.kramviapp.boards.ChangeBoardsScreen
import com.example.kramviapp.boards.TablesViewModel
import com.example.kramviapp.categories.CategoriesViewModel
import com.example.kramviapp.charge.ChargeCreditPortraitScreen
import com.example.kramviapp.charge.ChargeLandscapeScreen
import com.example.kramviapp.charge.ChargePortraitScreen
import com.example.kramviapp.charge.ChargeViewModel
import com.example.kramviapp.chargeProforma.ChargeProformaLandscapeScreen
import com.example.kramviapp.chargeProforma.ChargeProformaPortraitScreen
import com.example.kramviapp.customers.CustomersViewModel
import com.example.kramviapp.home.HomeScreen
import com.example.kramviapp.incidents.IncidentsViewModel
import com.example.kramviapp.inventories.InventoriesScreen
import com.example.kramviapp.invoices.InvoicesScreen
import com.example.kramviapp.invoices.InvoicesViewModel
import com.example.kramviapp.login.KramviWebScreen
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.login.LogoutScreen
import com.example.kramviapp.models.ModuleModel
import com.example.kramviapp.navigation.NavigationAppBar
import com.example.kramviapp.navigation.NavigationDrawer
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.openTurn.OpenTurnScreen
import com.example.kramviapp.openTurn.OpenTurnViewModel
import com.example.kramviapp.posBoard.PosBoardLandscapeScreen
import com.example.kramviapp.posBoard.PosBoardPortraitScreen
import com.example.kramviapp.posFastFood.PosFastFoodLandscapeScreen
import com.example.kramviapp.posFastFood.PosFastFoodPortraitScreen
import com.example.kramviapp.posStandard.PosStandardLandscapeScreen
import com.example.kramviapp.posStandard.PosStandardPortraitScreen
import com.example.kramviapp.products.CreateProductsScreen
import com.example.kramviapp.products.ProductsScreen
import com.example.kramviapp.products.ProductsViewModel
import com.example.kramviapp.proformaItems.ProformaItemsScreen
import com.example.kramviapp.proformaItems.ProformaItemsViewModel
import com.example.kramviapp.proformar.ProformarLandscapeScreen
import com.example.kramviapp.proformar.ProformarPortraitScreen
import com.example.kramviapp.proformas.ProformasScreen
import com.example.kramviapp.proformas.ProformasViewModel
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.saleItems.SaleItemsScreen
import com.example.kramviapp.saleItems.SaleItemsViewModel
import com.example.kramviapp.settings.SettingsScreen
import com.example.kramviapp.subscription.SubscriptionScreen
import com.example.kramviapp.ui.theme.KramviRed
import com.example.kramviapp.ui.theme.KramviappTheme
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.launch

private lateinit var loginViewModel: LoginViewModel
private lateinit var navigationViewModel: NavigationViewModel
private lateinit var openTurnViewModel: OpenTurnViewModel
private lateinit var boardsViewModel: BoardsViewModel
private lateinit var tablesViewModel: TablesViewModel
private lateinit var saleItemsViewModel: SaleItemsViewModel
private lateinit var proformaItemsViewModel: ProformaItemsViewModel
private lateinit var customersViewModel: CustomersViewModel
private lateinit var invoicesViewModel: InvoicesViewModel
private lateinit var productsViewModel: ProductsViewModel
private lateinit var billerViewModel: BillerViewModel
private lateinit var chargeViewModel: ChargeViewModel

private lateinit var proformasViewModel: ProformasViewModel
private lateinit var categoriesViewModel: CategoriesViewModel
private lateinit var incidentsViewModel: IncidentsViewModel

private val mainScreens = listOf(
    "home",
    "openTurn",
    "posStandard",
    "posFastFood",
    "boards",
    "boardsWaiter",
    "proformar",
    "proformas",
    "products",
    "inventories",
    "invoices",
    "biller",
    "kramviWeb",
    "settings",
    "subscription",
    "logout"
)

class MainActivity : ComponentActivity() {

    private fun checkForUpdates() {
        val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
                if (activityResult.resultCode != RESULT_OK) {
                    Log.e("update", "No pudimos actualizar")
                }
            }

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    private fun onUnauthorized(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        this.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        checkForUpdates()
        enableEdgeToEdge()

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        navigationViewModel = ViewModelProvider(this)[NavigationViewModel::class.java]
        openTurnViewModel = ViewModelProvider(this)[OpenTurnViewModel::class.java]
        boardsViewModel = ViewModelProvider(this)[BoardsViewModel::class.java]
        tablesViewModel = ViewModelProvider(this)[TablesViewModel::class.java]
        saleItemsViewModel = ViewModelProvider(this)[SaleItemsViewModel::class.java]
        proformaItemsViewModel = ViewModelProvider(this)[ProformaItemsViewModel::class.java]
        customersViewModel = ViewModelProvider(this)[CustomersViewModel::class.java]
        invoicesViewModel = ViewModelProvider(this)[InvoicesViewModel::class.java]
        productsViewModel = ViewModelProvider(this)[ProductsViewModel::class.java]
        billerViewModel = ViewModelProvider(this)[BillerViewModel::class.java]
        chargeViewModel = ViewModelProvider(this)[ChargeViewModel::class.java]
        proformasViewModel = ViewModelProvider(this)[ProformasViewModel::class.java]
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        incidentsViewModel = ViewModelProvider(this)[IncidentsViewModel::class.java]

        val database = Room.databaseBuilder(this, AppDatabase::class.java, "kramvi").build()

        setContent {
            val context = LocalContext.current
            val configuration = LocalConfiguration.current
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val isBarLoading by navigationViewModel.isBarLoading.collectAsState()
            val isSpinnerLoading by navigationViewModel.isSpinnerLoading.collectAsState()
            val navigateTo by navigationViewModel.navigateTo.collectAsState()
            val isLostNetwork by navigationViewModel.isLostNetwork.collectAsState()
            val currentPath by navigationViewModel.currentPath.collectAsState()
            val profile by loginViewModel.profile.collectAsState()
            val turn by openTurnViewModel.turn.collectAsState()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val route = navBackStackEntry?.destination?.route

            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    navigationViewModel.onLostNetwork(false)
                    //take action when network connection is gained
                }

                override fun onLost(network: Network) {
                    navigationViewModel.onLostNetwork(true)
                    //take action when network connection is lost
                }
            })

            LaunchedEffect(route) {
                route?.let {
                    if (currentPath != it) {
                        navigationViewModel.setActions(listOf())
                    }
                    if (mainScreens.contains(it)) {
                        navigationViewModel.hideBackTo()
                    } else {
                        navigationViewModel.showBackTo()
                    }
                    navigationViewModel.onSetCurrentPath(it)
                }
            }

            LaunchedEffect(navigateTo) {
                navigateTo?.let {
                    navigationViewModel.onNavigateTo(null)
                    if (it.isNoBack) {
                        navController.navigate(it.path) {
                            popUpTo(0)
                        }
                    } else {
                        navController.navigate(it.path)
                    }
                }
            }

            LaunchedEffect(profile) {
                profile?.let {
                    val activeModule = it.activeModule
                    val setting = it.setting
                    val office = it.office
                    val business = it.business
                    val user = it.user
                    val modules = mutableListOf<ModuleModel>()
                    if (activeModule.openBox) {
                        if (user.privileges["openBox"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Estado de caja",
                                    drawable = Icons.Default.PointOfSale,
                                    path = "openTurn"
                                )
                            )
                        }
                    }
                    if (activeModule.posStandard) {
                        if (user.privileges["posStandard"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Punto de venta",
                                    drawable = Icons.Default.DesktopWindows,
                                    path = "posStandard"
                                )
                            )
                        }
                    }
                    if (activeModule.posFastFood) {
                        if (user.privileges["posFastFood"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Punto de venta",
                                    drawable = Icons.Default.DesktopWindows,
                                    path = "posFastFood"
                                )
                            )
                        }
                    }
                    if (activeModule.boards) {
                        if (user.privileges["boards"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Atencion de mesas",
                                    drawable = Icons.Default.DesktopWindows,
                                    path = "boards"
                                )
                            )
                        }
                    }
                    if (activeModule.boardsWaiter) {
                        if (user.privileges["boardsWaiter"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Atencion de mesas",
                                    drawable = Icons.Default.DesktopWindows,
                                    path = "boardsWaiter"
                                )
                            )
                        }
                    }
                    if (activeModule.proformar) {
                        if (user.privileges["proformar"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Proformar",
                                    drawable = Icons.Default.DesktopWindows,
                                    path = "proformar"
                                )
                            )
                        }
                    }
                    if (activeModule.proformas) {
                        if (user.privileges["proformas"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Proformas",
                                    drawable = Icons.Default.CheckBox,
                                    path = "proformas"
                                )
                            )
                        }
                    }
                    if (activeModule.products) {
                        if (user.privileges["products"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Productos",
                                    drawable = Icons.Default.ShoppingBasket,
                                    path = "products"
                                )
                            )
                        }
                    }
                    if (activeModule.inventories) {
                        if (user.privileges["inventories"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Inventario",
                                    drawable = Icons.Default.CheckCircle,
                                    path = "inventories"
                                )
                            )
                        }
                    }
                    if (activeModule.invoices) {
                        if (user.privileges["invoices"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Comprobantes",
                                    drawable = Icons.Default.Receipt,
                                    path = "invoices"
                                )
                            )
                        }
                    }
                    if (activeModule.biller) {
                        if (user.privileges["biller"] == true || user.isAdmin) {
                            modules.add(
                                ModuleModel(
                                    name = "Facturador",
                                    drawable = Icons.Default.Star,
                                    path = "biller"
                                )
                            )
                        }
                    }
                    modules.add(
                        ModuleModel(
                            name = "KramviWeb",
                            drawable = Icons.AutoMirrored.Filled.OpenInNew,
                            path = "kramviWeb"
                        )
                    )

                    loginViewModel.setModules(modules)
                    loginViewModel.setSetting(setting)
                    loginViewModel.setOffice(office)
                    loginViewModel.setBusiness(business)
                    loginViewModel.setUser(user)
                    navigationViewModel.loadSpinnerFinish()

                    if (turn == null) {
                        if (setting.isOfficeTurn) {
                            openTurnViewModel.loadTurnOffice()
                        } else {
                            openTurnViewModel.loadTurnUser()
                        }
                    }
                    if (business.isDebtor) {
                        navigationViewModel.showMessageDialog("Es necesario renovar la suscripcion\n")
                    }
                }
                if (profile == null) {
                    val accessToken = loginViewModel.getAccessToken()
                    if (accessToken.isNotEmpty()) {
                        loginViewModel.setAccessToken(accessToken)
                        openTurnViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        boardsViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        tablesViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        customersViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        invoicesViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        productsViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        billerViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        chargeViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        proformasViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        categoriesViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })
                        incidentsViewModel.setAccessToken(accessToken, onUnauthorized = { onUnauthorized(context) })

                        loginViewModel.loadProfile(
                            onFailure = {
                                val intent = Intent(context, LoginActivity::class.java)
                                context.startActivity(intent)
                                this@MainActivity.finish()
                            }
                        )
                    } else {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        this@MainActivity.finish()
                    }
                }
            }

            KramviappTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface {
                    NavigationDrawer(
                        drawerState = drawerState,
                        loginViewModel = loginViewModel,
                        navController = navController,
                    ) {
                        NavigationAppBar(
                            onMenuPress = {
                                scope.launch {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    } else {
                                        drawerState.open()
                                    }
                                }
                            },
                            onBackPress = {
                                navController.navigateUp()
                            },
                            navigationViewModel = navigationViewModel
                        ) { paddingValues ->
                            if (isLostNetwork) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(65.dp)
                                        .zIndex(1f)
                                        .background(KramviRed),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "NO HAY CONEXION A INTERNET",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                            if (isBarLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(30.dp)
                                        .zIndex(1f),
                                ) {
                                    LinearProgressIndicator(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        trackColor = MaterialTheme.colorScheme.secondary,
                                    )
                                }
                            }
                            if (isSpinnerLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .zIndex(1f),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.width(64.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            trackColor = MaterialTheme.colorScheme.secondary,
                                        )
                                    }
                                }
                            }
                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                composable("home") {
                                    HomeScreen(
                                        loginViewModel,
                                        navigationViewModel,
                                    )
                                }
                                composable("openTurn") {
                                    OpenTurnScreen(
                                        database,
                                        loginViewModel,
                                        navigationViewModel,
                                        openTurnViewModel
                                    )
                                }
                                composable("posStandard") {
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            PosStandardLandscapeScreen(
                                                loginViewModel,
                                                productsViewModel,
                                                navigationViewModel,
                                                saleItemsViewModel,
                                                categoriesViewModel,
                                            )
                                        }

                                        else -> {
                                            PosStandardPortraitScreen(
                                                loginViewModel,
                                                productsViewModel,
                                                navigationViewModel,
                                                saleItemsViewModel,
                                            )
                                        }
                                    }
                                }
                                composable("saleItems") {
                                    SaleItemsScreen(
                                        loginViewModel,
                                        saleItemsViewModel,
                                    )
                                }
                                composable("proformaItems") {
                                    ProformaItemsScreen(
                                        loginViewModel,
                                        proformaItemsViewModel,
                                    )
                                }
                                composable(
                                    "charge/{navigateTo}?boardId={boardId}",
                                    arguments = listOf(
                                        navArgument("boardId") { defaultValue = "" },
                                        navArgument("navigateTo") { defaultValue = "" }
                                    )
                                ) { backStackEntry ->
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            ChargeLandscapeScreen(
                                                backStackEntry.arguments?.getString("navigateTo")
                                                    ?: "",
                                                backStackEntry.arguments?.getString("boardId"),
                                                database,
                                                loginViewModel,
                                                navigationViewModel,
                                                saleItemsViewModel,
                                                customersViewModel,
                                                openTurnViewModel,
                                                chargeViewModel,
                                            )
                                        }

                                        else -> {
                                            ChargePortraitScreen(
                                                backStackEntry.arguments?.getString("navigateTo")
                                                    ?: "",
                                                backStackEntry.arguments?.getString("boardId"),
                                                database,
                                                loginViewModel,
                                                navigationViewModel,
                                                saleItemsViewModel,
                                                customersViewModel,
                                                openTurnViewModel,
                                                chargeViewModel,
                                            )
                                        }
                                    }
                                }

                                composable(
                                    "chargeCredit/{navigateTo}?boardId={boardId}",
                                    arguments = listOf(
                                        navArgument("boardId") { defaultValue = "" },
                                        navArgument("navigateTo") { defaultValue = "" }
                                    )
                                ) { backStackEntry ->
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            ChargeLandscapeScreen(
                                                backStackEntry.arguments?.getString("navigateTo")
                                                    ?: "",
                                                backStackEntry.arguments?.getString("boardId"),
                                                database,
                                                loginViewModel,
                                                navigationViewModel,
                                                saleItemsViewModel,
                                                customersViewModel,
                                                openTurnViewModel,
                                                chargeViewModel,
                                            )
                                        }

                                        else -> {
                                            ChargeCreditPortraitScreen(
                                                backStackEntry.arguments?.getString("navigateTo")
                                                    ?: "",
                                                backStackEntry.arguments?.getString("boardId"),
                                                database,
                                                loginViewModel,
                                                navigationViewModel,
                                                saleItemsViewModel,
                                                customersViewModel,
                                                openTurnViewModel,
                                                chargeViewModel,
                                            )
                                        }
                                    }
                                }
                                composable("posFastFood") {
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            PosFastFoodLandscapeScreen(
                                                loginViewModel,
                                                navigationViewModel,
                                                productsViewModel,
                                                saleItemsViewModel,
                                                categoriesViewModel
                                            )
                                        }

                                        else -> {
                                            PosFastFoodPortraitScreen(
                                                loginViewModel,
                                                navigationViewModel,
                                                productsViewModel,
                                                saleItemsViewModel,
                                                categoriesViewModel
                                            )
                                        }
                                    }
                                }
                                composable("boards") {
                                    BoardsScreen(
                                        boardsViewModel,
                                        tablesViewModel,
                                        navigationViewModel
                                    )
                                }
                                composable("boardsWaiter") {
                                    BoardsWaiterScreen(
                                        boardsViewModel,
                                        tablesViewModel,
                                        navigationViewModel
                                    )
                                }
                                composable("changeBoard/{boardId}") { backStackEntry ->
                                    ChangeBoardsScreen(
                                        backStackEntry.arguments?.getString("boardId") ?: "",
                                        boardsViewModel,
                                        tablesViewModel,
                                        navigationViewModel,
                                    )
                                }
                                composable("posBoard/{tableIndex}/{isWaiter}") { backStackEntry ->
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            backStackEntry.arguments?.let { arguments ->
                                                val tableIndex = arguments.getString("tableIndex")
                                                val isWaiter = arguments.getString("isWaiter")
                                                if (tableIndex != null && isWaiter != null) {
                                                    PosBoardLandscapeScreen(
                                                        tableIndex.toInt(),
                                                        isWaiter.toBoolean(),
                                                        database,
                                                        productsViewModel,
                                                        boardsViewModel,
                                                        tablesViewModel,
                                                        saleItemsViewModel,
                                                        navigationViewModel,
                                                        loginViewModel,
                                                        categoriesViewModel,
                                                    )
                                                }
                                            }
                                        }

                                        else -> {
                                            backStackEntry.arguments?.let { arguments ->
                                                val tableIndex = arguments.getString("tableIndex")
                                                val isWaiter = arguments.getString("isWaiter")
                                                if (tableIndex != null && isWaiter != null) {
                                                    PosBoardPortraitScreen(
                                                        tableIndex.toInt(),
                                                        isWaiter.toBoolean(),
                                                        database,
                                                        productsViewModel,
                                                        boardsViewModel,
                                                        tablesViewModel,
                                                        saleItemsViewModel,
                                                        navigationViewModel,
                                                        loginViewModel,
                                                        categoriesViewModel
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                composable("proformar") {
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            ProformarLandscapeScreen(
                                                loginViewModel,
                                                productsViewModel,
                                                navigationViewModel,
                                                proformaItemsViewModel,
                                                categoriesViewModel,
                                            )
                                        }

                                        else -> {
                                            ProformarPortraitScreen(
                                                loginViewModel,
                                                productsViewModel,
                                                navigationViewModel,
                                                proformaItemsViewModel,
                                            )
                                        }
                                    }
                                }
                                composable("charge/proforma") {
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            ChargeProformaLandscapeScreen(
                                                database,
                                                loginViewModel,
                                                navigationViewModel,
                                                proformaItemsViewModel,
                                                customersViewModel,
                                                proformasViewModel,
                                            )
                                        }

                                        else -> {
                                            ChargeProformaPortraitScreen(
                                                database,
                                                loginViewModel,
                                                navigationViewModel,
                                                proformaItemsViewModel,
                                                customersViewModel,
                                                proformasViewModel,
                                            )
                                        }
                                    }
                                }
                                composable("proformas") {
                                    ProformasScreen(
                                        database,
                                        proformasViewModel,
                                        loginViewModel,
                                        navigationViewModel
                                    )
                                }
                                composable("products") {
                                    ProductsScreen(
                                        productsViewModel,
                                        navigationViewModel,
                                        incidentsViewModel,
                                        chargeViewModel,
                                    )
                                }
                                composable("inventories") {
                                    InventoriesScreen(
                                        productsViewModel,
                                        navigationViewModel,
                                        incidentsViewModel,
                                        chargeViewModel,
                                        loginViewModel,
                                    )
                                }
                                composable("createProducts?upc={upc}", arguments = listOf(
                                    navArgument("upc") { defaultValue = "" }
                                )) {backStackEntry ->
                                    CreateProductsScreen(
                                        backStackEntry.arguments?.getString("upc"),
                                        productsViewModel,
                                        navigationViewModel,
                                        categoriesViewModel,
                                        loginViewModel,
                                    )
                                }
                                composable("invoices") {
                                    InvoicesScreen(
                                        database,
                                        invoicesViewModel,
                                        chargeViewModel,
                                        loginViewModel,
                                        navigationViewModel
                                    )
                                }
                                composable("biller") {
                                    BillerScreen(navigationViewModel, loginViewModel)
                                }
                                composable("chargeBiller") {
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            ChargeBillerLandscapeScreen(
                                                database,
                                                loginViewModel,
                                                billerViewModel,
                                                navigationViewModel,
                                                customersViewModel,
                                                openTurnViewModel,
                                                chargeViewModel,
                                            )
                                        }

                                        else -> {
                                            ChargeBillerPortraitScreen(
                                                database,
                                                loginViewModel,
                                                billerViewModel,
                                                navigationViewModel,
                                                customersViewModel,
                                                openTurnViewModel,
                                                chargeViewModel,
                                            )
                                        }
                                    }
                                }
                                composable("creditBiller") {
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            CreditBillerLandscapeScreen(
                                                database,
                                                loginViewModel,
                                                navigationViewModel,
                                                saleItemsViewModel,
                                                customersViewModel,
                                                openTurnViewModel,
                                                chargeViewModel,
                                            )
                                        }

                                        else -> {
                                            CreditBillerPortraitScreen(
                                                database,
                                                loginViewModel,
                                                navigationViewModel,
                                                saleItemsViewModel,
                                                customersViewModel,
                                                openTurnViewModel,
                                                chargeViewModel,
                                            )
                                        }
                                    }
                                }
                                composable("kramviWeb") {
                                    KramviWebScreen(navigationViewModel, loginViewModel)
                                }
                                composable("settings") {
                                    SettingsScreen(database, navigationViewModel)
                                }
                                composable("subscription") {
                                    SubscriptionScreen(navigationViewModel)
                                }
                                composable("logout") {
                                    LogoutScreen(
                                        database,
                                        loginViewModel,
                                        navigationViewModel,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}