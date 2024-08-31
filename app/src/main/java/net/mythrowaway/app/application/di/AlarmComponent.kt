package net.mythrowaway.app.application.di

import dagger.Subcomponent
import net.mythrowaway.app.domain.alarm.presentation.view.AlarmActivity
import net.mythrowaway.app.domain.alarm.presentation.view.AlarmReceiver

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