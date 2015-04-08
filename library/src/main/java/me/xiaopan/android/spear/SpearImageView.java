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

package me.xiaopan.android.spear;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * SpearImageView
 */
public class SpearImageView extends ImageView implements SpearImageViewInterface{
    private static final String NAME = "SpearImageView";

    private static final int NONE = -1;
    private static final int FROM_FLAG_COLOR_MEMORY = 0x8800FF00;
    private static final int FROM_FLAG_COLOR_LOCAL = 0x880000FF;
    private static final int FROM_FLAG_COLOR_DISK_CACHE = 0x88FFFF00;
    private static final int FROM_FLAG_COLOR_NETWORK = 0x88FF0000;
    private static final int DEFAULT_PROGRESS_COLOR = 0x22000000;
    private static final int DEFAULT_RIPPLE_COLOR = 0x33000000;
    private static final int RIPPLE_ANIMATION_DURATION_SHORT = 100;
    private static final int RIPPLE_ANIMATION_DURATION_LENGTH = 500;

    private Request displayRequest;
    private MyListener myListener;
    private DisplayOptions displayOptions;
    private DisplayListener displayListener;
    private ProgressListener progressListener;
    private DisplayParams displayParams;

    private int fromFlagColor = NONE;
    private Path fromFlagPath;
    private Paint fromFlagPaint;
    private boolean showFromFlag;

    private int progressColor = DEFAULT_PROGRESS_COLOR;
    private Paint progressPaint;
    private float progress = NONE;
    private boolean showDownloadProgress;

    private View.OnClickListener onClickListener;
    private boolean replacedClickListener;
    private boolean clickRedisplayOnPauseDownload;
    private boolean clickRedisplayOnFailed;
    private boolean isSetImage;

    private int touchX;
    private int touchY;
    private int clickRippleColor = DEFAULT_RIPPLE_COLOR;
    private boolean pressed;
    private boolean showClickRipple;
    private Paint clickRipplePaint;
    private Scroller clickRippleScroller;
    private Runnable clickRippleRefreshRunnable;

    public SpearImageView(Context context) {
        super(context);
    }

    public SpearImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        this.onClickListener = l;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 重新计算三角形的位置
        if(fromFlagPath != null){
            fromFlagPath.reset();
            int x = getWidth()/10;
            int y = getWidth()/10;
            fromFlagPath.moveTo(getPaddingLeft(), getPaddingTop());
            fromFlagPath.lineTo(getPaddingLeft() + x, getPaddingTop());
            fromFlagPath.lineTo(getPaddingLeft(), getPaddingTop() + y);
            fromFlagPath.close();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制按下状态
        if(pressed || (clickRippleScroller != null && clickRippleScroller.computeScrollOffset())){
            if(clickRipplePaint == null){
                clickRipplePaint = new Paint();
                clickRipplePaint.setColor(clickRippleColor);
            }
            canvas.drawCircle(touchX, touchY, clickRippleScroller.getCurrX(), clickRipplePaint);
        }

        // 绘制进度
        if(showDownloadProgress && progress != NONE){
            if(progressPaint == null){
                progressPaint = new Paint();
                progressPaint.setColor(progressColor);
            }
            canvas.drawRect(getPaddingLeft(), getPaddingTop() + (progress * getHeight()), getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingTop() - getPaddingBottom(), progressPaint);
        }

        // 绘制三角形
        if(showFromFlag && fromFlagColor != NONE){
            if(fromFlagPath == null){
                fromFlagPath = new Path();
                int x = getWidth()/10;
                int y = getWidth()/10;
                fromFlagPath.moveTo(getPaddingLeft(), getPaddingTop());
                fromFlagPath.lineTo(getPaddingLeft()+x, getPaddingTop());
                fromFlagPath.lineTo(getPaddingLeft(), getPaddingTop()+y);
                fromFlagPath.close();
            }
            if(fromFlagPaint == null){
                fromFlagPaint = new Paint();
            }
            fromFlagPaint.setColor(fromFlagColor);
            canvas.drawPath(fromFlagPath, fromFlagPaint);
        }
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        if(showClickRipple && this.pressed != pressed){
            this.pressed = pressed;
            if(pressed){
                if(clickRippleScroller == null){
                    clickRippleScroller = new Scroller(getContext(), new DecelerateInterpolator());
                }
                clickRippleScroller.startScroll(0, 0, computeRippleRadius(), 0, RIPPLE_ANIMATION_DURATION_LENGTH);
                if(clickRippleRefreshRunnable == null){
                    clickRippleRefreshRunnable = new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                            if(clickRippleScroller.computeScrollOffset()){
                                post(this);
                            }
                        }
                    };
                }
                post(clickRippleRefreshRunnable);
            }

