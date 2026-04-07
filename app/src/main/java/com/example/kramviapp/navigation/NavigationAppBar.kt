package com.example.kramviapp.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.kramviapp.models.NavigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationAppBar(
    onMenuPress: () -> Unit,
    onBackPress: () -> Unit,
    navigationViewModel: NavigationViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val title by navigationViewModel.title.collectAsState()
    val actions by navigationViewModel.actions.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var key by remember { mutableStateOf("") }
    var showMessageDialog by remember { mutableStateOf(false) }
    val isShowSearch by navigationViewModel.isShowSearch.collectAsState()
    val isBackTo by navigationViewModel.isBackTo.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val message by navigationViewModel.message.collectAsState()
    val messageDialog by navigationViewModel.messageDialog.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            navigationViewModel.clearMessage()
        }
    }

    LaunchedEffect(messageDialog) {
        messageDialog?.let {
            showMessageDialog = true
        }
    }

    if (isBackTo) {
        BackHandler {
            onBackPress()
        }
    }

    if (showMessageDialog) {
        messageDialog?.let {
            MessageDialog(message = it) {
                navigationViewModel.clearMessageDialog()
                showMessageDialog = false
            }
        }
    }

    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { navigationViewModel.onNavigateTo(NavigateTo("createProducts")) },
//            ) {
//                Icon(Icons.Filled.Add, "Floating action button.")
//            }
//        },
//        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Card(
                    shape = RoundedCornerShape(5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.DarkGray,
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp, 5.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = snackbarData.visuals.message, color = Color.White)
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (isBackTo) {
                        IconButton(onClick = {
                            onBackPress()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            onMenuPress()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                },
                actions = {
                    if (isShowSearch) {
                        TextField(
                            value = key,
                            onValueChange = { key = it },
                            placeholder = { Text("Buscar") },
                            modifier = Modifier.focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (key.isNotEmpty()) {
                                        navigationViewModel.search(key)
                                        key = ""
                                    }
                                },
                            ),
                            singleLine = true,
                            maxLines = 1,
                            trailingIcon = {
                                IconButton(onClick = { navigationViewModel.hideSearch() }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Close search"
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            )
                        )
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }

                    } else {
                        actions.filter { !it.isHide }.forEach { action ->
                            IconButton(onClick = { navigationViewModel.setClickMenu(action.id) }) {
                                Icon(
                                    imageVector = action.drawer,
                                    contentDescription = action.text
                                )
                            }
                        }
                        if (actions.find { it.isHide } != null) {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "More vert"
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                actions.filter { it.isHide }.forEach { action ->
                                    DropdownMenuItem(
                                        onClick = {
                                            showMenu = false
                                            navigationViewModel.setClickMenu(action.id)
                                        },
                                        text = {
                                            Text(action.text)
                                        },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = action.drawer,
                                                contentDescription = "Close search"
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        content = content
    )
}