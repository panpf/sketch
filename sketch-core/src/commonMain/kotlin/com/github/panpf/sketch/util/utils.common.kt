/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round


internal inline fun <R> ifOrNull(value: Boolean, block: () -> R?): R? = if (value) block() else null

/**
 * Calls the specified function [block] with `this` value as its receiver and returns `this` value.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#apply).
 */
internal inline fun <T> T.ifApply(value: Boolean, block: T.() -> Unit): T {
    if (value) {
        block()
    }
    return this
}

/**
 * Calls the specified function [block] with `this` value as its argument and returns its result.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#let).
 */
internal inline fun <T> T.ifLet(value: Boolean, block: (T) -> T): T {
    return if (value) block(this) else this
}

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

/**
 * Convert to the type specified by the generic
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <R> Any.asOrThrow(): R {
    @Suppress("UNCHECKED_CAST")
    return this as R
}

internal expect fun isMainThread(): Boolean
// TODO Replaced with coroutine versions because they must be executed in the Main dispatcher
//fun CoroutineContext.isMainThread(): Boolean {
//    return !Dispatchers.Main.isDispatchNeeded(this)
//}

internal expect fun requiredMainThread()

internal expect fun requiredWorkThread()

@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> Deferred<T>.getCompletedOrNull(): T? {
    return try {
        getCompleted()
    } catch (_: Throwable) {
        null
    }
}

internal fun String.md5() = encodeUtf8().md5().hex()

// TODO 用 MD5 替换 SHA-256
internal fun String.sha256String() = encodeUtf8().sha256().hex()

internal fun Any.toHexString(): String = this.hashCode().toString(16)

internal fun Float.format(newScale: Int): Float {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier).toFloat()
    }
}

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
 */
internal fun Int.formatFileSize(decimals: Int = 1): String {
    return toLong().formatFileSize(decimals)
}

internal fun Int.formatFileSize(): String = toLong().formatFileSize()

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

internal fun intSplit(value: Int): Pair<Int, Int> {
    return (value shr 16) to ((value shl 16) shr 16)
}


/**
 * Gets a power of 2 that is less than or equal to the given integer
 *
 * Examples: -1->1，0->1，1->1，2->2，3->2，4->4，5->4，6->4，7->4，8->8，9->8
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
 */
internal fun ceilRoundPow2(number: Int): Int {
    val n = -1 ushr (number - 1).countLeadingZeroBits()
    return if (n < 0) 1 else if (n >= 1073741824) 1073741824 else n + 1
}

fun computeScaleMultiplierWithFit(
    srcWidth: Int,
    srcHeight: Int,
    dstWidth: Int,
    dstHeight: Int,
    fitScale: Boolean
): Double {
    val widthPercent = dstWidth / srcWidth.toDouble()
    val heightPercent = dstHeight / srcHeight.toDouble()
    return if (fitScale) {
        min(widthPercent, heightPercent)
    } else {
        max(widthPercent, heightPercent)
    }
}

fun computeScaleMultiplierWithOneSide(sourceSize: SketchSize, targetSize: SketchSize): Float {
    val scaleFactor: Float = when {
        targetSize.isNotEmpty -> {
            val widthScaleFactor = targetSize.width.toFloat() / sourceSize.width
            val heightScaleFactor = targetSize.height.toFloat() / sourceSize.height
            min(widthScaleFactor, heightScaleFactor)
        }

        targetSize.width > 0 -> targetSize.width.toFloat() / sourceSize.width
        targetSize.height > 0 -> targetSize.height.toFloat() / sourceSize.height
        else -> 1f
    }
    return scaleFactor
}

