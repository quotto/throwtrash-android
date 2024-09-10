package net.mythrowaway.app.application.di

import dagger.Subcomponent
import net.mythrowaway.app.module.account_link.presentation.view.ConnectActivity

@ActivityScope
@Subcomponent
interface ConnectComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ConnectComponent
    }

    fun inject(activity: ConnectActivity)
}