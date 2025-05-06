package com.github.panpf.sketch.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.ability.progressIndicator
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.sample.EventBus
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage
import com.github.panpf.zoomimage.SketchZoomAsyncImage
import com.github.panpf.zoomimage.SketchZoomState
import com.github.panpf.zoomimage.compose.zoom.ScrollBarSpec
import com.github.panpf.zoomimage.compose.zoom.bindKeyZoomWithKeyEventFlow
import com.github.panpf.zoomimage.rememberSketchZoomState
import com.github.panpf.zoomimage.zoom.ReadMode

@Composable
fun MyZoomAsyncImage(
    uri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    placeholderUri: String? = null,
    imageState: AsyncImageState = rememberAsyncImageState(),
    zoomState: SketchZoomState = rememberSketchZoomState(),
    highlightColor: Color? = null,
    pageSelected: Boolean = false,
    onLongPress: ((Offset) -> Unit)? = null
) {
    val context = LocalPlatformContext.current
    val appSettings = context.appSettings

    LaunchedEffect(zoomState) {
        appSettings.showTileBounds.collect {
            zoomState.subsampling.showTileBounds = it
        }
    }
    LaunchedEffect(zoomState) {
        appSettings.readModeEnabled.collect {
            zoomState.zoomable.readMode = if (it) ReadMode.Default else null
        }
    }
    LaunchedEffect(zoomState) {
        zoomState.zoomable.keepTransformWhenSameAspectRatioContentSizeChanged = true
    }

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

        placeholder(ThumbnailMemoryCacheStateImage(placeholderUri))
        crossfade(fadeStart = false)

        val platformAsyncImageSettings = composablePlatformAsyncImageSettings(appSettings)
        merge(platformAsyncImageSettings)
    }
    val sketch = SingletonSketch.get(context)

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
        bindKeyZoomWithKeyEventFlow(EventBus.keyEvent, zoomState.zoomable)
    }
}