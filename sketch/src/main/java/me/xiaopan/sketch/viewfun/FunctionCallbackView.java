/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import me.xiaopan.sketch.SketchView;
import me.xiaopan.sketch.request.DisplayCache;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.DownloadProgressListener;
import me.xiaopan.sketch.request.UriScheme;

/**
 * 这个类负责给function回调各种状态
 */
public abstract class FunctionCallbackView extends ImageView implements SketchView {

    OnClickListener wrappedClickListener;
    OnLongClickListener longClickListener;
    DisplayListener wrappedDisplayListener;
    DownloadProgressListener wrappedProgressListener;

    private ViewFunctions functions;

    private ProgressListenerProxy progressListenerProxy;
    private DisplayListenerProxy displayListenerProxy;
    private OnClickListenerProxy clickListenerProxy;

    public FunctionCallbackView(Context context) {
        super(context);
        init();
    }

    public FunctionCallbackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FunctionCallbackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        displayListenerProxy = new DisplayListenerProxy(this);
        progressListenerProxy = new ProgressListenerProxy(this);
        clickListenerProxy = new OnClickListenerProxy(this);

        super.setOnClickListener(clickListenerProxy);
        updateClickable();
    }

    ViewFunctions getFunctions() {
        if (functions == null) {
            synchronized (this) {
                if (functions == null) {
                    functions = new ViewFunctions(this);
                }
            }
        }
        return functions;
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

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (getFunctions().zoomFunction != null && scaleType != ScaleType.MATRIX) {
            getFunctions().zoomFunction.setScaleType(scaleType);
            return;
        }
        super.setScaleType(scaleType);
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
    public void setImageURI(Uri uri) {
        final Drawable oldDrawable = getDrawable();
        super.setImageURI(uri);
        final Drawable newDrawable = getDrawable();

        setDrawable("setImageURI", oldDrawable, newDrawable);
    }

    @Override
    public void setImageResource(int resId) {
        final Drawable oldDrawable = getDrawable();
        super.setImageResource(resId);
        final Drawable newDrawable = getDrawable();

        setDrawable("setImageResource", oldDrawable, newDrawable);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        final Drawable oldDrawable = getDrawable();
        super.setImageDrawable(drawable);
        final Drawable newDrawable = getDrawable();

        setDrawable("setImageDrawable", oldDrawable, newDrawable);
    }

    private void setDrawable(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        if (oldDrawable != newDrawable) {
            if (getFunctions().onDrawableChanged(callPosition, oldDrawable, newDrawable)) {
                invalidate();
            }
        }
    }

    @Override
    public void onReadyDisplay(UriScheme uriScheme) {
        if (getFunctions().onReadyDisplay(uriScheme)) {
            invalidate();
        }
    }

    @Override
    public DisplayOptions getOptions() {
        return getFunctions().requestFunction.getDisplayOptions();
    }

    @Override
    public void setOptions(DisplayOptions newDisplayOptions) {
        if (newDisplayOptions == null) {
            getFunctions().requestFunction.getDisplayOptions().reset();
        } else {
            getFunctions().requestFunction.getDisplayOptions().copy(newDisplayOptions);
        }
    }

    @Override
    public DisplayListener getDisplayListener() {
        return displayListenerProxy;
    }

    @Override
    public void setDisplayListener(DisplayListener displayListener) {
        this.wrappedDisplayListener = displayListener;
    }

    @Override
    public DownloadProgressListener getDownloadProgressListener() {
        if (getFunctions().showProgressFunction != null || wrappedProgressListener != null) {
            return progressListenerProxy;
        } else {
            return null;
        }
    }

    @Override
    public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.wrappedProgressListener = downloadProgressListener;
    }

    @Override
    public DisplayCache getDisplayCache() {
        return getFunctions().requestFunction.getDisplayCache();
    }

    @Override
    public void setDisplayCache(DisplayCache displayCache) {
        getFunctions().requestFunction.setDisplayCache(displayCache);
    }
}
