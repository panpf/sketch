package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.disallowAnimatedImage
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.image.PaletteInterceptor
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.sample.ui.components.composablePlatformAsyncImageSettings
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.util.toSketchSize
import com.github.panpf.sketch.util.windowContainerSize
import org.koin.compose.koinInject

@Composable
fun PhotoPagerBackground(
    imageUri: String,
    photoPaletteState: MutableState<PhotoPalette>,
) {
    val colorScheme = MaterialTheme.colorScheme
    val imageState = rememberAsyncImageState()
    LaunchedEffect(Unit) {
        snapshotFlow { imageState.result }.collect {
            if (it is ImageResult.Success) {
                photoPaletteState.value =
                    PhotoPalette(it.simplePalette, colorScheme = colorScheme)
            }
        }
    }
    // Cache the image size to prevent reloading the image when the window size changes
    val windowsSize = windowContainerSize()
    val imageSize = remember { (windowsSize / 4).toSketchSize() }
    val appSettings: AppSettings = koinInject()
    val request = ComposableImageRequest(imageUri) {
        resize(size = imageSize, precision = Precision.SMALLER_SIZE)
        addTransformations(BlurTransformation(radius = 20, maskColor = 0x63000000))
        memoryCachePolicy(CachePolicy.DISABLED)
        resultCachePolicy(CachePolicy.DISABLED)
        disallowAnimatedImage()
        crossfade(alwaysUse = true, durationMillis = 400)
        resizeOnDraw()
        components {
            add(PaletteInterceptor())
        }
        merge(composablePlatformAsyncImageSettings(appSettings))
    }
    AsyncImage(
        request = request,
        state = imageState,
        contentDescription = "Background",
        contentScale = ContentScale.Companion.Crop,
        modifier = Modifier.Companion.fillMaxSize()
    )
}