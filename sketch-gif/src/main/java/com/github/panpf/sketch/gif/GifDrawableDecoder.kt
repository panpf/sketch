package com.github.panpf.sketch.gif

import android.graphics.BitmapFactory
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetsDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.datasource.DrawableResDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.decode.internal.decodeBitmap
import com.github.panpf.sketch.decode.internal.readImageInfo
import com.github.panpf.sketch.request.DisplayRequest

class GifDrawableDecoder(
    private val sketch: Sketch,
    private val request: DisplayRequest,
    private val dataSource: DataSource
) : DrawableDecoder {

    // todo 参考 coil 的 gif 实现

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun decodeDrawable(): DrawableDecodeResult {
        val request = request
        val imageInfo = dataSource.readImageInfo(request)
        val gifDrawable = when (val source = dataSource) {
            is ByteArrayDataSource -> {
                SketchGifDrawableImpl(
                    request.key,
                    request.uriString,
                    imageInfo,
                    source.from,
                    sketch.bitmapPoolHelper,
                    source.data
                )
            }
            is DiskCacheDataSource -> {
                SketchGifDrawableImpl(
                    request.key,
                    request.uriString,
                    imageInfo,
                    source.from,
                    sketch.bitmapPoolHelper,
                    source.diskCacheEntry.file
                )
            }
            is DrawableResDataSource -> {
                SketchGifDrawableImpl(
                    request.key,
                    request.uriString,
                    imageInfo,
                    source.from,
                    sketch.bitmapPoolHelper,
                    source.context.resources,
                    source.drawableId
                )
            }
            is ContentDataSource -> {
                SketchGifDrawableImpl(
                    request.key,
                    request.uriString,
                    imageInfo,
                    source.from,
                    sketch.bitmapPoolHelper,
                    source.context.contentResolver,
                    source.contentUri
                )
            }
            is FileDataSource -> {
                SketchGifDrawableImpl(
                    request.key,
                    request.uriString,
                    imageInfo,
                    source.from,
                    sketch.bitmapPoolHelper,
                    source.file
                )
            }
            is AssetsDataSource -> {
                SketchGifDrawableImpl(
                    request.key,
                    request.uriString,
                    imageInfo,
                    source.from,
                    sketch.bitmapPoolHelper,
                    source.context.assets,
                    source.assetsFilePath
                )
            }
            else -> {
                throw Exception("Unsupported DataSource: ${source::class.qualifiedName}")
            }
        }
        return DrawableDecodeResult(gifDrawable, imageInfo, dataSource.from)
    }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: DisplayRequest,
            dataSource: DataSource
        ): GifDrawableDecoder? {
            if (request.disabledAnimationDrawable == true) {
                return null
            }
            // todo 改进判断方式，参考 coil 改成 BufferedSource
            val mimeType = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                dataSource.decodeBitmap(this)
            }.outMimeType.orEmpty()
            return if (mimeType == "image/gif") {
                GifDrawableDecoder(sketch, request, dataSource)
            } else {
                null
            }
        }
    }
}