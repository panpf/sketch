package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.image.photoUri2PhotoInfo
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.util.md5
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import org.koin.mp.KoinPlatform
import java.io.File
import java.util.Locale

actual class PhotoService actual constructor(val sketch: Sketch) {

    private val mutex = Mutex()
    private var cachedList: List<String>? = null
    private val acceptImageExtensions =
        setOf("jpeg", "jpg", "png", "gif", "webp", "bmp", "heic", "heif", "svg")

    actual suspend fun loadFromGallery(
        pageStart: Int,
        pageSize: Int
    ): List<Photo> = withContext(Dispatchers.IO) {
        val finalCachedList = mutex.withLock {
            cachedList ?: loadLocalPhotos(sketch.context).apply {
                this@PhotoService.cachedList = this
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

    private fun loadLocalPhotos(context: PlatformContext): List<String> {
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

    actual suspend fun saveToGallery(imageUri: String): Result<String?> {
        val fetchResultResult = withContext(Dispatchers.IO) {
            runCatching {
                val request = ImageRequest(sketch.context, imageUri)
                val requestContext = RequestContext(sketch, request, Size.Empty)
                val fetcher = sketch.components.newFetcherOrThrow(requestContext)
                fetcher.fetch().getOrThrow()
            }
        }
        if (fetchResultResult.isFailure) {
            return Result.failure(fetchResultResult.exceptionOrNull()!!)
        }
        val fetchResult = fetchResultResult.getOrThrow()

        val userHomeDir = File(System.getProperty("user.home"))
        val userPicturesDir = File(userHomeDir, "Pictures")
        val outDir = File(userPicturesDir, "sketch4").apply { mkdirs() }
        val fileExtension = MimeTypeMap.getExtensionFromUrl(imageUri)
            ?: MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
            ?: "jpeg"
        val imageFile = File(outDir, "${imageUri.md5()}.$fileExtension")
        val result = withContext(Dispatchers.IO) {
            runCatching {
                fetchResult.dataSource.openSource().buffer().use { input ->
                    imageFile.outputStream().sink().buffer().use { output ->
                        output.writeAll(input)
                    }
                }
            }
        }
        return if (result.isSuccess) {
            Result.success(null)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    actual suspend fun share(imageUri: String): Result<String?> {
        return Result.failure(Exception("Desktop platform does not support"))
    }
}