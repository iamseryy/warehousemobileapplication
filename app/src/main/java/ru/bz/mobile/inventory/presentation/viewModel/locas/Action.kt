package ru.bz.mobile.inventory.presentation.viewModel.locas

sealed interface Action {
    data class showMessage(val messageId: Int) : Action
    object navigateMainFragment : Action


}
