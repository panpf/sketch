package com.github.panpf.sketch.decode

import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.GifDrawableDecoder.Companion.MIME_TYPE
import com.github.panpf.sketch.drawable.ReuseGifDrawable
import com.github.panpf.sketch.drawable.SketchKoralGifDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isGif
import com.github.panpf.sketch.request.DisplayRequest

class KoralGifDrawableDecoder(
    private val sketch: Sketch,
    private val request: DisplayRequest,
    private val dataSource: DataSource,
) : DrawableDecoder {

    // todo 实现 GifExtensions 定义的扩展函数

    override suspend fun decodeDrawable(): DrawableDecodeResult {
        val request = request
        val bitmapPoolHelper = sketch.bitmapPoolHelper
        val gifDrawable = when (val source = dataSource) {
            is ByteArrayDataSource -> {
                ReuseGifDrawable(bitmapPoolHelper, source.data)
            }
            is DiskCacheDataSource -> {
                ReuseGifDrawable(bitmapPoolHelper, source.diskCacheEntry.file)
            }
            is ResourceDataSource -> {
                ReuseGifDrawable(bitmapPoolHelper, source.context.resources, source.drawableId)
            }
            is ContentDataSource -> {
                val contentResolver = source.context.contentResolver
                ReuseGifDrawable(bitmapPoolHelper, contentResolver, source.contentUri)
            }
            is FileDataSource -> {
                ReuseGifDrawable(bitmapPoolHelper, source.file)
            }
            is AssetDataSource -> {
                ReuseGifDrawable(bitmapPoolHelper, source.context.assets, source.assetFileName)
            }
            else -> {
                throw Exception("Unsupported DataSource: ${source::class.qualifiedName}")
            }
        }
        val width = gifDrawable.intrinsicWidth
        val height = gifDrawable.intrinsicHeight
        val imageInfo = ImageInfo(MIME_TYPE, width, height, ExifInterface.ORIENTATION_UNDEFINED)
        val drawable = SketchKoralGifDrawable(
            request.key,
            request.uriString,
            imageInfo,
            dataSource.from,
            gifDrawable
        )
        return DrawableDecodeResult(drawable, imageInfo, dataSource.from)
    }

    override fun close() {

    }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch, request: DisplayRequest, fetchResult: FetchResult
        ): KoralGifDrawableDecoder? {
            if (request.disabledAnimationDrawable != true) {
                if (MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)) {
                    return KoralGifDrawableDecoder(sketch, request, fetchResult.dataSource)
                } else if (fetchResult.headerBytes.isGif()) {
                    return KoralGifDrawableDecoder(sketch, request, fetchResult.dataSource)
                }
            }
            return null
        }
    }
}