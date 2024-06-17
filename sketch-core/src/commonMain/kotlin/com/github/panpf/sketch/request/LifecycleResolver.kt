package com.github.panpf.sketch.request

import androidx.lifecycle.Lifecycle
import kotlin.js.JsName

fun LifecycleResolver(lifecycle: Lifecycle): LifecycleResolver =
    FixedLifecycleResolver(lifecycle)

/**
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
fun interface LifecycleResolver {

    @JsName("getLifecycle")
    suspend fun lifecycle(): Lifecycle
}

class FixedLifecycleResolver constructor(
    val lifecycle: Lifecycle
) : LifecycleResolver {

    override suspend fun lifecycle(): Lifecycle = lifecycle

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FixedLifecycleResolver) return false
        return lifecycle == other.lifecycle
    }

    override fun hashCode(): Int {
        return lifecycle.hashCode()
    }

    override fun toString(): String = "FixedLifecycleResolver($lifecycle)"
}