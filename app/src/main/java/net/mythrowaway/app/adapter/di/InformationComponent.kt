package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.info.InformationActivity

@ActivityScope
@Subcomponent
interface InformationComponent {
    @Subcomponent.Factory
    interface  Factory {
        fun create(): InformationComponent
    }

    fun inject(activity: InformationActivity)
}