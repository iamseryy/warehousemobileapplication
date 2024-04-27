package ru.bz.mobile.inventory.domain.usecase

import ru.bz.mobile.inventory.data.local.db.repository.MainRepository
import ru.bz.mobile.inventory.domain.model.IOP
import javax.inject.Inject

class MainUseCase @Inject constructor(private val repository: MainRepository) {
    fun findCwarSync(cwar: String) = repository.findCwarSync(cwar)

    fun findLocaByCwarSync(cwar: String, loca: String) =  repository.findLocaByCwarSync(cwar, loca)

    fun getIOPListByCwarItemClotSync(
        cwar: String,
        excludeLoca: String,
        item: String,
        clot: String
    ) = repository.getIOPListByCwarItemClotSync(
        cwar,
        excludeLoca,
        item,
        clot
    )

    fun getIOPByIndexSync(
        cwar: String,
        loca: String,
        item: String,
        clot: String
    ) = repository.getIOPByIndexSync(
        cwar,
        loca,
        item,
        clot
    )

    suspend fun insertAll(dtos: List<IOP.Dto>) = repository.insertAll(dtos)

    suspend fun deleteAll() = repository.deleteAll()

    fun getUnitByItem(item: String) = repository.getUnitByItem(item)

    suspend fun insert(dto: IOP.Dto) = repository.insert(dto)

    suspend fun update(dto: IOP.Dto) = repository.update(dto)

    suspend fun replace(newDto: IOP.Dto, oldDto: IOP.Dto) = repository.replace(newDto, oldDto)
}