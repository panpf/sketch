package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform
import java.io.File
import java.util.Locale

actual class GalleryLocalPhotoListRepo actual constructor(val sketch: Sketch) : LocalPhotoListRepo {

    private val mutex = Mutex()
    private var cachedList: List<String>? = null
    private val acceptImageExtensions =
        setOf("jpeg", "jpg", "png", "gif", "webp", "bmp", "heic", "heif", "svg")

    actual override suspend fun loadLocalPhotoList(
        pageStart: Int,
        pageSize: Int
    ): List<Photo> = withContext(Dispatchers.IO) {
        val finalCachedList = mutex.withLock {
            cachedList ?: localImages(sketch.context).apply {
                this@GalleryLocalPhotoListRepo.cachedList = this
            }
        }
        if (pageStart < finalCachedList.size) {
            finalCachedList.subList(
                fromIndex = pageStart,
                toIndex = (pageStart + pageSize).coerceAtMost(finalCachedList.size)
            ).map {
                photoUri2PhotoInfo(sketch, it)
            }
        } else {
            emptyList()
        }
    }

    private fun localImages(context: PlatformContext): List<String> {
        val userHomeDir = File(System.getProperty("user.home"))
        val userPicturesDir = File(userHomeDir, "Pictures")
        val appSettings: AppSettings = KoinPlatform.getKoin().get()
        val localPhotosDir = appSettings.localPhotosDirPath.value
            .takeIf { it.isNotEmpty() }?.let { File(it) }
        return listOfNotNull(userPicturesDir, localPhotosDir)
            .flatMap { dir ->
                dir.walkTopDown()
                    .filter { it.isFile }
                    .filter { !it.name.startsWith(".") }
                    .filter {
                        val extension = it.extension
                        acceptImageExtensions.contains(extension.lowercase(Locale.getDefault()))
                    }
                    .map { it.path }.toList()
            }
    }
}