package com.github.panpf.sketch.sample.ui.setting

import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.util.name

actual fun makeZoomMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    val contentScales = listOf(
        ContentScale.Fit,
        ContentScale.Crop,
        ContentScale.Inside,
        ContentScale.FillWidth,
        ContentScale.FillHeight,
        ContentScale.FillBounds,
        ContentScale.None,
    )
    add(
        DropdownSettingItem(
            title = "Content Scale",
            desc = null,
            values = contentScales.map { it.name },
            state = appSettings.contentScale,
        )
    )

    val alignments = listOf(
        Alignment.TopStart,
        Alignment.TopCenter,
        Alignment.TopEnd,
        Alignment.CenterStart,
        Alignment.Center,
        Alignment.CenterEnd,
        Alignment.BottomStart,
        Alignment.BottomCenter,
        Alignment.BottomEnd,
    )
    add(
        DropdownSettingItem(
            title = "Alignment",
            desc = null,
            values = alignments.map { it.name },
            state = appSettings.alignment,
        )
    )
}