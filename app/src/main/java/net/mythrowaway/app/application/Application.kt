package net.mythrowaway.app.application

import android.app.Application
import net.mythrowaway.app.application.di.AppComponent
import net.mythrowaway.app.application.di.DaggerAppComponent
import net.mythrowaway.app.module.migration.usecase.MigrationUseCase
import net.mythrowaway.app.module.trash.usecase.SyncRepositoryInterface
import javax.inject.Inject

class MyThrowTrash: Application() {
    @Inject
    lateinit var migrationUseCase: MigrationUseCase
    @Inject
    lateinit var syncRepositoryImpl: SyncRepositoryInterface
    private val configurationVersion: Int = 2
    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        migrationUseCase.migration(configurationVersion)
        // 初回起動時にリモートからの最新化を行うため同期待ち状態にする
        syncRepositoryImpl.setSyncWait()
    }

    val appComponent by lazy{
        initializeApp()
    }
    private fun initializeApp(): AppComponent {
        return DaggerAppComponent.factory().create(this)
    }
}