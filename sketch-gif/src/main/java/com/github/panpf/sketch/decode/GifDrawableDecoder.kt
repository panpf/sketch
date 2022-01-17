package com.github.panpf.sketch.decode

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetsDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.drawable.SketchGifDrawableImpl
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DisplayRequest

// todo 参考 coil 的 gif 实现
class GifDrawableDecoder(
    private val sketch: Sketch,
    private val request: DisplayRequest,
    private val dataSource: DataSource,
    private val imageInfo: ImageInfo,
) : DrawableDecoder {

    companion object {
        const val MIME_TYPE = "image/gif"
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun decodeDrawable(): DrawableDecodeResult {
        val request = request
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
            is ResourceDataSource -> {
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

    override fun close() {

    }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: DisplayRequest,
            fetchResult: FetchResult
        ): GifDrawableDecoder? {
            if (request.disabledAnimationDrawable != true) {
                val imageInfo = fetchResult.imageInfo
                if (imageInfo != null && MIME_TYPE.equals(imageInfo.mimeType, ignoreCase = true)) {
                    return GifDrawableDecoder(sketch, request, fetchResult.dataSource, imageInfo)
                }
            }
            return null
        }
    }
}