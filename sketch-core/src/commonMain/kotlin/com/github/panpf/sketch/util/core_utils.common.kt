/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.util

import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okio.ByteString.Companion.encodeUtf8
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

/**
 * If [value] is true, execute [block] and return the result, otherwise return null
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testIfOrNull
 */
internal inline fun <R> ifOrNull(value: Boolean, block: () -> R?): R? =
    if (value) block() else null

/**
 * If [value] is true, execute [block]
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testIfApply
 */
internal inline fun <T> T.ifApply(value: Boolean, block: T.() -> Unit): T {
    if (value) {
        block()
    }
    return this
}

/**
 * If [value] is true, execute [block] and return the result, otherwise return itself
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testIfLet
 */
internal inline fun <T> T.ifLet(value: Boolean, block: (T) -> T): T {
    return if (value) block(this) else this
}

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testAsOrNull
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

/**
 * Convert to the type specified by the generic
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testAsOrThrow
 */
internal inline fun <R> Any.asOrThrow(): R {
    @Suppress("UNCHECKED_CAST")
    return this as R
}

/**
 * Returns true if currently on the main thread
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testIsMainThread
 * @see com.github.panpf.sketch.core.ios.test.util.CoreUtilsIosTest.testIsMainThread
 * @see com.github.panpf.sketch.core.desktop.test.util.CoreUtilsDesktopTest.testIsMainThread
 * @see com.github.panpf.sketch.core.jscommon.test.util.CoreUtilsJsCommonTest.testIsMainThread
 */
internal expect fun isMainThread(): Boolean

/**
 * Throws an exception if not currently on the main thread
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testRequiredMainThread
 * @see com.github.panpf.sketch.core.ios.test.util.CoreUtilsIosTest.testRequiredMainThread
 * @see com.github.panpf.sketch.core.desktop.test.util.CoreUtilsDesktopTest.testRequiredMainThread
 * @see com.github.panpf.sketch.core.jscommon.test.util.CoreUtilsJsCommonTest.testRequiredMainThread
 */
internal expect fun requiredMainThread()

/**
 * Throws an exception if not currently on the work thread
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testRequiredWorkThread
 * @see com.github.panpf.sketch.core.ios.test.util.CoreUtilsIosTest.testRequiredWorkThread
 * @see com.github.panpf.sketch.core.desktop.test.util.CoreUtilsDesktopTest.testRequiredWorkThread
 * @see com.github.panpf.sketch.core.jscommon.test.util.CoreUtilsJsCommonTest.testRequiredWorkThread
 */
expect fun requiredWorkThread()

/**
 * Gets the completed results, or null if not yet completed
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testGetCompletedOrNull
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> Deferred<T>.getCompletedOrNull(): T? {
    return try {
        getCompleted()
    } catch (_: Throwable) {
        null
    }
}

/**
 * Calculate MD5
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testMd5
 */
internal fun String.md5() = encodeUtf8().md5().hex()

/**
 * Returns a string representation of this Int value in the specified radix.
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testToHexString
 */
internal fun Any.toHexString(): String = this.hashCode().toString(16)

/**
 * Format Float with specified number of decimal places
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testFloatFormat
 */
internal fun Float.format(newScale: Int): Float {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier).toFloat()
    }
}

/**
 * Format a Double with the specified number of decimal places
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testFloatFormat
 */
internal fun Double.format(newScale: Int): Double {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier)
    }
}

/**
 * Returns the this size in human-readable format.
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testFormatFileSize
 */
internal fun Long.formatFileSize(decimals: Int = 1): String {
    val doubleString: (Double) -> String = { number ->
        if (number % 1 == 0.0) {
            number.toLong().toString()
        } else {
            number.toString()
        }
    }
    val finalFileSize: Double = this.coerceAtLeast(0L).toDouble()
    if (finalFileSize < 1000.0) return "${doubleString(finalFileSize)}B"
    val units = listOf("KB", "MB", "GB", "TB", "PB")
    units.forEachIndexed { index, suffix ->
        val powValue: Double = 1024.0.pow(index + 1)
        val powMaxValue: Double = powValue * 1000
        if (finalFileSize < powMaxValue || index == units.size - 1) {
            val value: Double = finalFileSize / powValue
            val formattedValue = value.format(decimals)
            return "${doubleString(formattedValue)}${suffix}"
        }
    }
    throw IllegalStateException("Can't format file size: $this")
}

