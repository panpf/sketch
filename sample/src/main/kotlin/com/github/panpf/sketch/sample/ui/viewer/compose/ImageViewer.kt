package com.github.panpf.sketch.sample.ui.viewer.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.findNavController
import com.github.panpf.sketch.compose.ability.progressIndicator
import com.github.panpf.sketch.compose.ability.rememberDrawableProgressPainter
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.common.compose.LoadState
import com.github.panpf.sketch.sample.ui.common.createDayNightSectorProgressDrawable
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ThumbnailMemoryCacheStateImage
import com.github.panpf.zoomimage.SketchZoomAsyncImage
import com.github.panpf.zoomimage.compose.internal.toPlatform
import com.github.panpf.zoomimage.compose.rememberZoomState
import com.github.panpf.zoomimage.compose.zoom.ScrollBarSpec
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.ReadMode
import com.github.panpf.zoomimage.zoom.valueOf
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ImageViewer(
    index: Int, imageDetail: ImageDetail,
    buttonBgColorState: MutableState<Int>,
    onClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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

    val progressPainter =
        rememberDrawableProgressPainter(remember { createDayNightSectorProgressDrawable(context) })

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
            onTap = { onClick.invoke() },
            onLongPress = {
                val displayResult = imageState.result
                if (displayResult != null) {
                    view
                        .findNavController()
                        .navigate(ImageInfoDialogFragment.createNavDirections(displayResult))
                }
            }
        )

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 30.dp),
        ) {
            val buttonBgColor = Color(buttonBgColorState.value)
            val buttonTextColor = Color.White
            val buttonModifier = Modifier
                .size(40.dp)
                .background(
                    color = buttonBgColor,
                    shape = RoundedCornerShape(50)
                )
                .padding(8.dp)
            IconButton(
                modifier = buttonModifier,
                onClick = { onShareClick.invoke() },
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_share),
                    contentDescription = "share",
                    tint = buttonTextColor
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            IconButton(
                modifier = buttonModifier,
                onClick = { onSaveClick.invoke() },
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_save),
                    contentDescription = "save",
                    tint = buttonTextColor
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            val zoomIcon by remember {
                derivedStateOf {
                    val zoomIn =
                        zoomState.zoomable.getNextStepScale() > zoomState.zoomable.transform.scaleX
                    if (zoomIn) {
                        R.drawable.ic_zoom_in
                    } else {
                        R.drawable.ic_zoom_out
                    }
                }
            }
            IconButton(
                modifier = buttonModifier,
                onClick = {
                    coroutineScope.launch {
                        val zoomable = zoomState.zoomable
                        val nextStepScale = zoomable.getNextStepScale()
                        zoomable.scale(nextStepScale, animated = true)
                    }
                },
            ) {
                Icon(
                    painter = painterResource(id = zoomIcon),
                    contentDescription = "zoom",
                    tint = buttonTextColor
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            IconButton(
                modifier = buttonModifier,
                onClick = {
                    coroutineScope.launch {
                        val zoomable = zoomState.zoomable
                        zoomable.rotate(zoomable.transform.rotation.roundToInt() + 90)
                    }
                },
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_rotate_right),
                    contentDescription = "right rotate",
                    tint = buttonTextColor
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            IconButton(
                modifier = buttonModifier,
                onClick = {
                    val displayResult = imageState.result
                    if (displayResult != null) {
                        view
                            .findNavController()
                            .navigate(ImageInfoDialogFragment.createNavDirections(displayResult))
                    }
                },
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_info_baseline),
                    contentDescription = "info",
                    tint = buttonTextColor
                )
            }
        }

        LoadState(
            modifier = Modifier.align(Alignment.Center),
            imageState = imageState
        )
    }
}