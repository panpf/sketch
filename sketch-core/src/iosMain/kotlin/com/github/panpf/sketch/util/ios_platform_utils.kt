/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright (C) 2026 Kuki93
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)

package com.github.panpf.sketch.util

import com.github.panpf.sketch.Bitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSOperatingSystemVersion
import platform.Foundation.NSProcessInfo
import platform.Photos.PHAsset
import platform.Photos.PHAssetMediaType
import platform.Photos.PHAssetMediaTypeAudio
import platform.Photos.PHAssetMediaTypeImage
import platform.Photos.PHAssetMediaTypeVideo
import platform.Photos.PHAssetResource
import platform.UIKit.UIImage
import platform.UIKit.UIImageOrientation
import platform.UniformTypeIdentifiers.UTType

private const val RESOURCE_TYPE_PHOTO = 1L
private const val RESOURCE_TYPE_VIDEO = 2L
private const val RESOURCE_TYPE_ALTERNATE_PHOTO = 4L
private const val RESOURCE_TYPE_FULL_SIZE_PHOTO = 5L
private const val RESOURCE_TYPE_FULL_SIZE_VIDEO = 6L
private const val RESOURCE_TYPE_ADJUSTMENT_BASE_PHOTO = 8L
private const val RESOURCE_TYPE_PAIRED_VIDEO = 9L
private const val RESOURCE_TYPE_FULL_SIZE_PAIRED_VIDEO = 10L

private val VIDEO_RESOURCE_TYPES = setOf(
    RESOURCE_TYPE_VIDEO,
    RESOURCE_TYPE_FULL_SIZE_VIDEO,
    RESOURCE_TYPE_PAIRED_VIDEO,
    RESOURCE_TYPE_FULL_SIZE_PAIRED_VIDEO,
)

private val IMAGE_RESOURCE_TYPES = setOf(
    RESOURCE_TYPE_PHOTO,
    RESOURCE_TYPE_ALTERNATE_PHOTO,
    RESOURCE_TYPE_FULL_SIZE_PHOTO,
    RESOURCE_TYPE_ADJUSTMENT_BASE_PHOTO,
)

/**
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testPreferredVideoResourceTypeOrder
 */
internal fun preferredVideoResourceTypeOrder(): List<Long> = listOf(
    RESOURCE_TYPE_FULL_SIZE_VIDEO,
    RESOURCE_TYPE_VIDEO,
    RESOURCE_TYPE_FULL_SIZE_PAIRED_VIDEO,
    RESOURCE_TYPE_PAIRED_VIDEO,
)

/**
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testPreferredImageResourceTypeOrder
 */
internal fun preferredImageResourceTypeOrder(preferredThumbnail: Boolean): List<Long> =
    if (preferredThumbnail) {
        listOf(
            RESOURCE_TYPE_PHOTO,
            RESOURCE_TYPE_ALTERNATE_PHOTO,
            RESOURCE_TYPE_FULL_SIZE_PHOTO,
            RESOURCE_TYPE_ADJUSTMENT_BASE_PHOTO,
        )
    } else {
        listOf(
            RESOURCE_TYPE_FULL_SIZE_PHOTO,
            RESOURCE_TYPE_PHOTO,
            RESOURCE_TYPE_ALTERNATE_PHOTO,
            RESOURCE_TYPE_ADJUSTMENT_BASE_PHOTO,
        )
    }

/**
 * Fetch a PHAsset from the Photos library using its local identifier.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testFetchPhotosAsset
 */
internal fun fetchPhotosAsset(localIdentifier: String): PHAsset? {
    val fetchResult = PHAsset.fetchAssetsWithLocalIdentifiers(
        identifiers = listOf(localIdentifier),
        options = null,
    )
    if (fetchResult.count.toInt() <= 0) return null
    return fetchResult.objectAtIndex(0uL) as? PHAsset
}

/**
 * Select the primary PHAssetResource for a given PHAsset, prioritizing certain resource types based on the media type and thumbnail preference.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testSelectPrimaryResource
 */
internal fun selectPrimaryResource(
    asset: PHAsset,
    preferredThumbnail: Boolean,
): PHAssetResource? {
    val resources = PHAssetResource.assetResourcesForAsset(asset)
        .mapNotNull { it as? PHAssetResource }
    if (resources.isEmpty()) return null
    return if (asset.mediaType == PHAssetMediaTypeVideo) {
        val orderedTypes = preferredVideoResourceTypeOrder()
        pickResourceByTypeOrder(resources = resources, orderedTypes = orderedTypes)
            ?: resources.firstOrNull { resolveMimeType(it)?.startsWith("video/") == true }
            ?: resources.first()
    } else {
        val orderedTypes = preferredImageResourceTypeOrder(preferredThumbnail)
        pickResourceByTypeOrder(resources, orderedTypes)
            ?: resources.firstOrNull { resolveMimeType(it)?.startsWith("image/") == true }
            ?: resources.first()
    }
}

