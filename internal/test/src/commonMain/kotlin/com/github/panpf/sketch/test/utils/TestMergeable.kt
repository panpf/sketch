package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.util.Mergeable

class TestMergeable(val values: Set<String>) : Mergeable {

    override fun merge(other: Mergeable): Mergeable {
        if (other !is TestMergeable) return this
        val newValues = mutableSetOf<String>()
        newValues.addAll(values)
        newValues.addAll(other.values)
        return TestMergeable(newValues)
    }
}