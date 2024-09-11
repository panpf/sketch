package com.github.panpf.sketch.sample.ui.test.transform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.transform.BlurTransformation
import kotlin.math.roundToInt

@Composable
fun BlurTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        var blurRadius by remember { mutableStateOf(30) }
        var maskColorName by remember { mutableStateOf("NONE") }
        var backgroundColorName by remember { mutableStateOf("NONE") }
        Row(Modifier.fillMaxWidth().weight(1f)) {
            MyAsyncImage(
                request = ImageRequest(LocalPlatformContext.current, ResourceImages.jpeg.uri) {
                    memoryCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    val maskColor = when (maskColorName) {
                        "RED" -> Color.Red.copy(alpha = 0.5f)
                        "GREEN" -> Color.Green.copy(alpha = 0.5f)
                        "BLUE" -> Color.Blue.copy(alpha = 0.5f)
                        else -> null
                    }?.toArgb()
                    val backgroundColor = when (backgroundColorName) {
                        "BLACK" -> Color.Black
                        "WHITE" -> Color.White
                        else -> null
                    }?.toArgb()
                    addTransformations(
                        BlurTransformation(
                            radius = blurRadius,
                            hasAlphaBitmapBgColor = backgroundColor,
                            maskColor = maskColor
                        )
                    )
//                    bitmapConfig(BitmapConfig.FixedQuality("RGB_565")) // TODO There is a problem
                },
                contentDescription = "image",
                modifier = Modifier.fillMaxHeight().weight(1f)
            )

            Spacer(Modifier.size(16.dp))
            MyAsyncImage(
                request = ImageRequest(LocalPlatformContext.current, ResourceImages.svg.uri) {
                    memoryCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    val maskColor = when (maskColorName) {
                        "RED" -> Color.Red.copy(alpha = 0.5f)
                        "GREEN" -> Color.Green.copy(alpha = 0.5f)
                        "BLUE" -> Color.Blue.copy(alpha = 0.5f)
                        else -> null
                    }?.toArgb()
                    val backgroundColor = when (backgroundColorName) {
                        "BLACK" -> Color.Black
                        "WHITE" -> Color.White
                        else -> null
                    }?.toArgb()
                    addTransformations(
                        BlurTransformation(
                            radius = blurRadius,
                            hasAlphaBitmapBgColor = backgroundColor,
                            maskColor = maskColor
                        )
                    )
                },
                contentDescription = "image",
                modifier = Modifier.fillMaxHeight().weight(1f)
            )
        }

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

        Text(text = "Background Color")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = backgroundColorName == "NONE",
                onClick = { backgroundColorName = "NONE" },
            )
            Text(text = "NONE", fontSize = 12.sp)

            Spacer(Modifier.size(4.dp))
            RadioButton(
                selected = backgroundColorName == "BLACK",
                onClick = { backgroundColorName = "BLACK" },
            )
            Text(text = "BLACK", fontSize = 12.sp)

            Spacer(Modifier.size(4.dp))
            RadioButton(
                selected = backgroundColorName == "WHITE",
                onClick = { backgroundColorName = "WHITE" },
            )
            Text(text = "WHITE", fontSize = 12.sp)
        }
    }
}