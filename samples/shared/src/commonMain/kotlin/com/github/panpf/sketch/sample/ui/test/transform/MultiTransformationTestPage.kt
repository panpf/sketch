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
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.setting.platformColorTypes
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MultiTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        val blurRadiusState = remember { mutableStateOf(30) }
        val roundedCornersRadiusState = remember { mutableStateOf(30) }
        val maskColorValues =
            remember { listOf("NONE", "RED", "GREEN", "BLUE").toImmutableList() }
        val maskColorState = remember { mutableStateOf("RED") }
        val rotateDegreesState = remember { mutableStateOf(45) }

        val colorTypeValues =
            remember { listOf("Default").plus(platformColorTypes()).toImmutableList() }
        val colorTypeState = remember { mutableStateOf("Default") }
        val colorType = remember(colorTypeState.value) {
            colorTypeState.value.takeIf { it != "Default" }?.let { BitmapColorType(it) }
        }

        MyAsyncImage(
            request = ComposableImageRequest(ComposeResImageFiles.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                val maskColor = when (maskColorState.value) {
                    "RED" -> Color.Red.copy(alpha = 0.5f)
                    "GREEN" -> Color.Green.copy(alpha = 0.5f)
                    "BLUE" -> Color.Blue.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }.toArgb()
                addTransformations(
                    BlurTransformation(radius = blurRadiusState.value),
                    RoundedCornersTransformation(allRadius = roundedCornersRadiusState.value.toFloat()),
                    MaskTransformation(maskColor = maskColor),
                    RotateTransformation(degrees = rotateDegreesState.value),
                )
                colorType(colorType)
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))

        sliderListItem(
            title = "Rotate Angle",
            state = rotateDegreesState,
            minValue = 0,
            maxValue = 360
        )

        sliderListItem(
            title = "Blur Radius",
            state = blurRadiusState,
            minValue = 1
        )

        sliderListItem(
            title = "Rounded Corners Radius",
            state = roundedCornersRadiusState,
        )

        singleChoiceListItem(
            title = "Mask Color",
            values = maskColorValues,
            state = maskColorState
        )

        singleChoiceListItem(
            title = "Bitmap Color Type",
            values = colorTypeValues,
            state = colorTypeState
        )
    }
}