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

package me.xiaopan.android.spear.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.File;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.DisplayListener;
import me.xiaopan.android.spear.request.DisplayOptions;
import me.xiaopan.android.spear.request.ProgressListener;
import me.xiaopan.android.spear.request.RequestFuture;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.util.RecyclingBitmapDrawable;
import me.xiaopan.android.spear.util.Scheme;

/**
 * SpearImageView
 */
public class SpearImageView extends ImageView{
    private static final int NONE = -1;
    private RequestFuture requestFuture;
    private DisplayOptions displayOptions;
    private DisplayListener displayListener;
    private ProgressListener progressListener;

    private Paint paint;
    private Path path;
    private int debugColor = NONE;
    private float progress = NONE;
    private int progressColor = 0x22000000;
    private DebugColorListener debugColorListener;
    private UpdateProgressListener updateProgressListener;
    private ProgressDisplayListener progressDisplayListener;

    public SpearImageView(Context context) {
        super(context);
    }

    public SpearImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 重新计算三角形的位置
        if(path != null){
            path.reset();
            int x = getWidth()/10;
            int y = getWidth()/10;
            path.moveTo(getPaddingLeft(), getPaddingTop());
            path.lineTo(getPaddingLeft()+x, getPaddingTop());
            path.lineTo(getPaddingLeft(), getPaddingTop()+y);
            path.close();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制三角形
        if(debugColor != NONE){
            if(paint == null){
                paint = new Paint();
            }
            paint.setColor(debugColor);
            if(path == null){
                path = new Path();
                int x = getWidth()/10;
                int y = getWidth()/10;
                path.moveTo(getPaddingLeft(), getPaddingTop());
                path.lineTo(getPaddingLeft()+x, getPaddingTop());
                path.lineTo(getPaddingLeft(), getPaddingTop()+y);
                path.close();
            }
            canvas.drawPath(path, paint);
        }

        if(progress != NONE){
            if(paint == null){
                paint = new Paint();
            }
            paint.setColor(progressColor);
            canvas.drawRect(getPaddingLeft(), getPaddingTop() + (progress*getHeight()), getWidth()-getPaddingLeft()-getPaddingRight(), getHeight()-getPaddingTop()-getPaddingBottom(), paint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
            final Drawable previousDrawable = getDrawable();
            if(previousDrawable != null){
                notifyDrawable(previousDrawable, false);
            }
        }
        super.onDetachedFromWindow();
    }

    /**
     * @see android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        // Keep hold of previous Drawable
        final Drawable previousDrawable = getDrawable();

        // Call super to set new Drawable
        super.setImageDrawable(drawable);

        // Notify new Drawable that it is being displayed
        if(drawable != null){
            notifyDrawable(drawable, true);
        }

        // Notify old Drawable so it is no longer being displayed
        if(previousDrawable != null){
            notifyDrawable(previousDrawable, false);
        }
    }

    /**
     * 根据URI设置图片
     * @param uri 支持以下6种Uri
     * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "https://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @return RequestFuture 你可以通过RequestFuture查看请求是否完成或主动取消请求
     */
    public RequestFuture setImageByUri(String uri){
        // 重置角标和进度
        if(debugColor != NONE || progress != NONE){
            debugColor = NONE;
            progress = NONE;
            invalidate();
        }

        requestFuture = Spear.with(getContext())
                .display(uri, this)
                .options(displayOptions)
                .listener(getDisplayListener())
                .progressListener(updateProgressListener!=null?updateProgressListener:progressListener)
                .fire();
        return requestFuture;
    }

    private DisplayListener getDisplayListener(){
        if(Spear.with(getContext()).isDebugMode()){
            if(debugColorListener == null){
                debugColorListener = new DebugColorListener();
            }
            return debugColorListener;
        }else if(updateProgressListener != null){
            if(progressDisplayListener == null){
                progressDisplayListener = new ProgressDisplayListener();
            }
            return progressDisplayListener;
        }else{
            return displayListener;
        }
    }

    /**
     * 根据文件设置图片
     * @param imageFile SD卡上的图片文件
     * @return RequestFuture 你可以通过RequestFuture查看请求是否完成或主动取消请求
     */
    public RequestFuture setImageByFile(File imageFile){
        return setImageByUri(Scheme.FILE.createUri(imageFile.getPath()));
    }

    /**
     * 根据Drawable ID设置图片
     * @param drawableResId Drawable ID
     * @return RequestFuture 你可以通过RequestFuture查看请求是否完成或主动取消请求
     */
    public RequestFuture setImageByResource(int drawableResId){
        return setImageByUri(Scheme.DRAWABLE.createUri(String.valueOf(drawableResId)));
    }

    /**
     * 根据assets文件名称设置图片
     * @param imageFileName ASSETS文件加下的图片文件的名称
     * @return RequestFuture 你可以通过RequestFuture查看请求是否完成或主动取消请求
     */
    public RequestFuture setImageByAssets(String imageFileName){
        return setImageByUri(Scheme.ASSETS.createUri(imageFileName));
    }

    /**
     * 根据Content Uri设置图片
     * @param uri Content Uri 这个URI是其它Content Provider返回的
     * @return RequestFuture 你可以通过RequestFuture查看请求是否完成或主动取消请求
     */
    public RequestFuture setImageByContent(Uri uri){
        return setImageByUri(uri.toString());
    }

    /**
     * 设置显示参数
     * @param displayOptions 显示参数
     */
    public void setDisplayOptions(DisplayOptions displayOptions) {
        this.displayOptions = displayOptions;
    }

    /**
     * 设置显示参数的名称
     * @param optionsName 显示参数的名称
     */
    public void setDisplayOptions(Enum<?> optionsName) {
        this.displayOptions = (DisplayOptions) Spear.getOptions(optionsName);
    }

    /**
     * 设置显示监听器
     * @param displayListener 显示监听器
     */
    public void setDisplayListener(DisplayListener displayListener) {
        this.displayListener = displayListener;
    }

    /**
     * 设置显示进度监听器
     * @param progressListener 进度监听器
     */
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * 获取RequestFuture，你需要对此方法返回的对象进行非null验证，如果你没有调用过setImageBy***系列方法设置图片，那么此方法将一直返回null
     * @return RequestFuture，你可以通过RequestFuture查看请求是否完成或主动取消请求
     */
    public RequestFuture getRequestFuture() {
        return requestFuture;
    }

    /**
     * 显示进度
     * @param showProgress 是否显示进度
     */
    public void setShowProgress(boolean showProgress){
        if(showProgress){
            if(updateProgressListener == null){
                updateProgressListener = new UpdateProgressListener();
            }
        }else{
            if(updateProgressListener != null){
                updateProgressListener = null;
            }
        }
    }

    /**
     * Notifies the drawable that it's displayed state has changed.
     * @param drawable Drawable
     * @param isDisplayed 是否已显示
     */
    private static void notifyDrawable(Drawable drawable, final boolean isDisplayed) {
        if (drawable instanceof RecyclingBitmapDrawable) {
            // The drawable is a CountingBitmapDrawable, so notify it
            ((RecyclingBitmapDrawable) drawable).setIsDisplayed(isDisplayed);
        } else if (drawable instanceof LayerDrawable) {
            // The drawable is a LayerDrawable, so recurse on each layer
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                notifyDrawable(layerDrawable.getDrawable(i), isDisplayed);
            }
        }
    }

