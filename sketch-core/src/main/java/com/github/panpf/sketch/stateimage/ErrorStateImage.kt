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

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayRequest.Builder
import com.github.panpf.sketch.request.internal.UriEmptyException
import com.github.panpf.sketch.request.isCausedBySaveCellularTraffic
import com.github.panpf.sketch.util.SketchException
import java.util.LinkedList
import java.util.regex.Matcher

class ErrorStateImage(val matcherList: List<Matcher>) : StateImage {

    override fun getDrawable(
        context: Context, sketch: Sketch, request: DisplayRequest, throwable: SketchException?
    ): Drawable? = matcherList
        .find { it.match(context, sketch, request, throwable) }
        ?.getDrawable(context, sketch, request, throwable)

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
            addMatcher(EmptyMatcherImpl(emptyImage))
        }

        fun emptyErrorImage(emptyDrawable: Drawable): Builder = apply {
            addMatcher(EmptyMatcherImpl(StateImage.drawable(emptyDrawable)))
        }

        fun emptyErrorImage(emptyImageResId: Int): Builder = apply {
            addMatcher(EmptyMatcherImpl(StateImage.drawableRes(emptyImageResId)))
        }

        fun saveCellularTrafficErrorImage(saveCellularTrafficImage: StateImage): Builder = apply {
            addMatcher(SaveCellularTrafficMatcherImpl(saveCellularTrafficImage))
        }

        fun saveCellularTrafficErrorImage(saveCellularTrafficDrawable: Drawable): Builder = apply {
            addMatcher(
                SaveCellularTrafficMatcherImpl(StateImage.drawable(saveCellularTrafficDrawable))
            )
        }

        fun saveCellularTrafficErrorImage(saveCellularTrafficImageResId: Int): Builder = apply {
            addMatcher(
                SaveCellularTrafficMatcherImpl(StateImage.drawableRes(saveCellularTrafficImageResId))
            )
        }

        fun build(): ErrorStateImage = ErrorStateImage(matcherList.plus(MatcherImpl(errorImage)))
    }

    interface Matcher {
        fun match(
            context: Context,
            sketch: Sketch,
            request: DisplayRequest,
            throwable: SketchException?
        ): Boolean

        fun getDrawable(
            context: Context,
            sketch: Sketch,
            request: DisplayRequest,
            throwable: SketchException?
        ): Drawable?
    }

    private class MatcherImpl(val errorImage: StateImage) : Matcher {
        override fun match(
            context: Context,
            sketch: Sketch,
            request: DisplayRequest,
            throwable: SketchException?
        ): Boolean = true

        override fun getDrawable(
            context: Context,
            sketch: Sketch,
            request: DisplayRequest,
            throwable: SketchException?
        ): Drawable? = errorImage.getDrawable(context, sketch, request, throwable)
    }

    private class EmptyMatcherImpl(val emptyImage: StateImage) : Matcher {
        override fun match(
            context: Context,
            sketch: Sketch,
            request: DisplayRequest,
            throwable: SketchException?
        ): Boolean = throwable is UriEmptyException

        override fun getDrawable(
            context: Context,
            sketch: Sketch,
            request: DisplayRequest,
            throwable: SketchException?
        ): Drawable? = emptyImage.getDrawable(context, sketch, request, throwable)
    }

    private class SaveCellularTrafficMatcherImpl(
        val saveCellularTrafficImage: StateImage
    ) : Matcher {

        override fun match(
            context: Context,
            sketch: Sketch,
            request: DisplayRequest,
            throwable: SketchException?
        ): Boolean = throwable?.isCausedBySaveCellularTraffic == true

        override fun getDrawable(
            context: Context,
            sketch: Sketch,
            request: DisplayRequest,
            throwable: SketchException?
        ): Drawable? = saveCellularTrafficImage.getDrawable(context, sketch, request, throwable)
    }
}