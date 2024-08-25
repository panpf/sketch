package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.AsyncImageState
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
import com.github.panpf.sketch.request.composableError
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image_broken_outline
import com.github.panpf.sketch.sample.resources.ic_image_outline
import com.github.panpf.sketch.sample.resources.ic_signal_cellular
import com.github.panpf.sketch.sample.ui.gallery.PhotoInfo
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.util.rememberMimeTypeLogoMap
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.sample.util.ifLet
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.state.saveCellularTrafficError


@Composable
fun MyListAsyncImage(
    photo: Photo,
    animatedPlaceholder: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val infoDialogState = rememberMyDialogState()
    val imageState = rememberAsyncImageState()
    val modifier1 = modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick() },
                onLongPress = { infoDialogState.show() }
            )
        }.buildListImageModifier(imageState)
    val request = buildListImageRequest(photo, animatedPlaceholder)
    AsyncImage(
        request = request,
        state = imageState,
        modifier = modifier1,
        contentScale = ContentScale.Crop,
        contentDescription = "photo",
    )
    MyDialog(infoDialogState) {
        PhotoInfo(imageState.result)
    }
}

@Composable
fun MyListSubcomposeAsyncImage(
    photo: Photo,
    animatedPlaceholder: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val infoDialogState = rememberMyDialogState()
    val imageState = rememberAsyncImageState()
    val modifier1 = modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick() },
                onLongPress = { infoDialogState.show() }
            )
        }.buildListImageModifier(imageState)
    val request = buildListImageRequest(photo, animatedPlaceholder)
    SubcomposeAsyncImage(
        request = request,
        state = imageState,
        modifier = modifier1,
        contentScale = ContentScale.Crop,
        contentDescription = "photo",
    )
    MyDialog(infoDialogState) {
        PhotoInfo(imageState.result)
    }
}

@Composable
fun MyListAsyncImagePainterImage(
    photo: Photo,
    animatedPlaceholder: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val infoDialogState = rememberMyDialogState()
    val imageState = rememberAsyncImageState()
    val modifier1 = modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick() },
                onLongPress = { infoDialogState.show() }
            )
        }.buildListImageModifier(imageState)
    val request = buildListImageRequest(photo, animatedPlaceholder)
    Image(
        painter = rememberAsyncImagePainter(
            request = request,
            state = imageState,
            contentScale = ContentScale.Crop
        ),
        modifier = modifier1,
        contentScale = ContentScale.Crop,
        contentDescription = "photo"
    )
    MyDialog(infoDialogState) {
        PhotoInfo(imageState.result)
    }
}

@Composable
private fun Modifier.buildListImageModifier(imageState: AsyncImageState): Modifier {
    val context = LocalPlatformContext.current
    val appSettings = context.appSettings
    val mimeTypeLogoMap = rememberMimeTypeLogoMap()
    val progressPainter = rememberThemeSectorProgressPainter(hiddenWhenIndeterminate = true)
    val showDataFromLogo by appSettings.showDataFromLogoInList.collectAsState()
    val showMimeTypeLogo by appSettings.showMimeTypeLogoInList.collectAsState()
    val showProgressIndicator by appSettings.showProgressIndicatorInList.collectAsState()
    return this
        .ifLet(showDataFromLogo) {
            it.dataFromLogo(imageState)
        }
        .ifLet(showMimeTypeLogo) {
            it.mimeTypeLogo(imageState, mimeTypeLogoMap, margin = 4.dp)
        }
        .ifLet(showProgressIndicator) {
            it.progressIndicator(imageState, progressPainter)
        }
}

@Composable
private fun buildListImageRequest(
    photo: Photo,
    animatedPlaceholder: Boolean
): ImageRequest {
    val context = LocalPlatformContext.current
    val appSettings = context.appSettings
    val colorScheme = MaterialTheme.colorScheme
    val memoryCacheEnabled by appSettings.memoryCache.collectAsState()
    val resultCacheEnabled by appSettings.resultCache.collectAsState()
    val downloadCacheEnabled by appSettings.downloadCache.collectAsState()
    val precision by appSettings.precision.collectAsState()
    val scale by appSettings.scale.collectAsState()
    val longImageScale by appSettings.longImageScale.collectAsState()
    val otherImageScale by appSettings.otherImageScale.collectAsState()
    val pauseLoadWhenScroll by appSettings.pauseLoadWhenScrollInList.collectAsState()
    val saveCellularTraffic by appSettings.saveCellularTrafficInList.collectAsState()
    val disallowAnimatedImage by appSettings.disallowAnimatedImageInList.collectAsState()
    return ComposableImageRequest(photo.listThumbnailUrl) {
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
                icon = Res.drawable.ic_image_broken_outline,
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
        platformListImageRequest(appSettings)
    }
}

@Composable
expect fun rememberAnimatedPlaceholderStateImage(context: PlatformContext): StateImage?

@Composable
expect inline fun ImageRequest.Builder.platformListImageRequest(appSettings: AppSettings)