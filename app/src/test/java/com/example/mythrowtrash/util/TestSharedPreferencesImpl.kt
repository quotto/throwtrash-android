package com.example.mythrowtrash.util

import android.content.SharedPreferences

class TestSharedPreferencesImpl: SharedPreferences {
    private val editor = MockEditor()
    fun removeAll() {
        editor.removeAll()
    }
    override fun contains(key: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return editor.intStore[key] ?: defValue
    }

    override fun getAll(): MutableMap<String, *> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun edit(): SharedPreferences.Editor {
        return editor
    }

    override fun getLong(key: String?, defValue: Long): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStringSet(
        key: String?,
        defValues: MutableSet<String>?
    ): MutableSet<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getString(key: String?, defValue: String?): String? {
        return editor.stringStore[key] ?: defValue
    }
}

class MockEditor: SharedPreferences.Editor {
    val stringStore:HashMap<String,String> = hashMapOf()
    val intStore:HashMap<String,Int> = hashMapOf()
    fun removeAll() {
        stringStore.clear()
        intStore.clear()
    }
    override fun clear(): SharedPreferences.Editor {
        stringStore.clear()
        return this
    }

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
        key?.let{key->intStore[key]=value}
        return this
    }

    override fun remove(key: String?): SharedPreferences.Editor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun commit(): Boolean {
        return true
    }

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun apply() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putString(key: String?, value: String?): SharedPreferences.Editor {
        key?.let{key-> value?.let{value->stringStore[key] = value}}
        return this
    }
}