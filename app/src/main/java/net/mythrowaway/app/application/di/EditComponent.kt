package net.mythrowaway.app.application.di

import dagger.Subcomponent
import net.mythrowaway.app.domain.trash.presentation.view.edit.EditActivity

@ActivityScope
@Subcomponent
interface EditComponent {
    @Subcomponent.Factory
    interface Factory{
        fun create():EditComponent
    }

    fun inject(activity: EditActivity)
}