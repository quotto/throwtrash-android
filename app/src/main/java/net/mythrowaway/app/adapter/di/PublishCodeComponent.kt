package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.PublishCodeActivity

@ActivityScope
@Subcomponent(modules = [PublishCodeModule::class])
interface PublishCodeComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PublishCodeComponent
    }

    fun inject(activity: PublishCodeActivity)
}