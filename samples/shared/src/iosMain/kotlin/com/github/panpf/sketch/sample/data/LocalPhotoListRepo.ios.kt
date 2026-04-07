package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.ui.model.Photo

actual class GalleryLocalPhotoListRepo actual constructor(val sketch: Sketch) : LocalPhotoListRepo {

    actual override suspend fun loadLocalPhotoList(
        pageStart: Int,
        pageSize: Int
    ): List<Photo> = emptyList()
}