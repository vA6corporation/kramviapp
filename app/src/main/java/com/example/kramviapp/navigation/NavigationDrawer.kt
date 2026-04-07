package com.example.kramviapp.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.ModuleModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    loginViewModel: LoginViewModel,
    navController: NavController,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val selectedItem: MutableState<ModuleModel?> = remember { mutableStateOf(null) }
    val modules by loginViewModel.modules.collectAsState()
    val office by loginViewModel.office.collectAsState()
    val scrollState = rememberScrollState()

    if (drawerState.isOpen) {
        BackHandler {
            scope.launch {
                drawerState.close()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DismissibleDrawerSheet {
                Column(Modifier.verticalScroll(scrollState)) {
                    Text(
                        text = office.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        },
                        modifier = Modifier.padding(27.dp, 15.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    modules.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.drawable, contentDescription = null) },
                            label = { Text(item.name) },
                            selected = item == selectedItem.value,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(item.path)
                                    selectedItem.value = item
                                }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Build, contentDescription = null) },
                        label = { Text("Ajustes") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate("settings")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                        label = { Text("Cerrar sesion") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate("logout")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        },
        content = content
    )
}