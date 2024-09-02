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

import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Format the number to the specified number of decimal places
 *
 * @see com.github.panpf.sketch.extensions.view.test.util.ExtensionsViewCoreUtilsTest.testFormat
 */
internal fun Float.format(newScale: Int): Float {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier).toFloat()
    }
}

/**
 * Convert dp to px
 *
 * @see com.github.panpf.sketch.extensions.view.test.util.ExtensionsViewCoreUtilsTest.testDp2Px
 */
internal fun Float.dp2Px(): Int {
    return (this * android.content.res.Resources.getSystem().displayMetrics.density + 0.5f).roundToInt()
}