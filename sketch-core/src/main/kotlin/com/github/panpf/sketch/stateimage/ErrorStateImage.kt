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
package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.stateimage.ErrorStateImage.Builder
import com.github.panpf.sketch.stateimage.internal.CompositeStateImage
import java.util.LinkedList

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
 * Provide Drawable specifically for error status, support custom [CompositeStateImage.Condition] Provide different Drawable according to different error types
 */
class ErrorStateImage(
    override val stateList: List<Pair<CompositeStateImage.Condition, StateImage?>>
) : CompositeStateImage {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ErrorStateImage
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

    class Builder constructor(private val defaultImage: StateImage?) {

        private val stateList = LinkedList<Pair<CompositeStateImage.Condition, StateImage?>>()

        /**
         * Add a custom state
         */
        fun addState(pair: Pair<CompositeStateImage.Condition, StateImage?>): Builder = apply {
            stateList.add(pair)
        }

        /**
         * Add a StateImage dedicated to the empty uri error
         */
        fun uriEmptyError(emptyImage: StateImage): Builder = apply {
            addState(UriEmptyCondition to emptyImage)
        }

        /**
         * Add a StateImage dedicated to the empty uri error
         */
        fun uriEmptyError(emptyDrawable: Drawable): Builder = apply {
            addState(UriEmptyCondition to DrawableStateImage(emptyDrawable))
        }

        /**
         * Add a StateImage dedicated to the empty uri error
         */
        fun uriEmptyError(emptyImageResId: Int): Builder = apply {
            addState(UriEmptyCondition to DrawableStateImage(emptyImageResId))
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

    object DefaultCondition : CompositeStateImage.Condition {

        override fun accept(request: ImageRequest, throwable: Throwable?): Boolean = true

        override fun toString(): String {
            return "DefaultCondition"
        }
    }

    object UriEmptyCondition : CompositeStateImage.Condition {

        override fun accept(request: ImageRequest, throwable: Throwable?): Boolean =
            throwable is UriInvalidException && (request.uriString.isEmpty() || request.uriString.isBlank())

        override fun toString(): String {
            return "UriEmptyCondition"
        }
    }
}