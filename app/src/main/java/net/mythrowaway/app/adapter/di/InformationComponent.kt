package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.InformationActivity

@ActivityScope
@Subcomponent(modules = [InformationModule::class])
interface InformationComponent {
    @Subcomponent.Factory
    interface  Factory {
        fun create(): InformationComponent
    }

    fun inject(activity: InformationActivity)
}