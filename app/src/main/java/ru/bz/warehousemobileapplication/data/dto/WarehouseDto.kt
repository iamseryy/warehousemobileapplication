package ru.bz.warehousemobileapplication.data.dto

import com.google.gson.annotations.SerializedName
import ru.bz.warehousemobileapplication.entity.Warehouse

class WarehouseDto(
    @SerializedName("id") override val id: String,
    @SerializedName("description") override val description: String
): Warehouse
