package com.example.kramviapp.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.ui.theme.LightBlue

@Composable
fun HomeScreen(
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
) {
    val modules by loginViewModel.modules.collectAsState()
    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Bienvenido")
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            items(modules) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(.3.dp)
                        .background(LightBlue)
                        .clickable {
                           navigationViewModel.onNavigateTo(NavigateTo(it.path))
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Row {
                        Icon(it.drawable, contentDescription = null)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = it.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item {
                if (modules.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .padding(.3.dp)
                            .background(LightBlue)
                            .clickable {
                                navigationViewModel.onNavigateTo(NavigateTo("logout"))
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row {
                            Icon(Icons.Default.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "Cerrar sesion", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}