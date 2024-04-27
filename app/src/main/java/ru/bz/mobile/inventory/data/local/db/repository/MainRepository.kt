package ru.bz.mobile.inventory.data.local.db.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import ru.bz.mobile.inventory.data.local.db.dao.IOPDao
import ru.bz.mobile.inventory.domain.model.IOP
import javax.inject.Inject


class MainRepository @Inject constructor(
    private val dao: IOPDao
) {
    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun findCwarSync(cwar: String): Boolean = !dao.getCwarSync(cwar).isNullOrEmpty()

    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getAllNotUTCZeroSync(): List<IOP.Dto> = dao.getAllNotUTCZeroSync()

    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun findLocaByCwarSync(cwar: String, loca: String): Boolean =
        !dao.getLocaByCwarSync(cwar, loca).isNullOrEmpty()

    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getLocaByCwarItemClotSync(cwar: String, item: String, clot: String): String? =
        dao.getLocaByCwarItemClotSync(cwar, item, clot)

    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getIOPListByCwarItemClotSync(
        cwar: String,
        excludeLoca: String,
        item: String,
        clot: String
    ): List<IOP.Dto> =
        dao.getIOPListByCwarItemClotSync(
            cwar = cwar,
            item = item,
            clot = clot,
            excludeLoca = excludeLoca
        )

    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getIOPByIndexSync(cwar: String, loca: String, item: String, clot: String): IOP.Dto? =
        dao.getIOPByIndexSync(cwar, loca, item, clot)

    @Suppress("RedundantSuspendModifier")
    @MainThread
    suspend fun insertAll(dtos: List<IOP.Dto>) = dao.insertAll(dtos)

    @Suppress("RedundantSuspendModifier")
    @MainThread
    suspend fun deleteAll() = dao.deleteAll()

    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getUnitByItem(item: String): String? = dao.getUnitByItem(item)


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(dto: IOP.Dto) = dao.insert(dto)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(dto: IOP.Dto) = dao.update(dto)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun replace(newDto: IOP.Dto, oldDto: IOP.Dto) {
        dao.replace(newDto, oldDto)
    }

}
