package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.isComposeResourceUri
import com.github.panpf.sketch.fetch.isFileUri
import com.github.panpf.sketch.fetch.isKotlinResourceUri
import com.github.panpf.sketch.fetch.isPhotosAssetUri
import com.github.panpf.sketch.fetch.newPhotosAssetUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.sample.image.photoUri2PhotoInfo
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
import platform.Foundation.NSData
import platform.Foundation.NSPredicate
import platform.Foundation.NSSortDescriptor
import platform.Foundation.create
import platform.Photos.PHAsset
import platform.Photos.PHAssetCreationRequest
import platform.Photos.PHAssetMediaTypeImage
import platform.Photos.PHAssetMediaTypeVideo
import platform.Photos.PHAssetResourceTypePhoto
import platform.Photos.PHFetchOptions
import platform.Photos.PHFetchResult
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.popoverPresentationController

actual class PhotoService actual constructor(val sketch: Sketch) {

    private var fetchResult: PHFetchResult? = null

    actual suspend fun loadFromGallery(
        pageStart: Int,
        pageSize: Int
    ): List<Photo> = withContext(Dispatchers.IO) {
        if (fetchResult == null || pageStart == 0) {
            loadAllAssets()
        }
        val result = fetchResult ?: return@withContext emptyList()
        val total = result.count().toInt()
        if (pageStart >= total) return@withContext emptyList()
        val end = minOf(pageStart + pageSize, total)

        val photoUris = mutableListOf<String>()
        for (i in pageStart until end) {
            val asset = result.objectAtIndex(i.toULong()) as PHAsset
            photoUris.add(newPhotosAssetUri(asset.localIdentifier))
        }
        return@withContext photoUris.map { photoUri2PhotoInfo(sketch, it) }
    }

    private fun loadAllAssets() {
        val options = PHFetchOptions().apply {
            predicate = NSPredicate.predicateWithFormat(
                predicateFormat = "mediaType == %d || mediaType == %d",
                PHAssetMediaTypeImage, PHAssetMediaTypeVideo
            )
            sortDescriptors = listOf(NSSortDescriptor(key = "creationDate", ascending = false))
        }
        fetchResult = PHAsset.fetchAssetsWithOptions(options)
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun saveToGallery(imageUri: String): Result<String?> {
        val uri = imageUri.toUri()
        if (isPhotosAssetUri(uri) || (isFileUri(uri) && !isComposeResourceUri(uri) && !isKotlinResourceUri(
                uri
            ))
        ) {
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
        val result = saveImageDataToGallery(data)
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

    private suspend fun saveImageDataToGallery(data: NSData): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val success = suspendCancellableCoroutine { continuation ->
                    // Only in this way can GIF be supported. Directly using UIImage to write to the album will lose the animation effect of GIF.
                    PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                        changeBlock = {
                            val request = PHAssetCreationRequest.creationRequestForAsset()
                            request.addResourceWithType(
                                type = PHAssetResourceTypePhoto,
                                data = data,
                                options = null
                            )
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