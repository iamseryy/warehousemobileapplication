package ru.bz.mobile.inventory.data.local.db.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import ru.bz.mobile.inventory.data.local.db.dao.IOPDao
import ru.bz.mobile.inventory.domain.model.IOP
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val iopDao: IOPDao
) {
    // sync
    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getAllNotUTCZeroSync(): List<IOP.Dto> = iopDao.getAllNotUTCZeroSync()

    @Suppress("RedundantSuspendModifier")
    @MainThread
    fun getLocaByCwarItemClotSync(cwar: String, item: String, clot: String): String? =
        iopDao.getLocaByCwarItemClotSync(cwar, item, clot)

    //  async
    @Suppress("RedundantSuspendModifier")
    @MainThread
    suspend fun insertAll(dtos: List<IOP.Dto>) = iopDao.insertAll(dtos)

    @Suppress("RedundantSuspendModifier")
    @MainThread
    suspend fun deleteAll() = iopDao.deleteAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(dto: IOP.Dto) = iopDao.insert(dto)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(dto: IOP.Dto) = iopDao.update(dto)

}
