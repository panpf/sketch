@file:OptIn(ExperimentalResourceApi::class)

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
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.compose.SubcomposeAsyncImage
import com.github.panpf.sketch.compose.ability.dataFromLogo
import com.github.panpf.sketch.compose.ability.mimeTypeLogo
import com.github.panpf.sketch.compose.ability.progressIndicator
import com.github.panpf.sketch.compose.rememberAsyncImagePainter
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.compose.stateimage.rememberIconPainterStateImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.util.rememberMimeTypeLogoMap
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.sample.util.ifLet
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import sketch_4.`0`.sample.generated.resources.Res.drawable
import sketch_4.`0`.sample.generated.resources.ic_error_baseline
import sketch_4.`0`.sample.generated.resources.ic_image_outline
import sketch_4.`0`.sample.generated.resources.ic_signal_cellular


@Composable
fun PhotoGridItem(
    index: Int,
    photo: Photo,
    @Suppress("UNUSED_PARAMETER") animatedPlaceholder: Boolean = false,
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

    val listSettings by appSettingsService.listsCombinedFlow.collectAsState(Unit)
    val colorScheme = MaterialTheme.colorScheme
    val placeholderStateImage = rememberIconPainterStateImage(
        icon = painterResource(drawable.ic_image_outline),
        background = colorScheme.primaryContainer,
        iconTint = colorScheme.onPrimaryContainer
    )
    val errorStateImage = rememberIconPainterStateImage(
        icon = painterResource(drawable.ic_error_baseline),
        background = colorScheme.primaryContainer,
        iconTint = colorScheme.onPrimaryContainer
    )
    val saveCellularTrafficStateImage = rememberIconPainterStateImage(
        icon = painterResource(drawable.ic_signal_cellular),
        background = colorScheme.primaryContainer,
        iconTint = colorScheme.onPrimaryContainer
    )
    val request = remember(photo.listThumbnailUrl, listSettings) {
        ImageRequest(context, photo.listThumbnailUrl) {
//                if (animatedPlaceholder) {   // TODO animatedPlaceholder
//                    placeholder(
//                        IconAnimatableStateImage(drawable.ic_placeholder_eclipse_animated) {
//                            resColorBackground(color.placeholder_bg)
//                        }
//                    )
//                } else {
            placeholder(placeholderStateImage)
//                }
            error(errorStateImage) {
                saveCellularTrafficError(saveCellularTrafficStateImage)
            }
            crossfade()
            resizeOnDraw()
            sizeMultiplier(2f)  // To get a clearer thumbnail
            merge(appSettingsService.buildListImageOptions())
        }
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