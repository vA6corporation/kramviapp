package com.example.kramviapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.kramviapp.login.LoginScreen
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.login.SetOfficeScreen
import com.example.kramviapp.login.SetUserScreen
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.ui.theme.KramviappTheme
import com.jakewharton.threetenabp.AndroidThreeTen

private lateinit var loginViewModel: LoginViewModel
private val navigationViewModel = NavigationViewModel()

class LoginActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        val database = Room.databaseBuilder(this, AppDatabase::class.java, "kramvi").build()
        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }
            val message by navigationViewModel.message.collectAsState()
            val isBarLoading by navigationViewModel.isBarLoading.collectAsState()
            val title by navigationViewModel.title.collectAsState()
            val navigateTo by navigationViewModel.navigateTo.collectAsState()

            LaunchedEffect(Unit) {
                val users = database.userDao().getAll()
                if (users.isNotEmpty()) {
                    navController.navigate("setUser")
                }
            }

            LaunchedEffect(message) {
                message?.let {
                    snackbarHostState.showSnackbar(it)
                    navigationViewModel.clearMessage()
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

            KramviappTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface {
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
                    Scaffold(
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                            )
                        },
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = "login",
                            modifier = Modifier
                                .padding(paddingValues)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            composable("login") {
                                LoginScreen(database, loginViewModel, navigationViewModel)
                            }
                            composable("setOffice") {
                                SetOfficeScreen(database, loginViewModel, navigationViewModel)
                            }
                            composable("setUser") {
                                SetUserScreen(database, loginViewModel, navigationViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}