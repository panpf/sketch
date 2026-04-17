@file:Suppress("PackageDirectoryMismatch")

package com.github.panpf.sketch

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import java.io.File

//@Deprecated(
//    message = "Use SingletonSketch.Factory instead",
//    replaceWith = ReplaceWith("SingletonSketch.Factory", "com.github.panpf.sketch.SingletonSketch")
//)
//interface SketchFactory : SingletonSketch.Factory {
//
//    override fun createSketch(context: PlatformContext): Sketch {
//        return createSketch()
//    }
//
//    fun createSketch(): Sketch
//}

@Deprecated(
    message = "Use add instead",
    replaceWith = ReplaceWith("add(decoder)")
)
fun ComponentRegistry.Builder.addBitmapDecoder(decoder: Decoder.Factory): ComponentRegistry.Builder =
    apply {
        add(decoder)
    }

@Deprecated(
    message = "Use add instead",
    replaceWith = ReplaceWith("add(decoder)")
)
fun ComponentRegistry.Builder.addDrawableDecoder(decoder: Decoder.Factory): ComponentRegistry.Builder =
    apply {
        add(decoder)
    }

@Deprecated(
    message = "Use loadImage instead",
    replaceWith = ReplaceWith("loadImage(uri, block)")
)
fun ImageView.displayImage(
    uri: String?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(uri, block)

@Deprecated(
    message = "Use loadImage instead",
    replaceWith = ReplaceWith("loadImage(uri, block)")
)
fun ImageView.displayImage(
    uri: Uri?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(uri, block)

@Deprecated(
    message = "Use loadImage instead",
    replaceWith = ReplaceWith("loadImage(drawableResId, block)")
)
fun ImageView.displayImage(
    @DrawableRes drawableResId: Int?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(drawableResId, block)

@Deprecated(
    message = "Use loadImage instead",
    replaceWith = ReplaceWith("loadImage(file, block)")
)
fun ImageView.displayImage(
    file: File?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(file, block)

@Deprecated(
    message = "Use loadAssetImage instead",
    replaceWith = ReplaceWith("loadImage(assetFileName, block)")
)
fun ImageView.displayAssetImage(
    assetFileName: String?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadAssetImage(assetFileName, block)

@Deprecated(
    message = "Use loadResourceImage instead",
    replaceWith = ReplaceWith("loadImage(drawableResId, block)")
)
fun ImageView.displayResourceImage(
    @DrawableRes drawableResId: Int?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadResourceImage(drawableResId, block)

@Deprecated(
    message = "Use loadResourceImage instead",
    replaceWith = ReplaceWith("loadImage(packageName, drawableResId, block)")
)
fun ImageView.displayResourceImage(
    packageName: String,
    @DrawableRes drawableResId: Int,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadResourceImage(packageName, drawableResId, block)

@Deprecated(
    message = "Use loadImage(newAppIconUri(packageName, versionCode)) instead",
    replaceWith = ReplaceWith("loadImage(newAppIconUri(packageName, versionCode))")
)
fun ImageView.displayAppIconImage(
    packageName: String,
    versionCode: Int,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(newAppIconUri(packageName, versionCode), block)

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