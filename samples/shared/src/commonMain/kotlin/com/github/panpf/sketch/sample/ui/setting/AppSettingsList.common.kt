@file:Suppress("EnumValuesSoftDeprecate")

package com.github.panpf.sketch.sample.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.components.DividerSettingItem
import com.github.panpf.sketch.sample.ui.components.DropdownSettingItem
import com.github.panpf.sketch.sample.ui.components.SwitchSettingItem
import com.github.panpf.sketch.sample.util.formatFileSize
import com.github.panpf.sketch.util.Logger
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.name
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

@Composable
fun AppSettingsList(page: AppSettingsPage) {
    val appSettings: AppSettings = koinInject()
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        if (page == AppSettingsPage.LIST) {
            ListSettingsList(appSettings)
        } else if (page == AppSettingsPage.VIEWER) {
            ViewerSettingsList(appSettings)
            ZoomSettingsList(appSettings)
        }

        DecodeSettingsList(appSettings)
        AnimatedSettingsList(appSettings)
        VideoSettingsList(appSettings)
        CacheSettingsList(appSettings)
        OtherSettingsList(appSettings, page)
    }
}

@Composable
private fun ListSettingsList(appSettings: AppSettings) {
    DividerSettingItem("List")

    val contentScaleValues = remember {
        listOf(
            ContentScaleCompat.Fit,
            ContentScaleCompat.Crop,
            ContentScaleCompat.Inside,
            ContentScaleCompat.FillWidth,
            ContentScaleCompat.FillHeight,
            ContentScaleCompat.FillBounds,
            ContentScaleCompat.None,
        ).map { it.name }
    }
    DropdownSettingItem(
        title = "Content Scale",
        desc = null,
        values = contentScaleValues,
        state = appSettings.listContentScaleName,
    )

    val alignmentValues = remember {
        listOf(
            AlignmentCompat.TopStart,
            AlignmentCompat.TopCenter,
            AlignmentCompat.TopEnd,
            AlignmentCompat.CenterStart,
            AlignmentCompat.Center,
            AlignmentCompat.CenterEnd,
            AlignmentCompat.BottomStart,
            AlignmentCompat.BottomCenter,
            AlignmentCompat.BottomEnd,
        ).map { it.name }
    }
    DropdownSettingItem(
        title = "Alignment",
        desc = null,
        values = alignmentValues,
        state = appSettings.listAlignmentName,
    )

    SwitchSettingItem(
        title = "Resize On Draw",
        desc = null,
        state = appSettings.resizeOnDrawEnabled,
    )

    SwitchSettingItem(
        title = "MimeType Logo",
        state = appSettings.showMimeTypeLogoInList,
        desc = "Displays the image type in the lower right corner of the ImageView"
    )

    SwitchSettingItem(
        title = "Data From Logo",
        state = appSettings.showDataFromLogoInList,
        desc = "A different color triangle is displayed in the lower right corner of the ImageView according to DataFrom"
    )

    SwitchSettingItem(
        title = "Progress Indicator",
        state = appSettings.showProgressIndicatorInList,
        desc = "A black translucent mask is displayed on the ImageView surface to indicate progress"
    )

    SwitchSettingItem(
        title = "Save Cellular Traffic",
        state = appSettings.saveCellularTrafficInList,
        desc = "Mobile cell traffic does not download pictures"
    )

    SwitchSettingItem(
        title = "Pause Load When Scrolling",
        state = appSettings.pauseLoadWhenScrollInList,
        desc = "No image is loaded during list scrolling to improve the smoothness"
    )

    val precisionValues = remember {
        Precision.values().map { it.name }.plus(listOf("LongImageMode"))
    }
    DropdownSettingItem(
        title = "Resize Precision",
        desc = null,
        values = precisionValues,
        state = appSettings.precisionName,
    )

    val scaleValues = remember {
        Scale.values().map { it.name }.plus(listOf("LongImageMode"))
    }
    DropdownSettingItem(
        title = "Resize Scale",
        desc = null,
        values = scaleValues,
        state = appSettings.scaleName,
    )

    val longImageModeEnabled = remember {
        appSettings.scaleName.map { it == "LongImageMode" }
    }
    DropdownSettingItem(
        title = "Long Image Resize Scale",
        desc = "Only Resize Scale is LongImageMode",
        values = Scale.values().asList(),
        state = appSettings.longImageScale,
        enabledState = longImageModeEnabled,
    )
    DropdownSettingItem(
        title = "Other Image Resize Scale",
        desc = "Only Resize Scale is LongImageMode",
        values = Scale.values().asList(),
        state = appSettings.otherImageScale,
        enabledState = longImageModeEnabled,
    )
}