private fun pickResourceByTypeOrder(
    resources: List<PHAssetResource>,
    orderedTypes: List<Long>,
): PHAssetResource? {
    orderedTypes.forEach { expectedType ->
        resources
            .firstOrNull { it.typeCode() == expectedType }
            ?.let { return it }
    }
    return null
}

/**
 * Resolve the MIME type of a PHAssetResource by first attempting to use its uniform type identifier and original filename, and if that fails, by checking its resource type code against known video and image resource types.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testResolveMimeTypeWithPHAssetResource
 */
internal fun resolveMimeType(resource: PHAssetResource): String? {
    return resolveMimeType(
        uniformTypeIdentifier = resource.uniformTypeIdentifier,
        originalFilename = resource.originalFilename,
    ) ?: resolveMimeTypeWithPHAssetResourceType(resource.typeCode())
}

/**
 * Resolve the MIME type of a PHAssetResource based on its uniform type identifier and original filename.
 *
 * The resolution process follows these steps:
 * 1. Attempt to get the preferred MIME type from the uniform type identifier using UTType.
 * 2. If that fails, check if the uniform type identifier contains known substrings that indicate common MIME types.
 * 3. If that also fails, attempt to resolve the MIME type based on the file extension from the original filename.
 *
 * @param uniformTypeIdentifier The uniform type identifier of the PHAssetResource, which may provide a direct way to determine the MIME type.
 * @param originalFilename The original filename of the PHAssetResource, which can be used to infer the MIME type from its extension if necessary.
 * @return The resolved MIME type as a string, or null if it cannot be determined.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testResolveMimeTypeWithUniformTypeIdentifierAndOriginalFilename
 */
internal fun resolveMimeType(uniformTypeIdentifier: String?, originalFilename: String?): String? {
    val preferredMimeType = uniformTypeIdentifier
        ?.let { runCatching { UTType.typeWithIdentifier(it)?.preferredMIMEType }.getOrNull() }
        ?.lowercase()
    if (preferredMimeType?.isNotEmpty() == true) {
        return preferredMimeType
    }

    if (uniformTypeIdentifier?.isNotEmpty() == true) {
        val lowercaseTypeIdentifier = uniformTypeIdentifier.lowercase()
        if (lowercaseTypeIdentifier.contains("jpeg")) return "image/jpeg"
        if (lowercaseTypeIdentifier.contains("jpg")) return "image/jpeg"
        if (lowercaseTypeIdentifier.contains("png")) return "image/png"
        if (lowercaseTypeIdentifier.contains("webp")) return "image/webp"
        if (lowercaseTypeIdentifier.contains("gif")) return "image/gif"
        if (lowercaseTypeIdentifier.contains("heic")) return "image/heic"
        if (lowercaseTypeIdentifier.contains("heif")) return "image/heif"
        if (lowercaseTypeIdentifier.contains("avif")) return "image/avif"
        if (lowercaseTypeIdentifier.contains("raw")) return "image/x-adobe-dng"
        if (lowercaseTypeIdentifier.contains("dng")) return "image/x-adobe-dng"
        if (lowercaseTypeIdentifier.contains("tiff")) return "image/tiff"
        if (lowercaseTypeIdentifier.contains("bmp")) return "image/bmp"
        if (lowercaseTypeIdentifier.contains("ico")) return "image/ico"
        if (lowercaseTypeIdentifier.contains("mov")) return "video/quicktime"
        if (lowercaseTypeIdentifier.contains("quicktime")) return "video/quicktime"
        if (lowercaseTypeIdentifier.contains("mpeg-4")) return "video/mp4"
        if (lowercaseTypeIdentifier.contains("mp4")) return "video/mp4"
        if (lowercaseTypeIdentifier.contains("m4v")) return "video/mp4"
        if (lowercaseTypeIdentifier.contains("mkv")) return "video/mkv"
        if (lowercaseTypeIdentifier.contains("avi")) return "video/avi"
        if (lowercaseTypeIdentifier.contains("rmvb")) return "video/rmvb"
        if (lowercaseTypeIdentifier.contains("rm")) return "video/rmvb"
    }

    val extension = originalFilename
        ?.substringAfterLast('.', missingDelimiterValue = "")
        ?.lowercase()
    if (extension?.isNotEmpty() == true) {
        if (extension == "jpeg") return "image/jpeg"
        if (extension == "jpg") return "image/jpeg"
        if (extension == "png") return "image/png"
        if (extension == "webp") return "image/webp"
        if (extension == "gif") return "image/gif"
        if (extension == "heic") return "image/heic"
        if (extension == "heif") return "image/heif"
        if (extension == "avif") return "image/avif"
        if (extension == "dng") return "image/x-adobe-dng"
        if (extension == "tiff") return "image/tiff"
        if (extension == "tif") return "image/tiff"
        if (extension == "mov") return "video/quicktime"
        if (extension == "mp4") return "video/mp4"
        if (extension == "m4v") return "video/mp4"
        if (extension == "mkv") return "video/mkv"
        if (extension == "avi") return "video/avi"
        if (extension == "rmvb") return "video/rmvb"
        if (extension == "rm") return "video/rmvb"
    }
    return null
}

