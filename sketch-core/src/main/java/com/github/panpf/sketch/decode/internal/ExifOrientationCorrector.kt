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
package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.cache.BitmapPoolHelper
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import java.io.IOException
import java.io.InputStream

/**
 * 图片方向纠正器，可让原本被旋转了的图片以正常方向显示
 */
class ExifOrientationCorrector(private val exifOrientation: Int) {

    init {
        if (!hasRotate(exifOrientation)) {
            throw IllegalArgumentException("No rotation: $exifOrientation")
        }
    }

    /**
     * 根据旋转角度计算新图片旋转后的尺寸
     */
    fun rotateSize(imageInfo: ImageInfo): ImageInfo {
        val matrix = Matrix()
        initializeMatrix(exifOrientation, matrix)
        val newRect = RectF(0F, 0F, imageInfo.width.toFloat(), imageInfo.height.toFloat())
        matrix.mapRect(newRect)
        return ImageInfo(
            imageInfo.mimeType,
            newRect.width().toInt(),
            newRect.height().toInt(),
            imageInfo.exifOrientation
        )
    }


    /**
     * 根据旋转角度计算新图片旋转后的尺寸
     */
    fun rotateSize(options: BitmapFactory.Options) {
        val matrix = Matrix()
        initializeMatrix(exifOrientation, matrix)
        val newRect = RectF(0F, 0F, options.outWidth.toFloat(), options.outHeight.toFloat())
        matrix.mapRect(newRect)
        options.outWidth = newRect.width().toInt()
        options.outHeight = newRect.height().toInt()
    }

    /**
     * 根据旋转角度计算新图片旋转后的尺寸
     */
    fun rotateSize(size: Point) {
        val matrix = Matrix()
        initializeMatrix(exifOrientation, matrix)
        val newRect = RectF(0F, 0F, size.x.toFloat(), size.y.toFloat())
        matrix.mapRect(newRect)
        size.x = newRect.width().toInt()
        size.y = newRect.height().toInt()
    }

    /**
     * 根据图片方向恢复被旋转前的尺寸
     */
    fun reverseRotateRect(srcRect: Rect, imageWidth: Int, imageHeight: Int) {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val rotateDegrees = 360 - getRotateDegrees(exifOrientation)
        when (rotateDegrees) {
            90 -> {
                val top = srcRect.top
                srcRect.top = srcRect.left
                srcRect.left = imageHeight - srcRect.bottom
                srcRect.bottom = srcRect.right
                srcRect.right = imageHeight - top
            }
            180 -> {
                val left = srcRect.left
                val top = srcRect.top
                srcRect.left = imageWidth - srcRect.right
                srcRect.right = imageWidth - left
                srcRect.top = imageHeight - srcRect.bottom
                srcRect.bottom = imageHeight - top
            }
            270 -> {
                val left = srcRect.left
                srcRect.left = srcRect.top
                srcRect.top = imageWidth - srcRect.right
                srcRect.right = srcRect.bottom
                srcRect.bottom = imageWidth - left
            }
        }
    }

    /**
     * 根据图片方向旋转图片
     */
    fun rotateBitmap(bitmap: Bitmap, bitmapPoolHelper: BitmapPoolHelper): Bitmap {
        val matrix = Matrix()
        initializeMatrix(exifOrientation, matrix)

        // 根据旋转角度计算新的图片的尺寸
        val newRect = RectF(0F, 0F, bitmap.width.toFloat(), bitmap.height.toFloat())
        matrix.mapRect(newRect)
        val newWidth = newRect.width().toInt()
        val newHeight = newRect.height().toInt()

        // 角度不能整除90°时新图片会是斜的，因此要支持透明度，这样倾斜导致露出的部分就不会是黑的
        val degrees = getRotateDegrees(exifOrientation)
        var config = bitmap.config ?: Bitmap.Config.ARGB_8888
        if (degrees % 90 != 0 && config != Bitmap.Config.ARGB_8888) {
            config = Bitmap.Config.ARGB_8888
        }
        val result = bitmapPoolHelper.getOrMake(newWidth, newHeight, config)
        matrix.postTranslate(-newRect.left, -newRect.top)
        val canvas = Canvas(result)
        val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(bitmap, matrix, paint)
        return result
    }

