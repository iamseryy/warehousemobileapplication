package ru.bz.mobile.inventory.domain.model.clots

import androidx.room.ColumnInfo

data class Clot(
    val id: Int = -1,
    val isChecked: Boolean = false,
    val isEnabled: Boolean = true,
    val clot: String,
    val loca: String?,
    val locaSize: Int = 0,
    val qstrSum: Double,
    val qntySum: Double,
    val unit: String,
    val porn: String,
    var utcDate: Long = 0,
) {

    data class Dto(
        @ColumnInfo(name = "clot") val clot: String,
        @ColumnInfo(name = "loca") val loca: String,
        @ColumnInfo(name = "qstrSum") val qstr: Double,
        @ColumnInfo(name = "qntySum") val qnty: Double,
        @ColumnInfo(name = "locaSize") val locaSize: Int,
        @ColumnInfo(name = "unit") val unit: String,
        @ColumnInfo(name = "porn") val porn: String,
        @ColumnInfo(name = "utcDate") var utcDate: Long,
    ) {
        fun toClot(
            id: Int = -1
        ): Clot {
            return Clot(
                id = id,
                isEnabled = locaSize > 1,
                clot = clot,
                loca = loca,
                locaSize = locaSize,
                qstrSum = qstr,
                qntySum = qnty,
                unit = unit,
                porn = porn,
                utcDate = utcDate
            )
        }
    }
}
