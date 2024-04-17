package com.github.panpf.sketch.compose.state

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.state.CurrentStateImage
import com.github.panpf.sketch.state.StateImage


@Composable
fun rememberCurrentStateImage(defaultImage: StateImage? = null): CurrentStateImage =
    remember(defaultImage) { CurrentStateImage(defaultImage) }

@Composable
fun rememberCurrentStateImage(defaultDrawable: Drawable): CurrentStateImage =
    remember(defaultDrawable) { CurrentStateImage(defaultDrawable) }

@Composable
fun rememberCurrentStateImage(@DrawableRes defaultDrawableRes: Int): CurrentStateImage =
    remember(defaultDrawableRes) { CurrentStateImage(defaultDrawableRes) }