package me.panpf.ktx

import android.app.Activity
import android.content.Context
import android.content.res.Configuration

fun Context.isPortraitOrientation() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun android.support.v4.app.Fragment.isPortraitOrientation() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun Activity.isPortraitOrientation() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT