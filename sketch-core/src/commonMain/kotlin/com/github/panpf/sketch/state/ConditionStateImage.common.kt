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

    override val key: String = "ConditionStateImage(${
        stateList.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ",",
            transform = { it.first.toString() + ":" + it.second.key })
    })"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? = stateList
        .find { it.first.accept(request, throwable) }
        ?.second?.getImage(sketch, request, throwable)

    override fun toString(): String = "ConditionStateImage(${
        stateList.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ", ",
            transform = { it.first.toString() + ":" + it.second.key })
    })"

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

    interface Condition {

        fun accept(
            request: ImageRequest,
            throwable: Throwable?
        ): Boolean
    }

    data object DefaultCondition : Condition {

        override fun accept(request: ImageRequest, throwable: Throwable?): Boolean = true
    }
}