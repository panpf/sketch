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

package me.panpf.sketch.viewfun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DisplayOptions;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.RedisplayListener;
import me.panpf.sketch.request.RequestLevel;
import me.panpf.sketch.uri.UriModel;

/**
 * 点击重试功能，可在显示失败或暂停下载的时候由用户手动点击 {@link android.widget.ImageView} 重新或强制显示图片
 */
public class ClickRetryFunction extends ViewFunction {
    private boolean clickRetryOnDisplayErrorEnabled;
    private boolean clickRetryOnPauseDownloadEnabled;

    private boolean displayError;
    private boolean pauseDownload;

    private FunctionCallbackView view;
    private RedisplayListener redisplayListener;

    public ClickRetryFunction(FunctionCallbackView view) {
        this.view = view;
    }

    @Override
    public boolean onReadyDisplay(@Nullable UriModel uriModel) {
        // 重新走了一遍显示流程，这些要重置
        displayError = false;
        pauseDownload = false;

        view.updateClickable();
        return false;
    }

    @Override
    public boolean onDisplayError(@NonNull ErrorCause errorCause) {
        // 正常的失败才能重试，因此要过滤一下失败原因
        displayError = errorCause != ErrorCause.URI_INVALID && errorCause != ErrorCause.URI_NO_SUPPORT;

        view.updateClickable();
        return false;
    }

    @Override
    public boolean onDisplayCanceled(@NonNull CancelCause cancelCause) {
        pauseDownload = cancelCause == CancelCause.PAUSE_DOWNLOAD;

        view.updateClickable();
        return false;
    }

    /**
     * 点击事件
     *
     * @param v View
     * @return true：已经消费了，不必往下传了
     */
    public boolean onClick(View v) {
        if (isClickable()) {
            if (redisplayListener == null) {
                redisplayListener = new RetryOnPauseDownloadRedisplayListener();
            }

            return view.redisplay(redisplayListener);
        }

        return false;
    }

    public boolean isClickable() {
        return (clickRetryOnDisplayErrorEnabled && displayError) || (clickRetryOnPauseDownloadEnabled && pauseDownload);
    }

    public boolean isClickRetryOnDisplayErrorEnabled() {
        return clickRetryOnDisplayErrorEnabled;
    }

    public boolean isClickRetryOnPauseDownloadEnabled() {
        return clickRetryOnPauseDownloadEnabled;
    }

    /**
     * 设置当暂停下载的时候点击显示图片
     */
    public void setClickRetryOnPauseDownloadEnabled(boolean clickRetryOnPauseDownloadEnabled) {
        this.clickRetryOnPauseDownloadEnabled = clickRetryOnPauseDownloadEnabled;
    }

    /**
     * 设置当失败的时候点击重新显示图片
     */
    public void setClickRetryOnDisplayErrorEnabled(boolean clickRetryOnDisplayErrorEnabled) {
        this.clickRetryOnDisplayErrorEnabled = clickRetryOnDisplayErrorEnabled;
    }

    private class RetryOnPauseDownloadRedisplayListener implements RedisplayListener {

        @Override
        public void onPreCommit(String cacheUri, DisplayOptions cacheOptions) {
            if (clickRetryOnPauseDownloadEnabled && pauseDownload) {
                cacheOptions.setRequestLevel(RequestLevel.NET);
            }
        }
    }
}
