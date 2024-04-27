package ru.bz.mobile.inventory.data.local.db.repository

import androidx.annotation.MainThread
import ru.bz.mobile.inventory.data.local.db.dao.ClotDao
import ru.bz.mobile.inventory.domain.model.clots.Clot
import javax.inject.Inject

class ClotRepository @Inject constructor(
    private val dao: ClotDao
) {
    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getClotsListGroupedByClotLocaUnitPornSync(cwar: String, item: String): List<Clot.Dto> =
        dao.getClotsListGroupedByClotLocaUnitPornSync(cwar, item)
}
