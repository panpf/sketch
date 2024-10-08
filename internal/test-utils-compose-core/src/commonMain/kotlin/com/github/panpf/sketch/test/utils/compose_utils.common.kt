package com.github.panpf.sketch.test.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.ComposeBitmap
import com.github.panpf.sketch.request.GlobalLifecycle

@Composable
fun Dp.dp2Px(): Float {
    return with(LocalDensity.current) { toPx() }
}

@Composable
fun Float.dp2Px(): Float {
    return with(LocalDensity.current) { this@dp2Px.dp.toPx() }
}

// https://github.com/JetBrains/compose-multiplatform/issues/2852
@Composable
fun PreviewContainer(content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalInspectionMode provides true, content = content)

@Composable
fun LifecycleContainer(content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalLifecycleOwner provides GlobalLifecycle.owner, content = content)

expect fun Bitmap.toComposeBitmap(): ComposeBitmap