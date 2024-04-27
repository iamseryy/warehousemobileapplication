package ru.bz.mobile.inventory.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.bz.mobile.inventory.data.local.db.AppDatabase
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class RoomModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "inventory_db"
        )
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideLocaDao(db: AppDatabase) = db.locaDao()

    @Provides
    @Singleton
    fun provideClotDao(db: AppDatabase) = db.clotDao()

    @Provides
    @Singleton
    fun provideIopDao(db: AppDatabase) = db.iopDao()
}