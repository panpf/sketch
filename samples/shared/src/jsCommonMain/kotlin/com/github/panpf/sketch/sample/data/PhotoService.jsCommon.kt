package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.ui.model.Photo

actual class PhotoService actual constructor(val sketch: Sketch) {

    actual suspend fun loadFromGallery(pageStart: Int, pageSize: Int): List<Photo> {
        return emptyList()
    }

    actual suspend fun saveToGallery(imageUri: String): Result<String?> {
        return Result.failure(Exception("Js platform does not support"))
    }

    actual suspend fun share(imageUri: String): Result<String?> {
        return Result.failure(Exception("Js platform does not support"))
    }
}