package com.github.panpf.sketch.decode

import androidx.annotation.Keep
import org.json.JSONObject

@Keep
interface Transformed {
    val key: String
    val cacheResultToDisk: Boolean
    fun serializationToJSON(): JSONObject
}