package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.sample.data.api.Response
import com.github.panpf.sketch.sample.data.api.pexels.PexelsApi
import com.github.panpf.sketch.sample.data.api.pexels.PexelsPhoto
import com.github.panpf.sketch.sample.ui.model.Photo

actual class PexelsPhotoListViewModel actual constructor(val pexelsApi: PexelsApi) : ViewModel() {

    val photoPaging = SimplePaging(
        scope = viewModelScope,
        initialPageStart = 1,
        pageSize = 80,
        loadData = { pageStart: Int, pageSize: Int ->
            pexelsApi.curated(pageStart, pageSize).let { response ->
                if (response is Response.Success) {
                    response.body.photos.map { it.toPhoto() }
                } else {
                    emptyList()
                }
            }
        },
        calculateNextPageStart = { currentPageStart: Int, _: Int ->
            currentPageStart + 1
        },
    )

    private fun PexelsPhoto.toPhoto(): Photo = Photo(
        originalUrl = src.original,
        mediumUrl = src.large,
        thumbnailUrl = src.medium,
        width = width,
        height = height,
    )
}