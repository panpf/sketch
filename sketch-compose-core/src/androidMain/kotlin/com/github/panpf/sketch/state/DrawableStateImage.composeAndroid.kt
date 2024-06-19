package com.github.panpf.sketch.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.drawable.DrawableEqualizer

@Composable
fun rememberDrawableStateImage(drawable: DrawableEqualizer): DrawableStateImage =
    remember(drawable) { DrawableStateImage(drawable) }

@Composable
fun rememberDrawableStateImage(@DrawableRes resId: Int): DrawableStateImage =
    remember(resId) { DrawableStateImage(resId) }
