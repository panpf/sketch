package com.github.panpf.sketch.sample.ui.photo.pexels

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.findNavController
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.SubcomposeAsyncImage
import com.github.panpf.sketch.compose.rememberAsyncImagePainter
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.common.createMimeTypeLogoMap
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sample.util.letIf
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import com.google.accompanist.drawablepainter.DrawablePainter

@Composable
fun PhotoGridItem(
    index: Int,
    photo: Photo,
    animatedPlaceholder: Boolean = false,
    staggeredGridMode: Boolean = false,
    onClick: (photo: Photo, index: Int) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val imageState = rememberAsyncImageState()
    val mimeTypeLogoMap =
        remember { createMimeTypeLogoMap().mapValues { DrawablePainter(it.value) } }
    val progressPainter = rememberDrawableProgressPainter(remember {
        SectorProgressDrawable(hiddenWhenIndeterminate = true)
    })
    val appSettingsService = context.appSettingsService
    val showDataFromLogo by appSettingsService.showDataFromLogo.stateFlow.collectAsState()
    val showMimeTypeLogo by appSettingsService.showMimeTypeLogoInLIst.stateFlow.collectAsState()
    val showProgressIndicator by appSettingsService.showProgressIndicatorInList.stateFlow.collectAsState()
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
                    val displayResult = imageState.result
                    if (displayResult != null) {
                        view
                            .findNavController()
                            .navigate(ImageInfoDialogFragment.createNavDirections(displayResult))
                    }
                }
            )
        }
        .letIf(showDataFromLogo) {
            it.dataFromLogo(imageState)
        }
        .letIf(showMimeTypeLogo) {
            it.mimeTypeLogo(imageState, mimeTypeLogoMap, margin = 4.dp)
        }
        .letIf(showProgressIndicator) {
            it.progressIndicator(imageState, progressPainter)
        }

    val listSettings by appSettingsService.listsCombinedFlow.collectAsState(Unit)
    val request = remember(photo.listThumbnailUrl, listSettings) {
        DisplayRequest(context, photo.listThumbnailUrl) {
            if (animatedPlaceholder) {
                placeholder(drawable.ic_placeholder_eclipse_animated)
            } else {
                placeholder(
                    IconStateImage(
                        drawable.ic_image_outline,
                        ResColor(color.placeholder_bg)
                    )
                )
            }
            error(IconStateImage(drawable.ic_error, ResColor(color.placeholder_bg))) {
                saveCellularTrafficError(
                    IconStateImage(drawable.ic_signal_cellular, ResColor(color.placeholder_bg))
                )
            }
            crossfade()
            resizeApplyToDrawable()
            merge(appSettingsService.buildListImageOptions())
        }
    }
    when (index % 3) {
        0 -> {
            AsyncImage(
                request = request,
                sketch = context.sketch,
                state = imageState,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo",
            )
        }

        1 -> {
            SubcomposeAsyncImage(
                request = request,
                sketch = context.sketch,
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
                    sketch = context.sketch,
                    state = imageState,
                    contentScale = ContentScale.Crop
                ),
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo"
            )
        }
    }
}