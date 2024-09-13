package com.github.panpf.sketch.sample.ui.test.transform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.setting.platformColorTypes
import com.github.panpf.sketch.transform.BlurTransformation
import kotlinx.collections.immutable.toImmutableList

@Composable
fun BlurTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        val blurRadiusState = remember { mutableStateOf(30) }
        val maskColorValues =
            remember { listOf("NONE", "RED", "GREEN", "BLUE").toImmutableList() }
        val maskColorState = remember { mutableStateOf("NONE") }
        val backgroundColorValues =
            remember { listOf("NONE", "BLACK", "WHITE").toImmutableList() }
        val backgroundColorState = remember { mutableStateOf("NONE") }
        val blurTransformation =
            remember(blurRadiusState.value, backgroundColorState.value, maskColorState.value) {
                val backgroundColor = when (backgroundColorState.value) {
                    "BLACK" -> Color.Black
                    "WHITE" -> Color.White
                    else -> null
                }?.toArgb()
                val maskColor = when (maskColorState.value) {
                    "RED" -> Color.Red.copy(alpha = 0.5f)
                    "GREEN" -> Color.Green.copy(alpha = 0.5f)
                    "BLUE" -> Color.Blue.copy(alpha = 0.5f)
                    else -> null
                }?.toArgb()
                BlurTransformation(
                    radius = blurRadiusState.value,
                    hasAlphaBitmapBgColor = backgroundColor,
                    maskColor = maskColor
                )
            }

        val colorTypeValues =
            remember { listOf("Default").plus(platformColorTypes()).toImmutableList() }
        val colorTypeState = remember { mutableStateOf("Default") }
        val colorType = remember(colorTypeState.value) {
            colorTypeState.value.takeIf { it != "Default" }?.let { BitmapColorType(it) }
        }

        Row(Modifier.fillMaxWidth().weight(1f)) {
            MyAsyncImage(
                request = ComposableImageRequest(ResourceImages.jpeg.uri) {
                    memoryCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    addTransformations(blurTransformation)
                    colorType(colorType)
                },
                contentDescription = "image",
                modifier = Modifier.fillMaxHeight().weight(1f)
            )

            Spacer(Modifier.size(16.dp))

            MyAsyncImage(
                request = ComposableImageRequest(ResourceImages.svg.uri) {
                    memoryCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    addTransformations(blurTransformation)
                    colorType(colorType)
                },
                contentDescription = "image",
                modifier = Modifier.fillMaxHeight().weight(1f)
            )
        }

        Spacer(Modifier.size(16.dp))

        sliderListItem(
            title = "Blur Radius",
            state = blurRadiusState,
            minValue = 1
        )

        singleChoiceListItem(
            title = "Mask Color",
            values = maskColorValues,
            state = maskColorState
        )

        singleChoiceListItem(
            title = "Background Color",
            values = backgroundColorValues,
            state = backgroundColorState
        )

        singleChoiceListItem(
            title = "Bitmap Color Type",
            values = colorTypeValues,
            state = colorTypeState
        )
    }
}