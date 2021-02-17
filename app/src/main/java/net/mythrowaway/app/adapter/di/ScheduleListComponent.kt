package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.ScheduleListFragment
import net.mythrowaway.app.view.ScheduleListActivity

@ActivityScope
@Subcomponent(modules = [ScheduleListModule::class])
interface ScheduleListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ScheduleListComponent
    }

    fun inject(activity: ScheduleListActivity)
    fun inject(fragment: ScheduleListFragment)
}