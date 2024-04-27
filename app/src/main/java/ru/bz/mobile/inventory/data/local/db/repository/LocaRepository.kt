package ru.bz.mobile.inventory.data.local.db.repository

import androidx.annotation.MainThread
import ru.bz.mobile.inventory.data.local.db.dao.LocaDao
import ru.bz.mobile.inventory.domain.model.locas.Loca
import javax.inject.Inject

class LocaRepository @Inject constructor(
    private val dao: LocaDao
) {

    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getLocasListByCwarItemClotSync(cwar: String, item: String, clot: String): List<Loca.Dto> =
        dao.getLocasListByCwarItemClotSync(cwar = cwar, item = item, clot = clot)

}
