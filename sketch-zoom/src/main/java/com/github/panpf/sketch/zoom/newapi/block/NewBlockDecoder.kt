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
package com.github.panpf.sketch.zoom.newapi.block

import android.content.Context
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.zoom.block.internal.KeyCounter

/**
 * 碎片解码器
 */
class NewBlockDecoder constructor(
    context: Context,
    private val blockDisplayer: Blocks,
) {

    companion object {
        private const val NAME = "BlockDecoder"
    }

    private val initKeyCounter: KeyCounter = KeyCounter()
    var decoder: NewImageRegionDecoder? = null
        private set
    private var running = false
    private var initializing = false
    private val logger = context.sketch.logger

    /**
     * 设置新的图片
     */
    fun setImage(imageUri: String?, exifOrientation: Int) {
        clean("setImage")
        decoder?.recycle()
        decoder = null
        if (imageUri?.isNotEmpty() == true) {
            initializing = true
            running = initializing
            blockDisplayer.blockExecutor.submitInit(
                imageUri,
                initKeyCounter,
                exifOrientation
            )
        } else {
            initializing = false
            running = initializing
        }
    }

    /**
     * 解码
     */
    fun decodeBlock(block: NewBlock) {
        if (!isReady) {
            logger.w(NAME, "not ready. decodeBlock. ${block.info}")
            return
        }
        block.decoder = decoder
        blockDisplayer.blockExecutor.submitDecodeBlock(block.key, block)
    }

    fun clean(why: String) {
        logger.v(NAME) { "clean. $why" }
        initKeyCounter.refresh()
    }

    fun recycle(why: String) {
        logger.v(NAME) { "recycle. $why" }
        decoder?.recycle()
    }

    fun initCompleted(imageUri: String, decoder: NewImageRegionDecoder) {
        logger.v(NAME) { "init completed. $imageUri" }
        initializing = false
        this.decoder = decoder
    }

    fun initError(imageUri: String, e: Exception) {
        logger.v(NAME) { "init failed. ${e.message ?: ""}. $imageUri" }
        initializing = false
    }

    val isReady: Boolean
        get() = running && decoder?.isReady == true

    val isInitializing: Boolean = running && initializing
}