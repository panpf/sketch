package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SubcomposeAsyncImage
import com.github.panpf.sketch.ability.dataFromLogo
import com.github.panpf.sketch.ability.mimeTypeLogo
import com.github.panpf.sketch.ability.progressIndicator
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.rememberAsyncImagePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.composableError
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image_outline
import com.github.panpf.sketch.sample.resources.ic_image_outline_broken
import com.github.panpf.sketch.sample.resources.ic_signal_cellular
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.util.rememberMimeTypeLogoMap
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.sample.util.ifLet
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.state.saveCellularTrafficError

@Composable
expect fun rememberAnimatedPlaceholderStateImage(context: PlatformContext): StateImage?

@Composable
fun PhotoGridItem(
    index: Int,
    photo: Photo,
    animatedPlaceholder: Boolean = false,
    staggeredGridMode: Boolean = false,
    onClick: (photo: Photo, index: Int) -> Unit,
) {
    var photoInfoImageResult by remember { mutableStateOf<ImageResult?>(null) }

    val context = LocalPlatformContext.current
    val imageState = rememberAsyncImageState()
    val mimeTypeLogoMap = rememberMimeTypeLogoMap()
    val progressPainter = rememberThemeSectorProgressPainter(hiddenWhenIndeterminate = true)
    val appSettingsService = context.appSettings
    val showDataFromLogo by appSettingsService.showDataFromLogoInList.collectAsState()
    val showMimeTypeLogo by appSettingsService.showMimeTypeLogoInList.collectAsState()
    val showProgressIndicator by appSettingsService.showProgressIndicatorInList.collectAsState()
    val modifier = Modifier
        .fillMaxWidth()
        .let {
            val photoWidth = photo.width ?: 0
            val photoHeight = photo.height ?: 0
            if (staggeredGridMode && photoWidth > 0 && photoHeight > 0) {
                it.aspectRatio(photoWidth.toFloat() / photoHeight)
            } else {
                it.aspectRatio(1f)
            }
        }
        .pointerInput(photo, index) {
            detectTapGestures(
                onTap = { onClick(photo, index) },
                onLongPress = {
                    val imageResult = imageState.result
                    if (imageResult != null) {
                        photoInfoImageResult = imageResult
                    }
                }
            )
        }
        .ifLet(showDataFromLogo) {
            it.dataFromLogo(imageState)
        }
        .ifLet(showMimeTypeLogo) {
            it.mimeTypeLogo(imageState, mimeTypeLogoMap, margin = 4.dp)
        }
        .ifLet(showProgressIndicator) {
            it.progressIndicator(imageState, progressPainter)
        }

    val colorScheme = MaterialTheme.colorScheme
    val memoryCacheEnabled by appSettingsService.memoryCache.collectAsState()
    val resultCacheEnabled by appSettingsService.resultCache.collectAsState()
    val downloadCacheEnabled by appSettingsService.downloadCache.collectAsState()
    val precision by appSettingsService.precision.collectAsState()
    val scale by appSettingsService.scale.collectAsState()
    val longImageScale by appSettingsService.longImageScale.collectAsState()
    val otherImageScale by appSettingsService.otherImageScale.collectAsState()
    val pauseLoadWhenScroll by appSettingsService.pauseLoadWhenScrollInList.collectAsState()
    val saveCellularTraffic by appSettingsService.saveCellularTrafficInList.collectAsState()
    val disallowAnimatedImage by appSettingsService.disallowAnimatedImageInList.collectAsState()
    val request = ComposableImageRequest(photo.listThumbnailUrl) {
        memoryCachePolicy(if (memoryCacheEnabled) ENABLED else DISABLED)
        resultCachePolicy(if (resultCacheEnabled) ENABLED else DISABLED)
        downloadCachePolicy(if (downloadCacheEnabled) ENABLED else DISABLED)
        precision(AppSettings.precision(precision))
        scale(AppSettings.scale(scale, longImageScale, otherImageScale))
        pauseLoadWhenScrolling(pauseLoadWhenScroll)
        saveCellularTraffic(saveCellularTraffic)
        disallowAnimatedImage(disallowAnimatedImage)

        val animatedPlaceholderStateImage =
            if (animatedPlaceholder) rememberAnimatedPlaceholderStateImage(context) else null
        val placeholderStateImage = animatedPlaceholderStateImage ?: rememberIconPainterStateImage(
            icon = Res.drawable.ic_image_outline,
            background = colorScheme.primaryContainer,
            iconTint = colorScheme.onPrimaryContainer
        )
        placeholder(placeholderStateImage)
        composableError(
            rememberIconPainterStateImage(
                icon = Res.drawable.ic_image_outline_broken,
                background = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer
            )
        ) {
            saveCellularTrafficError(
                rememberIconPainterStateImage(
                    icon = Res.drawable.ic_signal_cellular,
                    background = colorScheme.primaryContainer,
                    iconTint = colorScheme.onPrimaryContainer
                )
            )
        }
        crossfade()
        resizeOnDraw()
        sizeMultiplier(2f)  // To get a clearer thumbnail
        PlatformListImageSettings(appSettingsService, this)
    }
    when (index % 3) {
        0 -> {
            AsyncImage(
                request = request,
                state = imageState,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo",
            )
        }

        1 -> {
            SubcomposeAsyncImage(
                request = request,
                state = imageState,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo",
            )
        }

        else -> {
            Image(
                painter = rememberAsyncImagePainter(
                    request = request,
                    state = imageState,
                    contentScale = ContentScale.Crop
                ),
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo"
            )
        }
    }

    if (photoInfoImageResult != null) {
        PhotoInfoDialog(photoInfoImageResult) {
            photoInfoImageResult = null
        }
    }
}

@Composable
expect inline fun PlatformListImageSettings(appSettings: AppSettings, builder: ImageRequest.Builder)