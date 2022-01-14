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
package com.github.panpf.sketch.zoom.internal.block

import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.SLog.Companion.vmf
import com.github.panpf.sketch.SLog.Companion.wmf
import com.github.panpf.sketch.util.KeyCounter
import com.github.panpf.sketch.zoom.internal.BlockDisplayer

/**
 * 碎片解码器
 */
class BlockDecoder(private val blockDisplayer: BlockDisplayer) {

    companion object {
        private const val NAME = "BlockDecoder"
    }

    private val initKeyCounter: KeyCounter = KeyCounter()
    var decoder: ImageRegionDecoder? = null
        private set
    private var running = false
    private var initializing = false

    /**
     * 设置新的图片
     */
    fun setImage(imageUri: String?, correctImageOrientationDisabled: Boolean) {
        clean("setImage")
        decoder?.recycle()
        decoder = null
        if (imageUri?.isNotEmpty() == true) {
            initializing = true
            running = initializing
            blockDisplayer.blockExecutor.submitInit(
                imageUri,
                initKeyCounter,
                correctImageOrientationDisabled
            )
        } else {
            initializing = false
            running = initializing
        }
    }

    /**
     * 解码
     */
    fun decodeBlock(block: Block) {
        if (!isReady) {
            wmf(NAME, "not ready. decodeBlock. %s", block.info)
            return
        }
        block.decoder = decoder
        blockDisplayer.blockExecutor.submitDecodeBlock(block.key, block)
    }

    fun clean(why: String) {
        if (isLoggable(SLog.VERBOSE)) {
            vmf(NAME, "clean. %s", why)
        }
        initKeyCounter.refresh()
    }

    fun recycle(why: String) {
        if (isLoggable(SLog.VERBOSE)) {
            vmf(NAME, "recycle. %s", why)
        }
        decoder?.recycle()
    }

    fun initCompleted(imageUri: String, decoder: ImageRegionDecoder) {
        if (isLoggable(SLog.VERBOSE)) {
            vmf(NAME, "init completed. %s", imageUri)
        }
        initializing = false
        this.decoder = decoder
    }

    fun initError(imageUri: String, e: Exception) {
        if (isLoggable(SLog.DEBUG)) {
            vmf(NAME, "init failed. %s. %s", e.message ?: "", imageUri)
        }
        initializing = false
    }

    val isReady: Boolean
        get() = running && decoder?.isReady == true

    val isInitializing: Boolean = running && initializing
}