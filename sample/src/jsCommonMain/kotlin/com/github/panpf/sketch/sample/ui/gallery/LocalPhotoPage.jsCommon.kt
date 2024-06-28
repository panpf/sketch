package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.sample.ui.model.Photo


@Composable
actual fun Screen.LocalPhotoPage() {
    val navigator = LocalNavigator.current!!
    PhotoList(
        animatedPlaceholder = true,
        initialPageStart = 0,
        pageSize = 80,
        load = { pageStart: Int, _: Int ->
            if (pageStart == 0) {
                ResourceImages.statics
                    .plus(ResourceImages.anims)
                    .plus(ResourceImages.longQMSHT)
                    .plus(ResourceImages.clockExifs)
                    .plus(ResourceImages.mp4)
                    .map {
                        Photo(
                            originalUrl = it.uri,
                            mediumUrl = it.uri,
                            thumbnailUrl = it.uri,
                            width = it.size.width,
                            height = it.size.height,
                        )
                    }
            } else {
                emptyList()
            }
        },
        calculateNextPageStart = { currentPageStart: Int, loadedPhotoSize: Int ->
            currentPageStart + loadedPhotoSize
        },
        gridCellsMinSize = 150.dp,
        onClick = { photos1, _, index ->
            val params = buildPhotoPagerParams(photos1, index)
            navigator.push(PhotoPagerScreen(params))
        }
    )
}