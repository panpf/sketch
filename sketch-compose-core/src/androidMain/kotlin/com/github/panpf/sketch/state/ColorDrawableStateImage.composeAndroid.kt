package com.github.panpf.sketch.state

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun rememberIntColorDrawableStateImage(@ColorInt color: Int): ColorDrawableStateImage =
    remember(color) { IntColorDrawableStateImage(color) }

@Composable
fun rememberResColorDrawableStateImage(@ColorRes resId: Int): ColorDrawableStateImage =
    remember(resId) { ResColorDrawableStateImage(resId) }