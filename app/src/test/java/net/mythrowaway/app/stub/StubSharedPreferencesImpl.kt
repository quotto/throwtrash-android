package net.mythrowaway.app.stub

import android.content.SharedPreferences

class StubSharedPreferencesImpl: SharedPreferences {
    private val editor = StubEditor()
    fun removeAll() {
        editor.removeAll()
    }
    override fun contains(key: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return editor.booleanStore[key] ?: defValue
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

class StubEditor: SharedPreferences.Editor {
    val stringStore:HashMap<String,String> = hashMapOf()
    val intStore:HashMap<String,Int> = hashMapOf()
    val booleanStore:HashMap<String,Boolean> = hashMapOf()
    fun removeAll() {
        stringStore.clear()
        intStore.clear()
        booleanStore.clear()
    }
    override fun clear(): SharedPreferences.Editor {
        stringStore.clear()
        intStore.clear()
        booleanStore.clear()
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
        key?.let { removeKey ->
            stringStore.remove(removeKey)
            intStore.remove(removeKey)
            booleanStore.remove(removeKey)
        }
        return this
    }

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
        key?.let { storeKey -> booleanStore[storeKey] = value }
        return this
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
        println("Called apply()")
    }

    override fun putString(key: String?, value: String?): SharedPreferences.Editor {
        key?.let{key-> value?.let{value->stringStore[key] = value}}
        return this
    }
}
