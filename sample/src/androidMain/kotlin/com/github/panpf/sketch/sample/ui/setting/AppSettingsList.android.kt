package com.github.panpf.sketch.sample.ui.setting

import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.EventBus

actual fun platformBitmapConfigs(): List<String> {
    return Bitmap.Config.values().map { it.name }
}

actual fun platformColorSpaces(): List<String> {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        ColorSpace.Named.values().map { it.name }
    } else {
        emptyList()
    }
}

actual fun platformMakeDecodeMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        // Cannot use Named.entries, crashes on versions lower than O
        val items = listOf("Default").plus(platformColorSpaces())
        add(
            DropdownSettingItem(
                title = "Color Space",
                desc = null,
                values = items,
                state = appSettings.colorSpaceName,
            )
        )
    }
    if (VERSION.SDK_INT <= VERSION_CODES.M) {
        add(
            SwitchSettingItem(
                title = "inPreferQualityOverSpeed",
                desc = null,
                state = appSettings.inPreferQualityOverSpeed
            )
        )
    }
}

actual fun platformAnimatedMenuList(appSettings: AppSettings): List<SettingItem> = emptyList()

actual fun platformMakeOtherMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        DropdownSettingItem(
            title = "Http Client",
            desc = null,
            values = listOf("Ktor", "OkHttp", "HttpURLConnection"),
            state = appSettings.httpClient,
            onItemClick = {
                EventBus.toastFlow.emit("Restart the app to take effect")
            }
        )
    )
    add(
        DropdownSettingItem(
            title = "Video Frame Decoder",
            desc = null,
            values = listOf("FFmpeg", "AndroidBuiltIn"),
            state = appSettings.videoFrameDecoder,
            onItemClick = {
                EventBus.toastFlow.emit("Restart the app to take effect")
            }
        )
    )
    add(
        DropdownSettingItem(
            title = "Gif Decoder",
            desc = null,
            values = listOf("KoralGif", "Movie", "Movie+ImageDecoder"),
            state = appSettings.gifDecoder,
            onItemClick = {
                EventBus.toastFlow.emit("Restart the app to take effect")
            }
        )
    )
}