package ru.bz.mobile.inventory.domain.usecase

import ru.bz.mobile.inventory.data.local.db.repository.SettingsRepository
import ru.bz.mobile.inventory.domain.model.IOP
import javax.inject.Inject

class SettingsUseCase @Inject constructor(private val repository: SettingsRepository){
    fun getAllNotUTCZeroSync() = repository.getAllNotUTCZeroSync()

    fun getLocaByCwarItemClotSync(
        cwar: String,
        item: String,
        clot: String) = repository.getLocaByCwarItemClotSync(cwar, item, clot)

    suspend fun insertAll(dtos: List<IOP.Dto>) = repository.insertAll(dtos)

    suspend fun deleteAll() = repository.deleteAll()
}