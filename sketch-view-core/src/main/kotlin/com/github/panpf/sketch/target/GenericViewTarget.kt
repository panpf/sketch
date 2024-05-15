/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------------
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
package com.github.panpf.sketch.target

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asDrawableOrThrow
import com.github.panpf.sketch.request.allowNullImage
import com.github.panpf.sketch.request.internal.AttachObserver
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.target.TargetLifecycle.Event
import com.github.panpf.sketch.util.asOrNull

/**
 * An opinionated [ViewTarget] that simplifies updating the [Image] attached to a [View]
 * and supports automatically starting and stopping animated [Drawable]s.
 *
 * If you need custom behaviour that this class doesn't support it's recommended
 * to implement [ViewTarget] directly.
 */
abstract class GenericViewTarget<T : View>(view: T) : ViewTarget<T>, TransitionViewTarget,
    TargetLifecycle.EventObserver, AttachObserver {

    private var isStarted = false
    private var isAttached = false
    private val requestManager = view.requestManager

    override fun getRequestManager(): RequestManager = requestManager

    override fun onStart(requestContext: RequestContext, placeholder: Image?) {
        updateImage(requestContext, placeholder)
    }

    override fun onSuccess(requestContext: RequestContext, result: Image) {
        updateImage(requestContext, result)
    }

    override fun onError(requestContext: RequestContext, error: Image?) =
        updateImage(requestContext, error)

    override fun onStateChanged(source: TargetLifecycle, event: Event) {
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

    private fun updateImage(requestContext: RequestContext, image: Image?) {
        view ?: return
        // 'image != null' is important.
        // It makes it easier to implement crossfade animation between old and new drawables.
        // com.github.panpf.sketch.sample.ui.gallery.PhotoPagerViewFragment.loadBgImage() is an example.
        if (image != null || requestContext.request.allowNullImage) {
            val newDrawable = image?.asDrawableOrThrow()
            updateDrawable(newDrawable)
        }
    }

    private fun updateDrawable(newDrawable: Drawable?) {
        val oldDrawable = drawable
        if (newDrawable !== oldDrawable) {
            oldDrawable.asOrNull<Animatable>()?.stop()
            this.drawable = newDrawable
            updateAnimation()
        }
    }

    /** Start/stop the current [Drawable]'s animation based on the current lifecycle state. */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun updateAnimation() {
        val animatable = this.drawable.asOrNull<Animatable>() ?: return
        if (isStarted && isAttached) animatable.start() else animatable.stop()
    }
}