/**
 * Returns the this size in human-readable format.
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testFormatFileSize
 */
internal fun Int.formatFileSize(decimals: Int = 1): String = toLong().formatFileSize(decimals)

/**
 * Combine two Int values, the high 16 bits are highInt and the low 16 bits are lowInt
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testIntMergedAndIntSplit
 */
internal fun intMerged(highInt: Int, lowInt: Int): Int {
    require(highInt in 0.rangeTo(Short.MAX_VALUE)) {
        "The value range for 'highInt' is 0 to ${Short.MAX_VALUE}"
    }
    require(lowInt in 0.rangeTo(Short.MAX_VALUE)) {
        "The value range for 'lowInt' is 0 to ${Short.MAX_VALUE}"
    }
    val high2 = highInt shl 16
    val low2 = (lowInt shl 16) shr 16
    return high2 or low2
}

/**
 * Split the Int value into two Int values, the high 16 bits are highInt and the low 16 bits are lowInt
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testIntMergedAndIntSplit
 */
internal fun intSplit(value: Int): Pair<Int, Int> {
    return (value shr 16) to ((value shl 16) shr 16)
}


/**
 * Gets a power of 2 that is less than or equal to the given integer
 *
 * Examples: -1->1，0->1，1->1，2->2，3->2，4->4，5->4，6->4，7->4，8->8，9->8
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testFloorRoundPow2
 */
internal fun floorRoundPow2(number: Int): Int {
    return number.takeHighestOneBit().coerceAtLeast(1)
}

/**
 * Gets a power of 2 that is greater than or equal to the given integer
 *
 * Examples: -1->1，0->1，1->1，2->2，3->4，4->4，5->8，6->8，7->8，8->8，9->16
 *
 * Copy from Java 17 'HashMap.tableSizeFor()' method
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testCeilRoundPow2
 */
internal fun ceilRoundPow2(number: Int): Int {
    val n = -1 ushr (number - 1).countLeadingZeroBits()
    return if (n < 0) 1 else if (n >= 1073741824) 1073741824 else n + 1
}

/**
 * Calculate the scale multiplier according to the fit scale
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testCalculateScaleMultiplierWithFit
 */
fun calculateScaleMultiplierWithFit(
    srcWidth: Float,
    srcHeight: Float,
    dstWidth: Float,
    dstHeight: Float,
    fitScale: Boolean
): Float {
    val widthScaleFactor: Float = dstWidth / srcWidth
    val heightScaleFactor: Float = dstHeight / srcHeight
    return if (fitScale) {
        min(widthScaleFactor, heightScaleFactor)
    } else {
        max(widthScaleFactor, heightScaleFactor)
    }
}

/**
 * Calculate the scale multiplier according to the fit scale
 */
@Deprecated(
    message = "Use calculateScaleMultiplierWithFit(Float, Float, Float, Float, Boolean) instead",
    replaceWith = ReplaceWith("calculateScaleMultiplierWithFit(srcWidth, srcHeight, dstWidth, dstHeight, fitScale)")
)
fun computeScaleMultiplierWithFit(
    srcWidth: Int,
    srcHeight: Int,
    dstWidth: Int,
    dstHeight: Int,
    fitScale: Boolean
): Float = calculateScaleMultiplierWithFit(
    srcWidth = srcWidth.toFloat(),
    srcHeight = srcHeight.toFloat(),
    dstWidth = dstWidth.toFloat(),
    dstHeight = dstHeight.toFloat(),
    fitScale = fitScale
)

/**
 * Calculate the scale multiplier according to the inside scale
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testCalculateScaleMultiplierWithInside
 */
