package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.data.api.Apis
import com.github.panpf.sketch.sample.data.api.Response.Success
import com.github.panpf.sketch.sample.data.api.pexels.PexelsPhoto
import com.github.panpf.sketch.sample.ui.model.Photo

@Composable
actual fun Screen.PexelsPhotoPage() {
    val navigator = LocalNavigator.current!!
    PhotoList(
        animatedPlaceholder = false,
        initialPageStart = 1,
        pageSize = 80,
        load = { pageStart: Int, pageSize: Int ->
            Apis.pexelsApi.curated(pageStart, pageSize).let { response ->
                if (response is Success) {
                    response.body.photos.map { it.toPhoto() }
                } else {
                    emptyList()
                }
            }
        },
        calculateNextPageStart = { currentPageStart: Int, _: Int ->
            currentPageStart + 1
        },
        gridCellsMinSize = 150.dp,
        onClick = { photos1, _, index ->
            val params = buildPhotoPagerParams(photos1, index)
            navigator.push(PhotoPagerScreen(params))
        }
    )
}

private fun PexelsPhoto.toPhoto(): Photo = Photo(
    originalUrl = src.original,
    mediumUrl = src.large,
    thumbnailUrl = src.medium,
    width = width,
    height = height,
    exifOrientation = 0,
)