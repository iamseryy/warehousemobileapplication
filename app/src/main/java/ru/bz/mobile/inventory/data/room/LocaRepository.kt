package ru.bz.mobile.inventory.data.room

import androidx.annotation.MainThread
import ru.bz.mobile.inventory.data.room.dao.LocaDao
import ru.bz.mobile.inventory.model.locas.Loca

class LocaRepository(private val dao: LocaDao) {
    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getLocasListByCwarItemClotSync(cwar: String, item: String, clot: String): List<Loca.Dto> =
        dao.getLocasListByCwarItemClotSync(cwar = cwar, item = item, clot = clot)

}
