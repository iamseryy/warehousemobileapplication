package ru.bz.mobile.inventory.domain.model.locas

import androidx.room.ColumnInfo


data class Loca(
    val id: Int = -1,
    val isChecked: Boolean = false,
    val loca: String,
    val qstr: Double,
    val qnty: Double,
    val unit: String,
    val utcDate: Long
) {
    data class Dto(
        @ColumnInfo(name = "loca") val loca: String,
        @ColumnInfo(name = "qstr") val qstr: Double,
        @ColumnInfo(name = "qnty") val qnty: Double,
        @ColumnInfo(name = "unit") val unit: String,
        @ColumnInfo(name = "utcDate") val utcDate: Long
    ) {
        fun toLoca(id: Int = -1): Loca {
            return Loca(
                id = id,
                loca = loca,
                qstr = qstr,
                qnty = qnty,
                unit = unit,
                utcDate = utcDate
            )
        }
    }
}