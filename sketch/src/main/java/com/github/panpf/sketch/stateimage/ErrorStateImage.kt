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
package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.UriInvalidException
import com.github.panpf.sketch.stateimage.ErrorStateImage.Builder
import com.github.panpf.sketch.util.SketchException
import java.util.LinkedList

fun ErrorStateImage(
    defaultImage: StateImage,
    configBlock: (Builder.() -> Unit)? = null
): ErrorStateImage = Builder(defaultImage).apply {
    configBlock?.invoke(this)
}.build()

class ErrorStateImage private constructor(val matcherList: List<Matcher>) : StateImage {

    override fun getDrawable(
        sketch: Sketch,
        request: ImageRequest,
        exception: SketchException?
    ): Drawable? =
        matcherList
            .find { it.match(request, exception) }
            ?.getDrawable(sketch, request, exception)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ErrorStateImage

        if (matcherList != other.matcherList) return false

        return true
    }

    override fun hashCode(): Int {
        return matcherList.hashCode()
    }

    override fun toString(): String {
        return "ErrorStateImage(matcherList=$matcherList)"
    }

    class Builder(private val defaultImage: StateImage) {

        private val matcherList = LinkedList<Matcher>()

        fun addMatcher(matcher: Matcher): Builder = apply {
            matcherList.add(matcher)
        }

        fun uriEmptyError(emptyImage: StateImage): Builder = apply {
            addMatcher(UriEmptyMatcher(emptyImage))
        }

        fun uriEmptyError(emptyDrawable: Drawable): Builder = apply {
            addMatcher(UriEmptyMatcher(DrawableStateImage(emptyDrawable)))
        }

        fun uriEmptyError(emptyImageResId: Int): Builder = apply {
            addMatcher(UriEmptyMatcher(DrawableStateImage(emptyImageResId)))
        }

        fun build(): ErrorStateImage =
            ErrorStateImage(matcherList.plus(DefaultMatcher(defaultImage)))
    }

    interface Matcher {

        fun match(request: ImageRequest, exception: SketchException?): Boolean

        fun getDrawable(
            sketch: Sketch,
            request: ImageRequest,
            throwable: SketchException?
        ): Drawable?
    }

    private class DefaultMatcher(val stateImage: StateImage) : Matcher {

        override fun match(request: ImageRequest, exception: SketchException?): Boolean = true

        override fun getDrawable(
            sketch: Sketch, request: ImageRequest, throwable: SketchException?
        ): Drawable? =
            stateImage.getDrawable(sketch, request, throwable)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DefaultMatcher

            if (stateImage != other.stateImage) return false

            return true
        }

        override fun hashCode(): Int {
            return stateImage.hashCode()
        }

        override fun toString(): String {
            return "DefaultMatcher(stateImage=$stateImage)"
        }
    }

    class UriEmptyMatcher(val stateImage: StateImage) : Matcher {

        override fun match(request: ImageRequest, exception: SketchException?): Boolean =
            exception is UriInvalidException && (request.uriString.isEmpty() || request.uriString.isBlank())

        override fun getDrawable(
            sketch: Sketch, request: ImageRequest, throwable: SketchException?
        ): Drawable? =
            stateImage.getDrawable(sketch, request, throwable)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UriEmptyMatcher

            if (stateImage != other.stateImage) return false

            return true
        }

        override fun hashCode(): Int {
            return stateImage.hashCode()
        }

        override fun toString(): String {
            return "UriEmptyMatcher(stateImage=$stateImage)"
        }
    }
}