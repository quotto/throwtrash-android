package net.mythrowaway.app.application

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mythrowaway.app.application.di.AppComponent
import net.mythrowaway.app.application.di.DaggerAppComponent
import net.mythrowaway.app.module.info.infra.AuthManager
import net.mythrowaway.app.module.migration.usecase.MigrationUseCase
import net.mythrowaway.app.module.trash.usecase.SyncRepositoryInterface
import javax.inject.Inject

class MyThrowTrash: Application() {
    @Inject
    lateinit var migrationUseCase: MigrationUseCase
    @Inject
    lateinit var syncRepositoryImpl: SyncRepositoryInterface
    @Inject
    lateinit var authManager: AuthManager
    private val configurationVersion: Int = 2
    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this@MyThrowTrash)
        migrationUseCase.migration(configurationVersion)
        CoroutineScope(Dispatchers.IO).launch {
            authManager.initializeAuth()
            Log.i(this.javaClass.simpleName,"authManager initialized")
        }
    }

    val appComponent by lazy{
        initializeApp()
    }
    private fun initializeApp(): AppComponent {
        return DaggerAppComponent.factory().create(this)
    }
}