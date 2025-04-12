package net.mythrowaway.app.application

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.mythrowaway.app.R
import net.mythrowaway.app.application.di.AppComponent
import net.mythrowaway.app.application.di.DaggerAppComponent
import net.mythrowaway.app.module.account.usecase.AuthManagerInterface
import net.mythrowaway.app.module.migration.usecase.MigrationUseCase
import net.mythrowaway.app.module.trash.usecase.SyncRepositoryInterface
import javax.inject.Inject

class MyThrowTrash: Application() {
    @Inject
    lateinit var migrationUseCase: MigrationUseCase
    @Inject
    lateinit var syncRepositoryImpl: SyncRepositoryInterface
    @Inject
    lateinit var authManager: AuthManagerInterface

    // 認証初期化状態を管理するためのStateFlow
    private val _appInitialized = MutableStateFlow<Boolean?>(null) // null: 初期化中, true: 成功, false: 失敗
    private val appInitialized: StateFlow<Boolean?> = _appInitialized

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this@MyThrowTrash)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                authManager.initializeAuth()
                Log.i(this.javaClass.simpleName,"AuthManager initialized successfully")
                migrationUseCase.migration(R.string.version_config)
                Log.i(this.javaClass.simpleName,"Migration completed successfully")
                _appInitialized.value = true
            } catch (e: Exception) {
                Log.e(this.javaClass.simpleName,"Initialization failed: ${e.message}")
                _appInitialized.value = true
            }
        }
    }

    val appComponent by lazy{
        initializeApp()
    }
    private fun initializeApp(): AppComponent {
        return DaggerAppComponent.factory().create(this)
    }

    // 認証の初期化状態を確認するためのメソッド
    fun isAppInitialized(): StateFlow<Boolean?> {
        return appInitialized
    }
}