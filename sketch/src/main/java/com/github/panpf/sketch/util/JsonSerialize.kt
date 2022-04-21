package com.github.panpf.sketch.util

import org.json.JSONObject

interface JsonSerializer<T> {

    fun toJson(t: T): JSONObject

    fun fromJson(jsonObject: JSONObject): T
}

interface JsonSerializable {
    fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1>
}
