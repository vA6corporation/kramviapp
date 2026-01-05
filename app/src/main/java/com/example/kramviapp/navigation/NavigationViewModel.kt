package com.example.kramviapp.navigation

import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.ActionModel
import com.example.kramviapp.models.NavigateTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel: ViewModel() {

    private val _title = MutableStateFlow("Kramvi")
    val title = _title.asStateFlow()
    fun setTitle(title: String) { _title.value = title }

    private val _message: MutableStateFlow<String?> = MutableStateFlow(null)
    val message = _message.asStateFlow()
    fun showMessage(message: String) { _message.value = message }
    fun clearMessage() { _message.value = null }

    private val _messageDialog: MutableStateFlow<String?> = MutableStateFlow(null)
    val messageDialog = _messageDialog.asStateFlow()
    fun showMessageDialog(messageDialog: String) { _messageDialog.value = messageDialog }
    fun clearMessageDialog() { _messageDialog.value = null }

    private val _isLostNetwork: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLostNetwork = _isLostNetwork.asStateFlow()

    private val _currentPath: MutableStateFlow<String> = MutableStateFlow("")
    val currentPath = _currentPath.asStateFlow()
    fun onSetCurrentPath(currentPath: String) {
        _currentPath.value = currentPath
    }
    fun onLostNetwork(isLostNetwork: Boolean) { _isLostNetwork.value = isLostNetwork }

    private val _actions: MutableStateFlow<List<ActionModel>> = MutableStateFlow(listOf())
    val actions = _actions.asStateFlow()
    fun setActions(actions: List<ActionModel>) {
        _isShowSearch.value = false
        _actions.value = actions
    }

    private val _isBackTo: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isBackTo = _isBackTo.asStateFlow()
    fun showBackTo() { _isBackTo.value = true }
    fun hideBackTo() { _isBackTo.value = false }

    private val _navigateTo: MutableStateFlow<NavigateTo?> = MutableStateFlow(null)
    val navigateTo = _navigateTo.asStateFlow()
    fun onNavigateTo(navigateTo: NavigateTo?) { _navigateTo.value = navigateTo }

    private val _isShowSearch = MutableStateFlow(false)
    val isShowSearch = _isShowSearch.asStateFlow()
    fun showSearch() { _isShowSearch.value = true }
    fun hideSearch() { _isShowSearch.value = false }

    private val _onSearch: MutableStateFlow<String?> = MutableStateFlow(null)
    val onSearch = _onSearch.asStateFlow()
    fun search(key: String?) { _onSearch.value = key }

    private val _clickMenu: MutableStateFlow<String?> = MutableStateFlow(null)
    val clickMenu = _clickMenu.asStateFlow()
    fun setClickMenu(id: String?) { _clickMenu.value = id }

    private val _isSpinnerLoading = MutableStateFlow(true)
    val isSpinnerLoading = _isSpinnerLoading.asStateFlow()
    fun loadSpinnerStart() { _isSpinnerLoading.value = true }
    fun loadSpinnerFinish() { _isSpinnerLoading.value = false }

    private val _isBarLoading = MutableStateFlow(false)
    val isBarLoading = _isBarLoading.asStateFlow()
    fun loadBarStart() { _isBarLoading.value = true }
    fun loadBarFinish() { _isBarLoading.value = false }
}