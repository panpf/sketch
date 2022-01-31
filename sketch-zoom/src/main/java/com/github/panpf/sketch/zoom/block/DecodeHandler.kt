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
package com.github.panpf.sketch.zoom.block

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.internal.isInBitmapError
import com.github.panpf.sketch.decode.internal.isSrcRectError
import com.github.panpf.sketch.zoom.internal.ImageZoomer
import java.lang.ref.WeakReference

/**
 * 解码处理器，运行在解码线程中，负责解码
 */
class DecodeHandler constructor(looper: Looper, executor: BlockExecutor, imageZoomer: ImageZoomer) :
    Handler(looper) {

    companion object {
        private const val NAME = "DecodeHandler"
        private const val WHAT_DECODE = 1001
    }

    private var disableInBitmap = false
    private val reference: WeakReference<BlockExecutor> = WeakReference(executor)
    private val bitmapPool: BitmapPool = imageZoomer.imageView.sketch.bitmapPool
    private val logger by lazy {
        imageZoomer.imageView.sketch.logger
    }

    override fun handleMessage(msg: Message) {
        val decodeExecutor = reference.get()
        decodeExecutor?.callbackHandler?.cancelDelayDestroyThread()
        if (msg.what == WHAT_DECODE) {
            decode(decodeExecutor, msg.arg1, msg.obj as Block)
        }
        decodeExecutor?.callbackHandler?.postDelayRecycleDecodeThread()
    }

    fun postDecode(key: Int, block: Block) {
        val message = obtainMessage(WHAT_DECODE)
        message.arg1 = key
        message.obj = block
        message.sendToTarget()
    }

    private fun decode(executor: BlockExecutor?, key: Int, block: Block) {
        if (executor == null) {
            logger.w(NAME, "weak reference break. key: $key, block=${block.info}")
            return
        }
        if (block.isExpired(key)) {
            executor.callbackHandler.postDecodeError(
                key, block, DecodeErrorException(
                    DecodeErrorException.CAUSE_BEFORE_KEY_EXPIRED
                )
            )
            return
        }
        if (block.isDecodeParamEmpty) {
            executor.callbackHandler.postDecodeError(
                key, block, DecodeErrorException(
                    DecodeErrorException.CAUSE_DECODE_PARAM_EMPTY
                )
            )
            return
        }
        val regionDecoder = block.decoder
        if (regionDecoder == null || !regionDecoder.isReady) {
            executor.callbackHandler.postDecodeError(
                key, block, DecodeErrorException(
                    DecodeErrorException.CAUSE_DECODER_NULL_OR_NOT_READY
                )
            )
            return
        }
        val srcRect = Rect(block.srcRect)
        val inSampleSize = block.inSampleSize

        // 根据图片方向恢复src区域的真实位置
        val imageSize = regionDecoder.imageSize
        regionDecoder.exifOrientationCorrector
            ?.reverseRotateRect(srcRect, imageSize.x, imageSize.y)
        val options = BitmapFactory.Options()
        options.inSampleSize = inSampleSize
//        val imageType = regionDecoder.imageFormat
//        if (imageType != null) {
//            // todo 使用 DisplayOptions 中的参数
//            options.inPreferredConfig = imageType.getConfig(false)
//        }
        if (!disableInBitmap) {
            bitmapPool.setInBitmapForRegionDecoder(options, srcRect.width(), srcRect.height())
        }
        val time = System.currentTimeMillis()
        var bitmap: Bitmap? = null
        try {
            bitmap = regionDecoder.decodeRegion(srcRect, options)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            val inBitmap = options.inBitmap
            if (inBitmap != null && isInBitmapError(throwable, true)) {
                disableInBitmap = true
                val message =
                    "Bitmap region decode error. Because inBitmap. uri=${regionDecoder.imageUri}"
                logger.e(NAME, throwable, message)

                options.inBitmap = null
                bitmapPool.free(inBitmap)
                try {
                    bitmap = regionDecoder.decodeRegion(srcRect, options)
                } catch (throwable1: Throwable) {
                    throwable1.printStackTrace()
                }
            } else if (isSrcRectError(throwable, imageSize.x, imageSize.y, srcRect)) {
                val message =
                    "Bitmap region decode error. Because srcRect. imageUri=%s, imageSize=%dx%d, imageMimeType=%s, srcRect=%s, inSampleSize=%d".format(
                        regionDecoder.imageUri,
                        regionDecoder.imageSize.x,
                        regionDecoder.imageSize.y,
                        regionDecoder.imageFormat!!.mimeType,
                        srcRect.toString(),
                        options.inSampleSize
                    )
                logger.e(NAME, throwable, message)
            }
        }
        val useTime = (System.currentTimeMillis() - time).toInt()
        if (bitmap == null || bitmap.isRecycled) {
            executor.callbackHandler.postDecodeError(
                key, block, DecodeErrorException(DecodeErrorException.CAUSE_BITMAP_NULL)
            )
            return
        }
        if (block.isExpired(key)) {
            bitmapPool.free(bitmap)
            executor.callbackHandler.postDecodeError(
                key, block, DecodeErrorException(DecodeErrorException.CAUSE_AFTER_KEY_EXPIRED)
            )
            return
        }

        // 旋转图片
        val newBitmap =
            regionDecoder.exifOrientationCorrector?.rotateBitmap(bitmap, bitmapPool)
        if (newBitmap != null && newBitmap != bitmap) {
            bitmap = if (!newBitmap.isRecycled) {
                bitmapPool.free(bitmap)
                newBitmap
            } else {
                executor.callbackHandler.postDecodeError(
                    key,
                    block,
                    DecodeErrorException(DecodeErrorException.CAUSE_ROTATE_BITMAP_RECYCLED)
                )
                return
            }
        }
        if (bitmap.isRecycled) {
            executor.callbackHandler.postDecodeError(
                key, block, DecodeErrorException(DecodeErrorException.CAUSE_BITMAP_RECYCLED)
            )
            return
        }
        executor.callbackHandler.postDecodeCompleted(key, block, bitmap, useTime)
    }

    fun clean(why: String) {
        logger.v(NAME) { "clean. $why" }
        removeMessages(WHAT_DECODE)
    }

    class DecodeErrorException(private val errorCause: Int) : Exception() {
        val causeMessage: String
            get() = when (errorCause) {
                CAUSE_BITMAP_RECYCLED -> "bitmap is recycled"
                CAUSE_BITMAP_NULL -> "bitmap is null or recycled"
                CAUSE_BEFORE_KEY_EXPIRED -> "key expired before decode"
                CAUSE_AFTER_KEY_EXPIRED -> "key expired after decode"
                CAUSE_CALLBACK_KEY_EXPIRED -> "key expired before callback"
                CAUSE_DECODE_PARAM_EMPTY -> "decode param is empty"
                CAUSE_DECODER_NULL_OR_NOT_READY -> "decoder is null or not ready"
                CAUSE_ROTATE_BITMAP_RECYCLED -> "rotate result bitmap is recycled"
                else -> "unknown"
            }

        companion object {
            const val CAUSE_BITMAP_RECYCLED = 1100
            const val CAUSE_BITMAP_NULL = 1101
            const val CAUSE_BEFORE_KEY_EXPIRED = 1102
            const val CAUSE_AFTER_KEY_EXPIRED = 1103
            const val CAUSE_CALLBACK_KEY_EXPIRED = 1104
            const val CAUSE_DECODE_PARAM_EMPTY = 1105
            const val CAUSE_DECODER_NULL_OR_NOT_READY = 1106
            const val CAUSE_ROTATE_BITMAP_RECYCLED = 1107
        }
    }
}