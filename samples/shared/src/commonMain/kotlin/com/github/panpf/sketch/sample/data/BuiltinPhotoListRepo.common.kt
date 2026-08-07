package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.image.photoUri2PhotoInfo
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

expect suspend fun buildPlatformBuiltinPhotoList(sketch: Sketch): List<String>

class BuiltinPhotoListRepo(val sketch: Sketch) {

    private var _list: List<String>? = null
    private val mutex = Mutex()

    private suspend fun preparePhotoList(): List<String> {
        return _list
            ?: mutex.withLock {
                _list ?: buildPlatformBuiltinPhotoList(sketch).apply { _list = this }
            }
    }

    suspend fun getSize(): Int {
        val list = preparePhotoList()
        return list.size
    }

    suspend fun loadPhotoList(pageStart: Int, pageSize: Int): List<Photo> {
        val list = preparePhotoList()
        val listSize = list.size
        return if (pageStart < listSize) {
            list.subList(
                fromIndex = pageStart,
                toIndex = minOf(pageStart + pageSize, listSize)
            )
        } else {
            emptyList()
        }.map {
            photoUri2PhotoInfo(sketch, it)
        }
    }
}