fun calculateScaleMultiplierWithInside(
    srcWidth: Float,
    srcHeight: Float,
    dstWidth: Float,
    dstHeight: Float
): Float {
    if (srcWidth <= dstWidth && srcHeight <= dstHeight) {
        // center
        return 1f
    } else {
        // fit
        val widthScale = dstWidth / srcWidth
        val heightScale = dstHeight / srcHeight
        return min(widthScale, heightScale)
    }
}

/**
 * Calculate the scale multiplier according to the inside scale
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testCalculateScaleMultiplierWithCrop
 */
fun calculateScaleMultiplierWithCrop(
    srcWidth: Float,
    srcHeight: Float,
    dstWidth: Float,
    dstHeight: Float
): Float {
    val widthScale = dstWidth / srcWidth
    val heightScale = dstHeight / srcHeight
    return max(widthScale, heightScale)
}

/**
 * Calculate the scale multiplier according to the one side scale
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testCalculateScaleMultiplierWithOneSide
 */
fun calculateScaleMultiplierWithOneSide(sourceSize: SketchSize, targetSize: SketchSize): Float {
    if (targetSize.isNotEmpty) {
        val widthScaleFactor = targetSize.width.toFloat() / sourceSize.width
        val heightScaleFactor = targetSize.height.toFloat() / sourceSize.height
        val scaleFactor = min(widthScaleFactor, heightScaleFactor)
        return scaleFactor
    }
    if (targetSize.width > 0) {
        val scaleFactor = targetSize.width.toFloat() / sourceSize.width
        return scaleFactor
    }
    if (targetSize.height > 0) {
        val scaleFactor = targetSize.height.toFloat() / sourceSize.height
        return scaleFactor
    }
    return 1f
}

/**
 * Calculate the scale multiplier according to the one side scale
 */
@Deprecated(
    message = "Use calculateScaleMultiplierWithOneSide(SketchSize, SketchSize) instead",
    replaceWith = ReplaceWith("calculateScaleMultiplierWithOneSide(sourceSize, targetSize)")
)
fun computeScaleMultiplierWithOneSide(sourceSize: SketchSize, targetSize: SketchSize): Float =
    calculateScaleMultiplierWithOneSide(
        sourceSize = sourceSize,
        targetSize = targetSize
    )


/**
 * Calculate the bounds of the Drawable to inside the container.
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testCalculateInsideBounds
 */
@Deprecated("No longer used")
fun calculateInsideBounds(contentSize: Size, containerBounds: Rect): Rect {
    val containerWidth = containerBounds.width()
    val containerHeight = containerBounds.height()
    if (contentSize.width <= containerWidth && contentSize.height <= containerHeight) {
        // center
        val left = containerBounds.left + (containerWidth - contentSize.width) / 2
        val top = containerBounds.top + (containerHeight - contentSize.height) / 2
        val right = left + contentSize.width
        val bottom = top + contentSize.height
        return Rect(left, top, right, bottom)
    } else {
        // fit
        val widthScale = containerWidth.toFloat() / contentSize.width
        val heightScale = containerHeight.toFloat() / contentSize.height
        val scale = min(widthScale, heightScale)
        val scaledWidth = (contentSize.width * scale).toInt()
        val scaledHeight = (contentSize.height * scale).toInt()
        val left = containerBounds.left + (containerWidth - scaledWidth) / 2
        val top = containerBounds.top + (containerHeight - scaledHeight) / 2
        val right = left + scaledWidth
        val bottom = top + scaledHeight
        return Rect(left, top, right, bottom)
    }
}

/**
 * Calculate the bounds of the Drawable to crop the container.
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testCalculateCropBounds
 */
@Deprecated("No longer used")
fun calculateCropBounds(contentSize: Size, containerBounds: Rect): Rect {
    val containerWidth = containerBounds.width()
    val containerHeight = containerBounds.height()
    val widthScale = containerWidth.toFloat() / contentSize.width
    val heightScale = containerHeight.toFloat() / contentSize.height
    val scale = max(widthScale, heightScale)
    val scaledWidth = (contentSize.width * scale).toInt()
    val scaledHeight = (contentSize.height * scale).toInt()
    val left = containerBounds.left + (containerWidth - scaledWidth) / 2
    val top = containerBounds.top + (containerHeight - scaledHeight) / 2
    val right = left + scaledWidth
    val bottom = top + scaledHeight
    return Rect(left, top, right, bottom)
}

