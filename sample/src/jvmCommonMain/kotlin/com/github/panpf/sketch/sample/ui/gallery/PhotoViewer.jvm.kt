@file:OptIn(ExperimentalResourceApi::class)

package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.github.panpf.sketch.sample.ui.MyEvents
import com.github.panpf.sketch.sample.ui.common.list.LoadState
import com.github.panpf.sketch.sample.ui.model.Photo
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import sketch.sample.generated.resources.Res.drawable
import sketch.sample.generated.resources.ic_info_baseline
import sketch.sample.generated.resources.ic_rotate_right
import sketch.sample.generated.resources.ic_save
import sketch.sample.generated.resources.ic_share
import sketch.sample.generated.resources.ic_zoom_in
import sketch.sample.generated.resources.ic_zoom_out
import kotlin.math.roundToInt

@Composable
actual fun PhotoViewer(
    photo: Photo,
    buttonBgColorState: MutableState<Color>,
) {
    val context = LocalPlatformContext.current
    val appSettings = context.appSettings
    val coroutineScope = rememberCoroutineScope()
    val imageState = rememberAsyncImageState()
    val showOriginImage by appSettings.showOriginImage.collectAsState()
    val scrollBarEnabled by appSettings.scrollBarEnabled.collectAsState()
    val readModeEnabled by appSettings.readModeEnabled.collectAsState()
    val showTileBounds by appSettings.showTileBounds.collectAsState()
    val contentScaleName by appSettings.contentScale.collectAsState()
    val alignmentName by appSettings.alignment.collectAsState()
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
    val imageUri by remember {
        derivedStateOf {
            if (showOriginImage) {
                photo.originalUrl
            } else {
                photo.mediumUrl ?: photo.originalUrl
            }
        }
    }
    val scrollBar by remember {
        derivedStateOf {
            if (scrollBarEnabled) ScrollBarSpec.Default.copy(
                color = buttonBgColorState.value.copy(alpha = 0.5f)
            ) else null
        }
    }

    val progressPainter = rememberThemeSectorProgressPainter()

    val viewerSettings by appSettings.viewersCombinedFlow.collectAsState(Unit)
    val request = remember(imageUri, viewerSettings) {
        ImageRequest(context, imageUri) {
            merge(appSettings.buildViewerImageOptions())
            placeholder(ThumbnailMemoryCacheStateImage(photo.thumbnailUrl))
            crossfade(fadeStart = false)
        }
    }

    var photoInfoImageResult by remember { mutableStateOf<ImageResult?>(null) }
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
            onLongPress = {
                val imageResult = imageState.result
                if (imageResult != null) {
                    photoInfoImageResult = imageResult
                }
            }
        )

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .padding(vertical = 30.dp),
        ) {
            val buttonBgColor = buttonBgColorState.value
            val buttonTextColor = Color.White
            IconButton(onClick = {
                coroutineScope.launch {
                    MyEvents.sharePhotoFlow.emit(imageUri)
                }
            }) {
                Icon(
                    painter = painterResource(drawable.ic_share),
                    contentDescription = "share",
                    tint = buttonTextColor,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = buttonBgColor)
                        .padding(8.dp),
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            IconButton(onClick = {
                coroutineScope.launch {
                    MyEvents.savePhotoFlow.emit(imageUri)
                }
            }) {
                Icon(
                    painter = painterResource(drawable.ic_save),
                    contentDescription = "save",
                    tint = buttonTextColor,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = buttonBgColor)
                        .padding(8.dp),
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            val zoomIn by remember {
                derivedStateOf {
                    zoomState.zoomable.getNextStepScale() > zoomState.zoomable.transform.scaleX
                }
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    val zoomable = zoomState.zoomable
                    val nextStepScale = zoomable.getNextStepScale()
                    zoomable.scale(nextStepScale, animated = true)
                }
            }) {
                Icon(
                    painter = if (zoomIn) {
                        painterResource(drawable.ic_zoom_in)
                    } else {
                        painterResource(drawable.ic_zoom_out)
                    },
                    contentDescription = "zoom",
                    tint = buttonTextColor,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = buttonBgColor)
                        .padding(8.dp),
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            IconButton(onClick = {
                coroutineScope.launch {
                    val zoomable = zoomState.zoomable
                    zoomable.rotate(zoomable.transform.rotation.roundToInt() + 90)
                }
            }) {
                Icon(
                    painter = painterResource(drawable.ic_rotate_right),
                    contentDescription = "right rotate",
                    tint = buttonTextColor,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = buttonBgColor)
                        .padding(8.dp),
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            IconButton(onClick = {
                val imageResult = imageState.result
                if (imageResult != null) {
                    photoInfoImageResult = imageResult
                }
            }) {
                Icon(
                    painter = painterResource(drawable.ic_info_baseline),
                    contentDescription = "info",
                    tint = buttonTextColor,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = buttonBgColor)
                        .padding(8.dp),
                )
            }
        }

        LoadState(
            modifier = Modifier.align(Alignment.Center),
            imageState = imageState
        )
    }

    if (photoInfoImageResult != null) {
        PhotoInfoDialog(photoInfoImageResult) {
            photoInfoImageResult = null
        }
    }
}