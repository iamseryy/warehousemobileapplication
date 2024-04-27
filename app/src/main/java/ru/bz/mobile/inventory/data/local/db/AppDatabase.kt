package ru.bz.mobile.inventory.data.local.db


import androidx.room.Database
import androidx.room.RoomDatabase
import ru.bz.mobile.inventory.data.local.db.dao.ClotDao
import ru.bz.mobile.inventory.data.local.db.dao.IOPDao
import ru.bz.mobile.inventory.data.local.db.dao.LocaDao
import ru.bz.mobile.inventory.domain.model.IOP

@Database(entities = [IOP.Dto::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun iopDao(): IOPDao
    abstract fun clotDao(): ClotDao
    abstract fun locaDao(): LocaDao
}