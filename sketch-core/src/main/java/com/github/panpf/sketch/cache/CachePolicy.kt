package com.github.panpf.sketch.cache

/**
 * Represents the read/write policy for a cache source.
 */
enum class CachePolicy(
    val readEnabled: Boolean,
    val writeEnabled: Boolean
) {
    ENABLED(true, true),
    READ_ONLY(true, false),
    WRITE_ONLY(false, true),
    DISABLED(false, false)
}