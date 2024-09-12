package com.github.panpf.sketch.sample.ui.test.transform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.setting.platformBitmapConfigs
import com.github.panpf.sketch.transform.MaskTransformation
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MaskTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        val maskColorValues =
            remember { listOf("RED", "GREEN", "BLUE").toImmutableList() }
        val maskColorState = remember { mutableStateOf("RED") }

        val bitmapConfigValues =
            remember { listOf("Default").plus(platformBitmapConfigs()).toImmutableList() }
        val bitmapConfigState = remember { mutableStateOf("Default") }
        val bitmapConfig = remember(bitmapConfigState.value) {
            bitmapConfigState.value.takeIf { it != "Default" }?.let { BitmapConfig(it) }
        }

        MyAsyncImage(
            request = ComposableImageRequest(ResourceImages.png.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                val maskColor = when (maskColorState.value) {
                    "RED" -> Color.Red.copy(alpha = 0.5f)
                    "GREEN" -> Color.Green.copy(alpha = 0.5f)
                    "BLUE" -> Color.Blue.copy(alpha = 0.5f)
                    else -> throw IllegalArgumentException()
                }.toArgb()
                addTransformations(MaskTransformation(maskColor = maskColor))
                bitmapConfig(bitmapConfig)
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))

        singleChoiceListItem(
            title = "Mask Color",
            values = maskColorValues,
            state = maskColorState
        )

        singleChoiceListItem(
            title = "Bitmap Config",
            values = bitmapConfigValues,
            state = bitmapConfigState
        )
    }
}