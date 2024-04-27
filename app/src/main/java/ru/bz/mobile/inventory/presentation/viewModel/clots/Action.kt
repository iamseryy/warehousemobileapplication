package ru.bz.mobile.inventory.presentation.viewModel.clots

sealed interface Action {
    data class showMessage(val messageId: Int) : Action
    object navigateLocasFragment : Action
    object navigateMainFragment : Action
}
