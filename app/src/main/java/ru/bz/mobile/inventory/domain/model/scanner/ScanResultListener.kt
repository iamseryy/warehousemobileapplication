package ru.bz.mobile.inventory.domain.model.scanner

interface ScanResultListener {
    fun onResult(result: String)
    fun onFailure(message: String)
}