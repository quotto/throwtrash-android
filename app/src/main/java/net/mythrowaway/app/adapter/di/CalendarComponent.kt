package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.calendar.CalendarActivity
import net.mythrowaway.app.view.calendar.MonthCalendarFragment

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