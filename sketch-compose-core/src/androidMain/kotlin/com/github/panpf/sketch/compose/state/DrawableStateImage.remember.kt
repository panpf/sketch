package com.github.panpf.sketch.compose.state

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.util.DrawableFetcher
import com.github.panpf.sketch.util.RealDrawable
import com.github.panpf.sketch.util.ResDrawable

@Composable
fun rememberDrawableStateImage(drawableFetcher: DrawableFetcher): DrawableStateImage =
    remember(drawableFetcher) { DrawableStateImage(drawableFetcher) }

@Composable
fun rememberDrawableStateImage(drawable: Drawable): DrawableStateImage =
    remember(drawable) { DrawableStateImage(drawable) }

@Composable
fun rememberDrawableStateImage(@DrawableRes drawableRes: Int): DrawableStateImage =
    remember(drawableRes) { DrawableStateImage(drawableRes) }
