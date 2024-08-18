package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.share.ShareActivity

@ActivityScope
@Subcomponent
interface ShareComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ShareComponent
    }

    fun inject(activity: ShareActivity)
}