package com.github.panpf.sketch.sample.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

fun <T> Flow<T>.ignoreFirst(): Flow<T> {
    var first = true
    return filter {
        if (first) {
            first = false
            false
        } else {
            true
        }
    }
}