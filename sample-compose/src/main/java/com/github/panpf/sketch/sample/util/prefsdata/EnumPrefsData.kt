package com.github.panpf.sketch.sample.util.prefsdata

import android.content.Context
import android.content.SharedPreferences
import android.os.Looper
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData

class EnumPrefsData<T : Enum<T>>(
    context: Context,
    private val key: String,
    private val defaultValue: T,
    private val parse: (String) -> T,
    private val toStringValue: (T) -> String,
    prefsName: String? = null,
) : MutableLiveData<T>() {

    private val preference: SharedPreferences = if (prefsName != null) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } else {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    init {
        // Trigger initialization, requisite
        value = value
    }

    override fun postValue(value: T?) {
        preference.edit().apply {
            if (value != null) {
                putString(key, toStringValue(value))
            } else {
                remove(key)
            }
        }.apply()
        super.postValue(value ?: defaultValue)
    }

    override fun setValue(value: T?) {
        preference.edit().apply {
            if (value != null) {
                putString(key, toStringValue(value))
            } else {
                remove(key)
            }
        }.apply()
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.setValue(value ?: defaultValue)
        } else {
            super.postValue(value ?: defaultValue)
        }
    }

    override fun getValue(): T = parse(preference.getString(key, null) ?: defaultValue.name)
}