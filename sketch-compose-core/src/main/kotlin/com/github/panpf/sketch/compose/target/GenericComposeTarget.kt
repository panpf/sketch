package com.github.panpf.sketch.compose.target

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.internal.updateIsDisplayed
import com.github.panpf.sketch.compose.request.asPainter
import com.github.panpf.sketch.compose.transition.TransitionComposeTarget
import com.github.panpf.sketch.request.Image
import com.github.panpf.sketch.request.allowSetNullDrawable
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * An opinionated [ComposeTarget] that simplifies updating the [Image] attached to a ComposeComponent.
 *
 * If you need custom behaviour that this class doesn't support it's recommended
 * to implement [ComposeTarget] directly.
 */
// TODO test
abstract class GenericComposeTarget : ComposeTarget, TransitionComposeTarget {

    override val supportDisplayCount: Boolean = true

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
            // AsyncImageState's AsyncImageTarget will call Painter's onRemembered and onForgotten methods to trigger the start and stop of Animatable in DrawablePainter
//            this.drawable.asOrNull<Animatable>()?.stop()
            val newPainter = image?.asPainter()
            updatePainter(newPainter)
//            updateAnimation()
        }
    }

    fun updatePainter(newPainter: Painter?) {
        val oldDrawable = painter
        newPainter?.updateIsDisplayed(true, "AsyncImage")
        painter = newPainter
        oldDrawable?.updateIsDisplayed(false, "AsyncImage")
    }
}