package me.panpf.sketch.sample.bean

import android.content.Context
import me.panpf.sketch.sample.AppConfig

class CheckMenu(
    private val context: Context,
    val title: String,
    private val key: AppConfig.Key,
) {

    val isChecked: Boolean
        get() = AppConfig.getBoolean(context, key)

    fun onClick() {
        AppConfig.putBoolean(context, key, !isChecked)
    }
}
