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

package com.github.panpf.sketch.state

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.ErrorStateImage.Builder

/**
 * Create an ErrorStateImage
 *
 * @see com.github.panpf.sketch.core.common.test.state.ErrorStateImageTest.testFun
 */
fun ErrorStateImage(
    defaultImage: StateImage? = null,
    configBlock: (Builder.() -> Unit)? = null
): ErrorStateImage = Builder(defaultImage).apply {
    configBlock?.invoke(this)
}.build()

/**
 * Provide Drawable specifically for error status, support custom [ErrorStateImage.Condition] Provide different Drawable according to different error types
 *
 * @see com.github.panpf.sketch.core.common.test.state.ErrorStateImageTest
 */
class ErrorStateImage(
    val stateList: List<Pair<Condition, StateImage?>>
) : StateImage {

    override val key: String =
        "ErrorStateImage(${
            stateList.joinToString(
                prefix = "[",
                postfix = "]",
                separator = ",",
                transform = { it.first.toString() + ":" + it.second?.key })
        })"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? = stateList
        .find { it.first.accept(request, throwable) }
        ?.second?.getImage(sketch, request, throwable)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ErrorStateImage
        if (stateList != other.stateList) return false
        return true
    }

    override fun hashCode(): Int {
        return stateList.hashCode()
    }

    override fun toString(): String {
        return "ErrorStateImage(${
            stateList.joinToString(
                prefix = "[",
                postfix = "]",
                separator = ", ",
                transform = { it.first.toString() + ":" + it.second?.key })
        })"
    }

    interface Condition {

        fun accept(
            request: ImageRequest,
            throwable: Throwable?
        ): Boolean
    }

    class Builder constructor(private val defaultImage: StateImage?) {

        private val stateList = mutableListOf<Pair<Condition, StateImage?>>()

        /**
         * Add a custom error state
         */
        fun addState(condition: Condition, stateImage: StateImage?): Builder = apply {
            stateList.add(Pair(condition, stateImage))
        }

        fun build(): ErrorStateImage {
            val list = if (defaultImage != null) {
                stateList.plus(DefaultCondition to defaultImage)
            } else {
                stateList
            }
            return ErrorStateImage(list)
        }
    }

    data object DefaultCondition : Condition {

        override fun accept(request: ImageRequest, throwable: Throwable?): Boolean = true
    }
}