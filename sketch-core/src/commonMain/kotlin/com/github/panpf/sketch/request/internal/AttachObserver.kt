package com.github.panpf.sketch.request.internal

fun interface AttachObserver {
    fun onAttachedChanged(attached: Boolean)
}