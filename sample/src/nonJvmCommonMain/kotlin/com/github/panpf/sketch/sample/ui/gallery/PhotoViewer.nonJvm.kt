package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.util.valueOf
import com.github.panpf.sketch.stateimage.ThumbnailMemoryCacheStateImage

@Composable
actual fun PhotoViewer(
    photo: Photo,
    buttonBgColorState: MutableState<Color>,
) {
    val context = LocalPlatformContext.current
    val appSettings = context.appSettings
    val showOriginImage by appSettings.showOriginImage.collectAsState()
    val contentScaleName by appSettings.contentScale.collectAsState()
    val alignmentName by appSettings.alignment.collectAsState()
    val contentScale by remember {
        derivedStateOf {
            ContentScale.valueOf(contentScaleName)
        }
    }
    val alignment by remember {
        derivedStateOf {
            Alignment.valueOf(alignmentName)
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
    val viewerSettings by appSettings.viewersCombinedFlow.collectAsState(Unit)
    val request = remember(imageUri, viewerSettings) {
        ImageRequest(context, imageUri) {
            merge(appSettings.buildViewerImageOptions())
            placeholder(ThumbnailMemoryCacheStateImage(photo.thumbnailUrl))
            crossfade(fadeStart = false)
        }
    }
    // TODO ZoomAsyncImage
    AsyncImage(
        request = request,
        sketch = SingletonSketch.get(context),
        contentDescription = "view image",
        modifier = Modifier.fillMaxSize(),
        contentScale = contentScale,
        alignment = alignment,
    )
}