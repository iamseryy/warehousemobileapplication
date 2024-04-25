package ru.bz.mobile.inventory.presentation

import android.content.Context
import ru.bz.mobile.inventory.R

class ResourcesProvider(private val context:Context) {
    val DATAMATRIX_DELIMITER = getString(R.string.properties_dataMatrix_delimiter)!!
    fun getString(id:Int): String? {
        return context.getString(id)
    }
}