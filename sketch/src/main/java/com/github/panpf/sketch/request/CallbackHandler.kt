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
package com.github.panpf.sketch.request

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.github.panpf.sketch.util.SketchUtils.Companion.isMainThread

class CallbackHandler {
    companion object {
        private const val WHAT_RUN_COMPLETED = 33001
        private const val WHAT_RUN_FAILED = 33002
        private const val WHAT_RUN_CANCELED = 33003
        private const val WHAT_RUN_UPDATE_PROGRESS = 33004
        private const val WHAT_CALLBACK_STARTED = 44001
        private const val WHAT_CALLBACK_FAILED = 44002
        private const val WHAT_CALLBACK_CANCELED = 44003
        private const val PARAM_FAILED_CAUSE = "failedCause"
        private const val PARAM_CANCELED_CAUSE = "canceledCause"

        private val handler = Handler(Looper.getMainLooper()) { msg ->
            when (msg.what) {
                WHAT_RUN_COMPLETED -> (msg.obj as BaseRequest).runCompletedInMain()
                WHAT_RUN_CANCELED -> (msg.obj as BaseRequest).runCanceledInMain()
                WHAT_RUN_UPDATE_PROGRESS -> (msg.obj as BaseRequest).runUpdateProgressInMain(
                    msg.arg1,
                    msg.arg2
                )
                WHAT_RUN_FAILED -> (msg.obj as BaseRequest).runErrorInMain()
                WHAT_CALLBACK_STARTED -> (msg.obj as Listener).onStarted()
                WHAT_CALLBACK_FAILED -> (msg.obj as Listener).onError(
                    ErrorCause.valueOf(msg.data.getString(PARAM_FAILED_CAUSE)!!)
                )
                WHAT_CALLBACK_CANCELED -> (msg.obj as Listener).onCanceled(
                    CancelCause.valueOf(msg.data.getString(PARAM_CANCELED_CAUSE)!!)
                )
            }
            true
        }

        /**
         * 推到主线程处理完成
         */
        @JvmStatic
        fun postRunCompleted(request: BaseRequest) {
            if (request.isSync) {
                request.runCompletedInMain()
            } else {
                handler.obtainMessage(WHAT_RUN_COMPLETED, request).sendToTarget()
            }
        }

        /**
         * 推到主线程处理取消
         */
        @JvmStatic
        fun postRunCanceled(request: BaseRequest) {
            if (request.isSync) {
                request.runCanceledInMain()
            } else {
                handler.obtainMessage(WHAT_RUN_CANCELED, request).sendToTarget()
            }
        }

        /**
         * 推到主线程处理失败
         */
        @JvmStatic
        fun postRunError(request: BaseRequest) {
            if (request.isSync) {
                request.runErrorInMain()
            } else {
                handler.obtainMessage(WHAT_RUN_FAILED, request).sendToTarget()
            }
        }

        /**
         * 推到主线程处理进度
         */
        @JvmStatic
        fun postRunUpdateProgress(request: BaseRequest, totalLength: Int, completedLength: Int) {
            if (request.isSync) {
                request.runUpdateProgressInMain(totalLength, completedLength)
            } else {
                handler.obtainMessage(
                    WHAT_RUN_UPDATE_PROGRESS,
                    totalLength,
                    completedLength,
                    request
                )
                    .sendToTarget()
            }
        }

        @JvmStatic
        fun postCallbackStarted(listener: Listener?, sync: Boolean) {
            if (listener != null) {
                if (sync || isMainThread) {
                    listener.onStarted()
                } else {
                    handler.obtainMessage(WHAT_CALLBACK_STARTED, listener).sendToTarget()
                }
            }
        }

        @JvmStatic
        fun postCallbackError(listener: Listener?, errorCause: ErrorCause, sync: Boolean) {
            if (listener != null) {
                if (sync || isMainThread) {
                    listener.onError(errorCause)
                } else {
                    val message = handler.obtainMessage(WHAT_CALLBACK_FAILED, listener)
                    val bundle = Bundle()
                    bundle.putString(PARAM_FAILED_CAUSE, errorCause.name)
                    message.data = bundle
                    message.sendToTarget()
                }
            }
        }

        @JvmStatic
        fun postCallbackCanceled(listener: Listener?, cancelCause: CancelCause, sync: Boolean) {
            if (listener != null) {
                if (sync || isMainThread) {
                    listener.onCanceled(cancelCause)
                } else {
                    val message = handler.obtainMessage(WHAT_CALLBACK_CANCELED, listener)
                    val bundle = Bundle()
                    bundle.putString(PARAM_CANCELED_CAUSE, cancelCause.name)
                    message.data = bundle
                    message.sendToTarget()
                }
            }
        }
    }
}