package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.EditActivity
import net.mythrowaway.app.view.EditMainFragment

@ActivityScope
@Subcomponent(modules = [EditModule::class])
interface EditComponent {
    @Subcomponent.Factory
    interface Factory{
        fun create():EditComponent
    }

    fun inject(activity: EditActivity)
    fun inject(fragment: EditMainFragment)
}