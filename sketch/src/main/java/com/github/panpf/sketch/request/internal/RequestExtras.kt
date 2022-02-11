package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.util.asOrNull

class RequestExtras {
    val map = HashMap<String, Any>()

    fun getString(key: String): String? = synchronized(map) {
        map[key].asOrNull()
    }

    fun putString(key: String, value: String) {
        synchronized(map) {
            map[key] = value
        }
    }
}

fun RequestExtras.getCountDrawablePendingManagerKey(): String? =
    getString("CountDrawablePendingManagerKey")

fun RequestExtras.putCountDrawablePendingManagerKey(value: String) {
    putString("CountDrawablePendingManagerKey", value)
}