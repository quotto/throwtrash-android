package net.mythrowaway.app.application.di

import dagger.Subcomponent
import net.mythrowaway.app.module.account.presentation.view.AccountActivity

@ActivityScope
@Subcomponent
interface AccountComponent {
    @Subcomponent.Factory
    interface  Factory {
        fun create(): AccountComponent
    }

    fun inject(activity: AccountActivity)
}