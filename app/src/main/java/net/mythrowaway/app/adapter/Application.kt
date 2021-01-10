package net.mythrowaway.app.adapter

import android.app.Application
import androidx.preference.PreferenceManager
import net.mythrowaway.app.R
import net.mythrowaway.app.usecase.*

class MyThrowTrash: Application() {
    override fun onCreate() {
        super.onCreate()
        DIContainer.register(
            IPersistentRepository::class.java,
            PreferencePersistImpl(
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            )
        )
        val trashManager = TrashManager(
            DIContainer.resolve(IPersistentRepository::class.java)!!
        )
        DIContainer.register(TrashManager::class.java, trashManager)
        DIContainer.register(
            ICalendarManager::class.java,
            CalendarManager()
        )
        DIContainer.register(
            IConfigRepository::class.java,
            PreferenceConfigImpl(
                PreferenceManager.getDefaultSharedPreferences((applicationContext))
            )
        )
        DIContainer.register(
            IAPIAdapter::class.java,
            APIAdapterImpl(getString(R.string.url_api),getString(R.string.url_backend))
        )

        // Configのバージョンを初期化する
        DIContainer.resolve(IConfigRepository::class.java)?.apply {
            updateConfigVersion()
        }
    }
}