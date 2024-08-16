package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.edit.EditComposeActivity

@ActivityScope
@Subcomponent
interface EditComponent {
    @Subcomponent.Factory
    interface Factory{
        fun create():EditComponent
    }

    fun inject(activity: EditComposeActivity)
}