package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.sample.image.photoUri2PhotoInfo
import com.github.panpf.sketch.sample.ui.model.Photo


class BuiltinPhotoListRepo(val sketch: Sketch) {

    private val list: List<ComposeResImageFile> by lazy {
        ComposeResImageFiles.statics
            .asSequence()
            .plus(ComposeResImageFiles.anims)
            .plus(ComposeResImageFiles.numbersGif)
            .plus(ComposeResImageFiles.longQMSHT)
            .plus(ComposeResImageFiles.longCOMIC)
            .plus(ComposeResImageFiles.clockExifs)
            .plus(ComposeResImageFiles.mp4)
            .toList()
    }

    val size: Int
        get() = list.size

    suspend fun loadPhotoList(pageStart: Int, pageSize: Int): List<Photo> {
        return if (pageStart < size) {
            list.subList(
                fromIndex = pageStart,
                toIndex = minOf(pageStart + pageSize, size)
            )
        } else {
            emptyList()
        }.map {
            photoUri2PhotoInfo(sketch, it.uri)
        }
    }
}