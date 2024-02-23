package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.sample.image.PaletteDecodeInterceptor
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.util.BlurTransformation

@Composable
actual fun PagerBackground(
    imageUri: String,
    buttonBgColorState: MutableState<Color>,
    screenSize: IntSize,
) {
    val imageState = rememberAsyncImageState()
    LaunchedEffect(Unit) {
        snapshotFlow { imageState.result }.collect {
            if (it is ImageResult.Success) {
                val preferredSwatch = it.simplePalette?.run {
                    listOfNotNull(dominantSwatch, mutedSwatch, vibrantSwatch).firstOrNull()
                }
                if (preferredSwatch != null) {
                    buttonBgColorState.value = Color(preferredSwatch.rgb)
                }
            }
        }
    }
    AsyncImage(
        request = ImageRequest(LocalPlatformContext.current, imageUri) {
            resize(
                width = screenSize.width / 4,
                height = screenSize.height / 4,
                precision = SMALLER_SIZE
            )
            addTransformations(
                BlurTransformation(radius = 20, maskColor = 0x63000000)
            )
            disallowAnimatedImage()
            crossfade(alwaysUse = true, durationMillis = 400)
            resizeOnDraw()
            components {
                // TODO Invalid, seems to be lost
                addDecodeInterceptor(PaletteDecodeInterceptor())
            }
        },
        state = imageState,
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

actual fun getTopMargin(context: PlatformContext): Int {
    return 0
}