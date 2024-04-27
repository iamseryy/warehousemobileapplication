package ru.bz.mobile.inventory.domain.usecase

import ru.bz.mobile.inventory.data.local.db.repository.LocaRepository
import javax.inject.Inject

class LocaUseCase @Inject constructor(private val repository: LocaRepository){
    fun getLocasListByCwarItemClotSync(
        cwar: String,
        item: String,
        clot: String) = repository.getLocasListByCwarItemClotSync(cwar, item, clot)
}