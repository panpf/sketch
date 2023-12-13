package com.github.panpf.sketch.sample.ui.photo.pexels

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.findNavController
import com.github.panpf.sketch.compose.rememberAsyncImagePainter
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sample.util.letIf
import com.github.panpf.sketch.sample.widget.TextDrawable
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import com.github.panpf.tools4a.dimen.ktx.dp2px
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
    val dataFromLogoState = rememberDataFromLogoState()
    val mimeTypeLogoState = rememberMimeTypeLogoState {
        val newLogoDrawable: (String) -> Drawable = {
            TextDrawable.builder()
                .beginConfig()
                .width(((it.length + 1) * 6).dp2px)
                .height(16.dp2px)
                .fontSize(9.dp2px)
                .bold()
                .textColor(Color.WHITE)
                .endConfig()
                .buildRoundRect(it, Color.parseColor("#88000000"), 10.dp2px)
        }
        mapOf(
            "image/gif" to DrawablePainter(newLogoDrawable("GIF")),
            "image/png" to DrawablePainter(newLogoDrawable("PNG")),
            "image/jpeg" to DrawablePainter(newLogoDrawable("JPEG")),
            "image/webp" to DrawablePainter(newLogoDrawable("WEBP")),
            "image/bmp" to DrawablePainter(newLogoDrawable("BMP")),
            "image/svg+xml" to DrawablePainter(newLogoDrawable("SVG")),
            "image/heic" to DrawablePainter(newLogoDrawable("HEIC")),
            "image/heif" to DrawablePainter(newLogoDrawable("HEIF")),
        )
    }
    val progressDrawable = remember { SectorProgressDrawable(hiddenWhenIndeterminate = true) }
    val drawableProgressPainter = rememberDrawableProgressPainter(progressDrawable)
    val progressIndicatorState = rememberProgressIndicatorState(drawableProgressPainter)
    val appSettingsService = context.appSettingsService
    val showDataFromLogo by appSettingsService.showDataFromLogo.stateFlow.collectAsState()
    val showMimeTypeLogo by appSettingsService.showMimeTypeLogoInLIst.stateFlow.collectAsState()
    val showProgressIndicator by appSettingsService.showProgressIndicatorInList.stateFlow.collectAsState()

    var displayResult: DisplayResult? by remember { mutableStateOf(null) }
    val view = LocalView.current

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
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick(photo, index) },
                onLongPress = {
                    val displayResult1 = displayResult
                    if (displayResult1 != null) {
                        view
                            .findNavController()
                            .navigate(ImageInfoDialogFragment.createNavDirections(displayResult1))
                    }
                }
            )
        }
        .letIf(showDataFromLogo) {
            it.dataFromLogo(dataFromLogoState)
        }
        .letIf(showMimeTypeLogo) {
            it.mimeTypeLogo(mimeTypeLogoState, margin = 4.dp)
        }
        .letIf(showProgressIndicator) {
            it.progressIndicator(progressIndicatorState)
        }

    val listSettings by appSettingsService.listsCombinedFlow.collectAsState(Unit)
    // listener 会导致两次创建的 DisplayRequest equals 为 false，从而引发重组，所以这里必须用 remember
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
            listener(
                onStart = { _ ->
                    dataFromLogoState.dataFrom = null
                    mimeTypeLogoState.mimeType = null
                    progressIndicatorState.progress = 0f
                },
                onSuccess = { _, result ->
                    dataFromLogoState.dataFrom = result.dataFrom
                    mimeTypeLogoState.mimeType = result.imageInfo.mimeType
                    progressIndicatorState.progress = 1f
                    displayResult = result
                },
                onError = { _, _ ->
                    dataFromLogoState.dataFrom = null
                    mimeTypeLogoState.mimeType = null
                    progressIndicatorState.progress = -1f
                    displayResult = null
                }
            )
            progressListener { _, totalLength: Long, completedLength: Long ->
                val progress = if (totalLength > 0) completedLength.toFloat() / totalLength else 0f
                progressIndicatorState.progress = progress
            }
        }
    }
    when (index % 3) {
        0 -> {
            com.github.panpf.sketch.compose.AsyncImage(
                request = request,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo",
            )
        }

        1 -> {
            com.github.panpf.sketch.compose.SubcomposeAsyncImage(
                request = request,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo",
            )
        }

        else -> {
            Image(
                painter = rememberAsyncImagePainter(
                    request = request,
                ),
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo"
            )
        }
    }
}