package ru.bz.mobile.inventory.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.bz.mobile.inventory.data.local.datastore.DataStoreRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext context: Context) = DataStoreRepository(context)
}