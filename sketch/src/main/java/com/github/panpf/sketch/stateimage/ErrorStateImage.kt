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
import com.github.panpf.sketch.util.SketchException
import java.util.LinkedList

class ErrorStateImage(private val matcherList: List<Matcher>) : StateImage {

    override fun getDrawable(
        sketch: Sketch, request: ImageRequest, throwable: SketchException?
    ): Drawable? = matcherList
        .find { it.match(sketch, request, throwable) }
        ?.getDrawable(sketch, request, throwable)

    companion object {
        fun new(
            errorImage: StateImage,
            configBlock: (Builder.() -> Unit)? = null
        ): ErrorStateImage = Builder(errorImage).apply {
            configBlock?.invoke(this)
        }.build()

        fun newBuilder(
            errorImage: StateImage,
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder(errorImage).apply {
            configBlock?.invoke(this)
        }
    }

    class Builder(private val errorImage: StateImage) {
        private val matcherList = LinkedList<Matcher>()

        fun addMatcher(matcher: Matcher): Builder = apply {
            matcherList.add(matcher)
        }

        fun emptyErrorImage(emptyImage: StateImage): Builder = apply {
            addMatcher(EmptyMatcher(emptyImage))
        }

        fun emptyErrorImage(emptyDrawable: Drawable): Builder = apply {
            addMatcher(EmptyMatcher(StateImage.drawable(emptyDrawable)))
        }

        fun emptyErrorImage(emptyImageResId: Int): Builder = apply {
            addMatcher(EmptyMatcher(StateImage.drawableRes(emptyImageResId)))
        }

        fun build(): ErrorStateImage = ErrorStateImage(matcherList.plus(DefaultMatcher(errorImage)))
    }

    interface Matcher {
        fun match(
            sketch: Sketch,
            request: ImageRequest,
            throwable: SketchException?
        ): Boolean

        fun getDrawable(
            sketch: Sketch,
            request: ImageRequest,
            throwable: SketchException?
        ): Drawable?
    }

    private class DefaultMatcher(val errorImage: StateImage) : Matcher {
        override fun match(
            sketch: Sketch,
            request: ImageRequest,
            throwable: SketchException?
        ): Boolean = true

        override fun getDrawable(
            sketch: Sketch,
            request: ImageRequest,
            throwable: SketchException?
        ): Drawable? = errorImage.getDrawable(sketch, request, throwable)
    }

    private class EmptyMatcher(val emptyImage: StateImage) : Matcher {
        override fun match(
            sketch: Sketch,
            request: ImageRequest,
            throwable: SketchException?
        ): Boolean = throwable is UriInvalidException && (request.uriString.isEmpty() || request.uriString.isBlank())

        override fun getDrawable(
            sketch: Sketch,
            request: ImageRequest,
            throwable: SketchException?
        ): Drawable? = emptyImage.getDrawable(sketch, request, throwable)
    }
}