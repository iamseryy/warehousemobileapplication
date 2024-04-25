package ru.bz.mobile.inventory.presentation

fun interface FieldValidator {
    fun validate(s: CharSequence?): Boolean
}

object Validators {
    private const val ITEM_MASK = "01234567890Zz"
    private const val CLOT_MASK = "0123456789-migMIG"

    val itemValidator = FieldValidator { s -> s?.all { c -> c in ITEM_MASK } ?: false }
    val clotValidator = FieldValidator { s -> s?.all { c -> c in CLOT_MASK } ?: false }
}