/**
 * Get the difference between two ImageRequests
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testImageRequestDifference
 */
fun ImageRequest?.difference(other: ImageRequest?): String {
    if (this == null && other == null) return "Both are null"
    if (this == null) return "This is null"
    if (other == null) return "Other is null"
    if (this === other) return "Same instance"

    if (context != other.context) {
        return "context different: '${context}' vs '${other.context}'"
    }
    if (uri != other.uri) {
        return "uri different: '${uri}' vs '${other.uri}'"
    }
    if (listener != other.listener) {
        return "listener different: '${listener}' vs '${other.listener}'"
    }
    if (progressListener != other.progressListener) {
        return "progressListener different: '${progressListener}' vs '${other.progressListener}'"
    }
    if (target != other.target) {
        return "target different: '${target}' vs '${other.target}'"
    }
    if (lifecycleResolver != other.lifecycleResolver) {
        return "lifecycleResolver different: '${lifecycleResolver}' vs '${other.lifecycleResolver}'"
    }

    if (depthHolder != other.depthHolder) {
        return "depth different: '${depthHolder}' vs '${other.depthHolder}'"
    }
    if (extras != other.extras) {
        return "extras different: '${extras}' vs '${other.extras}'"
    }
    if (downloadCachePolicy != other.downloadCachePolicy) {
        return "downloadCachePolicy different: '${downloadCachePolicy}' vs '${other.downloadCachePolicy}'"
    }
    if (colorType != other.colorType) {
        return "colorType different: '${colorType}' vs '${other.colorType}'"
    }
    if (colorSpace != other.colorSpace) {
        return "colorSpace different: '${colorSpace}' vs '${other.colorSpace}'"
    }
    if (sizeResolver != other.sizeResolver) {
        return "sizeResolver different: '${sizeResolver}' vs '${other.sizeResolver}'"
    }
    if (sizeMultiplier != other.sizeMultiplier) {
        return "sizeMultiplier different: '${sizeMultiplier}' vs '${other.sizeMultiplier}'"
    }
    if (precisionDecider != other.precisionDecider) {
        return "precisionDecider different: '${precisionDecider}' vs '${other.precisionDecider}'"
    }
    if (scaleDecider != other.scaleDecider) {
        return "scaleDecider different: '${scaleDecider}' vs '${other.scaleDecider}'"
    }
    if (transformations != other.transformations) {
        return "transformations different: '${transformations}' vs '${other.transformations}'"
    }
    if (resultCachePolicy != other.resultCachePolicy) {
        return "resultCachePolicy different: '${resultCachePolicy}' vs '${other.resultCachePolicy}'"
    }
    if (placeholder != other.placeholder) {
        return "placeholder different: '${placeholder}' vs '${other.placeholder}'"
    }
    if (fallback != other.fallback) {
        return "fallback different: '${fallback}' vs '${other.fallback}'"
    }
    if (error != other.error) {
        return "error different: '${error}' vs '${other.error}'"
    }
    if (transitionFactory != other.transitionFactory) {
        return "transitionFactory different: '${transitionFactory}' vs '${other.transitionFactory}'"
    }
    if (resizeOnDraw != other.resizeOnDraw) {
        return "resizeOnDraw different: '${resizeOnDraw}' vs '${other.resizeOnDraw}'"
    }
    if (memoryCachePolicy != other.memoryCachePolicy) {
        return "memoryCachePolicy different: '${memoryCachePolicy}' vs '${other.memoryCachePolicy}'"
    }
    if (componentRegistry != other.componentRegistry) {
        return "componentRegistry different: '${componentRegistry}' vs '${other.componentRegistry}'"
    }

    if (defaultOptions != other.defaultOptions) {
        return "defaultOptions different: '${defaultOptions.difference(other.defaultOptions)}'"
    }
    if (definedRequestOptions != other.definedRequestOptions) {
        return "definedRequestOptions different: '${definedRequestOptions}' vs '${other.definedRequestOptions}'"
    }
    return "Same content"
}

