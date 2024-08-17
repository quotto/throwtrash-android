package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.alarm.AlarmActivity
import net.mythrowaway.app.view.AlarmReceiver

@ActivityScope
@Subcomponent
interface AlarmComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AlarmComponent
    }

    fun inject(activity: AlarmActivity)
    fun inject(activity: AlarmReceiver)
}