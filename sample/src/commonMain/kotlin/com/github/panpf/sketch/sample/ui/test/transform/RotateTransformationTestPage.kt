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
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.transform.RotateTransformation
import kotlin.math.roundToInt

@Composable
fun RotateTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        var rotateDegrees by remember { mutableStateOf(45) }
        MyAsyncImage(
            request = ImageRequest(LocalPlatformContext.current, AssetImages.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(RotateTransformation(rotateDegrees))
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))
        Row {
            Text(text = "Rotate Angle")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = rotateDegrees.toString())
        }
        Slider(
            value = (rotateDegrees / 360f),
            onValueChange = { rotateDegrees = (it * 360).roundToInt().coerceIn(0, 360) }
        )
    }
}