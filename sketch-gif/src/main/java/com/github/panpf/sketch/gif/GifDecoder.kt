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
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.decodeBitmap
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.LoadRequest
import pl.droidsonroids.gif.GifDrawable

class GifDecoder(sketch: Sketch, request: LoadRequest, dataSource: DataSource) :
    BitmapFactoryDecoder(sketch, request, dataSource), DrawableDecoder {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun decodeDrawable(): DrawableDecodeResult? {
        val request1 = request
        if (request1 !is DisplayRequest) return null
        if (request1.disabledAnimationDrawable == true) return null
        val imageInfo = readImageInfo()
        val gifDrawable = when (val source1 = source) {
            is ByteArrayDataSource -> {
                GifDrawable(source1.data)
            }
            is DiskCacheDataSource -> {
                GifDrawable(source1.diskCacheEntry.file)
            }
            is DrawableResDataSource -> {
                GifDrawable(source1.context.resources, source1.drawableId)
            }
            is ContentDataSource -> {
                GifDrawable(source1.context.contentResolver, source1.contentUri)
            }
            is FileDataSource -> {
                GifDrawable(source1.file)
            }
            is AssetsDataSource -> {
                GifDrawable(source1.context.assets, source1.assetsFilePath)
            }
            else -> {
                throw Exception("Unsupported DataSource: ${source1::class.qualifiedName}")
            }
        }
        return DrawableDecodeResult(gifDrawable, imageInfo, source.from)
    }

    class Factory : Decoder.Factory {

        override fun create(
            sketch: Sketch,
            request: LoadRequest,
            dataSource: DataSource
        ): Decoder? {
            // todo 改进判断方式，参考 coil 改成 BufferedSource
            val mimeType = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                dataSource.decodeBitmap(this)
            }.outMimeType.orEmpty()
            return if (mimeType == "image/gif") {
                GifDecoder(sketch, request, dataSource)
            } else {
                null
            }
        }
    }
}