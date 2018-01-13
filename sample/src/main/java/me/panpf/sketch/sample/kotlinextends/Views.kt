package me.panpf.sketch.sample.kotlinextends

import android.content.Context
import android.content.res.Configuration

fun Context.isPortraitOrientation() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT