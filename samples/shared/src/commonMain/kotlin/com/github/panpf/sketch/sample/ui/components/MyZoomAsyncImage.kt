package com.github.panpf.sketch.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.ability.progressIndicator
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage
import com.github.panpf.zoomimage.SketchZoomAsyncImage
import com.github.panpf.zoomimage.SketchZoomState
import com.github.panpf.zoomimage.compose.zoom.ScrollBarSpec
import com.github.panpf.zoomimage.compose.zoom.bindKeyZoomWithKeyEventFlow
import com.github.panpf.zoomimage.rememberSketchZoomState
import com.github.panpf.zoomimage.zoom.ReadMode
import org.koin.compose.koinInject

@Composable
fun MyZoomAsyncImage(
    uri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    thumbnailUri: String? = null,
    imageState: AsyncImageState = rememberAsyncImageState(),
    zoomState: SketchZoomState = rememberSketchZoomState(),
    highlightColor: Color? = null,
    pageSelected: Boolean = false,
    onLongPress: ((Offset) -> Unit)? = null
) {
    val appSettings: AppSettings = koinInject()

    val zoomImageLogLevel by appSettings.zoomImageLogLevel.collectAsState()
    zoomState.logger.level = zoomImageLogLevel

    val showTileBounds by appSettings.showTileBounds.collectAsState()
    val readModeEnabled by appSettings.readModeEnabled.collectAsState()
    zoomState.subsampling.setShowTileBounds(showTileBounds)
    zoomState.zoomable.setReadMode(if (readModeEnabled) ReadMode.Default else null)
    zoomState.zoomable.setKeepTransformWhenSameAspectRatioContentSizeChanged(true)

    val request = ComposableImageRequest(uri) {
        val memoryCache by appSettings.memoryCache.collectAsState()
        memoryCachePolicy(memoryCache)

        val resultCache by appSettings.resultCache.collectAsState()
        resultCachePolicy(resultCache)

        val downloadCache by appSettings.downloadCache.collectAsState()
        downloadCachePolicy(downloadCache)

        val colorType by appSettings.colorType.collectAsState()
        colorType(colorType)

        val colorSpace by appSettings.colorSpace.collectAsState()
        colorSpace(colorSpace)

        val repeatCount by appSettings.repeatCount.collectAsState()
        repeatCount(repeatCount)

        val thumbnailMode by appSettings.thumbnailMode.collectAsState()
        if (thumbnailUri != null) {
            if (thumbnailMode) {
                thumbnail(thumbnailUri)
            } else {
                placeholder(ThumbnailMemoryCacheStateImage(thumbnailUri))
            }
        }

        crossfade(fadeStart = false)

        val platformAsyncImageSettings = composablePlatformAsyncImageSettings(appSettings)
        merge(platformAsyncImageSettings)
    }
    val sketch: Sketch = koinInject()

    val progressPainter = rememberThemeSectorProgressPainter()
    val modifier1 = modifier.progressIndicator(imageState, progressPainter)

    val contentScale by appSettings.contentScale.collectAsState()
    val alignment by appSettings.alignment.collectAsState()

    val scrollBarEnabled by appSettings.scrollBarEnabled.collectAsState()
    val scrollBar = remember(scrollBarEnabled, highlightColor) {
        if (scrollBarEnabled && highlightColor != null)
            ScrollBarSpec.Default.copy(color = highlightColor) else null
    }

    SketchZoomAsyncImage(
        request = request,
        sketch = sketch,
        contentDescription = contentDescription,
        modifier = modifier1,
        state = imageState,
        contentScale = contentScale,
        alignment = alignment,
        zoomState = zoomState,
        scrollBar = scrollBar,
        onLongPress = onLongPress
    )

    if (pageSelected) {
        val appEvents: AppEvents = koinInject()
        bindKeyZoomWithKeyEventFlow(appEvents.keyEvent, zoomState.zoomable)
    }
}