fun ImageRequest?.difference(other: ImageRequest?): String {
    if (this == null && other == null) return "Both are null"
    if (this == null) return "This is null"
    if (other == null) return "Other is null"
    if (this === other) return "Same instance"
    if (this::class != other::class) return "Different class"
    if (context != other.context) return "context different: '${context}' vs '${other.context}'"
    if (uri != other.uri) return "uri different: '${uri}' vs '${other.uri}'"
    if (listener != other.listener) return "listener different: '${listener}' vs '${other.listener}'"
    if (progressListener != other.progressListener) {
        return "progressListener different: '${progressListener}' vs '${other.progressListener}'"
    }
    if (target != other.target) {
        return "target different: '${target}' vs '${other.target}'"
    }
    if (lifecycleResolver != other.lifecycleResolver) {
        return "lifecycleResolver different: '${lifecycleResolver}' vs '${other.lifecycleResolver}'"
    }
    if (definedOptions != other.definedOptions) {
        return "definedOptions different: '${definedOptions.difference(other.definedOptions)}'"
    }
    if (defaultOptions != other.defaultOptions) {
        return "defaultOptions different: '${defaultOptions.difference(other.defaultOptions)}'"
    }
    if (definedRequestOptions != other.definedRequestOptions) return "definedRequestOptions different: '${definedRequestOptions}' vs '${other.definedRequestOptions}'"
    if (depthHolder != other.depthHolder) return "depth different: '${depthHolder}' vs '${other.depthHolder}'"
    if (parameters != other.parameters) return "parameters different: '${parameters}' vs '${other.parameters}'"
    if (httpHeaders != other.httpHeaders) return "httpHeaders different: '${httpHeaders}' vs '${other.httpHeaders}'"
    if (downloadCachePolicy != other.downloadCachePolicy) return "downloadCachePolicy different: '${downloadCachePolicy}' vs '${other.downloadCachePolicy}'"
    if (sizeResolver != other.sizeResolver) return "sizeResolver different: '${sizeResolver}' vs '${other.sizeResolver}'"
    if (sizeMultiplier != other.sizeMultiplier) return "sizeMultiplier different: '${sizeMultiplier}' vs '${other.sizeMultiplier}'"
    if (precisionDecider != other.precisionDecider) return "precisionDecider different: '${precisionDecider}' vs '${other.precisionDecider}'"
    if (scaleDecider != other.scaleDecider) return "scaleDecider different: '${scaleDecider}' vs '${other.scaleDecider}'"
    if (transformations != other.transformations) return "transformations different: '${transformations}' vs '${other.transformations}'"
    if (resultCachePolicy != other.resultCachePolicy) return "resultCachePolicy different: '${resultCachePolicy}' vs '${other.resultCachePolicy}'"
    if (placeholder != other.placeholder) return "placeholder different: '${placeholder}' vs '${other.placeholder}'"
    if (uriEmpty != other.uriEmpty) return "uriEmpty different: '${uriEmpty}' vs '${other.uriEmpty}'"
    if (error != other.error) return "error different: '${error}' vs '${other.error}'"
    if (transitionFactory != other.transitionFactory) return "transitionFactory different: '${transitionFactory}' vs '${other.transitionFactory}'"
    if (disallowAnimatedImage != other.disallowAnimatedImage) return "disallowAnimatedImage different: '${disallowAnimatedImage}' vs '${other.disallowAnimatedImage}'"
    if (resizeOnDraw != other.resizeOnDraw) return "resizeOnDraw different: '${resizeOnDraw}' vs '${other.resizeOnDraw}'"
    if (memoryCachePolicy != other.memoryCachePolicy) return "memoryCachePolicy different: '${memoryCachePolicy}' vs '${other.memoryCachePolicy}'"
    if (componentRegistry != other.componentRegistry) return "componentRegistry different: '${componentRegistry}' vs '${other.componentRegistry}'"

    return "Same content"
}

fun ImageOptions?.difference(other: ImageOptions?): String {
    if (this == null && other == null) return "Both are null"
    if (this == null) return "This is null"
    if (other == null) return "Other is null"
    if (this === other) return "Same instance"
    if (depthHolder != other.depthHolder) return "depth different: '${depthHolder}' vs '${other.depthHolder}'"
    if (parameters != other.parameters) return "parameters different: '${parameters}' vs '${other.parameters}'"
    if (httpHeaders != other.httpHeaders) return "httpHeaders different: '${httpHeaders}' vs '${other.httpHeaders}'"
    if (downloadCachePolicy != other.downloadCachePolicy) return "downloadCachePolicy different: '${downloadCachePolicy}' vs '${other.downloadCachePolicy}'"
    if (sizeResolver != other.sizeResolver) return "sizeResolver different: '${sizeResolver}' vs '${other.sizeResolver}'"
    if (sizeMultiplier != other.sizeMultiplier) return "sizeMultiplier different: '${sizeMultiplier}' vs '${other.sizeMultiplier}'"
    if (precisionDecider != other.precisionDecider) return "precisionDecider different: '${precisionDecider}' vs '${other.precisionDecider}'"
    if (scaleDecider != other.scaleDecider) return "scaleDecider different: '${scaleDecider}' vs '${other.scaleDecider}'"
    if (transformations != other.transformations) return "transformations different: '${transformations}' vs '${other.transformations}'"
    if (resultCachePolicy != other.resultCachePolicy) return "resultCachePolicy different: '${resultCachePolicy}' vs '${other.resultCachePolicy}'"
    if (placeholder != other.placeholder) return "placeholder different: '${placeholder}' vs '${other.placeholder}'"
    if (uriEmpty != other.uriEmpty) return "uriEmpty different: '${uriEmpty}' vs '${other.uriEmpty}'"
    if (error != other.error) return "error different: '${error}' vs '${other.error}'"
    if (transitionFactory != other.transitionFactory) return "transitionFactory different: '${transitionFactory}' vs '${other.transitionFactory}'"
    if (disallowAnimatedImage != other.disallowAnimatedImage) return "disallowAnimatedImage different: '${disallowAnimatedImage}' vs '${other.disallowAnimatedImage}'"
    if (resizeOnDraw != other.resizeOnDraw) return "resizeOnDraw different: '${resizeOnDraw}' vs '${other.resizeOnDraw}'"
    if (memoryCachePolicy != other.memoryCachePolicy) return "memoryCachePolicy different: '${memoryCachePolicy}' vs '${other.memoryCachePolicy}'"
    if (componentRegistry != other.componentRegistry) return "componentRegistry different: '${componentRegistry}' vs '${other.componentRegistry}'"
    return "Same content"
}