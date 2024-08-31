package net.mythrowaway.app.application.di

import dagger.Subcomponent
import net.mythrowaway.app.domain.trash.presentation.view.share.ShareActivity

@ActivityScope
@Subcomponent
interface ShareComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ShareComponent
    }

    fun inject(activity: ShareActivity)
}