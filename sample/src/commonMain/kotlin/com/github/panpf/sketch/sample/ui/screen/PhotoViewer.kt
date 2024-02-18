package com.github.panpf.sketch.sample.ui.screen

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
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.compose.ability.progressIndicator
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.components.LoadState
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.rememberIconInfoBaseLinePainter
import com.github.panpf.sketch.sample.ui.rememberIconRotateRightPainter
import com.github.panpf.sketch.sample.ui.rememberIconSavePainter
import com.github.panpf.sketch.sample.ui.rememberIconSharePainter
import com.github.panpf.sketch.sample.ui.rememberIconZoomInPainter
import com.github.panpf.sketch.sample.ui.rememberIconZoomOutPainter
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
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
fun PhotoViewer(
    imageDetail: ImageDetail,
    buttonBgColorState: MutableState<Int>,
    onClick: () -> Unit,
    onLongClick: (ImageResult) -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    onInfoClick: (ImageResult) -> Unit,
) {
    val context = LocalPlatformContext.current
    val coroutineScope = rememberCoroutineScope()
    val appSettingsService = context.appSettings
    val imageState = rememberAsyncImageState()
    val showOriginImage by appSettingsService.showOriginImage.collectAsState()
    val scrollBarEnabled by appSettingsService.scrollBarEnabled.collectAsState()
    val readModeEnabled by appSettingsService.readModeEnabled.collectAsState()
    val showTileBounds by appSettingsService.showTileBounds.collectAsState()
    val contentScaleName by appSettingsService.contentScale.collectAsState()
    val alignmentName by appSettingsService.alignment.collectAsState()
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

    val progressPainter = rememberThemeSectorProgressPainter()

    val viewerSettings by appSettingsService.viewersCombinedFlow.collectAsState(Unit)
    val request = remember(imageUrl, viewerSettings) {
        ImageRequest(context, imageUrl) {
            merge(appSettingsService.buildViewerImageOptions())
            placeholder(ThumbnailMemoryCacheStateImage(imageDetail.thumbnailUrl))
            crossfade(fadeStart = false)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SketchZoomAsyncImage(
            request = request,
            sketch = SingletonSketch.get(context),
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
                val imageResult = imageState.result
                if (imageResult != null) {
                    onLongClick(imageResult)
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
                    painter = rememberIconSharePainter(),
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
                    painter = rememberIconSavePainter(),
                    contentDescription = "save",
                    tint = buttonTextColor
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            val zoomIn by remember {
                derivedStateOf {
                    zoomState.zoomable.getNextStepScale() > zoomState.zoomable.transform.scaleX
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
                    painter = if (zoomIn) {
                        rememberIconZoomInPainter()
                    } else {
                        rememberIconZoomOutPainter()
                    },
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
                    painter = rememberIconRotateRightPainter(),
                    contentDescription = "right rotate",
                    tint = buttonTextColor
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            IconButton(
                modifier = buttonModifier,
                onClick = {
                    val imageResult = imageState.result
                    if (imageResult != null) {
                        onInfoClick(imageResult)
                    }
                },
            ) {
                Icon(
                    painter = rememberIconInfoBaseLinePainter(),
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