package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.painter.BlurhashPainter
import com.github.panpf.sketch.request.ImageRequest

/**
 * Create a [ColorPainterStateImage] instance and remember it
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ColorPainterStateImageTest.testRememberColorPainterStateImage
 */
@Composable
fun rememberBlurhashStateImage(blurhash: String): BlurhashStateImage =
    remember(blurhash) { BlurhashStateImage(blurhash) }

/**
 * StateImage implemented by BlurhashPainter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ColorPainterStateImageTest
 */
@Stable
data class BlurhashStateImage(val blurhash: String) : StateImage {

    override val key: String = "BlurhashPainter(${blurhash})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return BlurhashPainter(blurhash).asImage()
    }

    override fun toString(): String = "BlurhashStateImage(blurhash=${blurhash})"
}