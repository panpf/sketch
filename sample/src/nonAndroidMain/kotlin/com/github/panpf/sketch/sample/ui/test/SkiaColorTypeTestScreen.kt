package com.github.panpf.sketch.sample.ui.test

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
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image_broken_outline
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.setting.platformColorTypes
import com.github.panpf.sketch.sample.ui.test.transform.singleChoiceListItem
import com.github.panpf.sketch.transform.TransformResult
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.util.installIntPixels
import com.github.panpf.sketch.util.readIntPixels
import kotlinx.collections.immutable.toImmutableList

class SkiaColorTypeTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "SkiaColorTypeTest") {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                val convertPixelsValues =
                    remember { listOf("Enabled", "Disabled").toImmutableList() }
                val convertPixelsState = remember { mutableStateOf("Disabled") }

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
                            colorType(colorType)
                            if (convertPixelsState.value == "Enabled") {
                                addTransformations(ConvertPixelsTransformation)
                            }
                            error(Res.drawable.ic_image_broken_outline)
                        },
                        contentDescription = "image",
                        modifier = Modifier.fillMaxHeight().weight(1f)
                    )

                    Spacer(Modifier.size(16.dp))

                    MyAsyncImage(
                        request = ComposableImageRequest(ResourceImages.png.uri) {
                            memoryCachePolicy(DISABLED)
                            resultCachePolicy(DISABLED)
                            colorType(colorType)
                            if (convertPixelsState.value == "Enabled") {
                                addTransformations(ConvertPixelsTransformation)
                            }
                            error(Res.drawable.ic_image_broken_outline)
                        },
                        contentDescription = "image",
                        modifier = Modifier.fillMaxHeight().weight(1f)
                    )
                }

                Spacer(Modifier.size(16.dp))

                singleChoiceListItem(
                    title = "Convert Pixels",
                    values = convertPixelsValues,
                    state = convertPixelsState
                )

                singleChoiceListItem(
                    title = "Bitmap Color Type",
                    values = colorTypeValues,
                    state = colorTypeState
                )
            }
        }
    }

    object ConvertPixelsTransformation : Transformation {

        override val key: String = "ConvertPixelsTransformation"

        override fun transform(requestContext: RequestContext, input: Image): TransformResult {
            val inputBitmap = (input as SkiaBitmapImage).bitmap
            val intPixels = inputBitmap.readIntPixels()
            val newSkiaBitmap = SkiaBitmap(inputBitmap.imageInfo)
            newSkiaBitmap.installIntPixels(intPixels)
            return TransformResult(
                image = input.copy(bitmap = newSkiaBitmap),
                transformed = "ConvertPixelsTransformation"
            )
        }
    }
}