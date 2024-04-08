package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.images.MyImages
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
                MyImages.statics
                    .plus(MyImages.anims)
                    .plus(MyImages.longQMSHT)
                    .plus(MyImages.clockExifs)
                    .plus(MyImages.mp4)
                    .map {
                        Photo(
                            originalUrl = it.uri,
                            mediumUrl = it.uri,
                            thumbnailUrl = it.uri,
                            width = it.size.width,
                            height = it.size.height,
                            exifOrientation = it.exifOrientation,
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