package net.mythrowaway.app.application.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.mythrowaway.app.R
import net.mythrowaway.app.module.account_link.infra.AccountLinkApi
import net.mythrowaway.app.module.alarm.infra.PreferenceAlarmRepositoryImpl
import net.mythrowaway.app.module.alarm.usecase.AlarmRepositoryInterface
import net.mythrowaway.app.module.account.infra.PreferenceUserRepositoryImpl
import net.mythrowaway.app.module.account.usecase.UserRepositoryInterface
import net.mythrowaway.app.module.migration.infra.MigrationApiImplInterface
import net.mythrowaway.app.module.migration.infra.MigrationApiInterface
import net.mythrowaway.app.module.migration.infra.PreferenceVersionRepositoryImpl
import net.mythrowaway.app.module.review.infra.PreferenceReviewRepositoryImpl
import net.mythrowaway.app.module.account_link.infra.PreferenceAccountLinkRepositoryImpl
import net.mythrowaway.app.module.account_link.usecase.AccountLinkApiInterface
import net.mythrowaway.app.module.account_link.usecase.AccountLinkRepositoryInterface
import net.mythrowaway.app.module.account.infra.FirebaseAuthManager
import net.mythrowaway.app.module.account.infra.UserApiImpl
import net.mythrowaway.app.module.account.service.AuthService
import net.mythrowaway.app.module.account.usecase.AuthManagerInterface
import net.mythrowaway.app.module.account.usecase.UserApiInterface
import net.mythrowaway.app.module.migration.infra.PreferenceMigrationRepositoryImpl
import net.mythrowaway.app.module.migration.usecase.MigrationRepositoryInterface
import net.mythrowaway.app.module.trash.infra.PreferenceSyncRepositoryImpl
import net.mythrowaway.app.module.trash.infra.PreferenceTrashRepositoryImpl
import net.mythrowaway.app.module.migration.usecase.VersionRepositoryInterface
import net.mythrowaway.app.module.review.usecase.ReviewRepositoryInterface
import net.mythrowaway.app.module.trash.infra.MobileApiImpl
import net.mythrowaway.app.module.trash.usecase.MobileApiInterface
import net.mythrowaway.app.module.trash.usecase.SyncRepositoryInterface
import net.mythrowaway.app.module.trash.usecase.TrashRepositoryInterface
import javax.inject.Singleton

@Module
abstract class SingletonModule {
    @Singleton
    @Binds
    abstract fun provideIPersistentRepository(persistentAdapter: PreferenceTrashRepositoryImpl): TrashRepositoryInterface

    @Singleton
    @Binds
    abstract fun provideIConfigRepository(configRepository: PreferenceVersionRepositoryImpl): VersionRepositoryInterface

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

    @Singleton
    @Binds
    abstract fun provideMigrationRepository(migrationRepository: PreferenceMigrationRepositoryImpl): MigrationRepositoryInterface

    @Singleton
    @Binds
    abstract fun provideAuthManager(authManager: FirebaseAuthManager): AuthManagerInterface
}

@Module
class APIAdapterModule (){
    @Singleton
    @Provides
    fun provideIAPIAdapter(
        context: Context
    ): MobileApiInterface {
        return MobileApiImpl(context.getString(R.string.url_api))
    }

    @Singleton
    @Provides
    fun provideAccountLinkApi(
        context: Context
    ): AccountLinkApiInterface {
        return AccountLinkApi(context.getString(R.string.url_api))
    }

    @Provides
    fun provideUserApi(
        context: Context,
    ): UserApiInterface {
        return UserApiImpl(context.getString(R.string.url_api))
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

@Module
class AccountServiceModule {
    @Singleton
    @Provides
    fun provideAuthService(firebaseAuthManager: FirebaseAuthManager): AuthService {
        return AuthService(firebaseAuthManager)
    }
}