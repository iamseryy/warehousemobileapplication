package ru.bz.mobile.inventory

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ru.bz.mobile.inventory.config.DataStoreManager
import ru.bz.mobile.inventory.data.room.ClotRepository
import ru.bz.mobile.inventory.data.room.IOPRoomDatabase
import ru.bz.mobile.inventory.data.room.LocaRepository
import ru.bz.mobile.inventory.data.room.MainRepository
import ru.bz.mobile.inventory.data.room.SettingsRepository

class App : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { IOPRoomDatabase.getDatabase(this, applicationScope) }
    val mainRepo by lazy { MainRepository(database.iopDao()) }
    val settingsRepo by lazy { SettingsRepository(database.iopDao()) }

    val clotRepo by lazy { ClotRepository(database.clotDao()) }
    val locaRepo by lazy { LocaRepository(database.locaDao()) }
    val dataStore by lazy { DataStoreManager(this) }
}
