package com.github.panpf.sketch.test.utils

fun interface ScopeAction<T> {
    fun T.invoke()
}