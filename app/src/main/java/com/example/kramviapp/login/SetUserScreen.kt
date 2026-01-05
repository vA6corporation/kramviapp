package com.example.kramviapp.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kramviapp.MainActivity
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.navigation.ConfirmDialog
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.UserModel
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("ContextCastToActivity")
@Composable
fun SetUserScreen(
    database: AppDatabase,
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    var enabledSubmit by remember { mutableStateOf(true) }
    var users: List<UserModel> by remember { mutableStateOf(listOf()) }
    var indexUser by remember { mutableIntStateOf(0) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    if (showConfirmDialog) {
        ConfirmDialog(
            onDismissRequest = {
                showConfirmDialog = false
            },
            onConfirmation = {
                scope.launch {
                    showConfirmDialog = false
                    database.userDao().delete(users[indexUser])
                    database.userDao().getAll().let {
                        users = it
                    }
                    if (users.isEmpty()) {
                        navigationViewModel.onNavigateTo(NavigateTo("login"))
                    }
                }
            },
            dialogText = "Estas seguro de eliminar?..."
        )
    }
    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Kramvi")
        users = database.userDao().getAll()
    }
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
//        Image(
//            painter = painterResource(id = R.drawable.kramvilogo),
//            contentDescription = "Preview",
//            modifier = Modifier
//                .size(100.dp)
//                .align(Alignment.CenterHorizontally)
//        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Por favor identifiquese")
        Spacer(modifier = Modifier.height(12.dp))
        Card {
            users.forEachIndexed { index, user ->
                ListItem(
                    modifier = Modifier.clickable {
                        loginViewModel.login(
                            user.email,
                            user.password,
                            onResponse = {
                                scope.launch {
                                    navigationViewModel.loadBarFinish()
                                    loginViewModel.setAccessToken(it.accessToken)
                                    if (it.office == null) {
                                        navigationViewModel.onNavigateTo(NavigateTo("setOffice"))
                                    } else {
                                        val intent = Intent(context, MainActivity::class.java)
                                        context.startActivity(intent)
                                        activity.finish()
                                    }
                                }
                            },
                            onFailure = { message ->
                                enabledSubmit = true
                                navigationViewModel.showMessage(message)
                                navigationViewModel.loadBarFinish()
                            }
                        )
                    },
                    headlineContent = {
                        Text(user.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        })
                    },
                    supportingContent = { Text(user.email) },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                showConfirmDialog = true
                                indexUser = index
                            },
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            enabled = enabledSubmit,
            onClick = {
                navigationViewModel.onNavigateTo(NavigateTo("login"))
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "USAR UNA CUENTA DIFERENTE")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "Soporte tecnico: 930 430 929", modifier = Modifier.align(Alignment.CenterHorizontally))
    }

}