@Composable
fun ViewerSettingsList(appSettings: AppSettings) {
    DividerSettingItem("Viewer")

    val contentScaleValues = remember {
        listOf(
            ContentScaleCompat.Fit,
            ContentScaleCompat.Crop,
            ContentScaleCompat.Inside,
            ContentScaleCompat.FillWidth,
            ContentScaleCompat.FillHeight,
            ContentScaleCompat.FillBounds,
            ContentScaleCompat.None,
        ).map { it.name }
    }
    DropdownSettingItem(
        title = "Content Scale",
        desc = null,
        values = contentScaleValues,
        state = appSettings.contentScaleName,
    )

    val alignmentValues = remember {
        listOf(
            AlignmentCompat.TopStart,
            AlignmentCompat.TopCenter,
            AlignmentCompat.TopEnd,
            AlignmentCompat.CenterStart,
            AlignmentCompat.Center,
            AlignmentCompat.CenterEnd,
            AlignmentCompat.BottomStart,
            AlignmentCompat.BottomCenter,
            AlignmentCompat.BottomEnd,
        ).map { it.name }
    }
    DropdownSettingItem(
        title = "Alignment",
        desc = null,
        values = alignmentValues,
        state = appSettings.alignmentName,
    )

    SwitchSettingItem(
        title = "Thumbnail Mode",
        desc = "Load low-resolution thumbnails first",
        state = appSettings.thumbnailMode,
    )
}

@Composable
fun ZoomSettingsList(appSettings: AppSettings) {
    DividerSettingItem("Zoom")

    SwitchSettingItem(
        title = "Scroll Bar",
        desc = null,
        state = appSettings.scrollBarEnabled,
    )

    SwitchSettingItem(
        title = "Read Mode",
        state = appSettings.readModeEnabled,
        desc = "Long images are displayed in full screen by default"
    )

    SwitchSettingItem(
        title = "Show Tile Bounds",
        desc = "Overlay the state and area of the tile on the View",
        state = appSettings.showTileBounds,
    )
}

@Composable
fun DecodeSettingsList(appSettings: AppSettings) {
    DividerSettingItem("Decode")

    val colorTypeValues = remember {
        listOf("Default", "LowQuality", "HighQuality").plus(platformColorTypes())
    }
    DropdownSettingItem(
        title = "Bitmap Color Type",
        desc = null,
        values = colorTypeValues,
        state = appSettings.colorTypeName,
    )

    PlatformDecodeSettingsList(appSettings)
}

@Composable
fun AnimatedSettingsList(appSettings: AppSettings) {
    DividerSettingItem("Animated")

    val repeatCountValues = remember {
        listOf(-1, 0, 1, 2, 4)
    }
    DropdownSettingItem(
        title = "Repeat Count",
        desc = null,
        values = repeatCountValues,
        state = appSettings.repeatCount,
    )

    PlatformAnimatedSettingsList(appSettings)
}

@Composable
expect fun PlatformDecodeSettingsList(appSettings: AppSettings)

@Composable
expect fun PlatformAnimatedSettingsList(appSettings: AppSettings)

@Composable
expect fun VideoSettingsList(appSettings: AppSettings)

