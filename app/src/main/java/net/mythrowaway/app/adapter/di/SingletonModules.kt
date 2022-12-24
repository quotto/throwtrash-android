package net.mythrowaway.app.adapter.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.repository.*
import net.mythrowaway.app.usecase.MobileApiInterface
import net.mythrowaway.app.usecase.ConfigRepositoryInterface
import net.mythrowaway.app.usecase.DataRepositoryInterface
import javax.inject.Singleton

@Module
abstract class SingletonModule {
    @Singleton
    @Binds
    abstract fun provideIPersistentRepository(persistentAdapter: PreferenceDataRepositoryImpl): DataRepositoryInterface

    @Singleton
    @Binds
    abstract fun provideIConfigRepository(configRepository: PreferenceConfigRepositoryImpl): ConfigRepositoryInterface
}

@Module
class APIAdapterModule {
    @Singleton
    @Provides
    fun provideIAPIAdapter(context: Context): MobileApiInterface {
        return MobileApiImpl(context.getString(R.string.url_api))
    }
}

@Module
class MigrationApiModule {
    @Singleton
    @Provides
    fun provideIMigrationApi(context: Context): MigrationApiInterface {
        return MigrationApiImplInterface(context.getString(R.string.url_api))
    }
}