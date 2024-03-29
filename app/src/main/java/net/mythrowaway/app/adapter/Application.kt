package net.mythrowaway.app.adapter

import android.app.Application
import net.mythrowaway.app.adapter.di.AppComponent
import net.mythrowaway.app.adapter.di.DaggerAppComponent
import net.mythrowaway.app.usecase.*
import javax.inject.Inject

class MyThrowTrash: Application() {
    @Inject
    lateinit var migrationUseCase: MigrationUseCase
    private val configurationVersion: Int = 2
    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        migrationUseCase.migration(configurationVersion)
    }

    val appComponent by lazy{
        initializeApp()
    }
    private fun initializeApp(): AppComponent {
        return DaggerAppComponent.factory().create(this)
    }
}