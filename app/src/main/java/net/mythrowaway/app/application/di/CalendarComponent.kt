package net.mythrowaway.app.application.di

import dagger.Subcomponent
import net.mythrowaway.app.module.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.module.trash.presentation.view.calendar.MonthCalendarFragment

@ActivityScope
@Subcomponent
interface CalendarComponent{
    @Subcomponent.Factory
    interface Factory {
        fun create(): CalendarComponent
    }

    fun inject(activity: CalendarActivity)
    fun inject(fragment: MonthCalendarFragment)
}