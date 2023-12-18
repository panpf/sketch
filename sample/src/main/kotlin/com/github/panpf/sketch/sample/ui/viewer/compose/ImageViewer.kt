package com.github.panpf.sketch.sample.ui.viewer.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.findNavController
import com.github.panpf.sketch.compose.ability.progressIndicator
import com.github.panpf.sketch.compose.ability.rememberDrawableProgressPainter
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.eventService
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.common.compose.LoadState
import com.github.panpf.sketch.sample.ui.common.zoom.SketchZoomAsyncImage
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ThumbnailMemoryCacheStateImage
import com.github.panpf.zoomimage.compose.internal.toPlatform
import com.github.panpf.zoomimage.compose.rememberZoomState
import com.github.panpf.zoomimage.compose.zoom.ScrollBarSpec
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.ReadMode
import com.github.panpf.zoomimage.zoom.valueOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.math.roundToInt

@Composable
fun ImageViewer(
    index: Int, imageDetail: ImageDetail,
    showInfoEvent: MutableSharedFlow<DisplayResult?>,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val appSettingsService = context.appSettingsService
    val imageState = rememberAsyncImageState()
    val showOriginImage by appSettingsService.showOriginImage.stateFlow.collectAsState()
    val scrollBarEnabled by appSettingsService.scrollBarEnabled.stateFlow.collectAsState()
    val readModeEnabled by appSettingsService.readModeEnabled.stateFlow.collectAsState()
    val showTileBounds by appSettingsService.showTileBounds.stateFlow.collectAsState()
    val contentScaleName by appSettingsService.contentScale.stateFlow.collectAsState()
    val alignmentName by appSettingsService.alignment.stateFlow.collectAsState()
    val contentScale by remember {
        derivedStateOf {
            ContentScaleCompat.valueOf(contentScaleName).toPlatform()
        }
    }
    val alignment by remember {
        derivedStateOf {
            AlignmentCompat.valueOf(alignmentName).toPlatform()
        }
    }
    val zoomState = rememberZoomState().apply {
        LaunchedEffect(showTileBounds) {
            subsampling.showTileBounds = showTileBounds
        }
        val readMode by remember {
            derivedStateOf {
                if (readModeEnabled) ReadMode.Default else null
            }
        }
        LaunchedEffect(readMode) {
            zoomable.readMode = readMode
        }
        LaunchedEffect(Unit) {
            context.eventService.viewerPagerRotateEvent.collect {
                zoomable.rotate(zoomable.transform.rotation.roundToInt() + 90)
            }
        }
    }
    LaunchedEffect(Unit) {
        context.eventService.viewerPagerInfoEvent.collect {
            if (index == it) {
                showInfoEvent.emit(imageState.result)
            }
        }
    }
    val imageUrl by remember {
        derivedStateOf {
            if (showOriginImage) {
                imageDetail.originUrl
            } else {
                imageDetail.mediumUrl ?: imageDetail.originUrl
            }
        }
    }
    val scrollBar by remember {
        derivedStateOf {
            if (scrollBarEnabled) ScrollBarSpec.Default else null
        }
    }

    val progressPainter = rememberDrawableProgressPainter(remember { SectorProgressDrawable() })

    val viewerSettings by appSettingsService.viewersCombinedFlow.collectAsState(Unit)
    val request = remember(imageUrl, viewerSettings) {
        DisplayRequest(context, imageUrl) {
            merge(appSettingsService.buildViewerImageOptions())
            placeholder(ThumbnailMemoryCacheStateImage(imageDetail.thumbnailUrl))
            crossfade(fadeStart = false)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val view = LocalView.current
        SketchZoomAsyncImage(
            request = request,
            sketch = context.sketch,
            contentDescription = "view image",
            modifier = Modifier
                .fillMaxSize()
                .progressIndicator(imageState, progressPainter),
            imageState = imageState,
            contentScale = contentScale,
            alignment = alignment,
            state = zoomState,
            scrollBar = scrollBar,
            onTap = { onClick?.invoke() },
            onLongPress = {
                val displayResult = imageState.result
                if (displayResult != null) {
                    view
                        .findNavController()
                        .navigate(ImageInfoDialogFragment.createNavDirections(displayResult))
                }
            }
        )

        LoadState(
            modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
            imageState = imageState
        )
    }
}