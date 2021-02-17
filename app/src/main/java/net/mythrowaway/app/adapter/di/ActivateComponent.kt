package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.ActivateActivity

@ActivityScope
@Subcomponent(modules = [ActivateModule::class])
interface ActivateComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivateComponent
    }

    fun inject(activity: ActivateActivity)
}