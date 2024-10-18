package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.data.api.Apis
import com.github.panpf.sketch.sample.data.api.Response
import com.github.panpf.sketch.sample.data.api.giphy.GiphyGif
import com.github.panpf.sketch.sample.ui.model.Photo


@Composable
actual fun GiphyPhotoListPage(screen: Screen) {
    val navigator = LocalNavigator.current!!
    PhotoList(
        animatedPlaceholder = true,
        initialPageStart = 0,
        pageSize = 80,
        load = { pageStart: Int, pageSize: Int ->
            Apis.giphyApi.trending(pageStart, pageSize).let { response ->
                when (response) {
                    is Response.Success -> {
                        Result.success(response.body.dataList?.map { it.toPhoto() } ?: emptyList())
                    }

                    is Response.Error -> {
                        Result.failure(response.throwable!!)
                    }

                    else -> {
                        throw IllegalStateException("Unsupported response: $response")
                    }
                }
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

private fun GiphyGif.toPhoto(): Photo = Photo(
    originalUrl = images.original.downloadUrl,
    mediumUrl = images.original.downloadUrl,
    thumbnailUrl = images.fixedWidth.downloadUrl,
    width = images.original.width.toInt(),
    height = images.original.height.toInt(),
)