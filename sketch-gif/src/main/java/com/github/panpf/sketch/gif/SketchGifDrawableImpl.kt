package com.github.panpf.sketch.gif

/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import com.github.panpf.sketch.cache.BitmapPoolHelper
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.ExifOrientationCorrector
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.util.byteCountCompat
import com.github.panpf.sketch.util.toHexString
import pl.droidsonroids.gif.AnimationListener
import pl.droidsonroids.gif.GifDrawable
import java.io.File
import java.io.FileDescriptor
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * 增加了从 BitmapPool 中寻找可复用 Bitmap 的功能以及图片的信息
 */
class SketchGifDrawableImpl : GifDrawable, SketchGifDrawable {

    companion object {
        private const val NAME = "SketchGifDrawable"
    }

    override var key: String
        private set
    override var uri: String
        private set
    private var imageInfo: ImageInfo
    override var dataFrom: DataFrom
        private set
    private var bitmapPoolHelper: BitmapPoolHelper?
    private var listenerMap: MutableMap<SketchGifDrawable.AnimationListener, AnimationListener>? =
        null

    override val originWidth: Int
        get() = imageInfo.width
    override val originHeight: Int
        get() = imageInfo.height
    override val mimeType: String
        get() = imageInfo.mimeType
    override val exifOrientation: Int
        get() = imageInfo.exifOrientation
    override val info: String by lazy {
        "${NAME}(ImageInfo=%dx%d/%s/%s,BitmapInfo=%dx%d/%s/%d/%s)".format(
            originWidth,
            originHeight,
            mimeType,
            ExifOrientationCorrector.toName(exifOrientation),
            mBuffer.width,
            mBuffer.height,
            mBuffer.config,
            mBuffer.byteCountCompat,
            mBuffer.toHexString(),
        )
    }
    override val byteCount: Int
        get() = allocationByteCount.toInt()
    override val bitmapConfig: Bitmap.Config?
        get() = mBuffer?.config

    internal constructor(
        key: String,
        uri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        afd: AssetFileDescriptor
    ) : super(afd) {
        this.key = key
        this.uri = uri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    internal constructor(
        key: String,
        uri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        assets: AssetManager,
        assetName: String
    ) : super(assets, assetName) {
        this.key = key
        this.uri = uri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    internal constructor(
        key: String,
        uri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        buffer: ByteBuffer
    ) : super(buffer) {
        this.key = key
        this.uri = uri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    internal constructor(
        key: String,
        uri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        bytes: ByteArray
    ) : super(bytes) {
        this.key = key
        this.uri = uri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    internal constructor(
        key: String,
        uri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        fd: FileDescriptor
    ) : super(fd) {
        this.key = key
        this.uri = uri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    internal constructor(
        key: String,
        uri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        file: File
    ) : super(file) {
        this.key = key
        this.uri = uri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    internal constructor(
        key: String,
        uri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        filePath: String
    ) : super(filePath) {
        this.key = key
        this.uri = uri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    internal constructor(
        key: String,
        uri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        res: Resources,
        id: Int
    ) : super(res, id) {
        this.key = key
        this.uri = uri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    internal constructor(
        key: String,
        imageUri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        resolver: ContentResolver?,
        uri: Uri
    ) : super(resolver, uri) {
        this.key = key
        this.uri = imageUri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    internal constructor(
        key: String,
        uri: String,
        imageInfo: ImageInfo,
        dataFrom: DataFrom,
        bitmapPoolHelper: BitmapPoolHelper?,
        stream: InputStream
    ) : super(stream) {
        this.key = key
        this.uri = uri
        this.imageInfo = imageInfo
        this.dataFrom = dataFrom
        this.bitmapPoolHelper = bitmapPoolHelper
    }

    override fun makeBitmap(width: Int, height: Int, config: Bitmap.Config): Bitmap {
        return bitmapPoolHelper?.getOrMake(width, height, config) ?: super.makeBitmap(
            width,
            height,
            config
        )
    }

    override fun recycleBitmap() {
        if (mBuffer == null) {
            return
        }
        val bitmapPoolHelper = bitmapPoolHelper
        if (bitmapPoolHelper != null) {
            bitmapPoolHelper.freeBitmapToPool(mBuffer)
        } else {
            super.recycleBitmap()
        }
    }


    override fun addAnimationListener(listener: SketchGifDrawable.AnimationListener) {
        val listenerMap =
            listenerMap ?: HashMap<SketchGifDrawable.AnimationListener, AnimationListener>().apply {
                this@SketchGifDrawableImpl.listenerMap = this
            }

        // 这个内部类配置了混淆时忽略警告，以后有变化时需要同步调整混淆配置，并打包验证
        val animationListener =
            AnimationListener { loopNumber -> listener.onAnimationCompleted(loopNumber) }
        addAnimationListener(animationListener)
        listenerMap[listener] = animationListener
    }

    override fun removeAnimationListener(listener: SketchGifDrawable.AnimationListener?): Boolean {
        val listenerMap = listenerMap
        if (listenerMap == null || listenerMap.isEmpty()) {
            return false
        }
        val animationListener = listenerMap.remove(listener)
        return animationListener != null && removeAnimationListener(animationListener)
    }

    override fun followPageVisible(userVisible: Boolean, fromDisplayCompleted: Boolean) {
        if (userVisible) {
            start()
        } else {
            if (fromDisplayCompleted) {
                // 图片加载完了，但是页面还不可见的时候就停留着在第一帧
                seekToFrame(0)
                stop()
            } else {
                stop()
            }
        }
    }
}