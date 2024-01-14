package com.github.panpf.sketch.request

import com.github.panpf.sketch.target.TargetLifecycle

fun interface LifecycleResolver {

    suspend fun lifecycle(): TargetLifecycle
}

fun LifecycleResolver(lifecycle: TargetLifecycle): LifecycleResolver =
    FixedLifecycleResolver(lifecycle)

class FixedLifecycleResolver constructor(
    val lifecycle: TargetLifecycle
) : LifecycleResolver {

    override suspend fun lifecycle(): TargetLifecycle = lifecycle

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FixedLifecycleResolver
        return lifecycle == other.lifecycle
    }

    override fun hashCode(): Int {
        return lifecycle.hashCode()
    }

    override fun toString(): String = "FixedLifecycleResolver($lifecycle)"
}

open class LifecycleResolverWrapper(
    val wrapped: LifecycleResolver
) : LifecycleResolver by wrapped {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LifecycleResolverWrapper
        return wrapped == other.wrapped
    }

    override fun hashCode(): Int {
        return wrapped.hashCode()
    }

    override fun toString(): String {
        return "LifecycleResolverWrapper($wrapped)"
    }
}

class DefaultLifecycleResolver(
    wrapped: LifecycleResolver
) : LifecycleResolverWrapper(wrapped) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DefaultLifecycleResolver
        return wrapped == other.wrapped
    }

    override fun hashCode(): Int {
        return wrapped.hashCode()
    }

    override fun toString(): String {
        return "DefaultLifecycleResolver($wrapped)"
    }
}

fun LifecycleResolver.isDefault() = this is DefaultLifecycleResolver

fun LifecycleResolver.findLeafLifecycleResolver(): LifecycleResolver =
    when (this) {
        is LifecycleResolverWrapper -> this.wrapped.findLeafLifecycleResolver()
        else -> this
    }