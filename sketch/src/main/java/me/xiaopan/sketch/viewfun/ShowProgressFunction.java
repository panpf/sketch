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

package me.xiaopan.sketch.viewfun;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.uri.UriModel;

/**
 * 显示下载进度功能，会在ImageView上面显示一个黑色半透明蒙层显示下载进度，蒙层会随着进度渐渐变小
 */
public class ShowProgressFunction extends ViewFunction {
    private static final String NAME = "ShowProgressFunction";

    private static final int NONE = -1;
    private static final int DEFAULT_PROGRESS_COLOR = 0x22000000;
    protected int downloadProgressColor = DEFAULT_PROGRESS_COLOR;
    protected Paint progressPaint;
    protected float progress = NONE;
    private View view;
    private ImageShapeFunction imageShapeFunction;

    public ShowProgressFunction(View view, ImageShapeFunction imageShapeFunction) {
        this.view = view;
        this.imageShapeFunction = imageShapeFunction;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (progress == NONE) {
            return;
        }

        boolean applyMaskClip = imageShapeFunction.getClipPath() != null;
        if (applyMaskClip) {
            canvas.save();
            try {
                canvas.clipPath(imageShapeFunction.getClipPath());
            } catch (UnsupportedOperationException e) {
                SLog.e(NAME, "The current environment doesn't support clipPath has shut down automatically hardware acceleration");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                e.printStackTrace();
            }
        }

        if (progressPaint == null) {
            progressPaint = new Paint();
            progressPaint.setColor(downloadProgressColor);
            progressPaint.setAntiAlias(true);
        }
        canvas.drawRect(
                view.getPaddingLeft(),
                view.getPaddingTop() + (progress * view.getHeight()),
                view.getWidth() - view.getPaddingLeft() - view.getPaddingRight(),
                view.getHeight() - view.getPaddingTop() - view.getPaddingBottom(),
                progressPaint);

        if (applyMaskClip) {
            canvas.restore();
        }
    }

    @Override
    public boolean onReadyDisplay(@Nullable UriModel uriModel) {
        progress = uriModel != null && uriModel.isFromNet() ? 0 : NONE;
        return true;
    }

    @Override
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        progress = (float) completedLength / totalLength;
        return true;
    }

    @Override
    public boolean onDisplayCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
        progress = NONE;
        return true;
    }

    @Override
    public boolean onDisplayError(@NonNull ErrorCause errorCause) {
        progress = NONE;
        return true;
    }

    @Override
    public boolean onDisplayCanceled(@NonNull CancelCause cancelCause) {
        progress = NONE;
        return false;
    }

    @SuppressWarnings("unused")
    public void setDownloadProgressColor(int downloadProgressColor) {
        this.downloadProgressColor = downloadProgressColor;
        if (progressPaint != null) {
            progressPaint.setColor(downloadProgressColor);
        }
    }
}
