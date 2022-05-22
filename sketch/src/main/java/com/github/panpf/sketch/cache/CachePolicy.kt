package com.github.panpf.sketch.cache

/**
 * Represents the read/write policy for a cache source.
 */
enum class CachePolicy(
    val readEnabled: Boolean,
    val writeEnabled: Boolean
) {
    /**
     * readable, writable
     */
    ENABLED(true, true),

    /**
     * readable, not writable
     */
    READ_ONLY(true, false),

    /**
     * not readable, writable
     */
    WRITE_ONLY(false, true),

    /**
     * not readable, not writable
     */
    DISABLED(false, false)
}


/**
 * Return true if readable or writable
 */
val CachePolicy.isReadOrWrite: Boolean
    get() = readEnabled || writeEnabled

/**
 * Return true if readable and writable
 */
val CachePolicy.isReadAndWrite: Boolean
    get() = readEnabled && writeEnabled