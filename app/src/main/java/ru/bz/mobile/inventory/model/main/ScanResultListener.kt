package ru.bz.mobile.inventory.model.main

interface ScanResultListener {
    fun onResult(result: String)
    fun onFailure(message: String)
}