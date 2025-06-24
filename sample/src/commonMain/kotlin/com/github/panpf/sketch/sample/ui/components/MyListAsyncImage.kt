package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.SubcomposeAsyncImage
import com.github.panpf.sketch.ability.dataFromLogo
import com.github.panpf.sketch.ability.mimeTypeLogo
import com.github.panpf.sketch.ability.progressIndicator
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.rememberAsyncImagePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.disallowAnimatedImage
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.buildScale
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image_broken_outline
import com.github.panpf.sketch.sample.resources.ic_image_outline
import com.github.panpf.sketch.sample.resources.ic_signal_cellular
import com.github.panpf.sketch.sample.ui.gallery.PhotoInfo
import com.github.panpf.sketch.sample.ui.util.rememberMimeTypeLogoMap
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.sample.util.ifLet
import com.github.panpf.sketch.state.ComposableConditionStateImage
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.state.saveCellularTrafficError
import org.koin.compose.koinInject

@Composable
fun MyListAsyncImage(
    uri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    animatedPlaceholder: Boolean = false,
    onClick: () -> Unit
) {
    val appSettings: AppSettings = koinInject()
    val contentScale by appSettings.listContentScale.collectAsState()
    val alignment by appSettings.listAlignment.collectAsState()
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
        contentScale = contentScale,
        alignment = alignment,
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
    val appSettings: AppSettings = koinInject()
    val contentScale by appSettings.listContentScale.collectAsState()
    val alignment by appSettings.listAlignment.collectAsState()
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
        contentScale = contentScale,
        alignment = alignment,
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
    val appSettings: AppSettings = koinInject()
    val contentScale by appSettings.listContentScale.collectAsState()
    val alignment by appSettings.listAlignment.collectAsState()
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
            contentScale = contentScale,
            alignment = alignment
        ),
        modifier = modifier1,
        contentScale = contentScale,
        alignment = alignment,
        contentDescription = contentDescription
    )
    MyDialog(infoDialogState) {
        PhotoInfo(imageState.result)
    }
}

@Composable
private fun Modifier.buildListImageModifier(imageState: AsyncImageState): Modifier {
    val appSettings: AppSettings = koinInject()
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
    val appSettings: AppSettings = koinInject()
    val colorScheme = MaterialTheme.colorScheme
    return ComposableImageRequest(uri) {
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

        val precision by appSettings.precision.collectAsState()
        precision(precision)

        val scaleName by appSettings.scaleName.collectAsState()
        val longImageScale by appSettings.longImageScale.collectAsState()
        val otherImageScale by appSettings.otherImageScale.collectAsState()
        val scale = remember(scaleName, longImageScale, otherImageScale) {
            buildScale(scaleName, longImageScale, otherImageScale)
        }
//        val scale by appSettings.scale.collectAsState()   // stateCombine will cause UI lag
        scale(scale)

        val repeatCount by appSettings.repeatCount.collectAsState()
        repeatCount(repeatCount)

        val pauseLoadWhenScroll by appSettings.pauseLoadWhenScrollInList.collectAsState()
        pauseLoadWhenScrolling(pauseLoadWhenScroll)

        val saveCellularTraffic by appSettings.saveCellularTrafficInList.collectAsState()
        saveCellularTraffic(saveCellularTraffic)

        val disallowAnimatedImage by appSettings.disallowAnimatedImageInList.collectAsState()
        disallowAnimatedImage(disallowAnimatedImage)

        val placeholderStateImage = if (animatedPlaceholder) {
            val density = LocalDensity.current
            val iconPainter = remember {
                val sizePx = with(density) { 24.dp.toPx() }
                val size = Size(sizePx, sizePx)
                NewMoonLoadingPainter(size).asEquitable("NewMoonLoadingPainter")
            }
            rememberIconAnimatablePainterStateImage(
                icon = iconPainter,
                background = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer
            )
        } else {
            rememberIconPainterStateImage(
                icon = Res.drawable.ic_image_outline,
                background = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer
            )
        }
        placeholder(placeholderStateImage)

        error(
            ComposableConditionStateImage(
                defaultImage = rememberIconPainterStateImage(
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
        )

        crossfade()

        val resizeOnDrawEnabled by appSettings.resizeOnDrawEnabled.collectAsState()
        resizeOnDraw(resizeOnDrawEnabled)

        sizeMultiplier(2f)  // To get a clearer thumbnail

        val platformAsyncImageSettings = composablePlatformAsyncImageSettings(appSettings)
        merge(platformAsyncImageSettings)
    }
}