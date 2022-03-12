/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.zoom.tile

import java.util.concurrent.atomic.AtomicInteger

class KeyCounter {
    private val number: AtomicInteger = AtomicInteger()

    val key: Int
        get() = number.get()

    fun refresh() {
        if (number.get() == Int.MAX_VALUE) {
            number.set(0)
        } else {
            number.addAndGet(1)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyCounter

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key
    }

    override fun toString(): String {
        return "KeyCounter(key=$key)"
    }
}