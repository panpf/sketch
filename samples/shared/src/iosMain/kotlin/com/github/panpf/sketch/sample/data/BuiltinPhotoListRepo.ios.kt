package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun buildPlatformBuiltinPhotoList(sketch: Sketch): List<String> {
    return ComposeResImageFiles.videos.mapNotNull {
        saveResImageToCache(sketch, it)
    }
}

private fun saveResImageToCache(sketch: Sketch, resImage: ComposeResImageFile): String? {
    val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
    val appDocumentDirectory = (paths.first() as String).toPath()
    val resImageCacheFile = appDocumentDirectory.resolve(resImage.name)
    if (sketch.fileSystem.exists(resImageCacheFile)) {
        return newFileUri(resImageCacheFile)
    }
    runBlocking {
        runCatching {
            sketch.fileSystem.createDirectories(appDocumentDirectory)
            resImage.toDataSource(sketch.context).openSource().buffer().use { source ->
                sketch.fileSystem.write(resImageCacheFile) {
                    this.writeAll(source)
                }
            }
        }.onFailure {
            sketch.fileSystem.delete(resImageCacheFile)
        }
    }
    if (sketch.fileSystem.exists(resImageCacheFile)) {
        return newFileUri(resImageCacheFile)
    }
    return null
}