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

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.request.RequestLevel;
import me.xiaopan.sketch.request.UriScheme;

/**
 * 点击重试功能，可在显示失败或暂停下载的时候由用户手动点击View重新或强制显示图片
 */
public class ClickRetryFunction implements ImageViewFunction, View.OnClickListener {
    private boolean clickRetryOnFailed;
    private boolean clickRetryOnPauseDownload;
    private View.OnClickListener wrapperClickListener;

    private boolean displayFailed;
    private boolean pauseDownload;

    private View view;
    private RequestFunction requestFunction;
    private ImageViewInterface imageViewInterface;

    public ClickRetryFunction(View view, RequestFunction requestFunction, ImageViewInterface imageViewInterface) {
        this.view = view;
        this.requestFunction = requestFunction;
        this.imageViewInterface = imageViewInterface;
    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public boolean onDisplay(UriScheme uriScheme) {
        // 重新走了一遍显示流程，这些要重置
        displayFailed = false;
        pauseDownload = false;

        updateClickable();

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public boolean onDetachedFromWindow() {
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        return false;
    }

    @Override
    public boolean onDisplayStarted() {
        // 重新走了一遍显示流程，这些要重置
        displayFailed = false;
        pauseDownload = false;

        updateClickable();
        return false;
    }

    @Override
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        return false;
    }

    @Override
    public boolean onDisplayCompleted(ImageFrom imageFrom, String mimeType) {
        return false;
    }

    @Override
    public boolean onDisplayFailed(FailedCause failedCause) {
        // 正常的失败才能重试，因此要过滤一下失败原因
        displayFailed = failedCause != FailedCause.URI_NULL_OR_EMPTY && failedCause != FailedCause.URI_NO_SUPPORT;
        updateClickable();
        return false;
    }

    @Override
    public boolean onCanceled(CancelCause cancelCause) {
        pauseDownload = cancelCause == CancelCause.PAUSE_DOWNLOAD;
        updateClickable();
        return false;
    }

    @Override
    public void onClick(View v) {
        if ((clickRetryOnFailed && displayFailed) || (clickRetryOnPauseDownload && pauseDownload)) {
            if (requestFunction.getDisplayParams() != null) {
                Sketch.with(view.getContext()).display(requestFunction.getDisplayParams(), imageViewInterface).requestLevel(RequestLevel.NET).commit();
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
    public void setClickRetryOnPauseDownload(boolean clickDisplayOnPauseDownload) {
        this.clickRetryOnPauseDownload = clickDisplayOnPauseDownload;
        updateClickable();
    }

    /**
     * 设置当失败的时候点击重新显示图片
     */
    public void setClickRetryOnFailed(boolean clickRedisplayOnFailed) {
        this.clickRetryOnFailed = clickRedisplayOnFailed;
        updateClickable();
    }

    public void setWrapperClickListener(View.OnClickListener wrapperClickListener) {
        this.wrapperClickListener = wrapperClickListener;
        updateClickable();
    }

    public void updateClickable() {
        view.setClickable((clickRetryOnFailed && displayFailed)
                || (clickRetryOnPauseDownload && pauseDownload)
                || wrapperClickListener != null);
    }
}
