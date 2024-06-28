package com.github.panpf.sketch.sample.ui.test.transform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.transform.MaskTransformation

@Composable
fun MaskTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        var maskColorName by remember { mutableStateOf("RED") }
        MyAsyncImage(
            request = ImageRequest(LocalPlatformContext.current, ResourceImages.png.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                val maskColor = when (maskColorName) {
                    "RED" -> Color.Red.copy(alpha = 0.5f)
                    "GREEN" -> Color.Green.copy(alpha = 0.5f)
                    "BLUE" -> Color.Blue.copy(alpha = 0.5f)
                    else -> throw IllegalArgumentException()
                }.toArgb()
                addTransformations(MaskTransformation(maskColor = maskColor))
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))
        Text(text = "Mask Color")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = maskColorName == "RED",
                onClick = { maskColorName = "RED" },
            )
            Text(text = "RED", fontSize = 12.sp)

            Spacer(Modifier.size(4.dp))
            RadioButton(
                selected = maskColorName == "GREEN",
                onClick = { maskColorName = "GREEN" },
            )
            Text(text = "GREEN", fontSize = 12.sp)

            Spacer(Modifier.size(4.dp))
            RadioButton(
                selected = maskColorName == "BLUE",
                onClick = { maskColorName = "BLUE" },
            )
            Text(text = "BLUE", fontSize = 12.sp)
        }
    }
}