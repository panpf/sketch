package com.github.panpf.sketch.sample.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.setting.Page.LIST
import com.github.panpf.sketch.sample.ui.setting.Page.ZOOM
import com.github.panpf.sketch.sample.ui.rememberIconExpandMorePainter
import com.github.panpf.sketch.sample.ui.util.formatFileSize
import com.github.panpf.sketch.sample.util.ifLet
import com.github.panpf.sketch.util.Logger.Level
import com.github.panpf.sketch.util.Logger.Level.DEBUG
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.name
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

@Composable
expect fun getSettingsDialogHeight(): Dp

@Composable
fun AppSettingsDialog(
    page: Page,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties()) {
        Column(
            Modifier
                .fillMaxWidth()
                .height(getSettingsDialogHeight())
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .verticalScroll(rememberScrollState())
        ) {
            val recreateSettingItems = remember { mutableStateOf(0) }
            val context = LocalPlatformContext.current
            val appSettings = context.appSettings
            val logLevel by appSettings.logLevel.collectAsState()
            val recreateCount by recreateSettingItems
            val settingItems = remember(logLevel, recreateCount) {
                createSettingItems(context, appSettings, page, recreateSettingItems)
            }
            settingItems.forEach { settingItem ->
                when (settingItem) {
                    is SwitchSettingItem -> SwitchSetting(settingItem)
                    is DropdownSettingItem<*> -> DropdownSetting(settingItem)
                    is DividerSettingItem -> DividerSetting(settingItem)
                    is GroupSettingItem -> GroupSetting(settingItem)
                }
            }
        }
    }
}

fun createSettingItems(
    context: PlatformContext,
    appSettings: AppSettings,
    page: Page,
    recreateSettingItems: MutableState<Int>
): List<SettingItem> {
    return buildList {
        if (page == LIST) {
            add(GroupSettingItem("List"))
            addAll(makeListMenuList(appSettings))
        } else if (page == ZOOM) {
            add(GroupSettingItem("Zoom"))
            addAll(makeZoomMenuList(appSettings))
        }
        add(GroupSettingItem("Decode"))
        addAll(makeDecodeMenuList(appSettings))
        addAll(platformMakeDecodeMenuList(appSettings))
        add(GroupSettingItem("Cache"))
        addAll(makeCacheMenuList(context, appSettings, recreateSettingItems))
        add(GroupSettingItem("Other"))
        addAll(makeOtherMenuList(appSettings))
    }
}


private fun makeListMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        SwitchSettingItem(
            title = "MimeType Logo",
            state = appSettings.showMimeTypeLogoInList,
            desc = "Displays the image type in the lower right corner of the ImageView"
        )
    )
    add(
        SwitchSettingItem(
            title = "Data From Logo",
            state = appSettings.showDataFromLogoInList,
            desc = "A different color triangle is displayed in the lower right corner of the ImageView according to DataFrom"
        )
    )
    add(
        SwitchSettingItem(
            title = "Progress Indicator",
            state = appSettings.showProgressIndicatorInList,
            desc = "A black translucent mask is displayed on the ImageView surface to indicate progress"
        )
    )
    add(
        SwitchSettingItem(
            title = "Save Cellular Traffic",
            state = appSettings.saveCellularTrafficInList,
            desc = "Mobile cell traffic does not download pictures"
        )
    )
    add(
        SwitchSettingItem(
            title = "Pause Load When Scrolling",
            state = appSettings.pauseLoadWhenScrollInList,
            desc = "No image is loaded during list scrolling to improve the smoothness"
        )
    )
    add(
        DropdownSettingItem(
            title = "Resize Precision",
            desc = null,
            values = Precision.values().map { it.name }.plus(listOf("LongImageClipMode")),
            state = appSettings.precision,
        )
    )
    add(
        DropdownSettingItem(
            title = "Resize Scale",
            desc = null,
            values = Scale.values().map { it.name }.plus(listOf("LongImageMode")),
            state = appSettings.scale,
        )
    )
    val enabled = appSettings.scale.map { it == "LongImageMode" }
    add(
        DropdownSettingItem(
            title = "Long Image Resize Scale",
            desc = "Only Resize Scale is LongImageMode",
            values = Scale.values().asList(),
            state = appSettings.longImageScale,
            enabled = enabled,
        )
    )
    add(
        DropdownSettingItem(
            title = "Other Image Resize Scale",
            desc = "Only Resize Scale is LongImageMode",
            values = Scale.values().asList(),
            state = appSettings.otherImageScale,
            enabled = enabled,
        )
    )
}

private fun makeZoomMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
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

expect fun platformMakeDecodeMenuList(appSettings: AppSettings): List<SettingItem>

private fun makeDecodeMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        DropdownSettingItem(
            title = "Bitmap Quality",
            desc = null,
            values = listOf("Default", "LOW", "HIGH"),
            state = appSettings.bitmapQuality,
        )
    )
    add(
        SwitchSettingItem(
            title = "Ignore Exif Orientation",
            desc = null,
            state = appSettings.ignoreExifOrientation,
        )
    )
}

