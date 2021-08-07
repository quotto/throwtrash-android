package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.AlarmActivity
import net.mythrowaway.app.view.AlarmReceiver

@ActivityScope
@Subcomponent(modules = [AlarmModule::class])
interface AlarmComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AlarmComponent
    }

    fun inject(activity: AlarmActivity)
    fun inject(activity: AlarmReceiver)
}