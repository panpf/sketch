package com.github.panpf.sketch.state

import androidx.annotation.DrawableRes
import com.github.panpf.sketch.drawable.DrawableEqualizer


fun CurrentStateImage(defaultDrawable: DrawableEqualizer): CurrentStateImage =
    CurrentStateImage(DrawableStateImage(defaultDrawable))

fun CurrentStateImage(@DrawableRes defaultResId: Int): CurrentStateImage =
    CurrentStateImage(DrawableStateImage(defaultResId))