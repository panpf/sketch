package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.sample.data.api.Response
import com.github.panpf.sketch.sample.data.api.giphy.GiphyApi
import com.github.panpf.sketch.sample.data.api.giphy.GiphyGif
import com.github.panpf.sketch.sample.ui.model.Photo

actual class GiphyPhotoListViewModel actual constructor(val giphyApi: GiphyApi) : ViewModel() {

    val photoPaging = SimplePaging(
        scope = viewModelScope,
        initialPageStart = 1,
        pageSize = 80,
        loadData = { pageStart: Int, pageSize: Int ->
            giphyApi.trending(pageStart, pageSize).let { response ->
                if (response is Response.Success) {
                    response.body.dataList?.map { it.toPhoto() } ?: emptyList()
                } else {
                    emptyList()
                }
            }
        },
        calculateNextPageStart = { currentPageStart: Int, loadedPhotoSize: Int ->
            currentPageStart + loadedPhotoSize
        },
    )

    private fun GiphyGif.toPhoto(): Photo = Photo(
        originalUrl = images.original.downloadUrl,
        mediumUrl = images.original.downloadUrl,
        thumbnailUrl = images.fixedWidth.downloadUrl,
        width = images.original.width.toInt(),
        height = images.original.height.toInt(),
    )
}