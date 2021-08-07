package net.mythrowaway.app.adapter

import android.app.Application
import androidx.preference.PreferenceManager
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.di.AppComponent
import net.mythrowaway.app.adapter.di.DaggerAppComponent
import net.mythrowaway.app.adapter.repository.APIAdapterImpl
import net.mythrowaway.app.adapter.repository.PreferenceConfigImpl
import net.mythrowaway.app.adapter.repository.PreferencePersistImpl
import net.mythrowaway.app.usecase.*
import javax.inject.Inject

class MyThrowTrash: Application() {
    @Inject
    lateinit var configRepository: IConfigRepository
    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        configRepository.updateConfigVersion()
    }

    val appComponent by lazy{
        initializeApp()
    }
    private fun initializeApp(): AppComponent {
        return DaggerAppComponent.factory().create(this)
    }
}