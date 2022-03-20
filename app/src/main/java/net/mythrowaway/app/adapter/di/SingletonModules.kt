package net.mythrowaway.app.adapter.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.repository.APIAdapterImpl
import net.mythrowaway.app.adapter.repository.PreferenceConfigImpl
import net.mythrowaway.app.adapter.repository.PreferencePersistImpl
import net.mythrowaway.app.service.UsageInfoService
import net.mythrowaway.app.usecase.IAPIAdapter
import net.mythrowaway.app.usecase.IConfigRepository
import net.mythrowaway.app.usecase.IPersistentRepository
import javax.inject.Singleton

@Module
abstract class SingletonModule {
    @Singleton
    @Binds
    abstract fun provideIPersistentRepository(persistentAdapter: PreferencePersistImpl): IPersistentRepository

    @Singleton
    @Binds
    abstract fun provideIConfigRepository(configRepository: PreferenceConfigImpl): IConfigRepository
}

@Module
class APIAdapterModule {
    @Singleton
    @Provides
    fun provideIAPIAdapter(context: Context): IAPIAdapter {
        return APIAdapterImpl(context.getString(R.string.url_api), context.applicationContext.getString(R.string.url_backend))
    }
}