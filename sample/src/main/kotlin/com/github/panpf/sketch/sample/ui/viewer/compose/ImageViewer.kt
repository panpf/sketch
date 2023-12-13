package com.github.panpf.sketch.sample.ui.viewer.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.findNavController
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.eventService
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.photo.pexels.progressIndicator
import com.github.panpf.sketch.sample.ui.photo.pexels.rememberDrawableProgressPainter
import com.github.panpf.sketch.sample.ui.photo.pexels.rememberProgressIndicatorState
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.stateimage.ThumbnailMemoryCacheStateImage
import com.github.panpf.zoomimage.SketchZoomAsyncImage
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
    var displayResult: DisplayResult? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        context.eventService.viewerPagerInfoEvent.collect {
            if (index == it) {
                showInfoEvent.emit(displayResult)
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


    val progressDrawable = remember { SectorProgressDrawable() }
    val drawableProgressPainter = rememberDrawableProgressPainter(progressDrawable)
    val progressIndicatorState = rememberProgressIndicatorState(drawableProgressPainter)

    val viewerSettings by appSettingsService.viewersCombinedFlow.collectAsState(Unit)
    // listener 会导致两次创建的 DisplayRequest equals 为 false，从而引发重组，所以这里必须用 remember
    val request = remember(imageUrl, viewerSettings) {
        DisplayRequest(context, imageUrl) {
            merge(appSettingsService.buildViewerImageOptions())
            placeholder(ThumbnailMemoryCacheStateImage(imageDetail.thumbnailUrl))
            crossfade(fadeStart = false)
            listener(
                onStart = {
                    progressIndicatorState.progress = 0f
                },
                onSuccess = { _, result ->
                    displayResult = result
                    progressIndicatorState.progress = 1f
                },
                onError = { _, _ ->
                    displayResult = null
                    progressIndicatorState.progress = -1f
                }
            )
            progressListener { _, totalLength: Long, completedLength: Long ->
                val progress = if (totalLength > 0) completedLength.toFloat() / totalLength else 0f
                progressIndicatorState.progress = progress
            }
        }
    }
    val view = LocalView.current
    // todo state
    SketchZoomAsyncImage(
        request = request,
        contentDescription = "view image",
        modifier = Modifier
            .fillMaxSize()
            .progressIndicator(progressIndicatorState),
        contentScale = contentScale,
        alignment = alignment,
        state = zoomState,
        scrollBar = scrollBar,
        onTap = { onClick?.invoke() },
        onLongPress = {
            val displayResult1 = displayResult
            if (displayResult1 != null) {
                view
                    .findNavController()
                    .navigate(ImageInfoDialogFragment.createNavDirections(displayResult1))
            }
        }
    )
}