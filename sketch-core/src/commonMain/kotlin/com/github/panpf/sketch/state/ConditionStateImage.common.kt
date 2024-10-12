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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.state

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Key

/**
 * Create an ConditionStateImage
 *
 * @see com.github.panpf.sketch.core.common.test.state.ConditionStateImageTest.testConditionStateImage
 */
fun ConditionStateImage(
    defaultImage: StateImage,
    conditionBlock: ConditionStateImage.Builder.() -> Unit
): ConditionStateImage = ConditionStateImage.Builder(defaultImage).apply {
    conditionBlock.invoke(this)
}.build()

/**
 * Support providing different [StateImage] based on different conditions
 *
 * @see com.github.panpf.sketch.core.common.test.state.ConditionStateImageTest
 */
data class ConditionStateImage(
    val stateList: List<Pair<Condition, StateImage>>
) : StateImage {

    override val key: String by lazy {
        val states = stateList.joinToString(prefix = "[", postfix = "]", separator = ",") {
            it.first.key + ":" + it.second.key
        }
        "Condition($states)"
    }

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? = stateList
        .find { it.first.accept(request, throwable) }
        ?.second?.getImage(sketch, request, throwable)

    override fun toString(): String {
        val states = stateList.joinToString(prefix = "[", postfix = "]", separator = ", ") {
            it.first.toString() + ":" + it.second.toString()
        }
        return "ConditionStateImage($states)"
    }

    class Builder constructor(private val defaultImage: StateImage) {

        private val stateList = mutableListOf<Pair<Condition, StateImage>>()

        fun addState(condition: Condition, stateImage: StateImage): Builder = apply {
            stateList.add(Pair(condition, stateImage))
        }

        fun build(): ConditionStateImage {
            val list = stateList.plus(DefaultCondition to defaultImage)
            return ConditionStateImage(list)
        }
    }

    interface Condition : Key {

        fun accept(
            request: ImageRequest,
            throwable: Throwable?
        ): Boolean

        override fun equals(other: Any?): Boolean

        override fun hashCode(): Int

        override fun toString(): String
    }

    data object DefaultCondition : Condition {

        override val key: String = "Default"

        override fun accept(request: ImageRequest, throwable: Throwable?): Boolean = true

        override fun toString(): String = "DefaultCondition"
    }
}