@file:Suppress("PackageDirectoryMismatch")

package com.github.panpf.sketch

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import java.io.File

@Deprecated(
    message = "Use SingletonSketch.Factory instead",
    replaceWith = ReplaceWith("SingletonSketch.Factory", "com.github.panpf.sketch.SingletonSketch")
)
interface SketchFactory : SingletonSketch.Factory {

    override fun createSketch(context: PlatformContext): Sketch {
        return createSketch()
    }

    fun createSketch(): Sketch
}

@Deprecated(
    message = "Use addDecoder instead",
    replaceWith = ReplaceWith("addDecoder(decoder)")
)
fun ComponentRegistry.Builder.addBitmapDecoder(decoder: Decoder.Factory): ComponentRegistry.Builder =
    apply {
        addDecoder(decoder)
    }

@Deprecated(
    message = "Use addDecoder instead",
    replaceWith = ReplaceWith("addDecoder(decoder)")
)
fun ComponentRegistry.Builder.addDrawableDecoder(decoder: Decoder.Factory): ComponentRegistry.Builder =
    apply {
        addDecoder(decoder)
    }

@Deprecated(
    message = "Use addDecodeInterceptor instead",
    replaceWith = ReplaceWith("addDecodeInterceptor(bitmapDecodeInterceptor)")
)
fun ComponentRegistry.Builder.addBitmapDecodeInterceptor(bitmapDecodeInterceptor: BitmapDecodeInterceptor): ComponentRegistry.Builder =
    apply {
        addDecodeInterceptor(bitmapDecodeInterceptor)
    }

@Deprecated(
    message = "Use addDecodeInterceptor instead",
    replaceWith = ReplaceWith("addDecodeInterceptor(bitmapDecodeInterceptor)")
)
fun ComponentRegistry.Builder.addDrawableDecodeInterceptor(drawableDecodeInterceptor: DrawableDecodeInterceptor): ComponentRegistry.Builder =
    apply {
        addDecodeInterceptor(drawableDecodeInterceptor)
    }

@Deprecated(
    message = "Use loadImage instead",
    replaceWith = ReplaceWith("loadImage(uri, configBlock)")
)
fun ImageView.displayImage(
    uri: String?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(uri, configBlock)

@Deprecated(
    message = "Use loadImage instead",
    replaceWith = ReplaceWith("loadImage(uri, configBlock)")
)
fun ImageView.displayImage(
    uri: Uri?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(uri, configBlock)

@Deprecated(
    message = "Use loadImage instead",
    replaceWith = ReplaceWith("loadImage(drawableResId, configBlock)")
)
fun ImageView.displayImage(
    @DrawableRes drawableResId: Int?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(drawableResId, configBlock)

@Deprecated(
    message = "Use loadImage instead",
    replaceWith = ReplaceWith("loadImage(file, configBlock)")
)
fun ImageView.displayImage(
    file: File?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(file, configBlock)

@Deprecated(
    message = "Use loadAssetImage instead",
    replaceWith = ReplaceWith("loadImage(assetFileName, configBlock)")
)
fun ImageView.displayAssetImage(
    assetFileName: String?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadAssetImage(assetFileName, configBlock)

@Deprecated(
    message = "Use loadResourceImage instead",
    replaceWith = ReplaceWith("loadImage(drawableResId, configBlock)")
)
fun ImageView.displayResourceImage(
    @DrawableRes drawableResId: Int?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadResourceImage(drawableResId, configBlock)

@Deprecated(
    message = "Use loadResourceImage instead",
    replaceWith = ReplaceWith("loadImage(packageName, drawableResId, configBlock)")
)
fun ImageView.displayResourceImage(
    packageName: String,
    @DrawableRes drawableResId: Int,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadResourceImage(packageName, drawableResId, configBlock)

@Deprecated(
    message = "Use disposeLoad() instead",
    replaceWith = ReplaceWith("disposeLoad()")
)
fun ImageView.disposeDisplay() = disposeLoad()

@Deprecated(
    message = "Use imageResult() instead",
    replaceWith = ReplaceWith("imageResult")
)
val ImageView.displayResult: ImageResult?
    get() = imageResult