    companion object {

        fun fromExifOrientation(exifOrientation: Int): ExifOrientationCorrector? =
            if (hasRotate(exifOrientation)) ExifOrientationCorrector(exifOrientation) else null

        /**
         * 读取图片方向
         *
         * @param inputStream 文件输入流
         * @return exif 保存的原始方向
         */
        @Throws(IOException::class)
        fun readExifOrientation(inputStream: InputStream): Int =
            ExifInterface(inputStream).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

        /**
         * 读取图片方向
         *
         * @param mimeType    图片的类型，某些类型不支持读取旋转角度，需要过滤掉，免得浪费精力
         * @param inputStream 输入流
         * @return exif 保存的原始方向
         */
        @Throws(IOException::class)
        fun readExifOrientation(mimeType: String, inputStream: InputStream): Int =
            if (support(mimeType)) {
                readExifOrientation(inputStream)
            } else {
                ExifInterface.ORIENTATION_UNDEFINED
            }

        /**
         * 读取图片方向
         *
         * @param mimeType   图片的类型，某些类型不支持读取旋转角度，需要过滤掉，免得浪费精力
         * @param dataSource DataSource
         * @return exif 保存的原始方向
         */
        fun readExifOrientation(mimeType: String, dataSource: DataSource): Int =
            if (support(mimeType)) {
                dataSource.newInputStream().use {
                    readExifOrientation(it)
                }
            } else {
                ExifInterface.ORIENTATION_UNDEFINED
            }

        /**
         * 根据mimeType判断该类型的图片是否支持通过ExitInterface读取旋转角度
         *
         * @param mimeType 从图片文件中取出的图片的mimeTye
         */
        fun support(mimeType: String): Boolean = ExifInterface.isSupportedMimeType(mimeType)

        /**
         * 根据exifOrientation判断图片是否被旋转了
         *
         * @param exifOrientation from exif info
         * @return true：已旋转
         */
        fun hasRotate(exifOrientation: Int): Boolean =
            exifOrientation != ExifInterface.ORIENTATION_UNDEFINED
                    && exifOrientation != ExifInterface.ORIENTATION_NORMAL

        fun toName(exifOrientation: Int): String {
            return when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> "ROTATE_90"
                ExifInterface.ORIENTATION_TRANSPOSE -> "TRANSPOSE"
                ExifInterface.ORIENTATION_ROTATE_180 -> "ROTATE_180"
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> "FLIP_VERTICAL"
                ExifInterface.ORIENTATION_ROTATE_270 -> "ROTATE_270"
                ExifInterface.ORIENTATION_TRANSVERSE -> "TRANSVERSE"
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> "FLIP_HORIZONTAL"
                ExifInterface.ORIENTATION_UNDEFINED -> "UNDEFINED"
                ExifInterface.ORIENTATION_NORMAL -> "NORMAL"
                else -> exifOrientation.toString()
            }
        }

        fun getRotateDegrees(exifOrientation: Int): Int = when (exifOrientation) {
            ExifInterface.ORIENTATION_TRANSPOSE,
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180,
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> 180
            ExifInterface.ORIENTATION_TRANSVERSE,
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        fun getTranslation(exifOrientation: Int): Int = when (exifOrientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL,
            ExifInterface.ORIENTATION_FLIP_VERTICAL,
            ExifInterface.ORIENTATION_TRANSPOSE,
            ExifInterface.ORIENTATION_TRANSVERSE -> -1
            else -> 1
        }

        fun initializeMatrix(exifOrientation: Int, matrix: Matrix) {
            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.setRotate(270f)
                    matrix.postScale(-1f, 1f)
                }
                else -> {
                }
            }
        }
    }
}