/**
 * Get the difference between two ImageOptions
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testImageOptionsDifference
 */
fun ImageOptions?.difference(other: ImageOptions?): String {
    if (this == null && other == null) return "Both are null"
    if (this == null) return "This is null"
    if (other == null) return "Other is null"
    if (this === other) return "Same instance"

    if (depthHolder != other.depthHolder) {
        return "depth different: '${depthHolder}' vs '${other.depthHolder}'"
    }
    if (extras != other.extras) {
        return "extras different: '${extras}' vs '${other.extras}'"
    }
    if (downloadCachePolicy != other.downloadCachePolicy) {
        return "downloadCachePolicy different: '${downloadCachePolicy}' vs '${other.downloadCachePolicy}'"
    }
    if (colorType != other.colorType) {
        return "colorType different: '${colorType}' vs '${other.colorType}'"
    }
    if (colorSpace != other.colorSpace) {
        return "colorSpace different: '${colorSpace}' vs '${other.colorSpace}'"
    }
    if (sizeResolver != other.sizeResolver) {
        return "sizeResolver different: '${sizeResolver}' vs '${other.sizeResolver}'"
    }
    if (sizeMultiplier != other.sizeMultiplier) {
        return "sizeMultiplier different: '${sizeMultiplier}' vs '${other.sizeMultiplier}'"
    }
    if (precisionDecider != other.precisionDecider) {
        return "precisionDecider different: '${precisionDecider}' vs '${other.precisionDecider}'"
    }
    if (scaleDecider != other.scaleDecider) {
        return "scaleDecider different: '${scaleDecider}' vs '${other.scaleDecider}'"
    }
    if (transformations != other.transformations) {
        return "transformations different: '${transformations}' vs '${other.transformations}'"
    }
    if (resultCachePolicy != other.resultCachePolicy) {
        return "resultCachePolicy different: '${resultCachePolicy}' vs '${other.resultCachePolicy}'"
    }
    if (placeholder != other.placeholder) {
        return "placeholder different: '${placeholder}' vs '${other.placeholder}'"
    }
    if (fallback != other.fallback) {
        return "fallback different: '${fallback}' vs '${other.fallback}'"
    }
    if (error != other.error) {
        return "error different: '${error}' vs '${other.error}'"
    }
    if (transitionFactory != other.transitionFactory) {
        return "transitionFactory different: '${transitionFactory}' vs '${other.transitionFactory}'"
    }
    if (resizeOnDraw != other.resizeOnDraw) {
        return "resizeOnDraw different: '${resizeOnDraw}' vs '${other.resizeOnDraw}'"
    }
    if (memoryCachePolicy != other.memoryCachePolicy) {
        return "memoryCachePolicy different: '${memoryCachePolicy}' vs '${other.memoryCachePolicy}'"
    }
    if (componentRegistry != other.componentRegistry) {
        return "componentRegistry different: '${componentRegistry}' vs '${other.componentRegistry}'"
    }
    return "Same content"
}

/**
 * Compare two versions
 *
 * The following version formats are supported:
 * 1.0, 1.0.0, 1.0.0.1, 1.0.0-snapshot1, 1.0.0-snapshot.1, 1.0.0-snapshot01, 1.0.0-alpha01, 1.0.0-beta01, 1.0.0-rc01
 *
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testCompareVersions
 */
