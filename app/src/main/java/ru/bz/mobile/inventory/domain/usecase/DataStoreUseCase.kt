package ru.bz.mobile.inventory.domain.usecase

import ru.bz.mobile.inventory.data.local.datastore.DataStoreRepository
import ru.bz.mobile.inventory.domain.model.DataStoreSave
import javax.inject.Inject

class DataStoreUseCase @Inject constructor(private val repository: DataStoreRepository) {
    fun load() = repository.load()
    suspend fun save(data: DataStoreSave) = repository.save(data)
}