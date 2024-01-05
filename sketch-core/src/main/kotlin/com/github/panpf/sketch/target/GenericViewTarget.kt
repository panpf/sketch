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
import android.view.View.OnAttachStateChangeListener
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.request.Image
import com.github.panpf.sketch.request.allowSetNullDrawable
import com.github.panpf.sketch.request.asDrawable
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.transition.TransitionViewTarget
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.updateIsDisplayed

/**
 * An opinionated [ViewTarget] that simplifies updating the [Image] attached to a [View]
 * and supports automatically starting and stopping animated [Drawable]s.
 *
 * If you need custom behaviour that this class doesn't support it's recommended
 * to implement [ViewTarget] directly.
 */
abstract class GenericViewTarget<T : View>(view: T) : ViewTarget<T>, TransitionViewTarget,
    DefaultLifecycleObserver {

    private var isStarted = false

    override val supportDisplayCount: Boolean = true

    init {
        view.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
            }

            override fun onViewDetachedFromWindow(v: View) {
                updateDrawable(null)    // To trigger setIsDisplayed
            }
        })
    }

    override fun onStart(requestContext: RequestContext, placeholder: Image?) =
        updateImage(requestContext, placeholder)

    override fun onError(requestContext: RequestContext, error: Image?) =
        updateImage(requestContext, error)

    override fun onSuccess(requestContext: RequestContext, result: Image) =
        updateImage(requestContext, result)

    override fun onStart(owner: LifecycleOwner) {
        isStarted = true
        updateAnimation()
    }

    override fun onStop(owner: LifecycleOwner) {
        isStarted = false
        updateAnimation()
    }

    private fun updateImage(requestContext: RequestContext, image: Image?) {
        // 'image != null' is important.
        // It makes it easier to implement crossfade animation between old and new drawables.
        // com.github.panpf.sketch.sample.ui.gallery.PhotoPagerViewFragment.loadBgImage() is an example.
        view ?: return
        if (image != null || requestContext.request.allowSetNullDrawable) {
            this.drawable.asOrNull<Animatable>()?.stop()
            val newDrawable = image?.asDrawable(requestContext.request.context.resources)
            updateDrawable(newDrawable)
            updateAnimation()
        }
    }

    private fun updateDrawable(newDrawable: Drawable?) {
        val oldDrawable = drawable
        newDrawable?.updateIsDisplayed(true, "ImageView")
        this.drawable = newDrawable
        if (newDrawable is CrossfadeDrawable) {
            val start = newDrawable.start
            if (start != null && start === oldDrawable) {
                require(start.callback == null) { "start.callback is not null. set after" }
                // TODO It may have something to do with the animation. I’ll see if I want to delete it after testing.
                start.callback = newDrawable
            }
        }
        oldDrawable?.updateIsDisplayed(false, "ImageView")
    }

    /** Start/stop the current [Drawable]'s animation based on the current lifecycle state. */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun updateAnimation() {
        val animatable = this.drawable.asOrNull<Animatable>() ?: return
        if (isStarted) animatable.start() else animatable.stop()
    }
}
