package com.github.panpf.sketch.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.github.panpf.sketch.compose.ContentPainterModifier
import com.github.panpf.sketch.compose.name
import com.github.panpf.sketch.compose.state.AsyncImagePainter2.Companion.DefaultTransform
import com.github.panpf.sketch.compose.state.AsyncImagePainter2.State
import com.github.panpf.sketch.request.DisplayRequest

/**
 * A composable that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * @param request [DisplayRequest].
 * @param contentDescription Text used by accessibility services to describe what this image
 *  represents. This should always be provided unless this image is used for decorative purposes,
 *  and does not represent a meaningful action that a user can take.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content.
 * @param transform A callback to transform a new [State] before it's applied to the
 *  [AsyncImagePainter2]. Typically this is used to modify the state's [Painter].
 * @param onState Called when the state of this painter changes.
 * @param alignment Optional alignment parameter used to place the [AsyncImagePainter2] in the given
 *  bounds defined by the width and height.
 * @param contentScale Optional scale parameter used to determine the aspect ratio scaling to be
 *  used if the bounds are a different size from the intrinsic size of the [AsyncImagePainter2].
 * @param alpha Optional opacity to be applied to the [AsyncImagePainter2] when it is rendered
 *  onscreen.
 * @param colorFilter Optional [ColorFilter] to apply for the [AsyncImagePainter2] when it is
 *  rendered onscreen.
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun AsyncImage2(
    state: AsyncImageState,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    transform: (State) -> State = DefaultTransform,
    onState: ((State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
) {
    state.sketch.logger.d("NewAsyncImageTest") {
        "AsyncImage2. setContentScale: ${state.contentScale?.name} -> ${contentScale.name}. ${state.request.uriString}"
    }
    state.contentScale = contentScale
    val painter = rememberAsyncImagePainter2(
        state, transform, onState, contentScale, filterQuality
    )
    // Draw the content without a parent composable or subcomposition.
    Content(
        modifier = modifier.onSizeChanged { size ->
            if (size != state.size) {
                state.sketch.logger.d("NewAsyncImageTest") {
                    "AsyncImage2. onSizeChanged: ${state.size} -> $size. ${state.request.uriString}"
                }
                state.size = size
            }
        },
        painter = painter,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        clipToBounds = clipToBounds,
    )
}

/** Draws the current image content. */
@Composable
internal fun Content(
    modifier: Modifier,
    painter: Painter,
    contentDescription: String?,
    alignment: Alignment,
    contentScale: ContentScale,
    alpha: Float,
    colorFilter: ColorFilter?,
    clipToBounds: Boolean = true,
) = Layout(
    modifier = modifier
        .contentDescription(contentDescription)
        .let { if (clipToBounds) it.clipToBounds() else it }
        .then(
            ContentPainterModifier(
                painter = painter,
                alignment = alignment,
                contentScale = contentScale,
                alpha = alpha,
                colorFilter = colorFilter
            )
        ),
    measurePolicy = { _, constraints ->
        layout(constraints.minWidth, constraints.minHeight) {}
    }
)

@Stable
private fun Modifier.contentDescription(contentDescription: String?): Modifier {
    @Suppress("LiftReturnOrAssignment")
    if (contentDescription != null) {
        return semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        return this
    }
}
