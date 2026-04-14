package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.isFileUri
import com.github.panpf.sketch.fetch.isPhotosAssetUri
import com.github.panpf.sketch.fetch.newPhotosAssetUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okio.buffer
import okio.use
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.popoverPresentationController

actual class PhotoService actual constructor(val sketch: Sketch) {

    actual suspend fun loadFromGallery(pageStart: Int, pageSize: Int): List<Photo> {
        // TODO Read photos from the ios gallery
        return emptyList()
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun saveToGallery(imageUri: String): Result<String?> {
        val uri = imageUri.toUri()
        if (isPhotosAssetUri(uri) || isFileUri(uri)) {
            return Result.failure(Exception("Local photos do not need to be saved to the gallery"))
        }
        val imageBytesResult = withContext(Dispatchers.IO) {
            runCatching {
                val request = ImageRequest(sketch.context, imageUri)
                val requestContext = RequestContext(sketch, request, Size.Empty)
                val fetcher = sketch.components.newFetcherOrThrow(requestContext)
                val fetchResult = fetcher.fetch().getOrThrow()
                fetchResult.dataSource.toByteArray()
            }
        }
        if (imageBytesResult.isFailure) {
            return Result.failure(imageBytesResult.exceptionOrNull()!!)
        }
        val imageBytes = imageBytesResult.getOrThrow()

        val data = imageBytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }
        val uiImage = UIImage.imageWithData(data)
            ?: return Result.failure(Exception("Unable to create UIImage"))

        val result = saveUiImageToGallery(uiImage)
        return if (result.isSuccess && result.getOrThrow()) {
            Result.success(null)
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun share(imageUri: String): Result<String?> {
        val imageBytesResult = withContext(Dispatchers.IO) {
            runCatching {
                val request = ImageRequest(sketch.context, imageUri)
                val requestContext = RequestContext(sketch, request, Size.Empty)
                val fetcher = sketch.components.newFetcherOrThrow(requestContext)
                val fetchResult = fetcher.fetch().getOrThrow()
                fetchResult.dataSource.toByteArray()
            }
        }
        if (imageBytesResult.isFailure) {
            return Result.failure(imageBytesResult.exceptionOrNull()!!)
        }
        val imageBytes = imageBytesResult.getOrThrow()

        val data = imageBytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }
        val uiImage = UIImage.imageWithData(data)
            ?: return Result.failure(Exception("Unable to create UIImage"))

        val activityItems = listOf(uiImage)
//    val activityItems = listOf(uiImage, "Come and see my photos", NSURL.URLWithString("https://sample.com/sample.jpeg"))
        val activityController = UIActivityViewController(
            activityItems = activityItems,
            applicationActivities = null
        )
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        // Adapted to iPad (iPad must specify the pop-up position, otherwise it will crash)
        activityController.popoverPresentationController?.sourceView = rootViewController?.view
        rootViewController?.presentViewController(
            viewControllerToPresent = activityController,
            animated = true,
            completion = null
        )
        return Result.success(null)
    }

    private suspend fun saveUiImageToGallery(uiImage: UIImage): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val success = suspendCancellableCoroutine { continuation ->
                    PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                        changeBlock = {
                            PHAssetChangeRequest.creationRequestForAssetFromImage(uiImage)
                        },
                        completionHandler = { success, error ->
                            val result = if (error != null) {
                                Result.failure(Exception(error.localizedDescription))
                            } else {
                                Result.success(success)
                            }
                            continuation.resumeWith(result)
                        }
                    )
                }
                Result.success(success)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}