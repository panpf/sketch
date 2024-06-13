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

/**
 * Gets the [index] generic parameter of the parent class, and if [index] is -1, it gets the last generic parameter
 */
fun Class<*>.getSuperGenericParam(index: Int): Class<*> {
    var clazz: Class<*>? = this
    var target: Class<*>? = null
    while (clazz != null) {
        val type = clazz.genericSuperclass
        if (type is ParameterizedType) {
            val actualTypeArguments = type.actualTypeArguments
            val finalIndex = if (index == -1) actualTypeArguments.size - 1 else index
            require(index in actualTypeArguments.indices) {
                "Index out of range, index is $index, size is ${actualTypeArguments.size}"
            }
            target = type.actualTypeArguments[finalIndex] as Class<*>
            break
        } else {
            clazz = clazz.superclass
        }
    }
    return target
        ?: throw IllegalArgumentException("'$this' or its parent classes  must have a generic parameter")
}

/**
 * Gets a generic parameter in the parent class, which must be a subclass of the [expectedSuperClass]
 */
fun Class<*>.getSuperGenericParam(expectedSuperClass: Class<*>): Class<*> {
    var clazz: Class<*>? = this
    var target: Class<*>? = null
    while (clazz != null) {
        val type = clazz.genericSuperclass
        if (type is ParameterizedType) {
            val actualTypeArguments = type.actualTypeArguments
            target =
                actualTypeArguments.find { expectedSuperClass.isAssignableFrom((it as Class<*>)) } as Class<*>?
            if (target != null) {
                break
            } else {
                clazz = clazz.superclass
            }
        } else {
            clazz = clazz.superclass
        }
    }
    return target
        ?: throw IllegalArgumentException("'$this' or its parent classes must have a generic parameter implemented from '${expectedSuperClass}'")
}