package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.data.builtinImages
import com.github.panpf.sketch.sample.ui.gridCellsMinSize
import com.github.panpf.sketch.sample.ui.model.Photo

actual fun localPhotoListPermission(): Any? = null

@Composable
actual fun LocalPhotoListPage() {
    val navigator = LocalNavigator.current!!
    PhotoList(
        animatedPlaceholder = true,
        initialPageStart = 0,
        pageSize = 80,
        load = { pageStart: Int, _: Int ->
            Result.success(
                if (pageStart == 0) {
                    builtinImages().map {
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
            )
        },
        calculateNextPageStart = { currentPageStart: Int, loadedPhotoSize: Int ->
            currentPageStart + loadedPhotoSize
        },
        gridCellsMinSize = gridCellsMinSize,
        onClick = { photos1, _, index ->
            val params = buildPhotoPagerParams(photos1, index)
            navigator.push(PhotoPagerScreen(params))
        }
    )
}