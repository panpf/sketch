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
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import kotlin.math.roundToInt

@Composable
fun RoundCornersTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        var roundedCornersRadius by remember { mutableStateOf(30) }
        AsyncImage(
            request = ImageRequest(LocalPlatformContext.current, AssetImages.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(RoundedCornersTransformation(roundedCornersRadius.toFloat()))
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))
        Row {
            Text(text = "Rounded Corners Radius")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = roundedCornersRadius.toString())
        }
        Slider(
            value = (roundedCornersRadius / 100f),
            onValueChange = { roundedCornersRadius = (it * 100).roundToInt().coerceIn(0, 100) }
        )
    }
}