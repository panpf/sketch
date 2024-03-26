package com.github.panpf.sketch.sample.ui.setting

import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.name

actual fun makeZoomMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    val contentScales = listOf(
        ContentScaleCompat.Fit,
        ContentScaleCompat.Crop,
        ContentScaleCompat.Inside,
        ContentScaleCompat.FillWidth,
        ContentScaleCompat.FillHeight,
        ContentScaleCompat.FillBounds,
        ContentScaleCompat.None,
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
        AlignmentCompat.TopStart,
        AlignmentCompat.TopCenter,
        AlignmentCompat.TopEnd,
        AlignmentCompat.CenterStart,
        AlignmentCompat.Center,
        AlignmentCompat.CenterEnd,
        AlignmentCompat.BottomStart,
        AlignmentCompat.BottomCenter,
        AlignmentCompat.BottomEnd,
    )
    add(
        DropdownSettingItem(
            title = "Alignment",
            desc = null,
            values = alignments.map { it.name },
            state = appSettings.alignment,
        )
    )
    add(
        SwitchSettingItem(
            title = "Scroll Bar",
            desc = null,
            state = appSettings.scrollBarEnabled,
        )
    )
    add(
        SwitchSettingItem(
            title = "Read Mode",
            state = appSettings.readModeEnabled,
            desc = "Long images are displayed in full screen by default"
        )
    )
    add(
        SwitchSettingItem(
            title = "Show Tile Bounds",
            desc = "Overlay the state and area of the tile on the View",
            state = appSettings.showTileBounds,
        )
    )
}
