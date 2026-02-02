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
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.setting.platformColorTypes
import com.github.panpf.sketch.transform.CircleCropTransformation
import kotlinx.collections.immutable.toImmutableList

@Composable
fun CircleTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        val scaleValues =
            remember { Scale.values().map { it.name }.toImmutableList() }
        val scaleState = remember { mutableStateOf(Scale.CENTER_CROP.name) }

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
                addTransformations(CircleCropTransformation(Scale.valueOf(scaleState.value)))
                colorType(colorType)
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))

        singleChoiceListItem(
            title = "Scale",
            values = scaleValues,
            state = scaleState
        )

        singleChoiceListItem(
            title = "Bitmap Color Type",
            values = colorTypeValues,
            state = colorTypeState
        )
    }
}