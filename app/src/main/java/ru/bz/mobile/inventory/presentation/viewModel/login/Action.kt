package ru.bz.mobile.inventory.presentation.viewModel.login

sealed interface Action {
    data class showMessage(val messageId: Int) : Action
    object navigate : Action

}
