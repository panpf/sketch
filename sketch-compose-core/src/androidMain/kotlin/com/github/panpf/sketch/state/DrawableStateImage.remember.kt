package com.github.panpf.sketch.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.state.DrawableEqualWrapper
import com.github.panpf.sketch.state.DrawableStateImage

@Composable
fun rememberDrawableStateImage(drawable: DrawableEqualWrapper): DrawableStateImage =
    remember(drawable) { DrawableStateImage(drawable) }

@Composable
fun rememberDrawableStateImage(@DrawableRes drawableRes: Int): DrawableStateImage =
    remember(drawableRes) { DrawableStateImage(drawableRes) }
