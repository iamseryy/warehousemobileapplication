package ru.bz.mobile.inventory.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.bz.mobile.inventory.data.local.file.FileRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FileModule {

    @Singleton
    @Provides
    fun provideFileRepository(@ApplicationContext context: Context) = FileRepository()
}