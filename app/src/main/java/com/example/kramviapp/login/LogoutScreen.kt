package com.example.kramviapp.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kramviapp.LoginActivity
import com.example.kramviapp.MainActivity
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.UserModel
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun LogoutScreen(
    database: AppDatabase,
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    val businesses by loginViewModel.businesses.collectAsState()
    val user by loginViewModel.user.collectAsState()
    var users: List<UserModel> by remember { mutableStateOf(listOf()) }
    LaunchedEffect(Unit) {
        users = database.userDao().getAll()
        navigationViewModel.setTitle("Sucursales")
        if (businesses.isEmpty() && user.assignedOfficeId == null) {
            navigationViewModel.loadBarStart()
            loginViewModel.loadBusinesses(
                onResponse = {
                    navigationViewModel.loadBarFinish()
                },
                onFailure = { message ->
                    navigationViewModel.showMessage(message)
                    navigationViewModel.loadBarFinish()
                }
            )
        }
    }
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = user.name, style = MaterialTheme.typography.titleMedium)
        Text(text = user.email)
        Spacer(modifier = Modifier.height(12.dp))
        businesses.forEach { business ->
            Text(
                text = business.businessName.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            business.offices.forEach { office ->
                Button(
                    onClick = {
                        loginViewModel.setBusinessOffice(
                            business,
                            office,
                            onResponse = {
                                scope.launch {
                                    loginViewModel.setAccessToken(it.accessToken)
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                    context.startActivity(intent)
                                }
                            },
                            onFailure = {}
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(office.name.uppercase()) }
            }
        }
        FilledTonalButton(
            onClick = {
                scope.launch {
                    loginViewModel.setAccessToken("")
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    activity.finish()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("CERRAR SESION") }
    }
}