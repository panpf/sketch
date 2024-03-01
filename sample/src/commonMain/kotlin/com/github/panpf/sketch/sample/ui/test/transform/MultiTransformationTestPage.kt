package com.github.panpf.sketch.sample.ui.test.transform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
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
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import kotlin.math.roundToInt

@Composable
fun MultiTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        var blurRadius by remember { mutableStateOf(30) }
        var roundedCornersRadius by remember { mutableStateOf(30) }
        var maskColorName by remember { mutableStateOf("RED") }
        var rotateDegrees by remember { mutableStateOf(45) }
        AsyncImage(
            request = ImageRequest(LocalPlatformContext.current, AssetImages.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                val maskColor = when (maskColorName) {
                    "RED" -> Color.Red.copy(alpha = 0.5f)
                    "GREEN" -> Color.Green.copy(alpha = 0.5f)
                    "BLUE" -> Color.Blue.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }.toArgb()
                addTransformations(
                    BlurTransformation(radius = blurRadius),
                    RoundedCornersTransformation(allRadius = roundedCornersRadius.toFloat()),
                    MaskTransformation(maskColor = maskColor),
                    RotateTransformation(degrees = rotateDegrees),
                )
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))
        Row {
            Text(text = "Rotate Degrees")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = rotateDegrees.toString())
        }
        Slider(
            value = (rotateDegrees / 360f),
            onValueChange = { rotateDegrees = (it * 360).roundToInt().coerceIn(0, 360) }
        )

        Spacer(Modifier.size(16.dp))
        Row {
            Text(text = "Blur Radius")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = blurRadius.toString())
        }
        Slider(
            value = (blurRadius / 100f),
            onValueChange = { blurRadius = (it * 100).roundToInt().coerceIn(1, 100) }
        )

        Row {
            Text(text = "Rounded Corners Radius")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = roundedCornersRadius.toString())
        }
        Slider(
            value = (roundedCornersRadius / 100f),
            onValueChange = { roundedCornersRadius = (it * 100).roundToInt().coerceIn(0, 100) }
        )

        Text(text = "Mask Color")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = maskColorName == "NONE",
                onClick = { maskColorName = "NONE" },
            )
            Text(text = "NONE", fontSize = 12.sp)

            Spacer(Modifier.size(4.dp))
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