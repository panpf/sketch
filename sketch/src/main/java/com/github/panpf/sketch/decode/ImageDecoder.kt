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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageDecodeUtils.Companion.decodeBitmap
import com.github.panpf.sketch.decode.ImageDecodeUtils.Companion.decodeError
import com.github.panpf.sketch.decode.ImageType.Companion.valueOfMimeType
import com.github.panpf.sketch.process.ImageProcessor
import com.github.panpf.sketch.request.ErrorCause
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.uri.GetDataSourceException
import com.github.panpf.sketch.util.ExifInterface
import java.util.*

/**
 * 图片解码器，工作内容如下：
 *  1. 读取图片的尺寸、格式和方向信息
 *  1. 计算采样比例、选择合适的 [Bitmap.Config]
 *  1. 解码图片
 *
 * 使用 [ImageProcessor] 处理图片
 *  1. 缓存经过处理的图片
 */
class ImageDecoder {

    companion object {
        private const val NAME = "ImageDecoder"
    }

    private val timeAnalyze = DecodeTimeAnalyze()
    private val decodeHelperList = listOf(
        TransformCacheDecodeHelper(),
        GifDecodeHelper(),
        ThumbnailModeDecodeHelper(),
        NormalDecodeHelper()
    )
    private val resultProcessorList = listOf(
        ProcessImageResultProcessor(),
        ProcessedResultCacheProcessor()
    )

    /**
     * 解码入口方法，统计解码时间、调用解码方法以及后续处理
     *
     * @param request [LoadRequest]
     * @return [DecodeResult]
     * @throws DecodeException 解码失败了
     */
    @Throws(DecodeException::class)
    fun decode(request: LoadRequest): DecodeResult {
        var result: DecodeResult? = null
        return try {
            var startTime: Long = 0
            if (isLoggable(SLog.VERBOSE)) {
                startTime = timeAnalyze.decodeStart()
            }
            result = doDecode(request)
            if (isLoggable(SLog.VERBOSE)) {
                timeAnalyze.decodeEnd(startTime, NAME, request.key)
            }
            try {
                doProcess(request, result)
            } catch (e: ProcessException) {
                result.recycle(request.configuration.bitmapPool)
                throw DecodeException(e, ErrorCause.DECODE_PROCESS_IMAGE_FAIL)
            }
            result
        } catch (e: DecodeException) {
            result?.recycle(request.configuration.bitmapPool)
            throw e
        } catch (tr: Throwable) {
            result?.recycle(request.configuration.bitmapPool)
            throw DecodeException(tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION)
        }
    }

    /**
     * 执行具体解码，这个方法里只读取出解码所需的一些属性，然后再交给具体的 [DecodeHelper] 去解码
     *
     * @param request [LoadRequest]
     * @return [DecodeResult]
     * @throws DecodeException 解码失败了
     */
    @Throws(DecodeException::class)
    private fun doDecode(request: LoadRequest): DecodeResult {
        val dataSource: DataSource = try {
            request.getDataSource(false)
        } catch (e: GetDataSourceException) {
            decodeError(request, null, NAME, "Unable create DataSource", e)
            throw DecodeException(
                "Unable create DataSource",
                e,
                ErrorCause.DECODE_UNABLE_CREATE_DATA_SOURCE
            )
        }

        // Decode bounds and mime info
        val boundOptions = BitmapFactory.Options()
        boundOptions.inJustDecodeBounds = true
        try {
            decodeBitmap(dataSource, boundOptions)
        } catch (e: Throwable) {
            decodeError(request, dataSource, NAME, "Unable read bound information", e)
            throw DecodeException(
                "Unable read bound information",
                e,
                ErrorCause.DECODE_UNABLE_READ_BOUND_INFORMATION
            )
        }

        // Exclude images with a width of less than or equal to 1
        if (boundOptions.outWidth <= 1 || boundOptions.outHeight <= 1) {
            val cause = String.format(
                Locale.US,
                "Image width or height less than or equal to 1px. imageSize: %dx%d",
                boundOptions.outWidth,
                boundOptions.outHeight
            )
            decodeError(request, dataSource, NAME, cause, null)
            throw DecodeException(cause, ErrorCause.DECODE_BOUND_RESULT_IMAGE_SIZE_INVALID)
        }

        // Read image orientation
        var exifOrientation = ExifInterface.ORIENTATION_UNDEFINED
        if (!request.options.isCorrectImageOrientationDisabled) {
            val imageOrientationCorrector = request.configuration.orientationCorrector
            exifOrientation =
                imageOrientationCorrector.readExifOrientation(boundOptions.outMimeType, dataSource)
        }
        val imageType = valueOfMimeType(boundOptions.outMimeType)

        // Set whether priority is given to quality or speed
        val decodeOptions = BitmapFactory.Options()
        if (request.options.isInPreferQualityOverSpeed) {
            decodeOptions.inPreferQualityOverSpeed = true
        }

        // Setup preferred bitmap config
        var newConfig = request.options.bitmapConfig
        if (newConfig == null && imageType != null) {
            newConfig = imageType.getConfig(request.options.isLowQualityImage)
        }
        if (newConfig != null) {
            decodeOptions.inPreferredConfig = newConfig
        }
        var decodeResult: DecodeResult? = null
        for (decodeHelper in decodeHelperList) {
            if (decodeHelper.match(request, dataSource, imageType, boundOptions)) {
                decodeResult = decodeHelper.decode(
                    request,
                    dataSource,
                    imageType,
                    boundOptions,
                    decodeOptions,
                    exifOrientation
                )
                break
            }
        }
        return if (decodeResult != null) {
            decodeResult
        } else {
            decodeError(request, null, NAME, "No matching DecodeHelper", null)
            throw DecodeException(
                "No matched DecodeHelper",
                ErrorCause.DECODE_NO_MATCHING_DECODE_HELPER
            )
        }
    }

    /**
     * 执行后续的处理，包括转换、缓存
     *
     * @param request [LoadRequest]
     * @param result  [DecodeResult]
     * @throws ProcessException 处理失败了
     */
    @Throws(ProcessException::class)
    private fun doProcess(request: LoadRequest, result: DecodeResult?) {
        if (result == null || result.isBanProcess) {
            return
        }
        for (resultProcessor in resultProcessorList) {
            resultProcessor.process(request, result)
        }
    }

    override fun toString(): String {
        return NAME
    }
}