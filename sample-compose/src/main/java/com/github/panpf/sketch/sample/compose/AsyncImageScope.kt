package com.github.panpf.sketch.sample.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale

@LayoutScopeMarker
@Immutable
interface AsyncImageScope : BoxScope {

    /** The painter that is drawn by [AsyncImageContent]. */
    val painter: AsyncImagePainter

    /** The content description for [AsyncImageContent]. */
    val contentDescription: String?

    /** The default alignment for any composables drawn in this scope. */
    val alignment: Alignment

    /** The content scale for [AsyncImageContent]. */
    val contentScale: ContentScale

    /** The alpha for [AsyncImageContent]. */
    val alpha: Float

    /** The color filter for [AsyncImageContent]. */
    val colorFilter: ColorFilter?

//    companion object {
//        /**
//         * The default content composable only draws [AsyncImageContent] for all
//         * [AsyncImagePainter] states.
//         */
//        val DefaultContent: @Composable (AsyncImageScope.(State) -> Unit) = { AsyncImageContent() }
//    }
}

data class RealAsyncImageScope(
    val parentScope: BoxWithConstraintsScope,
    override val painter: AsyncImagePainter,
    override val contentDescription: String?,
    override val alignment: Alignment,
    override val contentScale: ContentScale,
    override val alpha: Float,
    override val colorFilter: ColorFilter?,
) : AsyncImageScope, BoxScope by parentScope