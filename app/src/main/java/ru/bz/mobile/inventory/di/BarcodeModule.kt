package ru.bz.mobile.inventory.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.bz.mobile.inventory.presentation.controllers.BarcodeController
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object BarcodeModule {

    @Singleton
    @Provides
    fun provideBarcodeController(@ApplicationContext context: Context) = BarcodeController(context)
}