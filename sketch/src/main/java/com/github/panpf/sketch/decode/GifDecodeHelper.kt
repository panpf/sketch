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
package com.github.panpf.sketch.decode

import android.graphics.BitmapFactory
import android.os.Build
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.datasource.*
import com.github.panpf.sketch.drawable.SketchGifFactory
import com.github.panpf.sketch.request.ErrorCause
import com.github.panpf.sketch.request.LoadRequest
import java.io.IOException

class GifDecodeHelper : DecodeHelper() {

    override fun match(
        request: LoadRequest,
        dataSource: DataSource,
        imageType: ImageType?,
        boundOptions: BitmapFactory.Options
    ): Boolean {
        if (imageType == ImageType.GIF && request.options.isDecodeGifImage) {
            if (SketchGifFactory.isExistGifLibrary()) {
                return true
            } else {
                SLog.em(
                    "GifDecodeHelper", "Not found libpl_droidsonroids_gif.so. " +
                            "Please go to “https://github.com/panpf/sketch” find how to import the sketch-gif library"
                )
            }
        }
        return false
    }

    @Throws(DecodeException::class)
    override fun decode(
        request: LoadRequest,
        dataSource: DataSource,
        imageType: ImageType?,
        boundOptions: BitmapFactory.Options,
        decodeOptions: BitmapFactory.Options,
        exifOrientation: Int
    ): DecodeResult {
        return try {
            val imageAttrs = ImageAttrs(
                boundOptions.outMimeType,
                boundOptions.outWidth,
                boundOptions.outHeight,
                exifOrientation
            )

            val gifDrawable = when (dataSource) {
                is DiskCacheDataSource -> SketchGifFactory.createGifDrawable(
                    request.key,
                    request.uri,
                    imageAttrs,
                    dataSource.imageFrom,
                    request.configuration.bitmapPool,
                    dataSource.diskCacheEntry.file
                )
                is DrawableDataSource -> SketchGifFactory.createGifDrawable(
                    request.key,
                    request.uri,
                    imageAttrs,
                    dataSource.imageFrom,
                    request.configuration.bitmapPool,
                    dataSource.context.resources,
                    dataSource.drawableId
                )
                is AssetsDataSource -> SketchGifFactory.createGifDrawable(
                    request.key,
                    request.uri,
                    imageAttrs,
                    dataSource.imageFrom,
                    request.configuration.bitmapPool,
                    dataSource.context.assets,
                    dataSource.assetsFilePath
                )
                is ByteArrayDataSource -> SketchGifFactory.createGifDrawable(
                    request.key,
                    request.uri,
                    imageAttrs,
                    dataSource.imageFrom,
                    request.configuration.bitmapPool,
                    dataSource.data
                )
                is FileDataSource -> SketchGifFactory.createGifDrawable(
                    request.key,
                    request.uri,
                    imageAttrs,
                    dataSource.imageFrom,
                    request.configuration.bitmapPool,
                    dataSource.file
                )
                is ContentDataSource -> SketchGifFactory.createGifDrawable(
                    request.key,
                    request.uri,
                    imageAttrs,
                    dataSource.imageFrom,
                    request.configuration.bitmapPool,
                    dataSource.context.contentResolver,
                    dataSource.contentUri
                )
                else -> throw IllegalArgumentException("Unknown DataSource type: ${dataSource::class.qualifiedName}")
            }

            val result = GifDecodeResult(gifDrawable, imageAttrs, dataSource.imageFrom)
            result.isBanProcess = true
            result
        } catch (e: IOException) {
            throw DecodeException(e, ErrorCause.DECODE_FILE_IO_EXCEPTION)
        } catch (e: NotFoundGifLibraryException) {
            throw DecodeException(e, ErrorCause.DECODE_NOT_FOUND_GIF_LIBRARY)
        } catch (e: UnsatisfiedLinkError) {
            SLog.em(
                "GifDecodeHelper",
                "Didn't find “libpl_droidsonroids_gif.so” file, unable decode the GIF images. " +
                        "Please go to “https://github.com/panpf/sketch” find how to import the sketch-gif library"
            )
            val abis: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Build.SUPPORTED_ABIS.contentToString()
            } else {
                arrayOf(Build.CPU_ABI, Build.CPU_ABI2).contentToString()
            }
            SLog.emf("GifDecodeHelper", "abis=%s", abis)
            request.configuration.callback.onError(NotFoundGifSoException(e))
            throw DecodeException(e, ErrorCause.DECODE_NO_MATCHING_GIF_SO)
        } catch (e: ExceptionInInitializerError) {
            SLog.em(
                "GifDecodeHelper",
                "Didn't find “libpl_droidsonroids_gif.so” file, unable decode the GIF images. " +
                        "Please go to “https://github.com/panpf/sketch” find how to import the sketch-gif library"
            )
            val abis: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Build.SUPPORTED_ABIS.contentToString()
            } else {
                arrayOf(Build.CPU_ABI, Build.CPU_ABI2).contentToString()
            }
            SLog.emf("GifDecodeHelper", "abis=%s", abis)
            if (e is UnsatisfiedLinkError) {
                request.configuration.callback.onError(NotFoundGifSoException((e as UnsatisfiedLinkError)))
            } else {
                request.configuration.callback.onError(NotFoundGifSoException(e))
            }
            throw DecodeException(e, ErrorCause.DECODE_NO_MATCHING_GIF_SO)
        } catch (e: Throwable) {
            SLog.emf(
                "GifDecodeHelper",
                "onDecodeGifImageError. outWidth=%d, outHeight=%d + outMimeType=%s. %s",
                boundOptions.outWidth,
                boundOptions.outHeight,
                boundOptions.outMimeType,
                request.key
            )
            request.configuration.callback.onError(
                DecodeGifException(
                    e,
                    request,
                    boundOptions.outWidth,
                    boundOptions.outHeight,
                    boundOptions.outMimeType
                )
            )
            throw DecodeException(e, ErrorCause.DECODE_UNABLE_CREATE_GIF_DRAWABLE)
        }
    }
}