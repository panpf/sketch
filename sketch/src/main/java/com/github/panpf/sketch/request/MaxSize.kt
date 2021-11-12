/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.request

import com.github.panpf.sketch.Key
import java.util.*

/**
 * 用于计算 inSimpleSize 缩小图片
 */
data class MaxSize(val width: Int, val height: Int) : Key {

    override val key: String
        get() = toString()

    override fun toString(): String {
        return String.format(Locale.US, "MaxSize(%dx%d)", width, height)
    }
}