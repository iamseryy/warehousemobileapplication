package ru.bz.mobile.inventory.model.main

import ru.bz.mobile.inventory.model.IOP
import ru.bz.mobile.inventory.util.YesNo

data class MainModel(var isInventoryDataImported: Boolean = false, var iop: IOP = IOP.getEmpty()) {
    val iopIsEmpty: Boolean
        get() = cwar.isEmpty() && loca.isEmpty() && item.isEmpty() && clot.isEmpty()

    val iopIsFull: Boolean
        get() = cwar.isNotEmpty() && loca.isNotEmpty() && item.isNotEmpty() && clot.isNotEmpty()
    val noErrors: Boolean
        get() = cwarError == null && locaError == null && itemError == null && clotError == null
    var cwar
        get() = iop.cwar
        set(value) {
            iop.cwar = value
        }
    var loca
        get() = iop.loca
        set(value) {
            iop.loca = value
        }

    var item
        get() = iop.item
        set(value) {
            iop.item = value
        }
    var clot
        get() = iop.clot
        set(value) {
            iop.clot = value
        }
    var stkr
        get() = iop.stkr.flag
        set(value) {
            iop.stkr = YesNo.getYesNo(value)
        }
    var lock
        get() = iop.lock.flag
        set(value) {
            iop.lock = YesNo.getYesNo(value)
        }
    var illiquid
        get() = iop.illiquid.flag
        set(value) {
            iop.illiquid = YesNo.getYesNo(value)
        }
    var edited: Boolean = false
    var cwarError: String? = null
    var locaError: String? = null
    var itemError: String? = null
    var clotError: String? = null
    private var backup: MainModel? = null
    fun makeBackup() {
        backup =
            MainModel(
                isInventoryDataImported = this.isInventoryDataImported,
                iop = IOP.getEmpty()
            ).copy(
                this
            )
    }

    fun loadFromBackup() {
        backup?.let { backup ->
            this.copy(backup)
        }
    }

    fun clearBackup() {
        backup = null
    }

    private fun copy(other: MainModel): MainModel {
        return this.apply {
            isInventoryDataImported = other.isInventoryDataImported
            iop = iop.copy(other.iop)
            cwarError = other.cwarError
            locaError = other.locaError
            clotError = other.clotError
            itemError = other.itemError
            edited = other.edited
        }
    }
}
