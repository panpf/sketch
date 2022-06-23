package com.github.panpf.sketch.util

fun <R> ifOrNull(value: Boolean, block: () -> R?): R? = if (value) block() else null