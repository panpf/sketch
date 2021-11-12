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
import com.github.panpf.sketch.cache.BitmapPoolUtils.Companion.freeBitmapToPool
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.LoadRequest

/**
 * 解码图片
 */
abstract class DecodeHelper {
    /**
     * 当前 [DecodeHelper] 是否可以解码指定类型的图片
     *
     * @param request      [LoadRequest]
     * @param dataSource   [DataSource]. 图片数据源，用于读取图片数据
     * @param imageType    [ImageType]. 图片类型
     * @param boundOptions [BitmapFactory.Options]. 图片尺寸和类型信息
     * @return true：可以
     */
    abstract fun match(
        request: LoadRequest, dataSource: DataSource,
        imageType: ImageType?, boundOptions: BitmapFactory.Options
    ): Boolean

    /**
     * 解码图片
     *
     * @param request         [LoadRequest]
     * @param dataSource      [DataSource]. 图片数据源，用于读取图片数据
     * @param imageType       [ImageType]. 图片类型
     * @param boundOptions    [BitmapFactory.Options]. 图片尺寸和类型信息
     * @param decodeOptions   [BitmapFactory.Options]. 图片解码选项，在这之前会配置好
     * [BitmapFactory.Options.inPreferredConfig] 和 [BitmapFactory.Options.inPreferQualityOverSpeed] 属性
     * @param exifOrientation 图片方向
     * @return [DecodeResult] 解码结果
     * @throws DecodeException 解码失败
     */
    @Throws(DecodeException::class)
    abstract fun decode(
        request: LoadRequest,
        dataSource: DataSource,
        imageType: ImageType?,
        boundOptions: BitmapFactory.Options,
        decodeOptions: BitmapFactory.Options,
        exifOrientation: Int
    ): DecodeResult

    /**
     * 共子类纠正图片方向
     *
     * @param orientationCorrector [ImageOrientationCorrector] 图片方向纠正器
     * @param decodeResult         [DecodeResult] 解码结果
     * @param exifOrientation      图片方向
     * @param request              [LoadRequest]
     * @throws CorrectOrientationException 纠正方向失败了
     */
    @Throws(CorrectOrientationException::class)
    protected fun correctOrientation(
        orientationCorrector: ImageOrientationCorrector, decodeResult: DecodeResult,
        exifOrientation: Int, request: LoadRequest
    ) {
        if (decodeResult !is BitmapDecodeResult) {
            return
        }
        val bitmap = decodeResult.bitmap
        val newBitmap =
            orientationCorrector.rotate(bitmap, exifOrientation, request.configuration.bitmapPool)
        if (newBitmap != null && newBitmap != bitmap) {
            if (!newBitmap.isRecycled) {
                freeBitmapToPool(bitmap, request.configuration.bitmapPool)
                decodeResult.bitmap = newBitmap
                decodeResult.isProcessed = true
            } else {
                val orientationName = ImageOrientationCorrector.toName(exifOrientation)
                throw CorrectOrientationException("Bitmap recycled. exifOrientation=$orientationName")
            }
        }
    }
}