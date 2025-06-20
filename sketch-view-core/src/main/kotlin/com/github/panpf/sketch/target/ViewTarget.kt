/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.target

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.lifecycle.LifecycleObserver
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageOptionsProvider
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ListenerProvider
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.RequestDelegate
import com.github.panpf.sketch.request.internal.ViewRequestDelegate
import com.github.panpf.sketch.resize.ResizeOnDrawHelper
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.resize.ViewResizeOnDrawHelper
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.transition.ViewCrossfadeTransition
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.toScale
import kotlinx.coroutines.Job

/**
 * A [Target] with an associated [View]. Prefer this to [Target] if the given drawables will only
 * be used by [view].
 *
 * Optionally, [ViewTarget]s can implement [LifecycleObserver]. They are automatically registered
 * when the request starts and unregistered when the request completes.
 *
 * @see com.github.panpf.sketch.view.core.test.target.ViewTargetTest
 */
interface ViewTarget<T : View> : Target {

    /**
     * The [View] used by this [Target]. This field should be immutable.
     */
    val view: T?

    /**
     * Th e [view]'s current [Drawable].
     */
    val drawable: Drawable?

    /**
     * The [ScaleType] of the [ImageView] that this target is applied to.
     */
    val scaleType: ScaleType

    override val currentImage: Image?
        get() = drawable?.asImage()

    override fun getScaleDecider(): ScaleDecider? = ScaleDecider(scaleType.toScale())


    override fun newRequestDelegate(
        sketch: Sketch,
        initialRequest: ImageRequest,
        job: Job
    ): RequestDelegate = ViewRequestDelegate(sketch, initialRequest, this, job)


    override fun getListener(): Listener? =
        view?.asOrNull<ListenerProvider>()?.getListener()

    override fun getProgressListener(): ProgressListener? =
        view?.asOrNull<ListenerProvider>()?.getProgressListener()

    override fun getLifecycleResolver(): LifecycleResolver? =
        view?.let { com.github.panpf.sketch.request.ViewLifecycleResolver(it) }


    override fun getSizeResolver(): SizeResolver? = view?.let { ViewSizeResolver(it) }

    override fun getResizeOnDrawHelper(): ResizeOnDrawHelper? = ViewResizeOnDrawHelper

    override fun convertTransition(factory: Transition.Factory): Transition.Factory? {
        if (factory is CrossfadeTransition.Factory) {
            return ViewCrossfadeTransition.Factory(
                durationMillis = factory.durationMillis,
                fadeStart = factory.fadeStart,
                preferExactIntrinsicSize = factory.preferExactIntrinsicSize,
                alwaysUse = factory.alwaysUse,
            )
        }
        return null
    }

    override fun getImageOptions(): ImageOptions? =
        view?.asOrNull<ImageOptionsProvider>()?.imageOptions
}