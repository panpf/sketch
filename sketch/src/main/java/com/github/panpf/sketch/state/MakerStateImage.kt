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
package com.github.panpf.sketch.state

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.format.Formatter
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.SketchView
import com.github.panpf.sketch.cache.BitmapPoolUtils
import com.github.panpf.sketch.decode.ImageAttrs
import com.github.panpf.sketch.decode.ProcessImageException
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchRefBitmap
import com.github.panpf.sketch.drawable.SketchShapeBitmapDrawable
import com.github.panpf.sketch.process.ImageProcessor
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.request.Resize
import com.github.panpf.sketch.request.ShapeSize
import com.github.panpf.sketch.shaper.ImageShaper
import com.github.panpf.sketch.uri.DrawableUriModel
import com.github.panpf.sketch.uri.UriModel
import com.github.panpf.sketch.util.SketchUtils

/**
 * 可以使用 Options 中配置的 [ImageProcessor] 和 [Resize] 修改原图片，同样支持 [ShapeSize] 和 [ImageShaper]
 */
// TODO: 2017/10/30 重命名为 MakerDrawableStateImage 并像 DrawableStateImage 一样支持 drawable
class MakerStateImage(val resId: Int) : StateImage {

    override fun getDrawable(
        context: Context,
        sketchView: SketchView,
        displayOptions: DisplayOptions
    ): Drawable? {
        var drawable = makeDrawable(Sketch.with(context), displayOptions)
        val shapeSize = displayOptions.shapeSize
        val imageShaper = displayOptions.shaper
        if ((shapeSize != null || imageShaper != null) && drawable is BitmapDrawable) {
            drawable = SketchShapeBitmapDrawable(
                context,
                (drawable as BitmapDrawable?)!!,
                shapeSize,
                imageShaper
            )
        }
        return drawable
    }

    private fun makeDrawable(sketch: Sketch, options: DisplayOptions): Drawable? {
        val configuration = sketch.configuration
        var processor = options.processor
        val resize = options.resize
        val bitmapPool = configuration.bitmapPool

        // 不需要处理的时候直接取出图片返回
        if (processor == null && resize == null) {
            return configuration.context.resources.getDrawable(resId)
        }

        // 从内存缓存中取
        val imageUri = DrawableUriModel.makeUri(resId)
        val uriModel = UriModel.match(sketch, imageUri)
        var memoryCacheKey: String? = null
        if (uriModel != null) {
            memoryCacheKey =
                SketchUtils.makeRequestKey(imageUri, uriModel, options.makeStateImageKey())
        }
        val memoryCache = configuration.memoryCache
        var cachedRefBitmap: SketchRefBitmap? = null
        if (memoryCacheKey != null) {
            cachedRefBitmap = memoryCache[memoryCacheKey]
        }
        if (cachedRefBitmap != null) {
            if (!cachedRefBitmap.isRecycled) {
                return SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE)
            } else {
                memoryCache.remove(memoryCacheKey!!)
            }
        }

        // 读取图片
        var bitmap: Bitmap?
        var allowRecycle = false
        val tempLowQualityImage =
            configuration.isLowQualityImageEnabled || options.isLowQualityImage
        val drawable = configuration.context.resources.getDrawable(
            resId
        )
        if (drawable is BitmapDrawable) {
            bitmap = drawable.bitmap
        } else {
            bitmap = SketchUtils.drawableToBitmap(drawable, tempLowQualityImage, bitmapPool)
            allowRecycle = true
        }
        if (bitmap == null || bitmap.isRecycled) {
            return null
        }

        // 处理图片
        if (processor == null && resize != null) {
            processor = sketch.configuration.resizeProcessor
        }
        val newBitmap: Bitmap = try {
            processor!!.process(sketch, bitmap, resize, tempLowQualityImage)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            val application = sketch.configuration.context
            SLog.emf(
                "MakerStateImage", "onProcessImageError. imageUri: %s. processor: %s. " +
                        "appMemoryInfo: maxMemory=%s, freeMemory=%s, totalMemory=%s",
                DrawableUriModel.makeUri(resId), processor.toString(),
                Formatter.formatFileSize(application, Runtime.getRuntime().maxMemory()),
                Formatter.formatFileSize(application, Runtime.getRuntime().freeMemory()),
                Formatter.formatFileSize(application, Runtime.getRuntime().totalMemory())
            )
            sketch.configuration.callback.onError(
                ProcessImageException(
                    e, DrawableUriModel.makeUri(
                        resId
                    ), processor!!
                )
            )
            if (allowRecycle) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool)
            }
            return null
        }

        // bitmap变化了，说明创建了一张全新的图片，那么就要回收掉旧的图片
        if (newBitmap != bitmap) {
            if (allowRecycle) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool)
            }

            // 新图片不能用说你处理部分出现异常了，直接返回null即可
            if (newBitmap.isRecycled) {
                return null
            }
            bitmap = newBitmap
            allowRecycle = true
        }

        // 允许回收说明是创建了一张新的图片，不能回收说明还是从res中获取的BitmapDrawable可以直接使用
        return if (allowRecycle) {
            val boundsOptions = BitmapFactory.Options()
            boundsOptions.inJustDecodeBounds = true
            BitmapFactory.decodeResource(configuration.context.resources, resId, boundsOptions)
            val uri = DrawableUriModel.makeUri(resId)
            val imageAttrs = ImageAttrs(
                boundsOptions.outMimeType,
                boundsOptions.outWidth,
                boundsOptions.outHeight,
                0
            )
            val newRefBitmap =
                SketchRefBitmap(bitmap, memoryCacheKey!!, uri, imageAttrs, bitmapPool)
            memoryCache.put(memoryCacheKey, newRefBitmap)
            SketchBitmapDrawable(newRefBitmap, ImageFrom.LOCAL)
        } else {
            drawable
        }
    }
}