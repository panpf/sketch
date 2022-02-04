package com.github.panpf.sketch.sample.util.prefsdata

import android.content.Context
import android.content.SharedPreferences
import android.os.Looper
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData

class BooleanPrefsData(
    context: Context,
    private val key: String,
    private val defaultValue: Boolean = false,
    prefsName: String? = null,
) : MutableLiveData<Boolean>() {

    private val preference: SharedPreferences = if (prefsName != null) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } else {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    init {
        // Trigger initialization, requisite
        value = value
    }

    override fun postValue(value: Boolean?) {
        preference.edit().apply {
            if (value != null) {
                putBoolean(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.postValue(value ?: defaultValue)
    }

    override fun setValue(value: Boolean?) {
        preference.edit().apply {
            if (value != null) {
                putBoolean(key, value)
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

    override fun getValue(): Boolean = preference.getBoolean(key, defaultValue)
}