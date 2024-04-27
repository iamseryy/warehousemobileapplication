package ru.bz.mobile.inventory.domain.usecase

import ru.bz.mobile.inventory.data.local.resource.ResourcesRepository
import javax.inject.Inject

class ResourcesUseCase @Inject constructor(private val repository: ResourcesRepository){
    fun getDataMatrixDelimiter() = repository.getDataMatrixDelimiter()
    fun getString(id: Int) = repository.getString(id)
}