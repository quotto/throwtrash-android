package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.AccountLinkActivity
import net.mythrowaway.app.view.ConnectActivity

@ActivityScope
@Subcomponent(modules = [ConnectModule::class,AccountLinkModule::class])
interface ConnectComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ConnectComponent
    }

    fun inject(activity: ConnectActivity)
}