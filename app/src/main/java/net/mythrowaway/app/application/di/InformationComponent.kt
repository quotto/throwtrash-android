package net.mythrowaway.app.application.di

import dagger.Subcomponent
import net.mythrowaway.app.module.info.presentation.view.InformationActivity

@ActivityScope
@Subcomponent
interface InformationComponent {
    @Subcomponent.Factory
    interface  Factory {
        fun create(): InformationComponent
    }

    fun inject(activity: InformationActivity)
}