fun compareVersions(version1: String, version2: String): Int {
    val (numbers1, suffix1) = version1.split("-", limit = 2)
        .let { it[0].trim() to it.getOrNull(1)?.trim() }
    val (numbers2, suffix2) = version2.split("-", limit = 2)
        .let { it[0].trim() to it.getOrNull(1)?.trim() }

    // Compare numeric parts
    val numberParts1 = numbers1.split(".")
    val numberParts2 = numbers2.split(".")
    val maxLength = maxOf(numberParts1.size, numberParts2.size)
    var numberCompareResult = 0
    for (i in 0 until maxLength) {
        val numberPar1 = numberParts1.getOrNull(i)?.toIntOrNull() ?: 0
        val numberPart2 = numberParts2.getOrNull(i)?.toIntOrNull() ?: 0
        if (numberPar1 != numberPart2) {
            numberCompareResult = numberPar1.compareTo(numberPart2)
            break
        }
    }
    if (numberCompareResult != 0) {
        return numberCompareResult
    }

    // Compare suffix parts
    val suffixCompareResult: Int
    if (suffix1 == suffix2) {
        suffixCompareResult = 0
    } else if (suffix1 == null) {
        suffixCompareResult = 1
    } else if (suffix2 == null) {
        suffixCompareResult = -1
    } else {
        val lowercaseSuffix1 = suffix1.lowercase()
        val lowercaseSuffix2 = suffix2.lowercase()
        val suffixTypes = listOf("snapshot", "alpha", "beta", "rc")
            .mapIndexed { index, s -> index to s }
        val suffixType1 = suffixTypes.find { lowercaseSuffix1.startsWith(it.second) }
        val suffixType2 = suffixTypes.find { lowercaseSuffix2.startsWith(it.second) }
        if (suffixType1 != null && suffixType2 != null) {
            if (suffixType1 == suffixType2) {
                val suffix1Number = lowercaseSuffix1
                    .replace(suffixType1.second, "")
                    .replace(".", "")
                    .toIntOrNull() ?: 0
                val suffix2Number = lowercaseSuffix2.replace(suffixType2.second, "")
                    .replace(".", "")
                    .toIntOrNull() ?: 0
                suffixCompareResult = suffix1Number.compareTo(suffix2Number)
            } else {
                suffixCompareResult = suffixType1.first.compareTo(suffixType2.first)
            }
        } else if (suffixType1 == null) {
            suffixCompareResult = 1
        } else {
            // suffixType2 == null
            suffixCompareResult = -1
        }
    }
    return suffixCompareResult
}

/**
 * If one of the size is a thumbnail of the other size, it returns true.
 * The rule is that their scaling ratio is the same and the error after scaling does not exceed [epsilonPixels] pixels
 *
 * @param epsilonPixels The maximum allowable error pixels
 * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testIsThumbnailWithSize
 */
internal fun isThumbnailWithSize(
    size: Size,
    otherSize: Size,
    epsilonPixels: Float = 1.0f
): Boolean {
    // There is no need to compare pictures with width or height of 0
    if (size.isEmpty || otherSize.isEmpty) return false
    // There is no need to compare the image direction (horizontal or vertical)
    if (size.width > size.height && otherSize.width < otherSize.height) return false
    if (size.width < size.height && otherSize.width > otherSize.height) return false

    val (originSize, thumbnailSize) = when {
        size.width > otherSize.width && size.height > otherSize.height -> size to otherSize
        size.width < otherSize.width && size.height < otherSize.height -> otherSize to size
        else -> return false
    }

    // Verify height with width scaling
    val widthScale = originSize.width.toFloat() / thumbnailSize.width
    val targetHeight = originSize.height.toFloat() / widthScale
    val heightDiff = abs(targetHeight - thumbnailSize.height)
    val validByWidth = heightDiff <= epsilonPixels

    // Verify width with height scaling
    val heightScale = originSize.height.toFloat() / thumbnailSize.height
    val targetWidth = originSize.width.toFloat() / heightScale
    val widthDiff = abs(targetWidth - thumbnailSize.width)
    val validByHeight = widthDiff <= epsilonPixels

    val pass = validByWidth || validByHeight
//    println(
//        "isThumbnailWithSize: " +
//                "originSize=${originSize.toShortString()}, " +
//                "thumbnailSize=${thumbnailSize.toShortString()}. " +
//                "widthScale=${widthScale.format(2)}, " +
//                "targetHeight=${targetHeight.format(2)}, " +
//                "heightDiff=${heightDiff.format(2)}, " +
//                "validByWidth=${validByWidth}. " +
//                "heightScale=${heightScale.format(2)}, " +
//                "targetWidth=${targetWidth.format(2)}, " +
//                "widthDiff=${widthDiff.format(2)}, " +
//                "validByHeight=${validByHeight}. " +
//                "pass=$pass"
//    )
    return pass
}