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

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.AttachObserver
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.fitScale

/**
 * An opinionated [ViewTarget] that simplifies updating the [Image] attached to a [View]
 * and supports automatically starting and stopping animated [Drawable]s.
 *
 * If you need custom behaviour that this class doesn't support it's recommended
 * to implement [ViewTarget] directly.
 *
 * @see com.github.panpf.sketch.view.core.test.target.GenericViewTargetTest
 */
abstract class GenericViewTarget<T : View>(view: T) : ViewTarget<T>, TransitionViewTarget,
    LifecycleEventObserver, AttachObserver {

    internal var isStarted = false
    internal var isAttached = false

    private val requestManager = view.requestManager

    @Deprecated("Please use scaleType instead and will be deleted in the future")
    override val fitScale: Boolean
        get() = scaleType.fitScale

    override fun getRequestManager(): RequestManager = requestManager

    override fun onStart(sketch: Sketch, request: ImageRequest, placeholder: Image?) {
        updateImage(request, placeholder)
    }

    override fun onSuccess(
        sketch: Sketch,
        request: ImageRequest,
        result: ImageResult.Success,
        image: Image
    ) = updateImage(request, image)

    override fun onError(
        sketch: Sketch,
        request: ImageRequest,
        error: ImageResult.Error,
        image: Image?
    ) = updateImage(request, image)

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        when (event) {
            Event.ON_START -> {
                isStarted = true
                updateAnimation()
            }

            Event.ON_STOP -> {
                isStarted = false
                updateAnimation()
            }

            else -> {

            }
        }
    }

    override fun onAttachedChanged(attached: Boolean) {
        this.isAttached = attached
        updateAnimation()
    }

    private fun updateImage(request: ImageRequest, image: Image?) {
        view ?: return
        // 'image != null' is important.
        // It makes it easier to implement crossfade animation between old and new drawables.
        // com.github.panpf.sketch.sample.ui.gallery.PhotoPagerViewFragment.loadBgImage() is an example.
        if (image != null || request.allowNullImage == true) {
            val newDrawable = image?.asDrawable(request.context.resources)
            updateDrawable(newDrawable)
        }
    }

    private fun updateDrawable(newDrawable: Drawable?) {
        val oldDrawable = drawable
        if (newDrawable !== oldDrawable) {
            oldDrawable.asOrNull<Animatable>()?.stop()
            setDrawable(newDrawable)
            updateAnimation()
        }
    }

    protected abstract fun setDrawable(drawable: Drawable?)

    /** Start/stop the current [Drawable]'s animation based on the current lifecycle state. */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun updateAnimation() {
        val animatable = this.drawable.asOrNull<Animatable>() ?: return
        if (isStarted && isAttached) animatable.start() else animatable.stop()
    }
}