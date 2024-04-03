package com.github.panpf.sketch.sample.ui.test.transform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import kotlin.math.roundToInt

@Composable
fun RoundCornersTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        var topLeftRoundedCornersRadius by remember { mutableStateOf(10) }
        var topRightRoundedCornersRadius by remember { mutableStateOf(20) }
        var bottomLeftRoundedCornersRadius by remember { mutableStateOf(40) }
        var bottomRightRoundedCornersRadius by remember { mutableStateOf(80) }

        MyAsyncImage(
            request = ImageRequest(LocalPlatformContext.current, MyImages.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(
                    RoundedCornersTransformation(
                        topLeft = topLeftRoundedCornersRadius.toFloat(),
                        topRight = topRightRoundedCornersRadius.toFloat(),
                        bottomLeft = bottomLeftRoundedCornersRadius.toFloat(),
                        bottomRight = bottomRightRoundedCornersRadius.toFloat(),
                    )
                )
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))
        Row {
            Text(text = "Top Left Rounded Corners Radius")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = topLeftRoundedCornersRadius.toString())
        }
        Slider(
            value = (topLeftRoundedCornersRadius / 100f),
            onValueChange = {
                topLeftRoundedCornersRadius = (it * 100).roundToInt().coerceIn(0, 100)
            }
        )

        Row {
            Text(text = "Top Right Rounded Corners Radius")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = topRightRoundedCornersRadius.toString())
        }
        Slider(
            value = (topRightRoundedCornersRadius / 100f),
            onValueChange = {
                topRightRoundedCornersRadius = (it * 100).roundToInt().coerceIn(0, 100)
            }
        )

        Row {
            Text(text = "Bottom Left Rounded Corners Radius")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = bottomLeftRoundedCornersRadius.toString())
        }
        Slider(
            value = (bottomLeftRoundedCornersRadius / 100f),
            onValueChange = {
                bottomLeftRoundedCornersRadius = (it * 100).roundToInt().coerceIn(0, 100)
            }
        )

        Row {
            Text(text = "Bottom Right Rounded Corners Radius")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = bottomRightRoundedCornersRadius.toString())
        }
        Slider(
            value = (bottomRightRoundedCornersRadius / 100f),
            onValueChange = {
                bottomRightRoundedCornersRadius = (it * 100).roundToInt().coerceIn(0, 100)
            }
        )
    }
}