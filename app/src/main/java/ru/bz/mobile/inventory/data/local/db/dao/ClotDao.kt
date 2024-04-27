package ru.bz.mobile.inventory.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import ru.bz.mobile.inventory.domain.model.clots.Clot


const val WHINH501 = "whinh501"

@Dao
interface ClotDao {

    @Query(
        """
        SELECT
            clot,
            unit,
            porn,
            SUM(qstr) as qstrSum,
            SUM(qnty) as qntySum,
            COUNT(loca) as locaSize,
            min(loca) as loca,
            max(utcDate) as utcDate
        FROM $WHINH501
        WHERE
            cwar = :cwar
        AND item = :item
        GROUP BY
            clot, unit, porn
    """
    )

    fun getClotsListGroupedByClotLocaUnitPornSync(cwar: String, item: String): List<Clot.Dto>
}
