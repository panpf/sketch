package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.data.builtinImages
import com.github.panpf.sketch.sample.ui.model.Photo

actual class LocalPhotoListViewModel actual constructor(val sketch: Sketch) : ViewModel() {

    val photoPaging = SimplePaging(
        scope = viewModelScope,
        initialPageStart = 0,
        pageSize = 80,
        loadData = { pageStart: Int, _: Int ->
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
        },
        calculateNextPageStart = { currentPageStart: Int, loadedPhotoSize: Int ->
            currentPageStart + loadedPhotoSize
        },
    )
}