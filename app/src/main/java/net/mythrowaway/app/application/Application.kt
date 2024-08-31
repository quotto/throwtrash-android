package net.mythrowaway.app.application

import android.app.Application
import net.mythrowaway.app.application.di.AppComponent
import net.mythrowaway.app.application.di.DaggerAppComponent
import net.mythrowaway.app.domain.migration.usecase.MigrationUseCase
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