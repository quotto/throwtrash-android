package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.account_link.ConnectActivity

@ActivityScope
@Subcomponent
interface ConnectComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ConnectComponent
    }

    fun inject(activity: ConnectActivity)
}