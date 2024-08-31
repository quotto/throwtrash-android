package net.mythrowaway.app.adapter.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.repository.*
import net.mythrowaway.app.domain.TrashType
import net.mythrowaway.app.usecase.AccountLinkRepositoryInterface
import net.mythrowaway.app.usecase.AlarmRepositoryInterface
import net.mythrowaway.app.usecase.MobileApiInterface
import net.mythrowaway.app.usecase.ConfigRepositoryInterface
import net.mythrowaway.app.usecase.ReviewRepositoryInterface
import net.mythrowaway.app.usecase.SyncRepositoryInterface
import net.mythrowaway.app.usecase.TrashRepositoryInterface
import net.mythrowaway.app.usecase.UserRepositoryInterface
import javax.inject.Singleton

@Module
abstract class SingletonModule {
    @Singleton
    @Binds
    abstract fun provideIPersistentRepository(persistentAdapter: PreferenceTrashRepositoryImpl): TrashRepositoryInterface

    @Singleton
    @Binds
    abstract fun provideIConfigRepository(configRepository: PreferenceConfigRepositoryImpl): ConfigRepositoryInterface

    @Singleton
    @Binds
    abstract fun provideIUserRepository(userRepository: PreferenceUserRepositoryImpl): UserRepositoryInterface

    @Singleton
    @Binds
    abstract fun provideAlarmRepository(alarmRepository: PreferenceAlarmRepositoryImpl): AlarmRepositoryInterface

    @Singleton
    @Binds
    abstract fun provideSyncRepository(syncRepository: PreferenceSyncRepositoryImpl): SyncRepositoryInterface

    @Singleton
    @Binds
    abstract fun provideAccountLinkRepository(accountLinkRepository: PreferenceAccountLinkRepositoryImpl): AccountLinkRepositoryInterface

    @Singleton
    @Binds
    abstract fun provideReviewRepository(reviewRepository: PreferenceReviewRepositoryImpl): ReviewRepositoryInterface
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
class TrashDesignModule {
    @Singleton
    @Provides
    fun provideITrashDesignRepository(): TrashDesignRepository {
        return TrashColorPicker().apply {
            clearColorCode()
            clearDrawableId()
            setDrawableId(TrashType.BURN, R.drawable.background_calendar_trash_name_burn)
            setDrawableId(TrashType.CAN, R.drawable.background_calendar_trash_name_can)
            setDrawableId(TrashType.UNBURN, R.drawable.background_calendar_trash_name_unburn)
            setDrawableId(TrashType.PLASTIC, R.drawable.background_calendar_trash_name_plastic)
            setDrawableId(TrashType.PETBOTTLE, R.drawable.background_calendar_trash_name_petbottle)
            setDrawableId(TrashType.BOTTLE, R.drawable.background_calendar_trash_name_bin)
            setDrawableId(TrashType.PAPER, R.drawable.background_calendar_trash_name_paper)
            setDrawableId(TrashType.COARSE, R.drawable.background_calendar_trash_name_coarse)
            setDrawableId(TrashType.RESOURCE, R.drawable.background_calendar_trash_name_resource)
            setDrawableId(TrashType.OTHER, R.drawable.background_calendar_trash_name_other)
        }
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