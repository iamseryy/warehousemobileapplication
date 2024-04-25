package ru.bz.mobile.inventory.data.room

import androidx.annotation.MainThread
import ru.bz.mobile.inventory.data.room.dao.ClotDao
import ru.bz.mobile.inventory.model.clots.Clot

class ClotRepository(private val dao: ClotDao) {
    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getClotsListGroupedByClotLocaUnitPornSync(cwar: String, item: String): List<Clot.Dto> =
        dao.getClotsListGroupedByClotLocaUnitPornSync(cwar, item)
}
