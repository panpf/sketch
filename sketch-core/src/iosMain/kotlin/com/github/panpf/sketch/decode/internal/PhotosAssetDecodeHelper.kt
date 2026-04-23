package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.PhotosAssetDataSource
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.pixelSize
import com.github.panpf.sketch.util.toBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreGraphics.CGSizeMake
import platform.Photos.PHImageContentModeAspectFit
import platform.Photos.PHImageManager
import platform.Photos.PHImageRequestOptions
import platform.Photos.PHImageRequestOptionsDeliveryModeHighQualityFormat
import platform.Photos.PHImageRequestOptionsResizeModeExact
import platform.Photos.PHImageRequestOptionsResizeModeFast
import platform.UIKit.UIImage
import kotlin.coroutines.resumeWithException

/**
 * Help decode the photos asset in the iOS system album.
 *
 * @see com.github.panpf.sketch.core.ios.test.decode.internal.PhotosAssetDecodeHelperTest
 */
@OptIn(ExperimentalForeignApi::class)
class PhotosAssetDecodeHelper(
    val dataSource: PhotosAssetDataSource,
    val mimeType: String,
) : DecodeHelper {

    private val _imageInfo: ImageInfo by lazy {
        ImageInfo(size = dataSource.asset.pixelSize(), mimeType = mimeType)
    }

    override suspend fun getImageInfo(): ImageInfo {
        return _imageInfo
    }

    override suspend fun isSupportRegion(): Boolean = false

    override suspend fun decode(sampleSize: Int): Image {
        val targetSize = calculateSampledBitmapSize(
            imageSize = _imageInfo.size,
            sampleSize = sampleSize
        )
        val preferFastResize = dataSource.preferredThumbnail
        val allowNetworkAccess = dataSource.allowNetworkAccess
        val uiImage = requestImageForAsset(
            targetSize = targetSize,
            preferFastResize = preferFastResize,
            allowNetworkAccess = allowNetworkAccess,
        )
        val bitmap = uiImage.toBitmap()
        return bitmap.asImage()
    }

    override suspend fun decodeRegion(region: Rect, sampleSize: Int): Image =
        throw UnsupportedOperationException("Unsupported region decode")

    override fun close() {

    }

    private fun requestImageForAsset(
        targetSize: Size,
        preferFastResize: Boolean,
        allowNetworkAccess: Boolean,
    ): UIImage = runBlocking {
        suspendCancellableCoroutine { continuation ->
            val targetWidth = targetSize.width.coerceAtLeast(1)
            val targetHeight = targetSize.height.coerceAtLeast(1)
            val options = PHImageRequestOptions().apply {
                this.networkAccessAllowed = allowNetworkAccess
                this.deliveryMode = PHImageRequestOptionsDeliveryModeHighQualityFormat
                this.resizeMode = if (preferFastResize)
                    PHImageRequestOptionsResizeModeFast else PHImageRequestOptionsResizeModeExact
            }
            PHImageManager.defaultManager().requestImageForAsset(
                asset = dataSource.asset,
                targetSize = CGSizeMake(targetWidth.toDouble(), targetHeight.toDouble()),
                contentMode = PHImageContentModeAspectFit,
                options = options,
                resultHandler = { result, info ->
                    if (result != null) {
                        continuation.resumeWith(Result.success(result))
                    } else {
                        val message = "requestImageForAsset return null. " +
                                "targetSize=${targetWidth}x$targetHeight, " +
                                "preferFastResize=$preferFastResize, " +
                                "allowNetworkAccess=$allowNetworkAccess, " +
                                "info='$info'"
                        continuation.resumeWithException(DecodeException(message))
                    }
                },
            )
        }
    }
}