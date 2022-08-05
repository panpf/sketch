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
package com.github.panpf.sketch.sample.util

import java.lang.reflect.ParameterizedType

fun Class<*>.getSuperGenericParamClass(index: Int): Class<*> {
    val type = genericSuperclass
    if (type is ParameterizedType) {
        val actualTypeArguments = type.actualTypeArguments
        val finalIndex = if (index == -1) actualTypeArguments.size - 1 else index
        require(index in actualTypeArguments.indices) {
            "Index out of range, index is $index, size is ${actualTypeArguments.size}"
        }
        return type.actualTypeArguments[finalIndex] as Class<*>
    } else {
        throw IllegalArgumentException("'$this' parent class of must have a generic parameter")
    }
}