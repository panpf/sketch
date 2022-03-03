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

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.HandlerThread
import android.os.Looper
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.zoom.block.NewDecodeHandler.DecodeErrorException
import com.github.panpf.sketch.zoom.block.internal.KeyCounter
import java.util.concurrent.atomic.AtomicInteger

/**
 * 碎片解码执行器，负责初始化解码器以及管理解码线程
 */
class NewBlockExecutor constructor(val context: Context, val callback: Callback) {

    companion object {
        private const val NAME = "BlockExecutor"
        private val THREAD_NUMBER = AtomicInteger()
    }

    private val handlerThreadLock = Any()

    var callbackHandler: NewCallbackHandler = NewCallbackHandler(
        Looper.getMainLooper(),
        this,
        context.sketch.bitmapPool,
        context.sketch.logger
    )
    private var handlerThread: HandlerThread? = null
    private var initHandler: NewInitHandler? = null
    private var decodeHandler: NewDecodeHandler? = null
    private val logger = context.sketch.logger

    /**
     * 安装解码线程
     */
    private fun installHandlerThread() {
        if (this@NewBlockExecutor.handlerThread == null) {
            synchronized(handlerThreadLock) {
                if (this@NewBlockExecutor.handlerThread == null) {
                    if (THREAD_NUMBER.get() >= Int.MAX_VALUE) {
                        THREAD_NUMBER.set(0)
                    }
                    val handlerThread =
                        HandlerThread("ImageRegionDecodeThread" + THREAD_NUMBER.addAndGet(1))
                    this@NewBlockExecutor.handlerThread = handlerThread
                    handlerThread.start()
                    logger.v(NAME) {
                        "image region decode thread '${handlerThread.name}' started"
                    }
                    decodeHandler = NewDecodeHandler(context, handlerThread.looper, this)
                    initHandler = NewInitHandler(context, handlerThread.looper, this)
                    callbackHandler.postDelayRecycleDecodeThread()
                }
            }
        }
    }

    /**
     * 初始化解码器，初始化结果会通过Callback的onInitCompleted()或onInitError(Exception)方法回调
     */
    fun submitInit(
        imageUri: String,
        keyCounter: KeyCounter,
        exifOrientation: Int
    ) {
        installHandlerThread()
        initHandler?.postInit(
            imageUri,
            exifOrientation,
            keyCounter.key,
            keyCounter
        )
    }

    /**
     * 提交一个解码请求
     */
    fun submitDecodeBlock(key: Int, block: NewBlock) {
        installHandlerThread()
        decodeHandler?.postDecode(key, block)
    }

    /**
     * 取消所有的解码任务
     */
    fun cleanDecode(why: String) {
        decodeHandler?.clean(why)
    }

    /**
     * 回收所有资源
     */
    fun recycle(why: String) {
        initHandler?.clean(why)
        decodeHandler?.clean(why)
        recycleDecodeThread()
    }

    fun recycleDecodeThread() {
        initHandler?.clean("recycleDecodeThread")
        decodeHandler?.clean("recycleDecodeThread")
        synchronized(handlerThreadLock) {
            val handlerThread = this@NewBlockExecutor.handlerThread
            if (handlerThread != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    handlerThread.quitSafely()
                } else {
                    handlerThread.quit()
                }
                logger.v(NAME) {
                    "image region decode thread '${handlerThread.name}' quit"
                }
                this@NewBlockExecutor.handlerThread = null
            }
        }
    }

    interface Callback {
        val context: Context
        fun onInitCompleted(imageUri: String, decoder: NewImageRegionDecoder)
        fun onInitError(imageUri: String, e: Exception)
        fun onDecodeCompleted(block: NewBlock, bitmap: Bitmap, useTime: Int)
        fun onDecodeError(block: NewBlock, exception: DecodeErrorException)
    }
}