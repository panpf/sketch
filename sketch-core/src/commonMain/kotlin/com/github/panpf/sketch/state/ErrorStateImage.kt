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
package com.github.panpf.sketch.state

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.state.ErrorStateImage.Builder

/**
 * Create an ErrorStateImage
 */
fun ErrorStateImage(
    defaultImage: StateImage? = null,
    configBlock: (Builder.() -> Unit)? = null
): ErrorStateImage = Builder(defaultImage).apply {
    configBlock?.invoke(this)
}.build()

/**
 * Provide Drawable specifically for error status, support custom [CombinedStateImage.Condition] Provide different Drawable according to different error types
 */
interface ErrorStateImage : CombinedStateImage {

    class Builder constructor(private val defaultImage: StateImage?) {

        private val stateList = mutableListOf<Pair<CombinedStateImage.Condition, StateImage?>>()

        /**
         * Add a custom state
         */
        fun addState(pair: Pair<CombinedStateImage.Condition, StateImage?>): Builder = apply {
            stateList.add(pair)
        }

        /**
         * Add a StateImage dedicated to the empty uri error
         */
        fun uriEmptyError(emptyImage: StateImage): Builder = apply {
            addState(UriEmptyCondition to emptyImage)
        }

        fun build(): ErrorStateImage {
            val list = if (defaultImage != null) {
                stateList.plus(DefaultCondition to defaultImage)
            } else {
                stateList
            }
            return ErrorStateImageImpl(list)
        }
    }

    data object DefaultCondition : CombinedStateImage.Condition {

        override fun accept(request: ImageRequest, throwable: Throwable?): Boolean = true

    }

    data object UriEmptyCondition : CombinedStateImage.Condition {

        override fun accept(request: ImageRequest, throwable: Throwable?): Boolean =
            throwable is UriInvalidException && (request.uriString.isEmpty() || request.uriString.isBlank())

    }
}

private class ErrorStateImageImpl(
    override val stateList: List<Pair<CombinedStateImage.Condition, StateImage?>>
) : ErrorStateImage {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ErrorStateImage) return false
        if (stateList != other.stateList) return false
        return true
    }

    override fun hashCode(): Int {
        return stateList.hashCode()
    }

    override fun toString(): String {
        val listString = stateList.joinToString(prefix = "[", postfix = "]")
        return "ErrorStateImage(${listString})"
    }
}