@Composable
fun CacheSettingsList(appSettings: AppSettings) {
    DividerSettingItem("Cache")

    val sketch: Sketch = koinInject()
    var memoryCacheDescRefreshState by remember { mutableStateOf(true) }
    val memoryCacheDesc = remember(memoryCacheDescRefreshState) {
        val sizeFormatted = sketch.memoryCache.size.formatFileSize(0)
        val maxSizeFormatted = sketch.memoryCache.maxSize.formatFileSize(0)
        "$sizeFormatted/${maxSizeFormatted}（Long Press Clean）"
    }
    SwitchSettingItem(
        title = "Memory Cache",
        desc = memoryCacheDesc,
        state = appSettings.memoryCacheName,
        onLongClick = {
            sketch.memoryCache.clear()
            memoryCacheDescRefreshState = !memoryCacheDescRefreshState
        }
    )

    var resultCacheDescRefreshState by remember { mutableStateOf(true) }
    val resultCacheDesc = remember(resultCacheDescRefreshState) {
        val sizeFormatted = sketch.resultCache.size.formatFileSize(0)
        val maxSizeFormatted = sketch.resultCache.maxSize.formatFileSize(0)
        "$sizeFormatted/${maxSizeFormatted}（Long Press Clean）"
    }
    SwitchSettingItem(
        title = "Result Cache",
        desc = resultCacheDesc,
        state = appSettings.resultCacheName,
        onLongClick = {
            sketch.resultCache.clear()
            resultCacheDescRefreshState = !resultCacheDescRefreshState
        }
    )

    var downloadCacheDescRefreshState by remember { mutableStateOf(true) }
    val downloadCacheDesc = remember(downloadCacheDescRefreshState) {
        val sizeFormatted = sketch.downloadCache.size.formatFileSize(0)
        val maxSizeFormatted = sketch.downloadCache.maxSize.formatFileSize(0)
        "$sizeFormatted/${maxSizeFormatted}（Long Press Clean）"
    }
    SwitchSettingItem(
        title = "Download Cache",
        desc = downloadCacheDesc,
        state = appSettings.downloadCacheName,
        onLongClick = {
            sketch.downloadCache.clear()
            downloadCacheDescRefreshState = !downloadCacheDescRefreshState
        }
    )
}

@Composable
fun OtherSettingsList(appSettings: AppSettings, page: AppSettingsPage) {
    DividerSettingItem("Other")

    val logLevel by appSettings.logLevel.collectAsState()
    val logLevelDesc by remember {
        derivedStateOf {
            if (logLevel <= Logger.Level.Debug)
                "DEBUG and below will reduce UI fluency" else null
        }
    }
    val logLevelValues = remember {
        Logger.Level.values().toList()
    }
    DropdownSettingItem(
        title = "Logger Level",
        desc = logLevelDesc,
        values = logLevelValues,
        state = appSettings.logLevel,
    )

    val zoomImageLogLevel by appSettings.zoomImageLogLevel.collectAsState()
    val zoomImageLogLevelDesc by remember {
        derivedStateOf {
            if (zoomImageLogLevel <= com.github.panpf.zoomimage.util.Logger.Level.Debug)
                "DEBUG and below will reduce UI fluency" else null
        }
    }
    val zoomImageLogLevelValues = remember {
        com.github.panpf.zoomimage.util.Logger.Level.values().toList()
    }
    DropdownSettingItem(
        title = "ZoomImage Logger Level",
        desc = zoomImageLogLevelDesc,
        values = zoomImageLogLevelValues,
        state = appSettings.zoomImageLogLevel,
    )

    val appEvents: AppEvents = koinInject()
    val parallelismLimitedValue = remember {
        listOf(-1, 1, 2, 4, 10, 20)
    }
    DropdownSettingItem(
        title = "Network Parallelism Limited",
        desc = "No limit when less than or equal to 0",
        values = parallelismLimitedValue,
        state = appSettings.networkParallelismLimited,
        onItemClick = {
            appEvents.toastFlow.emit("Restart the app to take effect")
        }
    )

    DropdownSettingItem(
        title = "Decode Parallelism Limited",
        desc = "No limit when less than or equal to 0",
        values = parallelismLimitedValue,
        state = appSettings.decodeParallelismLimited,
        onItemClick = {
            appEvents.toastFlow.emit("Restart the app to take effect")
        }
    )

    PlatformOtherSettingsList(appSettings, page)
}

@Composable
expect fun PlatformOtherSettingsList(appSettings: AppSettings, page: AppSettingsPage)

expect fun platformColorTypes(): List<String>

expect fun platformColorSpaces(): List<String>