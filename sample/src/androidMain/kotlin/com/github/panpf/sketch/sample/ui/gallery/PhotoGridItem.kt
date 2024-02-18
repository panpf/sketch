//package com.github.panpf.sketch.sample.ui.gallery
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalView
//import androidx.compose.ui.unit.dp
//import androidx.navigation.findNavController
//import com.github.panpf.sketch.compose.AsyncImage
//import com.github.panpf.sketch.compose.SubcomposeAsyncImage
//import com.github.panpf.sketch.compose.ability.dataFromLogo
//import com.github.panpf.sketch.compose.ability.mimeTypeLogo
//import com.github.panpf.sketch.compose.ability.progressIndicator
//import com.github.panpf.sketch.compose.rememberAsyncImagePainter
//import com.github.panpf.sketch.compose.rememberAsyncImageState
//import com.github.panpf.sketch.request.ImageRequest
//import com.github.panpf.sketch.sample.R.color
//import com.github.panpf.sketch.sample.R.drawable
//import com.github.panpf.sketch.sample.appSettingsService
//import com.github.panpf.sketch.sample.ui.model.Photo
//import com.github.panpf.sketch.sample.ui.util.createMimeTypeLogoMap
//import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
//import com.github.panpf.sketch.sample.util.letIf
//import com.github.panpf.sketch.stateimage.AnimatableIconStateImage
//import com.github.panpf.sketch.stateimage.IconStateImage
//import com.github.panpf.sketch.stateimage.saveCellularTrafficError
//import com.google.accompanist.drawablepainter.DrawablePainter
//
//@Composable
//fun PhotoGridItem(
//    index: Int,
//    photo: Photo,
//    animatedPlaceholder: Boolean = false,
//    staggeredGridMode: Boolean = false,
//    onClick: (photo: Photo, index: Int) -> Unit
//) {
//    val context = LocalContext.current
//    val view = LocalView.current
//    val imageState = rememberAsyncImageState()
//    val mimeTypeLogoMap =
//        remember { createMimeTypeLogoMap().mapValues { DrawablePainter(it.value) } }
//    val progressPainter = rememberThemeSectorProgressPainter(hiddenWhenIndeterminate = true)
//    val appSettingsService = context.appSettingsService
//    val showDataFromLogo by appSettingsService.showDataFromLogo.collectAsState()
//    val showMimeTypeLogo by appSettingsService.showMimeTypeLogoInLIst.collectAsState()
//    val showProgressIndicator by appSettingsService.showProgressIndicatorInList.collectAsState()
//    val modifier = Modifier
//        .fillMaxWidth()
//        .let {
//            val photoWidth = photo.width ?: 0
//            val photoHeight = photo.height ?: 0
//            if (staggeredGridMode && photoWidth > 0 && photoHeight > 0) {
//                it.aspectRatio(photoWidth.toFloat() / photoHeight)
//            } else {
//                it.aspectRatio(1f)
//            }
//        }
//        .pointerInput(photo, index) {
//            detectTapGestures(
//                onTap = { onClick(photo, index) },
//                onLongPress = {
//                    val displayResult = imageState.result
//                    if (displayResult != null) {
//                        view
//                            .findNavController()
//                            .navigate(PhotoInfoDialogFragment.createNavDirections(displayResult))
//                    }
//                }
//            )
//        }
//        .letIf(showDataFromLogo) {
//            it.dataFromLogo(imageState)
//        }
//        .letIf(showMimeTypeLogo) {
//            it.mimeTypeLogo(imageState, mimeTypeLogoMap, margin = 4.dp)
//        }
//        .letIf(showProgressIndicator) {
//            it.progressIndicator(imageState, progressPainter)
//        }
//
//    val listSettings by appSettingsService.listsCombinedFlow.collectAsState(Unit)
//    val request = remember(photo.listThumbnailUrl, listSettings) {
//        ImageRequest(context, photo.listThumbnailUrl) {
//            if (animatedPlaceholder) {
//                placeholder(
//                    AnimatableIconStateImage(drawable.ic_placeholder_eclipse_animated) {
//                        resColorBackground(color.placeholder_bg)
//                    }
//                )
//            } else {
//                placeholder(
//                    IconStateImage(drawable.ic_image_outline) {
//                        resColorBackground(color.placeholder_bg)
//                    }
//                )
//            }
//            error(
//                IconStateImage(drawable.ic_error_baseline) {
//                    resColorBackground(color.placeholder_bg)
//                }
//            ) {
//                saveCellularTrafficError(
//                    IconStateImage(drawable.ic_signal_cellular) {
//                        resColorBackground(color.placeholder_bg)
//                    }
//                )
//            }
//            crossfade()
//            resizeOnDraw()
//            merge(appSettingsService.buildListImageOptions())
//        }
//    }
//    when (index % 3) {
//        0 -> {
//            AsyncImage(
//                request = request,
//                state = imageState,
//                modifier = modifier,
//                contentScale = ContentScale.Crop,
//                contentDescription = "photo",
//            )
//        }
//
//        1 -> {
//            SubcomposeAsyncImage(
//                request = request,
//                state = imageState,
//                modifier = modifier,
//                contentScale = ContentScale.Crop,
//                contentDescription = "photo",
//            )
//        }
//
//        else -> {
//            Image(
//                painter = rememberAsyncImagePainter(
//                    request = request,
//                    state = imageState,
//                    contentScale = ContentScale.Crop
//                ),
//                modifier = modifier,
//                contentScale = ContentScale.Crop,
//                contentDescription = "photo"
//            )
//        }
//    }
//}