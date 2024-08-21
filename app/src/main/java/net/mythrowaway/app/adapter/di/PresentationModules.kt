package net.mythrowaway.app.adapter.di

import dagger.*
import net.mythrowaway.app.adapter.presenter.*
import net.mythrowaway.app.usecase.*
@Module
abstract  class InformationModule {
    @ActivityScope
    @Binds
    abstract  fun provideIInformationPresenter(informationPresenter: InformationPresenterImpl): InformationPresenterInterface
}