package com.github.panpf.sketch.sample.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode

// https://github.com/JetBrains/compose-multiplatform/issues/2852
@Composable
fun PreviewWrapper(content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalInspectionMode provides true, content = content)