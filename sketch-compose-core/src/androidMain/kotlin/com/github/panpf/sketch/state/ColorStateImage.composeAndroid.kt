package com.github.panpf.sketch.state

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun rememberIntColorStateImage(@ColorInt color: Int): ColorStateImage =
    remember(color) { IntColorStateImage(color) }

@Composable
fun rememberResColorStateImage(@ColorRes resId: Int): ColorStateImage =
    remember(resId) { ResColorStateImage(resId) }