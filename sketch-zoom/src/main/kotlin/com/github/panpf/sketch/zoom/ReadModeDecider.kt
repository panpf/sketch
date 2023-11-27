/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.zoom

import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.LongImageDecider

interface ReadModeDecider {

    fun should(
        imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int
    ): Boolean
}

class LongImageReadModeDecider(
    val longImageDecider: LongImageDecider = DefaultLongImageDecider(notSameDirectionMultiple = 10f)
) : ReadModeDecider {

    override fun should(
        imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int
    ): Boolean = longImageDecider.isLongImage(imageWidth, imageHeight, viewWidth, viewHeight)

    override fun toString(): String {
        return "LongImageReadModeDecider($longImageDecider)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LongImageReadModeDecider
        if (longImageDecider != other.longImageDecider) return false
        return true
    }

    override fun hashCode(): Int {
        return longImageDecider.hashCode()
    }
}