package ru.bz.mobile.inventory.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.bz.mobile.inventory.data.room.dao.ClotDao
import ru.bz.mobile.inventory.data.room.dao.IOPDao
import ru.bz.mobile.inventory.data.room.dao.LocaDao
import ru.bz.mobile.inventory.model.IOP

@Database(entities = [IOP.Dto::class ], version = 1)
abstract class IOPRoomDatabase : RoomDatabase() {
    abstract fun iopDao(): IOPDao
    abstract fun clotDao(): ClotDao
    abstract fun locaDao(): LocaDao

    companion object {

        @Volatile
        private var INSTANCE: IOPRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): IOPRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IOPRoomDatabase::class.java,
                    "IOP_db"
                )
                    .allowMainThreadQueries()
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(IOPDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class IOPDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
//                        populateDatabase(database.iopDao())
                    }
                }
            }
        }
    }

}

//tables
const val WHINH501 = "whinh501"