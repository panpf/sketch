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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import me.panpf.sketch.SLog;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DisplayCache;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.shaper.ImageShaper;
import me.panpf.sketch.uri.UriModel;

/**
 * 显示下载进度功能，会在 {@link android.widget.ImageView} 上面显示一个黑色半透明蒙层显示下载进度，蒙层会随着进度渐渐变小
 */
public class ShowDownloadProgressFunction extends ViewFunction {
    static final int DEFAULT_MASK_COLOR = 0x22000000;

    private static final String NAME = "ShowProgressFunction";
    private static final int NONE = -1;

    private FunctionPropertyView view;
    private int maskColor = DEFAULT_MASK_COLOR;
    private ImageShaper maskShaper;

    private Paint maskPaint;
    private float progress = NONE;
    private Rect bounds;

    public ShowDownloadProgressFunction(@NonNull FunctionPropertyView view) {
        this.view = view;
    }

    @Override
    public boolean onReadyDisplay(@Nullable UriModel uriModel) {
        long newProgress = uriModel != null && uriModel.isFromNet() ? 0 : NONE;
        boolean needRefresh = progress != newProgress;
        progress = newProgress;
        return needRefresh;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (progress == NONE) {
            return;
        }

        ImageShaper shaper = getMaskShaper();
        if (shaper != null) {
            canvas.save();
            try {
                if (bounds == null) {
                    bounds = new Rect();
                }
                bounds.set(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
                Path maskPath = shaper.getPath(bounds);
                canvas.clipPath(maskPath);
            } catch (UnsupportedOperationException e) {
                SLog.e(NAME, "The current environment doesn't support clipPath has shut down automatically hardware acceleration");
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                e.printStackTrace();
            }
        }

        if (maskPaint == null) {
            maskPaint = new Paint();
            maskPaint.setColor(maskColor);
            maskPaint.setAntiAlias(true);
        }
        canvas.drawRect(
                view.getPaddingLeft(),
                view.getPaddingTop() + (progress * view.getHeight()),
                view.getWidth() - view.getPaddingLeft() - view.getPaddingRight(),
                view.getHeight() - view.getPaddingTop() - view.getPaddingBottom(),
                maskPaint);

        if (shaper != null) {
            canvas.restore();
        }
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

    public boolean setMaskColor(@ColorInt int maskColor) {
        if (this.maskColor == maskColor) {
            return false;
        }

        this.maskColor = maskColor;
        if (maskPaint != null) {
            maskPaint.setColor(maskColor);
        }
        return true;
    }

    private ImageShaper getMaskShaper() {
        if (maskShaper != null) {
            return maskShaper;
        }

        DisplayCache displayCache = view.getDisplayCache();
        ImageShaper shaperFromCacheOptions = displayCache != null ? displayCache.options.getShaper() : null;
        if (shaperFromCacheOptions != null) {
            return shaperFromCacheOptions;
        }

        ImageShaper shaperFromOptions = view.getOptions().getShaper();
        if (shaperFromOptions != null) {
            return shaperFromOptions;
        }

        return null;
    }

    public boolean setMaskShaper(@Nullable ImageShaper maskShaper) {
        if (this.maskShaper == maskShaper) {
            return false;
        }

        this.maskShaper = maskShaper;
        return true;
    }
}
