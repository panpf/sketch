/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.SketchView;
import me.panpf.sketch.request.DisplayCache;
import me.panpf.sketch.request.DisplayListener;
import me.panpf.sketch.request.DisplayOptions;
import me.panpf.sketch.request.DownloadProgressListener;

/**
 * 这个类负责给 function 回调各种状态
 */
public abstract class FunctionCallbackView extends ImageView implements SketchView {

    @Nullable
    OnClickListener wrappedClickListener;
    @Nullable
    OnLongClickListener longClickListener;
    @Nullable
    DisplayListener wrappedDisplayListener;
    @Nullable
    DownloadProgressListener wrappedProgressListener;

    @Nullable
    private ViewFunctions functions;
    @NonNull
    private ProgressListenerProxy progressListenerProxy;
    @NonNull
    private DisplayListenerProxy displayListenerProxy;
    @NonNull
    private OnClickListenerProxy clickListenerProxy;

    public FunctionCallbackView(@NonNull Context context) {
        this(context, null, 0);
    }

    public FunctionCallbackView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunctionCallbackView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        displayListenerProxy = new DisplayListenerProxy(this);
        progressListenerProxy = new ProgressListenerProxy(this);
        clickListenerProxy = new OnClickListenerProxy(this);

        super.setOnClickListener(clickListenerProxy);
        updateClickable();
    }

    ViewFunctions getFunctions() {
        /* 为什么要搞成延迟 new 的？因为父类会第一时间调用 setDrawable() 方法，
        这个方法里需要用到 functions，即使直接 ViewFunctions functions = new ViewFunctions(this); 都不行  */
        if (functions == null) {
            synchronized (this) {
                if (functions == null) {
                    functions = new ViewFunctions(this);
                }
            }
        }
        return functions;
    }

    public void addViewFunction(@NonNull ViewFunction viewFunction, int priority) {
        getFunctions().addViewFunction(viewFunction, priority);
    }

    public void removeViewFunction(@NonNull ViewFunction viewFunction) {
        getFunctions().removeViewFunction(viewFunction);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        getFunctions().onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        getFunctions().onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getFunctions().onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return getFunctions().onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getFunctions().onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (getFunctions().onDetachedFromWindow()) {
            super.setImageDrawable(null);
        }
    }

    public OnClickListener getOnClickListener() {
        return clickListenerProxy;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        wrappedClickListener = l;
        updateClickable();
    }

    public OnLongClickListener getOnLongClickListener() {
        return longClickListener;
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);
        this.longClickListener = l;
    }

    void updateClickable() {
        setClickable(clickListenerProxy.isClickable());
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        final Drawable oldDrawable = getDrawable();
        super.setImageURI(uri);
        final Drawable newDrawable = getDrawable();

        setDrawable("setImageURI", oldDrawable, newDrawable);
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        final Drawable oldDrawable = getDrawable();
        super.setImageResource(resId);
        final Drawable newDrawable = getDrawable();

        setDrawable("setImageResource", oldDrawable, newDrawable);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        final Drawable oldDrawable = getDrawable();
        super.setImageDrawable(drawable);
        final Drawable newDrawable = getDrawable();

        setDrawable("setImageDrawable", oldDrawable, newDrawable);
    }

    private void setDrawable(@NonNull String callPosition, @Nullable Drawable oldDrawable, @Nullable Drawable newDrawable) {
        if (newDrawable == null) {
            getFunctions().requestFunction.clean();
        }
        if (oldDrawable != newDrawable) {
            if (getFunctions().onDrawableChanged(callPosition, oldDrawable, newDrawable)) {
                invalidate();
            }
        }
    }

    @Override
    public void onReadyDisplay(@NonNull String uri) {
        if (getFunctions().onReadyDisplay(uri)) {
            invalidate();
        }
    }

    @NonNull
    @Override
    public DisplayOptions getOptions() {
        return getFunctions().requestFunction.getDisplayOptions();
    }

    @Override
    public void setOptions(@Nullable DisplayOptions newDisplayOptions) {
        if (newDisplayOptions == null) {
            getFunctions().requestFunction.getDisplayOptions().reset();
        } else {
            getFunctions().requestFunction.getDisplayOptions().copy(newDisplayOptions);
        }
    }

    @Nullable
    @Override
    public DisplayListener getDisplayListener() {
        return displayListenerProxy;
    }

    @Override
    public void setDisplayListener(@Nullable DisplayListener displayListener) {
        this.wrappedDisplayListener = displayListener;
    }

    @Nullable
    @Override
    public DownloadProgressListener getDownloadProgressListener() {
        if (getFunctions().showDownloadProgressFunction != null || wrappedProgressListener != null) {
            return progressListenerProxy;
        } else {
            return null;
        }
    }

    @Override
    public void setDownloadProgressListener(@Nullable DownloadProgressListener downloadProgressListener) {
        this.wrappedProgressListener = downloadProgressListener;
    }

    @Nullable
    @Override
    public DisplayCache getDisplayCache() {
        return getFunctions().requestFunction.getDisplayCache();
    }

    @Override
    public void setDisplayCache(@NonNull DisplayCache displayCache) {
        getFunctions().requestFunction.setDisplayCache(displayCache);
    }

    @Override
    public boolean isUseSmallerThumbnails() {
        return false;
    }
}
