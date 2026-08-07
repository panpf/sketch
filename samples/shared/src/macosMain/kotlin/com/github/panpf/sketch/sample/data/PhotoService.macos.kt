package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.isComposeResourceUri
import com.github.panpf.sketch.fetch.isFileUri
import com.github.panpf.sketch.fetch.isKotlinResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.image.photoUri2PhotoInfo
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.util.md5
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import org.koin.mp.KoinPlatform
import platform.Foundation.NSHomeDirectory

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
            cachedList ?: loadLocalPhotos().also { cachedList = it }
        }
        if (pageStart >= finalCachedList.size) {
            emptyList()
        } else {
            finalCachedList.subList(
                fromIndex = pageStart,
                toIndex = (pageStart + pageSize).coerceAtMost(finalCachedList.size)
            ).map { photoUri2PhotoInfo(sketch, it) }
        }
    }

    private fun loadLocalPhotos(): List<String> {
        val picturesDir = NSHomeDirectory().toPath().resolve("Pictures")
        val appSettings: AppSettings = KoinPlatform.getKoin().get()
        val configuredDir = appSettings.localPhotosDirPath.value
            .takeIf { it.isNotEmpty() }
            ?.toPath()
        return listOfNotNull(picturesDir, configuredDir)
            .filter { sketch.fileSystem.metadataOrNull(it)?.isDirectory == true }
            .flatMap { directory ->
                sketch.fileSystem.listRecursively(directory)
                    .filter { path -> sketch.fileSystem.metadataOrNull(path)?.isRegularFile == true }
                    .filter { path -> !path.name.startsWith(".") }
                    .filter { path ->
                        acceptImageExtensions.contains(path.extension.lowercase())
                    }
                    .map(Path::toString)
                    .toList()
            }
    }

    actual suspend fun saveToGallery(imageUri: String): Result<String?> {
        val uri = imageUri.toUri()
        if (isFileUri(uri) && !isComposeResourceUri(uri) && !isKotlinResourceUri(uri)) {
            return Result.failure(Exception("Local photos do not need to be saved to the gallery"))
        }
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
        // TODO sketch4 change to Sketch Image Loader
        val outDir = NSHomeDirectory().toPath().resolve("Pictures").resolve("sketch4")
        val fileExtension = MimeTypeMap.getExtensionFromUrl(imageUri)
            ?: MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
            ?: "jpeg"
        val imageFile = outDir.resolve("${imageUri.md5()}.$fileExtension")
        val result = withContext(Dispatchers.IO) {
            runCatching {
                sketch.fileSystem.createDirectories(outDir)
                fetchResult.dataSource.openSource().buffer().use { input ->
                    sketch.fileSystem.sink(imageFile).buffer().use { output ->
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
        return Result.failure(Exception("macOS platform does not support sharing"))
    }
}

private val Path.extension: String
    get() = name.substringAfterLast('.', missingDelimiterValue = "")
