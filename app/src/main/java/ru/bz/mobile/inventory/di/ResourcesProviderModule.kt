package ru.bz.mobile.inventory.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.bz.mobile.inventory.data.local.resource.ResourcesRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ResourcesProviderModule {
    @Singleton
    @Provides
    fun provideResourcesRepository(@ApplicationContext context: Context)= ResourcesRepository(context)

}