package net.mythrowaway.app.adapter.di

import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import net.mythrowaway.app.view.CalendarActivity
import net.mythrowaway.app.view.CalendarAdapter
import net.mythrowaway.app.view.CalendarFragment
import java.util.*

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