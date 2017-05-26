/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.feature;

import android.view.View;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.request.RedisplayListener;
import me.xiaopan.sketch.request.RequestLevel;
import me.xiaopan.sketch.request.UriScheme;

/**
 * 点击重试功能，可在显示失败或暂停下载的时候由用户手动点击View重新或强制显示图片
 */
public class ClickRetryFunction extends SketchImageView.Function implements View.OnClickListener {
    private boolean clickRetryOnErrorEnabled;
    private boolean clickRetryOnPauseDownloadEnabled;
    private View.OnClickListener wrapperClickListener;

    private boolean displayError;
    private boolean pauseDownload;

    private View view;
    private ImageViewInterface imageViewInterface;
    private RedisplayListener redisplayListener;

    public ClickRetryFunction(View view, ImageViewInterface imageViewInterface) {
        this.view = view;
        this.imageViewInterface = imageViewInterface;
    }

    @Override
    public boolean onReadyDisplay(UriScheme uriScheme) {
        // 重新走了一遍显示流程，这些要重置
        displayError = false;
        pauseDownload = false;

        updateClickable();

        return false;
    }

    @Override
    public boolean onDisplayStarted() {
        // 重新走了一遍显示流程，这些要重置
        displayError = false;
        pauseDownload = false;

        updateClickable();
        return false;
    }

    @Override
    public boolean onDisplayError(ErrorCause errorCause) {
        // 正常的失败才能重试，因此要过滤一下失败原因
        displayError = errorCause != ErrorCause.URI_NULL_OR_EMPTY && errorCause != ErrorCause.URI_NO_SUPPORT;
        updateClickable();
        return false;
    }

    @Override
    public boolean onDisplayCanceled(CancelCause cancelCause) {
        pauseDownload = cancelCause == CancelCause.PAUSE_DOWNLOAD;
        updateClickable();
        return false;
    }

    @Override
    public void onClick(View v) {
        if ((clickRetryOnErrorEnabled && displayError) || (clickRetryOnPauseDownloadEnabled && pauseDownload)) {
            if (redisplayListener == null) {
                redisplayListener = new RetryOnPauseDownloadRedisplayListener();
            }

            if (imageViewInterface.redisplay(redisplayListener)) {
                return;
            }
        }

        if (wrapperClickListener != null) {
            wrapperClickListener.onClick(v);
        }
    }

    /**
     * 设置当暂停下载的时候点击显示图片
     */
    public void setClickRetryOnPauseDownloadEnabled(boolean clickRetryOnPauseDownloadEnabled) {
        this.clickRetryOnPauseDownloadEnabled = clickRetryOnPauseDownloadEnabled;
        updateClickable();
    }

    /**
     * 设置当失败的时候点击重新显示图片
     */
    public void setClickRetryOnErrorEnabled(boolean clickRetryOnErrorEnabled) {
        this.clickRetryOnErrorEnabled = clickRetryOnErrorEnabled;
        updateClickable();
    }

    public void setWrapperClickListener(View.OnClickListener wrapperClickListener) {
        this.wrapperClickListener = wrapperClickListener;
        updateClickable();
    }

    public void updateClickable() {
        view.setClickable((clickRetryOnErrorEnabled && displayError)
                || (clickRetryOnPauseDownloadEnabled && pauseDownload)
                || wrapperClickListener != null);
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
