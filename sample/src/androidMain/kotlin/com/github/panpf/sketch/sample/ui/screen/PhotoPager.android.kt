package com.github.panpf.sketch.sample.ui.screen

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.ColorUtils
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.sample.image.PaletteDecodeInterceptor
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight

@Composable
actual fun PagerBackground(
    imageUri: String,
    buttonBgColorState: MutableState<Color>,
    screenSize: IntSize,
) {
    val imageState = rememberAsyncImageState()
    LaunchedEffect(Unit) {
        snapshotFlow { imageState.result }.collect {
            if (it is Success) {
                val simplePalette = it.simplePalette
                val accentColor = (simplePalette?.dominantSwatch?.rgb
                    ?: simplePalette?.lightVibrantSwatch?.rgb
                    ?: simplePalette?.vibrantSwatch?.rgb
                    ?: simplePalette?.lightMutedSwatch?.rgb
                    ?: simplePalette?.mutedSwatch?.rgb
                    ?: simplePalette?.darkVibrantSwatch?.rgb
                    ?: simplePalette?.darkMutedSwatch?.rgb)
                if (accentColor != null) {
                    buttonBgColorState.value = Color(accentColor)
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
                BlurTransformation(
                    radius = 20,
                    maskColor = ColorUtils.setAlphaComponent(Color.Black.value.toInt(), 100)
                )
            )
            disallowAnimatedImage()
            crossfade(alwaysUse = true, durationMillis = 400)
            resizeOnDraw()
            components {
                addDecodeInterceptor(PaletteDecodeInterceptor())
            }
        },
        state = imageState,
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

actual fun getTopMargin(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        context.getStatusBarHeight()
    } else {
        0
    }
}