private fun makeCacheMenuList(
    context: PlatformContext,
    appSettings: AppSettings,
    recreateSettingItems: MutableState<Int>,
): List<SettingItem> = buildList {
    val sketch = SingletonSketch.get(context)

    add(
        SwitchSettingItem(
            title = "Disabled Memory Cache",
            desc = "%s/%s（Long Click Clean）".format(
                sketch.memoryCache.size.formatFileSize(
                    0,
                    decimalPlacesFillZero = false,
                    compact = true
                ),
                sketch.memoryCache.maxSize.formatFileSize(
                    0,
                    decimalPlacesFillZero = false,
                    compact = true
                )
            ),
            state = appSettings.disabledMemoryCache,
            onLongClick = {
                sketch.memoryCache.clear()
                recreateSettingItems.value = recreateSettingItems.value + 1
            }
        )
    )

    add(
        SwitchSettingItem(
            title = "Disabled Result Cache",
            desc = "%s/%s（Long Click Clean）".format(
                sketch.resultCache.size.formatFileSize(
                    0,
                    decimalPlacesFillZero = false,
                    compact = true
                ),
                sketch.resultCache.maxSize.formatFileSize(
                    0,
                    decimalPlacesFillZero = false,
                    compact = true
                )
            ),
            state = appSettings.disabledResultCache,
            onLongClick = {
                sketch.resultCache.clear()
                recreateSettingItems.value = recreateSettingItems.value + 1
            }
        )
    )

    add(
        SwitchSettingItem(
            title = "Disabled Download Cache",
            desc = "%s/%s（Long Click Clean）".format(
                sketch.downloadCache.size.formatFileSize(
                    0,
                    decimalPlacesFillZero = false,
                    compact = true
                ),
                sketch.downloadCache.maxSize.formatFileSize(
                    0,
                    decimalPlacesFillZero = false,
                    compact = true
                )
            ),
            state = appSettings.disabledDownloadCache,
            onLongClick = {
                sketch.downloadCache.clear()
                recreateSettingItems.value = recreateSettingItems.value + 1
            }
        )
    )
}

private fun makeOtherMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        DropdownSettingItem(
            title = "Logger Level",
            desc = if (appSettings.logLevel.value <= DEBUG) "DEBUG and below will reduce UI fluency" else null,
            values = Level.values().asList(),
            state = appSettings.logLevel,
        )
    )
}

interface SettingItem {
    val title: String
    val desc: String?
    val enabled: Flow<Boolean>
}

data class SwitchSettingItem(
    override val title: String,
    val state: MutableStateFlow<Boolean>,
    override val desc: String? = null,
    override val enabled: Flow<Boolean> = MutableStateFlow(true),
    val onLongClick: (() -> Unit)? = null,
) : SettingItem

data class DropdownSettingItem<T>(
    override val title: String,
    val values: List<T>,
    val state: MutableStateFlow<T>,
    override val desc: String? = null,
    override val enabled: Flow<Boolean> = MutableStateFlow(true),
) : SettingItem

data object DividerSettingItem : SettingItem {
    override val title: String = "Divider"
    override val desc: String? = null
    override val enabled: Flow<Boolean> = MutableStateFlow(true)
}

data class GroupSettingItem(override val title: String) : SettingItem {
    override val desc: String? = null
    override val enabled: Flow<Boolean> = MutableStateFlow(true)
}

@Composable
fun DividerSetting(settingItem: DividerSettingItem) {
    val enabled by settingItem.enabled.collectAsState(false)
    if (enabled) {
        Divider(Modifier.padding(horizontal = 20.dp, vertical = 10.dp))
    }
}

@Composable
fun GroupSetting(settingItem: GroupSettingItem) {
    val enabled by settingItem.enabled.collectAsState(false)
    if (enabled) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = settingItem.title,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
            )
            Divider(
                Modifier.fillMaxWidth().height(0.5.dp).padding(horizontal = 20.dp)
                    .background(Color.Gray)
            )
        }
    }
}

val menuItemHeight = 50.dp

@Composable
fun SwitchSetting(settingItem: SwitchSettingItem) {
    val enabled by settingItem.enabled.collectAsState(false)
    if (enabled) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = menuItemHeight)
                .clickable { settingItem.state.value = !settingItem.state.value }
                .ifLet(settingItem.onLongClick != null) {
                    it.pointerInput(settingItem) {
                        detectTapGestures(onLongPress = { settingItem.onLongClick?.invoke() })
                    }
                }
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = settingItem.title,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                )
                if (settingItem.desc != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = settingItem.desc,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 14.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            val checked by settingItem.state.collectAsState()
            Switch(
                checked = checked,
                onCheckedChange = null,
            )
        }
    }
}

@Composable
fun <T> DropdownSetting(settingItem: DropdownSettingItem<T>) {
    val enabled by settingItem.enabled.collectAsState(false)
    if (enabled) {
        Box(modifier = Modifier.fillMaxWidth()) {
            var expanded by remember { mutableStateOf(false) }
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = menuItemHeight)
                    .clickable { expanded = true }
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = settingItem.title,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                    )
                    if (settingItem.desc != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = settingItem.desc,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 14.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))
                val value by settingItem.state.collectAsState()
                Text(text = value.toString(), fontSize = 10.sp)
                Icons.Filled.MoreVert
                Icon(
                    painter = rememberIconExpandMorePainter(),
                    contentDescription = "more"
                )
            }

            DropdownMenu(
                expanded = expanded,
                modifier = Modifier.align(Alignment.CenterEnd),
                onDismissRequest = { expanded = false },
            ) {
                settingItem.values.forEachIndexed { index, value ->
                    if (index > 0) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(text = value.toString()) },
                        onClick = {
                            settingItem.state.value = value
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}