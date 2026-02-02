package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.panpf.sketch.sample.ui.components.MyListAsyncImage
import com.github.panpf.sketch.sample.ui.components.MyListAsyncImagePainterImage
import com.github.panpf.sketch.sample.ui.components.MyListSubcomposeAsyncImage
import com.github.panpf.sketch.sample.ui.model.Photo

@Composable
fun PhotoGridItem(
    index: Int,
    photo: Photo,
    animatedPlaceholder: Boolean = false,
    staggeredGridMode: Boolean = false,
    onClick: (photo: Photo, index: Int) -> Unit,
) {
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

    when (index % 3) {
        0 -> MyListAsyncImage(
            uri = photo.listThumbnailUrl,
            contentDescription = "photo",
            modifier = modifier,
            animatedPlaceholder = animatedPlaceholder,
            onClick = { onClick(photo, index) }
        )

        1 -> MyListSubcomposeAsyncImage(
            uri = photo.listThumbnailUrl,
            contentDescription = "photo",
            modifier = modifier,
            animatedPlaceholder = animatedPlaceholder,
            onClick = { onClick(photo, index) }
        )

        else -> MyListAsyncImagePainterImage(
            uri = photo.listThumbnailUrl,
            contentDescription = "photo",
            modifier = modifier,
            animatedPlaceholder = animatedPlaceholder,
            onClick = { onClick(photo, index) }
        )
    }
}