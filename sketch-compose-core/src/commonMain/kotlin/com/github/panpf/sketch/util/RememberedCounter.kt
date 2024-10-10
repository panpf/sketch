/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.util

/**
 * RememberObserver will be remembered and forgotten multiple times when passed as argument
 * to the Composable function, but we only need to do something the
 * first time it is remembered and the last time it is forgotten
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.RememberedCounterTest
 */
class RememberedCounter {

    /**
     * Why use 'var count: Int' instead of 'var remembered: Boolean'
     *
     * Since we only need to do something when it is first remembered and last forgotten, we have to use a counter
     */
    var count: Int = 0
        private set

    val isRemembered: Boolean
        get() = count > 0

    fun remember(): Boolean {
        count++
        return count == 1
    }

    fun forget(): Boolean {
        if (count <= 0) {
            return false
        }
        count--
        return count == 0
    }
}