package com.github.panpf.sketch.sample.ui.components

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.bitmapQualityValue
import com.github.panpf.sketch.sample.colorSpaceValue
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage

@Composable
actual fun rememberAnimatedPlaceholderStateImage(context: PlatformContext): StateImage? {
    // AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above android api 29
    return rememberIconAnimatablePainterStateImage(
        icon = R.drawable.ic_placeholder_eclipse_animated,
        background = R.color.placeholder_bg,
    )
}

@Composable
actual inline fun ImageRequest.Builder.platformListImageRequest(appSettings: AppSettings) {
    val bitmapQuality by appSettings.bitmapQuality.collectAsState()
    bitmapConfig(AppSettings.bitmapQualityValue(bitmapQuality))
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        val colorSpace by appSettings.colorSpace.collectAsState()
        colorSpace(AppSettings.colorSpaceValue(colorSpace))
    }
    val inPreferQualityOverSpeed by appSettings.inPreferQualityOverSpeed.collectAsState()
    @Suppress("DEPRECATION")
    preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed)
}