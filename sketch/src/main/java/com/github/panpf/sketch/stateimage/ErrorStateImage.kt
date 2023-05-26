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
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.stateimage.ErrorStateImage.Builder
import com.github.panpf.sketch.stateimage.ErrorStateImage.ErrorRules
import com.github.panpf.sketch.util.ifOrNull
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
 * Provide Drawable specifically for error status, support custom [ErrorRules] Provide different Drawable according to different error types
 */
interface ErrorStateImage : StateImage {

    val errorRulesList: List<ErrorRules>

    override fun getDrawable(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Drawable? = errorRulesList
        .firstNotNullOfOrNull { it.getDrawable(sketch, request, throwable) }
        ?.getOrNull()

    class Builder constructor(private val defaultImage: StateImage?) {

        private val errorRulesList = LinkedList<ErrorRules>()

        /**
         * Add a custom [ErrorRules]
         */
        fun addErrorRules(errorRules: ErrorRules): Builder = apply {
            errorRulesList.add(errorRules)
        }

        /**
         * Add a StateImage dedicated to the empty uri error
         */
        fun uriEmptyError(emptyImage: StateImage): Builder = apply {
            addErrorRules(UriEmptyErrorRules(emptyImage))
        }

        /**
         * Add a StateImage dedicated to the empty uri error
         */
        fun uriEmptyError(emptyDrawable: Drawable): Builder = apply {
            addErrorRules(UriEmptyErrorRules(DrawableStateImage(emptyDrawable)))
        }

        /**
         * Add a StateImage dedicated to the empty uri error
         */
        fun uriEmptyError(emptyImageResId: Int): Builder = apply {
            addErrorRules(UriEmptyErrorRules(DrawableStateImage(emptyImageResId)))
        }

        fun build(): ErrorStateImage {
            val list = if (defaultImage != null) {
                errorRulesList.plus(DefaultErrorRules(defaultImage))
            } else {
                errorRulesList
            }
            return ErrorStateImageImpl(list)
        }
    }

    class ErrorStateImageImpl(override val errorRulesList: List<ErrorRules>) :
        ErrorStateImage {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as ErrorStateImageImpl
            if (errorRulesList != other.errorRulesList) return false
            return true
        }

        override fun hashCode(): Int {
            return errorRulesList.hashCode()
        }

        override fun toString(): String {
            val factoryListString =
                errorRulesList.joinToString(prefix = "[", postfix = "]")
            return "ErrorStateImage(${factoryListString})"
        }
    }

    /**
     * Match the error and return a dedicated Drawable
     */
    interface ErrorRules {

        fun getDrawable(
            sketch: Sketch,
            request: ImageRequest,
            throwable: Throwable?
        ): Result<Drawable?>?
    }

    class DefaultErrorRules(private val stateImage: StateImage) : ErrorRules {

        override fun getDrawable(
            sketch: Sketch, request: ImageRequest, throwable: Throwable?
        ): Result<Drawable?> = Result.success(stateImage.getDrawable(sketch, request, throwable))

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as DefaultErrorRules
            if (stateImage != other.stateImage) return false
            return true
        }

        override fun hashCode(): Int {
            return stateImage.hashCode()
        }

        override fun toString(): String {
            return "DefaultErrorRules($stateImage)"
        }
    }

    class UriEmptyErrorRules(private val stateImage: StateImage) : ErrorRules {

        override fun getDrawable(
            sketch: Sketch, request: ImageRequest, throwable: Throwable?
        ): Result<Drawable?>? =
            ifOrNull(throwable is UriInvalidException && (request.uriString.isEmpty() || request.uriString.isBlank())) {
                Result.success(stateImage.getDrawable(sketch, request, throwable))
            }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as UriEmptyErrorRules
            if (stateImage != other.stateImage) return false
            return true
        }

        override fun hashCode(): Int {
            return stateImage.hashCode()
        }

        override fun toString(): String {
            return "UriEmptyErrorRules($stateImage)"
        }
    }
}