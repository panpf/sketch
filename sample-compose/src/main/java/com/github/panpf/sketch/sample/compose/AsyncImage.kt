package com.github.panpf.sketch.sample.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.times
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Constraints
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch

@Composable
fun AsyncImage(
    imageUri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
) {
    val context = LocalContext.current
    val sketch = context.sketch
    val isPreview = LocalInspectionMode.current
    val asyncImagePainter = remember {
        AsyncImagePainter(context, sketch, imageUri, configBlock, isPreview, filterQuality)
    }
    // Invoke this manually so `painter.state` is up to date immediately.
    // It must be updated immediately or it will crash in the LazyColumn or LazyVerticalGrid
    asyncImagePainter.onRemembered()

    BoxWithConstraints(modifier, alignment) {
        // Resolve the size for the image request.
        // todo Limit the size of the
//        (request.sizeResolver as? ConstraintsSizeResolver)?.setConstraints(constraints)

//        // Draw the content.
//        val scope = RealAsyncImageScope(
//            parentScope = this,
//            painter = painter,
//            contentDescription = contentDescription,
//            alignment = alignment,
//            contentScale = contentScale,
//            alpha = alpha,
//            colorFilter = colorFilter
//        )

        val state = asyncImagePainter.state
        val painter = state.painter
        if (painter != null) {
            // Compute the intrinsic size of the content.
            val contentSize = computeContentSize(
                constraints = constraints,
                srcSize = painter.intrinsicSize,
                contentScale = contentScale
            )

            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = if (contentSize.isSpecified) {
                    // Apply `modifier` second to allow overriding `contentSize`.
                    Modifier
                        .size(with(LocalDensity.current) { contentSize.toDpSize() })
                        .then(modifier)
                } else {
                    modifier
                },
                alignment = alignment,
                contentScale = contentScale,
                alpha = alpha,
                colorFilter = colorFilter
            )
        }
    }
}

@Stable
private fun computeContentSize(
    constraints: Constraints,
    srcSize: Size,
    contentScale: ContentScale
): Size {
    if (constraints.isZero || srcSize.isUnspecified) {
        return Size.Unspecified
    }

    // Only set a specific content size if at least one dimension is fixed.
    val hasFixedAndBoundedWidth = constraints.hasFixedWidth && constraints.hasBoundedWidth
    val hasFixedAndBoundedHeight = constraints.hasFixedHeight && constraints.hasBoundedHeight
    if (!hasFixedAndBoundedWidth && !hasFixedAndBoundedHeight) {
        return Size.Unspecified
    }

    val dstSize = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
    return srcSize * contentScale.computeScaleFactor(srcSize, dstSize)
}