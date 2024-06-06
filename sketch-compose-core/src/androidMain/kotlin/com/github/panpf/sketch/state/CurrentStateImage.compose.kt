package com.github.panpf.sketch.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.util.DrawableEqualizer


@Composable
fun rememberCurrentStateImage(defaultImage: StateImage? = null): CurrentStateImage =
    remember(defaultImage) { CurrentStateImage(defaultImage) }

@Composable
fun rememberCurrentStateImage(defaultDrawable: DrawableEqualizer): CurrentStateImage =
    remember(defaultDrawable) { CurrentStateImage(defaultDrawable) }

@Composable
fun rememberCurrentStateImage(@DrawableRes defaultDrawableRes: Int): CurrentStateImage =
    remember(defaultDrawableRes) { CurrentStateImage(defaultDrawableRes) }