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
package com.github.panpf.sketch.compose.target

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.compose.asPainter
import com.github.panpf.sketch.compose.internal.asOrNull
import com.github.panpf.sketch.compose.painter.AnimatablePainter
import com.github.panpf.sketch.compose.transition.TransitionComposeTarget
import com.github.panpf.sketch.request.allowSetNullDrawable
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * An opinionated [ComposeTarget] that simplifies updating the [Image] attached to a ComposeComponent.
 *
 * If you need custom behaviour that this class doesn't support it's recommended
 * to implement [ComposeTarget] directly.
 */
abstract class GenericComposeTarget : ComposeTarget, TransitionComposeTarget {

    override fun onStart(requestContext: RequestContext, placeholder: Image?) =
        updateImage(requestContext, placeholder)

    override fun onSuccess(requestContext: RequestContext, result: Image) =
        updateImage(requestContext, result)

    override fun onError(requestContext: RequestContext, error: Image?) =
        updateImage(requestContext, error)

    private fun updateImage(requestContext: RequestContext, image: Image?) {
        // 'image != null' is important.
        // It makes it easier to implement crossfade animation between old and new drawables.
        // com.github.panpf.sketch.sample.ui.gallery.PhotoPagerComposeFragment#PagerBgImage() is an example.
        if (image != null || requestContext.request.allowSetNullDrawable) {
            val newPainter = image?.asPainter()
            updatePainter(newPainter)
            // TODO Start and stop animations based on lifecycle
        }
    }

    private fun updatePainter(newPainter: Painter?) {
        val oldPainter = painter
        if (newPainter !== oldPainter) {
            // TODO 停止旧动画
            painter = newPainter
            // AsyncImageState's AsyncImageTarget will call Painter's onRemembered and onForgotten
            // methods to trigger the start and stop of Animatable in DrawablePainter
            // TODO 播放动画
        }
    }

    fun onForgotten() {
        // TODO 不需要再设置为 null 了
        updatePainter(null)  // To trigger setIsDisplayed and onForgotten
        // TODO 搞一个 ComposeRequestManager，用来回调 onForgotten，以及回调 Lifecycle 的 Event
        // TODO RequestManager 从 Target 获取，Compose 版本最终由 AsyncImageState 提供
    }

//    /** Start/stop the current [Drawable]'s animation based on the current lifecycle state. */
//    @Suppress("MemberVisibilityCanBePrivate")
//    protected fun updateAnimation() {
//        val animatable = this.painter.asOrNull<AnimatablePainter>() ?: return
//        if (isStarted) animatable.start() else animatable.stop()
//    }
}