package ru.bz.mobile.inventory.presentation.viewModel.settings

sealed interface Action {
    data class showMessage(val messageId: Int) : Action
    object openFilePicker : Action
    object requestPermissions : Action
}
