package com.github.panpf.sketch.util

interface Equalizer<T> {

    val wrapped: T
    val equalityKey: Any

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String
}