package ru.bz.mobile.inventory.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import ru.bz.mobile.inventory.domain.model.locas.Loca

@Dao
interface LocaDao {

    @Query(
        """
        SELECT
            qstr,
            qnty,
            loca,
            unit,
            utcDate
        FROM $WHINH501
        WHERE
            cwar = :cwar
        AND item = :item
        AND clot = :clot
    """
    )
    fun getLocasListByCwarItemClotSync(cwar: String, item: String, clot: String): List<Loca.Dto>

}
