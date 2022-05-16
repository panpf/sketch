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