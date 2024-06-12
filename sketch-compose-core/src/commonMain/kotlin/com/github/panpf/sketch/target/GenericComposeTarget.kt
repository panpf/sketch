/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.lifecycle.PlatformLifecycle.Event
import com.github.panpf.sketch.lifecycle.PlatformLifecycleEventObserver
import com.github.panpf.sketch.lifecycle.PlatformLifecycleOwner
import com.github.panpf.sketch.painter.AnimatablePainter
import com.github.panpf.sketch.request.allowNullImage
import com.github.panpf.sketch.request.internal.AttachObserver
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.transition.TransitionComposeTarget
import com.github.panpf.sketch.util.asOrNull

/**
 * An opinionated [ComposeTarget] that simplifies updating the [Image] attached to a Compose Component
 * and supports automatically starting and stopping animated [Painter]s.
 *
 * If you need custom behaviour that this class doesn't support it's recommended
 * to implement [ComposeTarget] directly.
 */
abstract class GenericComposeTarget : ComposeTarget, TransitionComposeTarget,
    PlatformLifecycleEventObserver, AttachObserver {

    private var isStarted = false
    private var isAttached = false

    override fun onStart(requestContext: RequestContext, placeholder: Image?) =
        updateImage(requestContext, placeholder)

    override fun onError(requestContext: RequestContext, error: Image?) =
        updateImage(requestContext, error)

    override fun onSuccess(requestContext: RequestContext, result: Image) =
        updateImage(requestContext, result)

    override fun onStateChanged(source: PlatformLifecycleOwner, event: Event) {
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
        // 'image != null' is important.
        // It makes it easier to implement crossfade animation between old and new drawables.
        // com.github.panpf.sketch.sample.ui.gallery.PhotoPagerComposeFragment#PagerBgImage() is an example.
        if (image != null || requestContext.request.allowNullImage) {
            val newPainter = image?.asPainter()
            updatePainter(newPainter)
        }
    }

    private fun updatePainter(newPainter: Painter?) {
        val oldPainter = painter
        if (newPainter !== oldPainter) {
            oldPainter.asOrNull<AnimatablePainter>()?.stop()
            painter = newPainter
            updateAnimation()
        }
    }

    /** Start/stop the current [AnimatablePainter]'s animation based on the current lifecycle state. */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun updateAnimation() {
        val animatablePainter = this.painter.asOrNull<AnimatablePainter>() ?: return
        if (isStarted && isAttached) animatablePainter.start() else animatablePainter.stop()
    }
}