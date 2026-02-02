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
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.setting.platformColorTypes
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import kotlinx.collections.immutable.toImmutableList

@Composable
fun RoundCornersTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        val topLeftRoundedCornersRadiusState = remember { mutableStateOf(10) }
        val topRightRoundedCornersRadiusState = remember { mutableStateOf(20) }
        val bottomLeftRoundedCornersRadiusState = remember { mutableStateOf(40) }
        val bottomRightRoundedCornersRadiusState = remember { mutableStateOf(80) }

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
                addTransformations(
                    RoundedCornersTransformation(
                        topLeft = topLeftRoundedCornersRadiusState.value.toFloat(),
                        topRight = topRightRoundedCornersRadiusState.value.toFloat(),
                        bottomLeft = bottomLeftRoundedCornersRadiusState.value.toFloat(),
                        bottomRight = bottomRightRoundedCornersRadiusState.value.toFloat(),
                    )
                )
                colorType(colorType)
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))

        sliderListItem(
            title = "Top Left Rounded Corners Radius",
            state = topLeftRoundedCornersRadiusState
        )

        sliderListItem(
            title = "Top Right Rounded Corners Radius",
            state = topRightRoundedCornersRadiusState
        )

        sliderListItem(
            title = "Bottom Left Rounded Corners Radius",
            state = bottomLeftRoundedCornersRadiusState
        )

        sliderListItem(
            title = "Bottom Right Rounded Corners Radius",
            state = bottomRightRoundedCornersRadiusState
        )

        singleChoiceListItem(
            title = "Bitmap Color Type",
            values = colorTypeValues,
            state = colorTypeState
        )
    }
}