            invalidate();
        }
    }

    /**
     * 计算涟漪的半径
     * @return 涟漪的半径
     */
    private int computeRippleRadius(){
        int centerX = getWidth()/2;
        int centerY = getHeight()/2;
        // 当按下位置在第一或第四象限的时候，比较按下位置在左上角到右下角这条线上距离谁最远就以谁为半径，否则在左下角到右上角这条线上比较
        if((touchX < centerX && touchY < centerY) || (touchX > centerX && touchY > centerY)) {
            int toLeftTopXDistance = touchX;
            int toLeftTopYDistance = touchY;
            int toLeftTopDistance = (int) Math.sqrt((toLeftTopXDistance * toLeftTopXDistance) + (toLeftTopYDistance * toLeftTopYDistance));
            int toRightBottomXDistance = Math.abs(touchX - getWidth());
            int toRightBottomYDistance = Math.abs(touchY - getHeight());
            int toRightBottomDistance = (int) Math.sqrt((toRightBottomXDistance * toRightBottomXDistance) + (toRightBottomYDistance * toRightBottomYDistance));
            return toLeftTopDistance > toRightBottomDistance ? toLeftTopDistance : toRightBottomDistance;
        }else{
           int toLeftBottomXDistance = touchX;
           int toLeftBottomYDistance = Math.abs(touchY - getHeight());
           int toLeftBottomDistance = (int) Math.sqrt((toLeftBottomXDistance * toLeftBottomXDistance) + (toLeftBottomYDistance * toLeftBottomYDistance));
           int toRightTopXDistance = Math.abs(touchX - getWidth());
           int toRightTopYDistance = touchY;
           int toRightTopDistance = (int) Math.sqrt((toRightTopXDistance * toRightTopXDistance) + (toRightTopYDistance * toRightTopYDistance));
           return toLeftBottomDistance > toRightTopDistance ? toLeftBottomDistance : toRightTopDistance;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(showClickRipple && event.getAction() == MotionEvent.ACTION_DOWN && !pressed){
            touchX = (int) event.getX();
            touchY = (int) event.getY();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!isSetImage && displayParams != null){
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + "：" + "restore image on attached to window" + " - " + displayParams.uri);
            }
            Spear.with(getContext()).display(displayParams, SpearImageView.this).fire();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isSetImage = false;
        if(displayRequest != null && !displayRequest.isFinished()){
            displayRequest.cancel();
        }
        final Drawable oldDrawable = getDrawable();
        if(oldDrawable != null){
            super.setImageDrawable(null);
            notifyDrawable("onDetachedFromWindow", oldDrawable, false);
        }
    }

    @Override
    public void setImageDrawable(Drawable newDrawable) {
        final Drawable oldDrawable = getDrawable();
        super.setImageDrawable(newDrawable);

        if(newDrawable != null){
            notifyDrawable("setImageDrawable:newDrawable", newDrawable, true);
        }
        if(oldDrawable != null){
            notifyDrawable("setImageDrawable:oldDrawable", oldDrawable, false);
        }
    }

    @Override
    public void onDisplay() {

    }

    @Override
    public Request displayImage(String uri){
        return Spear.with(getContext()).display(uri, this).fire();
    }

    @Override
    public Request displayFilImage(String imageFilePath){
        return Spear.with(getContext()).display(imageFilePath, this).fire();
    }

    @Override
    public Request displayResourceImage(int drawableResId){
        return Spear.with(getContext()).display(UriScheme.DRAWABLE.createUri(String.valueOf(drawableResId)), this).fire();
    }

    @Override
    public Request displayAssetImage(String imageFileName){
        return Spear.with(getContext()).display(UriScheme.ASSET.createUri(imageFileName), this).fire();
    }

    @Override
    public Request displayContentImage(Uri uri){
        return Spear.with(getContext()).display(uri != null ? UriScheme.ASSET.createUri(uri.toString()):null, this).fire();
    }

    @Override
    public DisplayOptions getDisplayOptions() {
        return displayOptions;
    }

    @Override
    public void setDisplayOptions(DisplayOptions displayOptions) {
        this.displayOptions = displayOptions;
    }

    @Override
    public void setDisplayOptions(Enum<?> optionsName) {
        this.displayOptions = (DisplayOptions) Spear.getOptions(optionsName);
    }

    @Override
    public DisplayListener getDisplayListener(boolean isPauseDownload){
        if(showFromFlag || showDownloadProgress || (isPauseDownload && clickRedisplayOnPauseDownload) || clickRedisplayOnFailed){
            if(myListener == null){
                myListener = new MyListener();
            }
            return myListener;
        }else{
            return displayListener;
        }
    }

    @Override
    public void setDisplayListener(DisplayListener displayListener) {
        this.displayListener = displayListener;
    }

    @Override
    public ProgressListener getProgressListener(){
        if(showDownloadProgress){
            if(myListener == null){
                myListener = new MyListener();
            }
            return myListener;
        }else{
            return progressListener;
        }
    }

    @Override
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Request getDisplayRequest() {
        return displayRequest;
    }

    @Override
    public void setDisplayRequest(Request displayRequest) {
        this.displayRequest = displayRequest;
    }

    @Override
    public DisplayParams getDisplayParams() {
        return displayParams;
    }

    @Override
    public void setDisplayParams(DisplayParams displayParams) {
        this.displayParams = displayParams;
        this.isSetImage = true;
        if(replacedClickListener){
            setOnClickListener(onClickListener);
            if(onClickListener == null){
                setClickable(false);
            }
            replacedClickListener = false;
        }
    }

    /**
     * 设置当暂停下载的时候是否点击重新显示
     * @param clickRedisplayOnPauseDownload 当暂停下载的时候是否点击重新显示
     */
    public void setClickRedisplayOnPauseDownload(boolean clickRedisplayOnPauseDownload) {
        this.clickRedisplayOnPauseDownload = clickRedisplayOnPauseDownload;
    }

    /**
     * 设置当失败的时候是否点击重新显示
     * @param clickRedisplayOnFailed 当失败的时候是否点击重新显示
     */
    public void setClickRedisplayOnFailed(boolean clickRedisplayOnFailed) {
        this.clickRedisplayOnFailed = clickRedisplayOnFailed;
    }

    /**
     * 设置是否显示点击涟漪效果，开启后按下的时候会在ImageView表面显示一个黑色半透明的涟漪效果，此功能需要注册点击事件或设置Clickable为true
     * @param showClickRipple 是否显示点击涟漪效果
     */
    public void setShowClickRipple(boolean showClickRipple) {
        this.showClickRipple = showClickRipple;
    }

    /**
     * 设置是否显示下载进度
     * @param showDownloadProgress 是否显示进度
     */
    public void setShowDownloadProgress(boolean showDownloadProgress) {
        this.showDownloadProgress = showDownloadProgress;
    }

    /**
     * 设置点击涟漪效果的颜色
     * @param clickRippleColor 点击涟漪效果的颜色
     */
    public void setClickRippleColor(int clickRippleColor) {
        this.clickRippleColor = clickRippleColor;
        if(clickRipplePaint != null){
            clickRipplePaint.setColor(clickRippleColor);
        }
    }

    /**
     * 设置进度的颜色
     * @param progressColor 进度的颜色
     */
    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        if(progressPaint != null){
            progressPaint.setColor(progressColor);
        }
    }

    /**
     * 设置是否开启调试模式，开启后会在View的左上角显示一个纯色三角形，红色代表本次是从网络加载的，黄色代表本次是从本地加载的，绿色代表本次是从内存加载的
     * @param showFromFlag 是否开启调试模式
     */
    public void setShowFromFlag(boolean showFromFlag) {
        boolean oldDebugMode = this.showFromFlag;
        this.showFromFlag = showFromFlag;
        if(oldDebugMode){
            fromFlagColor = NONE;
            invalidate();
        }
    }

    /**
     * 修改Drawable显示状态
     * @param callingStation 调用位置
     * @param drawable Drawable
     * @param isDisplayed 是否已显示
     */
    private static void notifyDrawable(String callingStation, Drawable drawable, final boolean isDisplayed) {
        if(drawable instanceof BindBitmapDrawable){
            BindBitmapDrawable bindBitmapDrawable = (BindBitmapDrawable) drawable;
            DisplayRequest displayRequest = bindBitmapDrawable.getDisplayRequest();
            if(displayRequest != null && !displayRequest.isFinished()){
                displayRequest.cancel();
            }
        }else if (drawable instanceof RecycleDrawable) {
            ((RecycleDrawable) drawable).setIsDisplayed(callingStation, isDisplayed);
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                notifyDrawable(callingStation, layerDrawable.getDrawable(i), isDisplayed);
            }
        }
    }

    private class MyListener implements DisplayListener, ProgressListener, View.OnClickListener{
        @Override
        public void onStarted() {
            if(showFromFlag){
                fromFlagColor = NONE;
            }
            if(showDownloadProgress){
                progress = 0;
            }
            if(showFromFlag || showDownloadProgress){
                invalidate();
            }
            if(displayListener != null){
                displayListener.onStarted();
            }
        }

        @Override
        public void onCompleted(ImageFrom imageFrom) {
            if(showFromFlag){
                if(imageFrom != null){
                    switch (imageFrom){
                        case MEMORY_CACHE: fromFlagColor = FROM_FLAG_COLOR_MEMORY; break;
                        case DISK_CACHE: fromFlagColor = FROM_FLAG_COLOR_DISK_CACHE; break;
                        case NETWORK: fromFlagColor = FROM_FLAG_COLOR_NETWORK; break;
                        case LOCAL: fromFlagColor = FROM_FLAG_COLOR_LOCAL; break;
                    }
                }else{
                    fromFlagColor = NONE;
                }
            }
            if(showDownloadProgress){
                progress = NONE;
            }
            if(showFromFlag || showDownloadProgress){
                invalidate();
            }
            if(displayListener != null){
                displayListener.onCompleted(imageFrom);
            }
        }

        @Override
        public void onFailed(FailCause failCause) {
            if(showFromFlag){
                fromFlagColor = NONE;
            }
            if(showDownloadProgress){
                progress = NONE;
            }
            if(showDownloadProgress || showFromFlag){
                invalidate();
            }
            if(clickRedisplayOnFailed){
                SpearImageView.super.setOnClickListener(this);
                replacedClickListener = true;
            }
            if (displayListener != null){
                displayListener.onFailed(failCause);
            }
        }

        @Override
        public void onCanceled(CancelCause cancelCause) {
            if(cancelCause != null && cancelCause == CancelCause.PAUSE_DOWNLOAD && clickRedisplayOnPauseDownload){
                SpearImageView.super.setOnClickListener(this);
                replacedClickListener = true;
            }
            if(displayListener != null){
                displayListener.onCanceled(cancelCause);
            }
        }

        @Override
        public void onUpdateProgress(int totalLength, int completedLength) {
            if(showDownloadProgress){
                progress = (float) completedLength/totalLength;
                invalidate();
            }
            if(progressListener != null){
                progressListener.onUpdateProgress(totalLength, completedLength);
            }
        }

        @Override
        public void onClick(View v) {
            if(displayParams != null){
                Spear.with(getContext()).display(displayParams, SpearImageView.this).level(RequestHandleLevel.NET).fire();
            }
        }
    }
}
