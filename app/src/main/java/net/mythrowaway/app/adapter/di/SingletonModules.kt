package net.mythrowaway.app.adapter.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.repository.*
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
        return APIAdapterImpl(context.getString(R.string.url_api))
    }
}

@Module
class MigrationApiModule {
    @Singleton
    @Provides
    fun provideIMigrationApi(context: Context): IMigrationApi {
        return MigrationApiImpl(context.getString(R.string.url_api))
    }
}