package com.example.mythrowtrash.usecase

import android.app.Application
import androidx.preference.PreferenceManager
import com.example.mythrowtrash.adapter.DIContainer
import com.example.mythrowtrash.adapter.PreferenceConfigImpl
import com.example.mythrowtrash.adapter.PreferencePersistImpl

class MyThrowTrash: Application() {
    override fun onCreate() {
        super.onCreate()
        DIContainer.register(
            IPersistentRepository::class.java, PreferencePersistImpl(
                PreferenceManager.getDefaultSharedPreferences(applicationContext)))
        val trashManager = TrashManager(DIContainer.resolve(IPersistentRepository::class.java)!!)
        DIContainer.register(TrashManager::class.java, trashManager)
        DIContainer.register(ICalendarManager::class.java,CalendarManager())
        DIContainer.register(IConfigRepository::class.java, PreferenceConfigImpl(
            PreferenceManager.getDefaultSharedPreferences((applicationContext))
        ))
    }
}