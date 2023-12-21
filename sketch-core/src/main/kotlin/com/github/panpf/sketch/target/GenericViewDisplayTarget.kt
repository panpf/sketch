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
import android.widget.ImageView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.request.allowSetNullDrawable
import com.github.panpf.sketch.transition.TransitionDisplayTarget
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.util.asOrNull

/**
 * An opinionated [ViewDisplayTarget] that simplifies updating the [Drawable] attached to a [View]
 * and supports automatically starting and stopping animated [Drawable]s.
 *
 * If you need custom behaviour that this class doesn't support it's recommended
 * to implement [ViewDisplayTarget] directly.
 */
abstract class GenericViewDisplayTarget<T : View> : ViewDisplayTarget<T>, TransitionDisplayTarget,
    DefaultLifecycleObserver {

    private var isStarted = false

    /**
     * The current [Drawable] attached to [view].
     */
    abstract override var drawable: Drawable?

    override fun onStart(placeholder: Drawable?) = updateDrawable(placeholder)

    override fun onError(error: Drawable?) = updateDrawable(error)

    override fun onSuccess(result: Drawable) = updateDrawable(result)

    override fun onStart(owner: LifecycleOwner) {
        isStarted = true
        updateAnimation()
    }

    override fun onStop(owner: LifecycleOwner) {
        isStarted = false
        updateAnimation()
    }

    /** Replace the [ImageView]'s current drawable with [drawable]. */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun updateDrawable(drawable: Drawable?) {
        // 'drawable != null' is important.
        // It makes it easier to implement crossfade animation between old and new drawables.
        // com.github.panpf.sketch.sample.ui.viewer.view.ImagePagerFragment.loadBgImage() is an example.
        val view = view as View?
        if (view != null) {
            val request = SketchUtils.getRequest(view)
            if (drawable != null || (request?.allowSetNullDrawable == true)) {
                this.drawable.asOrNull<Animatable>()?.stop()
                this.drawable = drawable
                updateAnimation()
            }
        }
    }

    /** Start/stop the current [Drawable]'s animation based on the current lifecycle state. */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun updateAnimation() {
        val animatable = this.drawable.asOrNull<Animatable>() ?: return
        if (isStarted) animatable.start() else animatable.stop()
    }
}
