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

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.SLog.Companion.vmf
import com.github.panpf.sketch.SLog.Companion.wmf
import com.github.panpf.sketch.util.KeyCounter
import java.lang.ref.WeakReference

/**
 * 运行在解码线程中，负责初始化 [BlockDecoder]
 */
internal class InitHandler(looper: Looper, decodeExecutor: BlockExecutor) : Handler(looper) {

    companion object {
        private const val NAME = "InitHandler"
        private const val WHAT_INIT = 1002
    }

    private val reference: WeakReference<BlockExecutor> = WeakReference(decodeExecutor)

    override fun handleMessage(msg: Message) {
        val decodeExecutor = reference.get()
        decodeExecutor?.callbackHandler?.cancelDelayDestroyThread()
        if (msg.what == WHAT_INIT) {
            val wrapper = msg.obj as Wrapper
            init(
                decodeExecutor,
                wrapper.imageUri,
                wrapper.correctImageOrientationDisabled,
                msg.arg1,
                wrapper.keyCounter
            )
        }
        decodeExecutor?.callbackHandler?.postDelayRecycleDecodeThread()
    }

    fun postInit(
        imageUri: String,
        correctImageOrientationDisabled: Boolean,
        key: Int,
        keyCounter: KeyCounter
    ) {
        removeMessages(WHAT_INIT)
        val message = obtainMessage(WHAT_INIT)
        message.arg1 = key
        message.obj = Wrapper(imageUri, correctImageOrientationDisabled, keyCounter)
        message.sendToTarget()
    }

    private fun init(
        decodeExecutor: BlockExecutor?,
        imageUri: String,
        correctImageOrientationDisabled: Boolean,
        key: Int,
        keyCounter: KeyCounter
    ) {
        if (decodeExecutor == null) {
            wmf(NAME, "weak reference break. key: %d, imageUri: %s", key, imageUri)
            return
        }
        var newKey = keyCounter.key
        if (key != newKey) {
            wmf(
                NAME,
                "init key expired. before init. key: %d, newKey: %d, imageUri: %s",
                key,
                newKey,
                imageUri
            )
            return
        }
        val decoder: ImageRegionDecoder = try {
            ImageRegionDecoder.build(
                decodeExecutor.callback.context,
                imageUri,
                correctImageOrientationDisabled
            )
        } catch (e: Exception) {
            e.printStackTrace()
            decodeExecutor.callbackHandler.postInitError(e, imageUri, key, keyCounter)
            return
        }
        if (!decoder.isReady) {
            decodeExecutor.callbackHandler.postInitError(
                Exception("decoder is null or not ready"),
                imageUri,
                key,
                keyCounter
            )
            return
        }
        newKey = keyCounter.key
        if (key != newKey) {
            wmf(
                NAME,
                "init key expired. after init. key: %d, newKey: %d, imageUri: %s",
                key,
                newKey,
                imageUri
            )
            decoder.recycle()
            return
        }
        decodeExecutor.callbackHandler.postInitCompleted(decoder, imageUri, key, keyCounter)
    }

    fun clean(why: String?) {
        if (isLoggable(SLog.VERBOSE)) {
            vmf(NAME, "clean. %s", why!!)
        }
        removeMessages(WHAT_INIT)
    }

    class Wrapper(
        var imageUri: String,
        var correctImageOrientationDisabled: Boolean,
        var keyCounter: KeyCounter
    )

}