/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.util.SketchUtils;

public class CallbackHandler {
    private static final Handler handler;

    private static final int WHAT_RUN_COMPLETED = 33001;
    private static final int WHAT_RUN_FAILED = 33002;
    private static final int WHAT_RUN_CANCELED = 33003;
    private static final int WHAT_RUN_UPDATE_PROGRESS = 33004;

    private static final int WHAT_CALLBACK_STARTED = 44001;
    private static final int WHAT_CALLBACK_FAILED = 44002;
    private static final int WHAT_CALLBACK_CANCELED = 44003;

    private static final String PARAM_FAILED_CAUSE = "failedCause";
    private static final String PARAM_CANCELED_CAUSE = "canceledCause";

    static {
        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_RUN_COMPLETED:
                        ((AsyncRequest) msg.obj).runCompletedInMainThread();
                        break;
                    case WHAT_RUN_CANCELED:
                        ((AsyncRequest) msg.obj).runCanceledInMainThread();
                        break;
                    case WHAT_RUN_UPDATE_PROGRESS:
                        ((AsyncRequest) msg.obj).runUpdateProgressInMainThread(msg.arg1, msg.arg2);
                        break;
                    case WHAT_RUN_FAILED:
                        ((AsyncRequest) msg.obj).runErrorInMainThread();
                        break;

                    case WHAT_CALLBACK_STARTED:
                        ((Listener) msg.obj).onStarted();
                        break;
                    case WHAT_CALLBACK_FAILED:
                        ((Listener) msg.obj).onError(ErrorCause.valueOf(msg.getData().getString(PARAM_FAILED_CAUSE)));
                        break;
                    case WHAT_CALLBACK_CANCELED:
                        ((Listener) msg.obj).onCanceled(CancelCause.valueOf(msg.getData().getString(PARAM_CANCELED_CAUSE)));
                        break;
                }

                return true;
            }
        });
    }

    private CallbackHandler() {
    }

    /**
     * 推到主线程处理完成
     */
    static void postRunCompleted(@NonNull AsyncRequest request) {
        if (request.isSync()) {
            request.runCompletedInMainThread();
        } else {
            handler.obtainMessage(WHAT_RUN_COMPLETED, request).sendToTarget();
        }
    }

    /**
     * 推到主线程处理取消
     */
    static void postRunCanceled(@NonNull AsyncRequest request) {
        if (request.isSync()) {
            request.runCanceledInMainThread();
        } else {
            handler.obtainMessage(WHAT_RUN_CANCELED, request).sendToTarget();
        }
    }

    /**
     * 推到主线程处理失败
     */
    static void postRunError(@NonNull AsyncRequest request) {
        if (request.isSync()) {
            request.runErrorInMainThread();
        } else {
            handler.obtainMessage(WHAT_RUN_FAILED, request).sendToTarget();
        }
    }

    /**
     * 推到主线程处理进度
     */
    static void postRunUpdateProgress(@NonNull AsyncRequest request, int totalLength, int completedLength) {
        if (request.isSync()) {
            request.runUpdateProgressInMainThread(totalLength, completedLength);
        } else {
            handler.obtainMessage(WHAT_RUN_UPDATE_PROGRESS, totalLength, completedLength, request).sendToTarget();
        }
    }

    static void postCallbackStarted(@Nullable Listener listener, boolean sync) {
        if (listener != null) {
            if (sync || SketchUtils.isMainThread()) {
                listener.onStarted();
            } else {
                handler.obtainMessage(WHAT_CALLBACK_STARTED, listener).sendToTarget();
            }
        }
    }

    static void postCallbackError(@Nullable Listener listener, @NonNull ErrorCause errorCause, boolean sync) {
        if (listener != null) {
            if (sync || SketchUtils.isMainThread()) {
                listener.onError(errorCause);
            } else {
                Message message = handler.obtainMessage(WHAT_CALLBACK_FAILED, listener);

                Bundle bundle = new Bundle();
                bundle.putString(PARAM_FAILED_CAUSE, errorCause.name());
                message.setData(bundle);

                message.sendToTarget();
            }
        }
    }

    static void postCallbackCanceled(@Nullable Listener listener, @NonNull CancelCause cancelCause, boolean sync) {
        if (listener != null) {
            if (sync || SketchUtils.isMainThread()) {
                listener.onCanceled(cancelCause);
            } else {
                Message message = handler.obtainMessage(WHAT_CALLBACK_CANCELED, listener);

                Bundle bundle = new Bundle();
                bundle.putString(PARAM_CANCELED_CAUSE, cancelCause.name());
                message.setData(bundle);

                message.sendToTarget();
            }
        }
    }
}
