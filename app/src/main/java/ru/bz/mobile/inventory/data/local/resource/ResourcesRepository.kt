package ru.bz.mobile.inventory.data.local.resource

import android.content.Context
import ru.bz.mobile.inventory.R



class ResourcesRepository(val context: Context)  {
    fun getDataMatrixDelimiter() = getString(R.string.properties_dataMatrix_delimiter)
    fun getString(id: Int) = context.getString(id)
}