/**
 * Resolve the MIME type of a PHAssetResource based on its resource type code, by checking if it matches known video or image resource types.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testResolveMimeTypeWithPHAssetResourceType
 */
internal fun resolveMimeTypeWithPHAssetResourceType(type: Long): String? {
    when (type) {
        in IMAGE_RESOURCE_TYPES -> return "image/*"
        in VIDEO_RESOURCE_TYPES -> return "video/*"
    }
    return null
}

/**
 * Resolve the MIME type of a PHAsset based on its media type.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testResolveMimeTypeWithPHAsset
 */
internal fun resolveMimeType(asset: PHAsset): String? = resolveMimeType(asset.mediaType)

/**
 * Map the media type of PHAsset (video, picture, audio) to the corresponding MIME type string
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testResolveMimeTypeWithPHAssetMediaType
 */
internal fun resolveMimeType(mediaType: PHAssetMediaType): String? = when (mediaType) {
    PHAssetMediaTypeImage -> "image/*"
    PHAssetMediaTypeVideo -> "video/*"
    PHAssetMediaTypeAudio -> "audio/*"
    else -> null
}

private fun PHAssetResource.typeCode(): Long = when (val raw: Any = type) {
    is ULong -> raw.toLong()
    is UInt -> raw.toLong()
    is Number -> raw.toLong()
    else -> -1L
}

/**
 * Get the pixel dimensions of a PHAsset as a Size object, using its pixelWidth and pixelHeight properties.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testPixelSize
 */
fun PHAsset.pixelSize(): Size = Size(
    width = pixelWidth.toInt(),
    height = pixelHeight.toInt(),
)

/**
 * Correct the orientation of a UIImage by checking its imageOrientation property and, if it is not already in the upright orientation, creating a new image with the correct orientation using UIGraphicsImageRenderer.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testCorrectExifOrientation
 */
fun UIImage.correctExifOrientation(): UIImage {
    if (this.imageOrientation == UIImageOrientation.UIImageOrientationUp) return this
    val format = platform.UIKit.UIGraphicsImageRendererFormat.defaultFormat()
    val renderer = platform.UIKit.UIGraphicsImageRenderer(size = this.size, format = format)
    val newImage = renderer.imageWithActions {
        val width = this.size.useContents { width }
        val height = this.size.useContents { height }
        this.drawInRect(CGRectMake(x = 0.0, y = 0.0, width = width, height = height))
    }
    return newImage
}

/**
 * Convert a UIImage to a Bitmap by creating a bitmap context, drawing the image into it, and then installing the pixel data into a Bitmap object.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testUIImageToBitmap
 */
fun UIImage.toBitmap(sampleSize: Int = 1, region: Rect? = null): Bitmap {
    val cgImage = this.CGImage ?: throw Exception("UIImage has no CGImage")
    return cgImage.toBitmap(sampleSize = sampleSize, region = region)
}

/**
 * Convert a UIImage to a Bitmap by creating a bitmap context, drawing the image into it, and then installing the pixel data into a Bitmap object.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testUIImageToBitmap
 */
fun UIImage.toBitmap(): Bitmap {
    return toBitmap(sampleSize = 1, region = null)
}

/**
 * Get the size of a UIImage as a Size object, using its size property which contains the width and height in points, and converting them to integers.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testSketchSize
 */
fun UIImage.sketchSize(): Size {
    return size().useContents {
        Size(this.width.toInt(), this.height.toInt())
    }
}

/**
 * Check if the current iOS version is at least the specified major, minor, and patch version by creating an NSOperatingSystemVersion struct and using NSProcessInfo to compare it against the current system version.
 *
 * @see com.github.panpf.sketch.core.ios.test.util.IosPlatformUtilsTest.testIsVersionAtLeast
 */
fun isVersionAtLeast(major: Int, minor: Int = 0, patch: Int = 0): Boolean {
    val version = cValue<NSOperatingSystemVersion> {
        majorVersion = major.toLong()
        minorVersion = minor.toLong()
        patchVersion = patch.toLong()
    }
    return NSProcessInfo.processInfo.isOperatingSystemAtLeastVersion(version)
}
