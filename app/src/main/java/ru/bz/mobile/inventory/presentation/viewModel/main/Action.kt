package ru.bz.mobile.inventory.presentation.viewModel.main

import ru.bz.mobile.inventory.domain.model.IOP

sealed interface Action {
    data class showMessage(val messageId: Int) : Action
    object beginEdit : Action
    object endEdit : Action
    object undoEdit : Action
    object navigateClotsFragment : Action
    object navigateLocasFragment : Action
    data class showSaveDialog(
        val loca: String,
        val inRepo: Boolean,
        val dto: IOP.Dto?,
        val unit: String?,
        val positiveAction: ((Double) -> Unit)?,
        val negativeAction: (() -> Unit)? = null
    ) : Action

    data class showDeleteDialog(
        val positiveAction: (() -> Unit)?,
        val negativeAction: (() -> Unit)? = null
    ) : Action

    data class showClotsDialog(
        val positiveAction: (() -> Unit)?,
        val negativeAction: (() -> Unit)? = null
    ) : Action

    data class showLocaDialog(
        val loca: String,
        val positiveAction: (() -> Unit)?,
        val negativeAction: (() -> Unit)? = null
    ) : Action

    data class showLocasDialog(
        val locas: String,
        val positiveAction: (() -> Unit)?,
        val negativeAction: (() -> Unit)? = null
    ) : Action

    object clearFocus : Action

}
