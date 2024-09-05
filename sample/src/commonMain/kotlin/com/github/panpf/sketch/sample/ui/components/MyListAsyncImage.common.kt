package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.github.panpf.sketch.rememberAsyncImagePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.composableError
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.buildScale
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image_broken_outline
import com.github.panpf.sketch.sample.resources.ic_image_outline
import com.github.panpf.sketch.sample.resources.ic_signal_cellular
import com.github.panpf.sketch.sample.ui.gallery.PhotoInfo
import com.github.panpf.sketch.sample.ui.util.rememberMimeTypeLogoMap
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.sample.util.ifLet
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.state.saveCellularTrafficError


@Composable
fun MyListAsyncImage(
    uri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    animatedPlaceholder: Boolean = false,
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
    val request = buildListImageRequest(uri, animatedPlaceholder)
    AsyncImage(
        request = request,
        state = imageState,
        modifier = modifier1,
        contentScale = ContentScale.Crop,
        contentDescription = contentDescription
    )
    MyDialog(infoDialogState) {
        PhotoInfo(imageState.result)
    }
}

@Composable
fun MyListSubcomposeAsyncImage(
    uri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    animatedPlaceholder: Boolean = false,
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
    val request = buildListImageRequest(uri, animatedPlaceholder)
    SubcomposeAsyncImage(
        request = request,
        state = imageState,
        modifier = modifier1,
        contentScale = ContentScale.Crop,
        contentDescription = contentDescription
    )
    MyDialog(infoDialogState) {
        PhotoInfo(imageState.result)
    }
}

@Composable
fun MyListAsyncImagePainterImage(
    uri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    animatedPlaceholder: Boolean = false,
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
    val request = buildListImageRequest(uri, animatedPlaceholder)
    Image(
        painter = rememberAsyncImagePainter(
            request = request,
            state = imageState,
            contentScale = ContentScale.Crop
        ),
        modifier = modifier1,
        contentScale = ContentScale.Crop,
        contentDescription = contentDescription
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
    uri: String,
    animatedPlaceholder: Boolean
): ImageRequest {
    val context = LocalPlatformContext.current
    val appSettings = context.appSettings
    val colorScheme = MaterialTheme.colorScheme
    return ComposableImageRequest(uri) {
        val memoryCache by appSettings.memoryCache.collectAsState()
        memoryCachePolicy(memoryCache)

        val resultCache by appSettings.resultCache.collectAsState()
        resultCachePolicy(resultCache)

        val downloadCache by appSettings.downloadCache.collectAsState()
        downloadCachePolicy(downloadCache)

        val precision by appSettings.precision.collectAsState()
        precision(precision)

        val scaleName by appSettings.scaleName.collectAsState()
        val longImageScale by appSettings.longImageScale.collectAsState()
        val otherImageScale by appSettings.otherImageScale.collectAsState()
        val scale = remember(scaleName, longImageScale, otherImageScale) {
            buildScale(scaleName, longImageScale, otherImageScale)
        }
//        val scale by appSettings.scale.collectAsState()   // TODO Will cause lag
        scale(scale)

        val pauseLoadWhenScroll by appSettings.pauseLoadWhenScrollInList.collectAsState()
        pauseLoadWhenScrolling(pauseLoadWhenScroll)

        val saveCellularTraffic by appSettings.saveCellularTrafficInList.collectAsState()
        saveCellularTraffic(saveCellularTraffic)

        val disallowAnimatedImage by appSettings.disallowAnimatedImageInList.collectAsState()
        disallowAnimatedImage(disallowAnimatedImage)

        val animatedPlaceholderStateImage =
            if (animatedPlaceholder) rememberAnimatedPlaceholderStateImage(context) else null
        val placeholderStateImage = animatedPlaceholderStateImage
            ?: rememberIconPainterStateImage(
                icon = Res.drawable.ic_image_outline,
                background = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer
            )
        placeholder(placeholderStateImage)

        val errorStateImage = rememberIconPainterStateImage(
            icon = Res.drawable.ic_image_broken_outline,
            background = colorScheme.primaryContainer,
            iconTint = colorScheme.onPrimaryContainer
        )
        val saveCellularTrafficErrorStateImage = rememberIconPainterStateImage(
            icon = Res.drawable.ic_signal_cellular,
            background = colorScheme.primaryContainer,
            iconTint = colorScheme.onPrimaryContainer
        )
        composableError(errorStateImage) {
            saveCellularTrafficError(saveCellularTrafficErrorStateImage)
        }

        crossfade()
        resizeOnDraw()

        sizeMultiplier(2f)  // To get a clearer thumbnail

        val platformAsyncImageSettings = composablePlatformAsyncImageSettings(appSettings)
        merge(platformAsyncImageSettings)
    }
}

@Composable
expect fun rememberAnimatedPlaceholderStateImage(context: PlatformContext): StateImage?