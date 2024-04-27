package ru.bz.mobile.inventory.domain.usecase

import ru.bz.mobile.inventory.data.local.db.repository.ClotRepository
import javax.inject.Inject

class ClotUseCase @Inject constructor(private val repository: ClotRepository) {
    fun getClotsListGroupedByClotLocaUnitPornSync(
        cwar: String,
        item: String
    ) = repository.getClotsListGroupedByClotLocaUnitPornSync(cwar, item)
}