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

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.internal.updateIsDisplayed
import com.github.panpf.sketch.compose.request.asPainter
import com.github.panpf.sketch.compose.transition.TransitionComposeTarget
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.allowSetNullDrawable
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * An opinionated [ComposeTarget] that simplifies updating the [Image] attached to a ComposeComponent.
 *
 * If you need custom behaviour that this class doesn't support it's recommended
 * to implement [ComposeTarget] directly.
 */
abstract class GenericComposeTarget : ComposeTarget, TransitionComposeTarget {

    override fun supportDisplayCount(): Boolean = true

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
            newPainter?.updateIsDisplayed(true, "AsyncImage")
            painter = newPainter
            oldPainter?.updateIsDisplayed(false, "AsyncImage")
            // AsyncImageState's AsyncImageTarget will call Painter's onRemembered and onForgotten
            // methods to trigger the start and stop of Animatable in DrawablePainter
        }
    }

    fun onForgotten() {
        updatePainter(null)  // To trigger setIsDisplayed and onForgotten
    }
}