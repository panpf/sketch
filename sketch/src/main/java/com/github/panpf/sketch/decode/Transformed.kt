package com.github.panpf.sketch.decode

import com.github.panpf.sketch.util.JsonSerializable

interface Transformed : JsonSerializable {
    val key: String
    val cacheResultToDisk: Boolean
}