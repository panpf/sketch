@file:Suppress("PackageDirectoryMismatch")

package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.asBitmap
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.resize.LongImagePrecisionDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.PrecisionDecider

@Deprecated(
    message = "Use LongImagePrecisionDecider instead",
    replaceWith = ReplaceWith(
        "LongImagePrecisionDecider",
        "com.github.panpf.sketch.resize.LongImagePrecisionDecider"
    )
)
typealias LongImageClipPrecisionDecider = LongImagePrecisionDecider


@Deprecated(
    message = "Use ImageRequest instead",
    replaceWith = ReplaceWith("ImageRequest", "com.github.panpf.sketch.request.ImageRequest")
)
typealias DisplayRequest = ImageRequest

@Deprecated(
    message = "Use ImageRequest instead",
    replaceWith = ReplaceWith("ImageRequest", "com.github.panpf.sketch.request.ImageRequest")
)
typealias LoadRequest = ImageRequest

@Deprecated(
    message = "Use ImageRequest instead",
    replaceWith = ReplaceWith("ImageRequest", "com.github.panpf.sketch.request.ImageRequest")
)
typealias DownloadRequest = ImageRequest


@Deprecated(
    message = "Use ImageResult instead",
    replaceWith = ReplaceWith("ImageResult", "com.github.panpf.sketch.request.ImageResult")
)
typealias DisplayResult = ImageResult

@Deprecated(
    message = "Use ImageResult instead",
    replaceWith = ReplaceWith("ImageResult", "com.github.panpf.sketch.request.ImageResult")
)
typealias LoadResult = ImageResult

@Deprecated(
    message = "Use ImageResult instead",
    replaceWith = ReplaceWith("ImageResult", "com.github.panpf.sketch.request.ImageResult")
)
typealias DownloadResult = ImageResult


@Deprecated(
    message = "Use ImageData instead",
    replaceWith = ReplaceWith("ImageData", "com.github.panpf.sketch.request.ImageData")
)
typealias DisplayData = ImageData

@Deprecated(
    message = "Use ImageData instead",
    replaceWith = ReplaceWith("ImageData", "com.github.panpf.sketch.request.ImageData")
)
typealias LoadData = ImageData

@Deprecated(
    message = "Use ImageData instead",
    replaceWith = ReplaceWith("ImageData", "com.github.panpf.sketch.request.ImageData")
)
typealias DownloadData = ImageData


@Deprecated(
    message = "Use uri.toString() instead",
    replaceWith = ReplaceWith("uri.toString()")
)
val ImageRequest.uriString: String
    get() = uri.toString()

@Deprecated(
    message = "Use addListener instead",
    replaceWith = ReplaceWith("addListener(listener)")
)
fun ImageRequest.Builder.listener(
    listener: Listener
): ImageRequest.Builder = addListener(listener)

@Deprecated(
    message = "Use addListener instead",
    replaceWith = ReplaceWith("addListener(onStart, onCancel, onError, onSuccess)")
)
inline fun ImageRequest.Builder.listener(
    crossinline onStart: (request: ImageRequest) -> Unit = {},
    crossinline onCancel: (request: ImageRequest) -> Unit = {},
    crossinline onError: (request: ImageRequest, result: ImageResult.Error) -> Unit = { _, _ -> },
    crossinline onSuccess: (request: ImageRequest, result: ImageResult.Success) -> Unit = { _, _ -> }
): ImageRequest.Builder = addListener(onStart, onCancel, onError, onSuccess)

@Deprecated(
    message = "Use addProgressListener instead",
    replaceWith = ReplaceWith("addProgressListener(progressListener)")
)
fun ImageRequest.Builder.progressListener(
    progressListener: ProgressListener
): ImageRequest.Builder = addProgressListener(progressListener)

@Deprecated(
    message = "Use resizeOnDraw instead",
    replaceWith = ReplaceWith("resizeOnDraw(apply)")
)
fun ImageOptions.Builder.resizeApplyToDrawable(apply: Boolean? = true): ImageOptions.Builder =
    resizeOnDraw(apply)

@Deprecated(
    message = "Use resizeOnDraw instead",
    replaceWith = ReplaceWith("resizeOnDraw(apply)")
)
fun ImageRequest.Builder.resizeApplyToDrawable(apply: Boolean? = true): ImageRequest.Builder =
    resizeOnDraw(apply)

@Deprecated(
    message = "Use precision instead",
    replaceWith = ReplaceWith("precision(precisionDecider)")
)
fun ImageOptions.Builder.resizePrecision(precisionDecider: PrecisionDecider?): ImageOptions.Builder =
    precision(precisionDecider)

@Deprecated(
    message = "Use precision instead",
    replaceWith = ReplaceWith("precision(precisionDecider)")
)
fun ImageRequest.Builder.resizePrecision(precisionDecider: PrecisionDecider?): ImageRequest.Builder =
    precision(precisionDecider)

@Deprecated(
    message = "Use precision instead",
    replaceWith = ReplaceWith("precision(precision)")
)
fun ImageOptions.Builder.resizePrecision(precision: Precision): ImageOptions.Builder =
    precision(precision)

@Deprecated(
    message = "Use precision instead",
    replaceWith = ReplaceWith("precision(precision)")
)
fun ImageRequest.Builder.resizePrecision(precision: Precision): ImageRequest.Builder =
    precision(precision)

@Deprecated(
    message = "Use colorType instead",
    replaceWith = ReplaceWith("colorType(colorType)")
)
fun ImageOptions.Builder.bitmapConfig(colorType: BitmapColorType): ImageOptions.Builder =
    colorType(colorType)

@Deprecated(
    message = "Use colorType instead",
    replaceWith = ReplaceWith("colorType(colorType)")
)
fun ImageRequest.Builder.bitmapConfig(colorType: BitmapColorType): ImageRequest.Builder =
    colorType(colorType)

@Deprecated(
    message = "Use updateImageOptions instead",
    replaceWith = ReplaceWith("updateImageOptions(block)")
)
fun ImageOptionsProvider.updateDisplayImageOptions(block: (ImageOptions.Builder.() -> Unit)) =
    updateImageOptions(block)

@Deprecated(
    message = "Use asBitmap() instead",
    replaceWith = ReplaceWith(expression = "asBitmap()", "com.github.panpf.sketch.asBitmap")
)
val ImageResult.Success.bitmap: Bitmap
    get() = image.asBitmap()

@Deprecated(
    message = "Use asDrawable(Resources) instead",
    replaceWith = ReplaceWith("asDrawable(resources)", "com.github.panpf.sketch.asDrawable")
)
val ImageResult.Success.drawable: Drawable
    get() = image.asDrawable()