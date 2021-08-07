package net.mythrowaway.app.adapter.di

import dagger.*
import net.mythrowaway.app.adapter.IAlarmView
import net.mythrowaway.app.adapter.ICalendarView
import net.mythrowaway.app.adapter.IEditView
import net.mythrowaway.app.adapter.presenter.*
import net.mythrowaway.app.usecase.*
import net.mythrowaway.app.view.CalendarActivity
import net.mythrowaway.app.view.CalendarAdapter
import net.mythrowaway.app.view.EditActivity
import net.mythrowaway.app.view.EditMainFragment

@Module
abstract class CalendarModule{
    @ActivityScope
    @Binds
    abstract fun provideICalendarPresenter(calendarPresenter: CalendarPresenterImpl): ICalendarPresenter
}

@Module
abstract class EditModule {
    @ActivityScope
    @Binds
    abstract fun provideIEditPresenter(editPresenter: EditPresenterImpl): IEditPresenter
}

@Module
abstract class ScheduleListModule {
    @ActivityScope
    @Binds
    abstract fun provideIScheduleListPresenter(scheduleListPresenter: ScheduleListPresenterImpl): IScheduleListPresenter
}

@Module
abstract class AlarmModule {
    @ActivityScope
    @Binds
    abstract fun provideIAlarmPresenter(alarmPresenter: AlarmPresenterImpl): IAlarmPresenter
}

@Module
abstract class ConnectModule {
    @ActivityScope
    @Binds
    abstract fun provideIConnectPresenter(connectPresenter: ConnectPresenterImpl): IConnectPresenter
}

@Module
abstract class AccountLinkModule {
    @ActivityScope
    @Binds
    abstract fun provideIAccountLinkPresenter(accountLinkPresenter: AccountLinkPresenterImpl): IAccountLinkPresenter
}

@Module
abstract class ActivateModule {
    @ActivityScope
    @Binds
    abstract fun provideIActivatePresenter(activatePresenter: ActivatePresenterImpl): IActivatePresenter
}

@Module
abstract class PublishCodeModule {
    @ActivityScope
    @Binds
    abstract fun provideIPublishCodePresenter(publishCodePresenter: PublishCodePresenterImpl): IPublishCodePresenter
}