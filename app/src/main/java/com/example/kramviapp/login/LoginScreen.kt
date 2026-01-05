package com.example.kramviapp.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kramviapp.MainActivity
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.room.AppDatabase
import com.example.kramviapp.room.UserModel
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(
    database: AppDatabase,
    loginViewModel: LoginViewModel,
    navigationViewModel: NavigationViewModel,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var enabledSubmit by remember { mutableStateOf(true) }
    var checked by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Kramvi")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface)
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
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            maxLines = 1,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }){
                    Icon(imageVector  = image, description)
                }
            }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            enabled = enabledSubmit,
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    enabledSubmit = false
                    navigationViewModel.loadBarStart()
                    loginViewModel.login(
                        email,
                        password,
                        onResponse = {
                            scope.launch {
                                navigationViewModel.loadBarFinish()
                                loginViewModel.setAccessToken(it.accessToken)
                                if (checked) {
                                    val user = UserModel()
                                    user.name = it.user.name
                                    user.email = email
                                    user.password = password
                                    database.userDao().insert(user)
                                }
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
                        },
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "INGRESAR")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Recuerdame")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "Soporte tecnico: 930 430 929", modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}