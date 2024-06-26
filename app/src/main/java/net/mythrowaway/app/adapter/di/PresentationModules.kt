package net.mythrowaway.app.adapter.di

import dagger.*
import net.mythrowaway.app.adapter.presenter.*
import net.mythrowaway.app.usecase.*

@Module
abstract class EditModule {
    @ActivityScope
    @Binds
    abstract fun provideIEditPresenter(editPresenter: EditPresenterImpl): EditPresenterInterface
}

@Module
abstract class ScheduleListModule {
    @ActivityScope
    @Binds
    abstract fun provideIScheduleListPresenter(scheduleListPresenter: ScheduleListPresenterImpl): ScheduleListPresenterInterface
}

@Module
abstract class AlarmModule {
    @ActivityScope
    @Binds
    abstract fun provideIAlarmPresenter(alarmPresenter: AlarmPresenterImpl): AlarmPresenterInterface
}

@Module
abstract class ConnectModule {
    @ActivityScope
    @Binds
    abstract fun provideIConnectPresenter(connectPresenter: ConnectPresenterImplInterface): ConnectPresenterInterface
}

@Module
abstract class AccountLinkModule {
    @ActivityScope
    @Binds
    abstract fun provideIAccountLinkPresenter(accountLinkPresenter: AccountLinkPresenterImpl): AccountLinkPresenterInterface
}

@Module
abstract class ActivateModule {
    @ActivityScope
    @Binds
    abstract fun provideIActivatePresenter(activatePresenter: ActivatePresenterImpl): ActivatePresenterInterface
}

@Module
abstract class PublishCodeModule {
    @ActivityScope
    @Binds
    abstract fun provideIPublishCodePresenter(publishCodePresenter: PublishCodePresenterImpl): PublishCodePresenterInterface
}

@Module
abstract  class InformationModule {
    @ActivityScope
    @Binds
    abstract  fun provideIInformationPresenter(informationPresenter: InformationPresenterImpl): InformationPresenterInterface
}