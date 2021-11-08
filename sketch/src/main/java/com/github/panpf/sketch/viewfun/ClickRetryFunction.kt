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
package com.github.panpf.sketch.viewfun

import com.github.panpf.sketch.request.*

/**
 * 点击重试功能，可在显示失败或暂停下载的时候由用户手动点击 [android.widget.ImageView] 重新或强制显示图片
 */
class ClickRetryFunction(private val view: FunctionCallbackView) : ViewFunction() {

    /**
     * 设置当失败的时候点击重新显示图片
     */
    var isClickRetryOnDisplayErrorEnabled = false

    /**
     * 设置当暂停下载的时候点击显示图片
     */
    var isClickRetryOnPauseDownloadEnabled = false

    val isClickable: Boolean
        get() = isClickRetryOnDisplayErrorEnabled && displayError || isClickRetryOnPauseDownloadEnabled && pauseDownload

    private var displayError = false
    private var pauseDownload = false
    private var redisplayListener: RedisplayListener? = null

    override fun onReadyDisplay(uri: String): Boolean {
        // 重新走了一遍显示流程，这些要重置
        displayError = false
        pauseDownload = false
        view.updateClickable()
        return false
    }

    override fun onDisplayError(errorCause: ErrorCause): Boolean {
        // 正常的失败才能重试，因此要过滤一下失败原因
        displayError =
            errorCause != ErrorCause.URI_INVALID && errorCause != ErrorCause.URI_NO_SUPPORT
        view.updateClickable()
        return false
    }

    override fun onDisplayCanceled(cancelCause: CancelCause): Boolean {
        pauseDownload = cancelCause == CancelCause.PAUSE_DOWNLOAD
        view.updateClickable()
        return false
    }

    /**
     * 点击事件
     *
     * @return true：已经消费了，不必往下传了
     */
    fun onClick(): Boolean {
        if (isClickable) {
            if (redisplayListener == null) {
                redisplayListener = RetryOnPauseDownloadRedisplayListener()
            }
            return view.redisplay(redisplayListener)
        }
        return false
    }

    private inner class RetryOnPauseDownloadRedisplayListener : RedisplayListener {
        override fun onPreCommit(cacheUri: String, cacheOptions: DisplayOptions) {
            if (isClickRetryOnPauseDownloadEnabled && pauseDownload) {
                cacheOptions.requestLevel = RequestLevel.NET
            }
        }
    }
}