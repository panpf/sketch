package com.github.panpf.sketch.drawable

import android.content.ContentResolver
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.cache.BitmapPoolHelper
import com.github.panpf.sketch.util.byteCountCompat
import pl.droidsonroids.gif.GifDrawable
import java.io.File

class ReuseGifDrawable : GifDrawable, Animatable2Compat {

    private val callbacks = mutableListOf<Animatable2Compat.AnimationCallback>()

    val bitmapConfig: Bitmap.Config?
        get() = mBuffer?.config

    val bitmapWidth: Int
        get() = mBuffer?.width ?: 0

    val bitmapHeight: Int
        get() = mBuffer?.height ?: 0

    val bitmapByteCount: Int
        get() = mBuffer?.byteCountCompat ?: 0

    internal constructor(
        bitmapPoolHelper: BitmapPoolHelper, assets: AssetManager, assetName: String
    ) : super(assets, assetName, bitmapPoolHelper)

    internal constructor(bitmapPoolHelper: BitmapPoolHelper, bytes: ByteArray) : super(
        bytes,
        bitmapPoolHelper
    )

    internal constructor(bitmapPoolHelper: BitmapPoolHelper, file: File) : super(
        file,
        bitmapPoolHelper
    )

    internal constructor(
        bitmapPoolHelper: BitmapPoolHelper, res: Resources, id: Int
    ) : super(res, id, bitmapPoolHelper)

    internal constructor(
        bitmapPoolHelper: BitmapPoolHelper, resolver: ContentResolver?, uri: Uri
    ) : super(resolver, uri, bitmapPoolHelper)

    override fun start() {
        val isRunning = isRunning
        super.start()
        if (!isRunning) {
            callbacks.forEach { it.onAnimationStart(this) }
        }
    }

    override fun stop() {
        val isRunning = isRunning
        super.stop()
        if (isRunning) {
            callbacks.forEach { it.onAnimationEnd(this) }
        }
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbacks.remove(callback)
    }

    override fun clearAnimationCallbacks() = callbacks.clear()
}