package com.github.panpf.sketch.util

interface Key {
    val key: String
}

interface NullableKey {
    val key: String?
}

fun key(it: Any): String {
    return when (it) {
        is Key -> it.key
        is NullableKey -> it.key ?: it.toString()
        else -> it.toString()
    }
}

fun keyOrNull(it: Any?): String? {
    return when (it) {
        is Key -> it.key
        is NullableKey -> it.key
        else -> it?.toString()
    }
}