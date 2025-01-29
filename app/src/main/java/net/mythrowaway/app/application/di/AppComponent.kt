package net.mythrowaway.app.application.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import net.mythrowaway.app.application.MyThrowTrash
import javax.inject.Singleton

@Singleton
@Component(modules = [
    SingletonModule::class,
    APIAdapterModule::class,
    MigrationApiModule::class,
    AppSubComponents::class,
])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun calendarComponent(): CalendarComponent.Factory
    fun editComponent(): EditComponent.Factory
    fun alarmComponent(): AlarmComponent.Factory
    fun shareComponent(): ShareComponent.Factory
    fun connectComponent(): ConnectComponent.Factory
    fun informationComponent(): InformationComponent.Factory
    fun inject(application: MyThrowTrash)
}