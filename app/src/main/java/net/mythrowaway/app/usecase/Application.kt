package net.mythrowaway.app.usecase

import android.app.Application
import androidx.preference.PreferenceManager
import net.mythrowaway.app.adapter.APIAdapterImpl
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.PreferenceConfigImpl
import net.mythrowaway.app.adapter.PreferencePersistImpl

class MyThrowTrash: Application() {
    override fun onCreate() {
        super.onCreate()
        DIContainer.register(IPersistentRepository::class.java,
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
            APIAdapterImpl()
        )
    }
}