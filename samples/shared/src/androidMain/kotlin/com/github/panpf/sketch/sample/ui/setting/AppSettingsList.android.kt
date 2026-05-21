package com.github.panpf.sketch.sample.ui.setting

import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.components.DividerSettingItem
import com.github.panpf.sketch.sample.ui.components.DropdownSettingItem
import com.github.panpf.sketch.sample.ui.components.SwitchSettingItem
import org.koin.compose.koinInject

actual fun platformColorTypes(): List<String> {
    return Bitmap.Config.values().map { it.name }
}

actual fun platformColorSpaces(): List<String> {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        ColorSpace.Named.values().map { it.name }
    } else {
        emptyList()
    }
}

@Composable
actual fun PlatformDecodeSettingsList(appSettings: AppSettings) {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        // Cannot use Named.entries, crashes on versions lower than O
        val colorSpaceValues = remember {
            listOf("Default").plus(platformColorSpaces())
        }
        DropdownSettingItem(
            title = "Bitmap Color Space",
            desc = null,
            values = colorSpaceValues,
            state = appSettings.colorSpaceName,
        )
    }
    if (VERSION.SDK_INT <= VERSION_CODES.M) {
        SwitchSettingItem(
            title = "preferQualityOverSpeed",
            desc = null,
            state = appSettings.preferQualityOverSpeed
        )
    }
}

@Composable
actual fun PlatformAnimatedSettingsList(appSettings: AppSettings) {

}

@Composable
actual fun VideoSettingsList(appSettings: AppSettings) {
    DividerSettingItem("Video")

    val videoFramePercentValue = remember {
        listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
    }
    DropdownSettingItem(
        title = "Video Frame Percent",
        desc = null,
        values = videoFramePercentValue,
        state = appSettings.videoFramePercent,
    )

    SwitchSettingItem(
        title = "Prefer Video Cover",
        desc = null,
        state = appSettings.preferVideoCover,
    )
}

@Composable
actual fun PlatformOtherSettingsList(appSettings: AppSettings, page: AppSettingsPage) {
    val appEvents: AppEvents = koinInject()

    val httpClientValues = remember {
        listOf("Ktor", "OkHttp", "HttpURLConnection")
    }
    DropdownSettingItem(
        title = "Http Client",
        desc = null,
        values = httpClientValues,
        state = appSettings.httpClient,
        onItemClick = {
            appEvents.toastFlow.emit("Restart the app to take effect")
        }
    )

    val videoFrameDecoderValues = remember {
        listOf("FFmpeg", "AndroidBuiltIn")
    }
    DropdownSettingItem(
        title = "Video Frame Decoder",
        desc = null,
        values = videoFrameDecoderValues,
        state = appSettings.videoFrameDecoder,
        onItemClick = {
            appEvents.toastFlow.emit("Restart the app to take effect")
        }
    )

    val gifDecoderValues = remember {
        listOf("KoralGif", "Movie", "Movie+ImageDecoder")
    }
    DropdownSettingItem(
        title = "Gif Decoder",
        desc = null,
        values = gifDecoderValues,
        state = appSettings.gifDecoder,
        onItemClick = {
            appEvents.toastFlow.emit("Restart the app to take effect")
        }
    )
}