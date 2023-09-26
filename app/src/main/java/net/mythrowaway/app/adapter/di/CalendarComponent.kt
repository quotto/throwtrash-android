package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.calendar.CalendarActivity
import net.mythrowaway.app.view.calendar.CalendarFragment

@ActivityScope
@Subcomponent(modules = [CalendarModule::class])
interface CalendarComponent{
    @Subcomponent.Factory
    interface Factory {
        fun create(): CalendarComponent
    }

    fun inject(activity: CalendarActivity)
    fun inject(fragment: CalendarFragment)
}