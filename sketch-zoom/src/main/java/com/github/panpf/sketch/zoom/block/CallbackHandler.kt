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
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.github.panpf.sketch.SLog.Companion.wmf
import com.github.panpf.sketch.Sketch.Companion.with
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.BitmapPoolUtils.Companion.freeBitmapToPoolForRegionDecoder
import com.github.panpf.sketch.util.KeyCounter
import com.github.panpf.sketch.zoom.block.DecodeHandler.DecodeErrorException
import java.lang.ref.WeakReference

/**
 * 运行在主线程，负责将执行器的结果发送到主线程
 */
class CallbackHandler(looper: Looper, executor: BlockExecutor) : Handler(looper) {

    companion object {
        private const val NAME = "CallbackHandler"
        private const val WHAT_RECYCLE_DECODE_THREAD = 2001
        private const val WHAT_INIT_COMPLETED = 2002
        private const val WHAT_INIT_FAILED = 2003
        private const val WHAT_DECODE_COMPLETED = 2004
        private const val WHAT_DECODE_FAILED = 2005
    }

    private val bitmapPool: BitmapPool = with(executor.callback.context).configuration.bitmapPool
    private val executorReference: WeakReference<BlockExecutor> = WeakReference(executor)

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            WHAT_RECYCLE_DECODE_THREAD -> recycleDecodeThread()
            WHAT_INIT_COMPLETED -> {
                val initResult = msg.obj as InitResult
                initCompleted(
                    initResult.imageRegionDecoder,
                    initResult.imageUrl,
                    msg.arg1,
                    initResult.keyCounter
                )
            }
            WHAT_INIT_FAILED -> {
                val initErrorResult = msg.obj as InitErrorResult
                initError(
                    initErrorResult.exception,
                    initErrorResult.imageUrl,
                    msg.arg1,
                    initErrorResult.keyCounter
                )
            }
            WHAT_DECODE_COMPLETED -> {
                val decodeResult = msg.obj as DecodeResult
                decodeCompleted(
                    msg.arg1,
                    decodeResult.block,
                    decodeResult.bitmap,
                    decodeResult.useTime
                )
            }
            WHAT_DECODE_FAILED -> {
                val decodeErrorResult = msg.obj as DecodeErrorResult
                decodeError(msg.arg1, decodeErrorResult.block, decodeErrorResult.exception)
            }
        }
    }

    /**
     * 延迟三十秒停止解码线程
     */
    fun postDelayRecycleDecodeThread() {
        cancelDelayDestroyThread()
        val destroyMessage = obtainMessage(WHAT_RECYCLE_DECODE_THREAD)
        sendMessageDelayed(destroyMessage, (30 * 1000).toLong())
    }

    private fun recycleDecodeThread() {
        val executor = executorReference.get()
        executor?.recycleDecodeThread()
    }

    /**
     * 取消停止解码线程的延迟任务
     */
    fun cancelDelayDestroyThread() {
        removeMessages(WHAT_RECYCLE_DECODE_THREAD)
    }

    fun postInitCompleted(
        decoder: ImageRegionDecoder,
        imageUri: String,
        initKey: Int,
        keyCounter: KeyCounter
    ) {
        val message = obtainMessage(WHAT_INIT_COMPLETED)
        message.arg1 = initKey
        message.obj = InitResult(decoder, imageUri, keyCounter)
        message.sendToTarget()
    }

    fun postInitError(e: Exception, imageUri: String, key: Int, keyCounter: KeyCounter) {
        val message = obtainMessage(WHAT_INIT_FAILED)
        message.arg1 = key
        message.obj = InitErrorResult(e, imageUri, keyCounter)
        message.sendToTarget()
    }

    fun postDecodeCompleted(key: Int, block: Block, bitmap: Bitmap, useTime: Int) {
        val message = obtainMessage(WHAT_DECODE_COMPLETED)
        message.arg1 = key
        message.obj = DecodeResult(bitmap, block, useTime)
        message.sendToTarget()
    }

    fun postDecodeError(key: Int, block: Block, exception: DecodeErrorException) {
        val message = obtainMessage(WHAT_DECODE_FAILED)
        message.arg1 = key
        message.obj = DecodeErrorResult(block, exception)
        message.sendToTarget()
    }

    private fun initCompleted(
        decoder: ImageRegionDecoder,
        imageUri: String,
        key: Int,
        keyCounter: KeyCounter
    ) {
        val executor = executorReference.get()
        if (executor == null) {
            wmf(
                NAME,
                "weak reference break. initCompleted. key: %d, imageUri: %s",
                key,
                decoder.imageUri
            )
            decoder.recycle()
            return
        }
        val newKey = keyCounter.key
        if (key != newKey) {
            wmf(
                NAME,
                "init key expired. initCompleted. key: %d. newKey: %d, imageUri: %s",
                key,
                newKey,
                decoder.imageUri
            )
            decoder.recycle()
            return
        }
        executor.callback.onInitCompleted(imageUri, decoder)
    }

    private fun initError(
        exception: Exception,
        imageUri: String,
        key: Int,
        keyCounter: KeyCounter
    ) {
        val executor = executorReference.get()
        if (executor == null) {
            wmf(NAME, "weak reference break. initError. key: %d, imageUri: %s", key, imageUri)
            return
        }
        val newKey = keyCounter.key
        if (key != newKey) {
            wmf(
                NAME,
                "key expire. initError. key: %d. newKey: %d, imageUri: %s",
                key,
                newKey,
                imageUri
            )
            return
        }
        executor.callback.onInitError(imageUri, exception)
    }

    private fun decodeCompleted(key: Int, block: Block, bitmap: Bitmap, useTime: Int) {
        val executor = executorReference.get()
        if (executor == null) {
            wmf(NAME, "weak reference break. decodeCompleted. key: %d, block=%s", key, block.info)
            freeBitmapToPoolForRegionDecoder(bitmap, bitmapPool)
            return
        }
        if (!block.isExpired(key)) {
            executor.callback.onDecodeCompleted(block, bitmap, useTime)
        } else {
            freeBitmapToPoolForRegionDecoder(bitmap, bitmapPool)
            executor.callback.onDecodeError(
                block,
                DecodeErrorException(DecodeErrorException.CAUSE_CALLBACK_KEY_EXPIRED)
            )
        }
    }

    private fun decodeError(key: Int, block: Block, exception: DecodeErrorException) {
        val executor = executorReference.get()
        if (executor == null) {
            wmf(NAME, "weak reference break. decodeError. key: %d, block=%s", key, block.info)
            return
        }
        executor.callback.onDecodeError(block, exception)
    }

    private class DecodeResult(
        var bitmap: Bitmap,
        var block: Block,
        var useTime: Int
    )

    private class DecodeErrorResult(
        var block: Block,
        var exception: DecodeErrorException
    )

    private class InitResult(
        var imageRegionDecoder: ImageRegionDecoder,
        var imageUrl: String,
        var keyCounter: KeyCounter
    )

    private class InitErrorResult(
        var exception: Exception,
        var imageUrl: String,
        var keyCounter: KeyCounter
    )
}