    private class DebugColorListener implements DisplayListener{
        @Override
        public void onStarted() {
            debugColor = NONE;
            progress = updateProgressListener!=null?0:NONE;
            invalidate();
            if(displayListener != null){
                displayListener.onStarted();
            }
        }

        @Override
        public void onCompleted(String uri, ImageView imageView, BitmapDrawable drawable, From from) {
            if(from != null){
                switch (from){
                    case MEMORY: debugColor = 0x8800FF00; break;
                    case LOCAL: debugColor = 0x88FFFF00; break;
                    case NETWORK: debugColor = 0x88FF0000; break;
                }
            }else{
                debugColor = NONE;
            }
            progress = NONE;
            invalidate();
            if(displayListener != null){
                displayListener.onCompleted(uri, imageView, drawable, from);
            }
        }

        @Override
        public void onFailed(FailureCause failureCause) {
            debugColor = NONE;
            progress = NONE;
            invalidate();
            if(displayListener != null){
                displayListener.onFailed(failureCause);
            }
        }

        @Override
        public void onCanceled() {
            if(displayListener != null){
                displayListener.onCanceled();
            }
        }
    }

    private class UpdateProgressListener implements ProgressListener{
        @Override
        public void onUpdateProgress(int totalLength, int completedLength) {
            progress = (float) completedLength/totalLength;
            invalidate();
            if(progressListener != null){
                progressListener.onUpdateProgress(totalLength, completedLength);
            }
        }
    }

    private class ProgressDisplayListener implements DisplayListener{

        @Override
        public void onStarted() {
            progress = 0;
            invalidate();
            if(displayListener != null){
                displayListener.onStarted();
            }
        }

        @Override
        public void onCompleted(String uri, ImageView imageView, BitmapDrawable drawable, From from) {
            progress = NONE;
            invalidate();
            if(displayListener != null){
                displayListener.onCompleted(uri, imageView, drawable, from);
            }
        }

        @Override
        public void onFailed(FailureCause failureCause) {
            progress = NONE;
            invalidate();
            if(displayListener != null){
                displayListener.onFailed(failureCause);
            }
        }

        @Override
        public void onCanceled() {
            if(displayListener != null){
                displayListener.onCanceled();
            }
        }
    }
}
