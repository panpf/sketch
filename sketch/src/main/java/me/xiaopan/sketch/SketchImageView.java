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

package me.xiaopan.sketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import me.xiaopan.sketch.util.SketchUtils;

public class SketchImageView extends ImageView implements ImageViewInterface {
    protected static final String NAME = "SketchImageView";

    private DisplayRequest displayRequest;
    private MyListener myListener;
    private DisplayOptions displayOptions = new DisplayOptions();
    private DisplayListener displayListener;
    private DownloadProgressListener downloadProgressListener;
    private DisplayParams displayParams;
    private View.OnClickListener onClickListener;
    private boolean replacedClickListener;
    private boolean clickDisplayOnPauseDownload;
    private boolean clickRedisplayOnFailed;
    private boolean isSetImage;

    private ShowImageFromFunction showImageFromFunction;
    private ShowProgressFunction showProgressFunction;
    private ShowPressedFunction showPressedFunction;
    private ShowGifFlagFunction showGifFlagFunction;

    private ImageShapeFunction imageShapeFunction = new ImageShapeFunction(this);

    public SketchImageView(Context context) {
        super(context);
    }

    public SketchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SketchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        this.onClickListener = l;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(showProgressFunction != null){
            showProgressFunction.onLayout(changed, left, top, right, bottom);
        }
        if(showPressedFunction != null){
            showPressedFunction.onLayout(changed, left, top, right, bottom);
        }
        if(showImageFromFunction != null){
            showImageFromFunction.onLayout(changed, left, top, right, bottom);
        }
        if(showGifFlagFunction != null){
            showGifFlagFunction.onLayout(changed, left, top, right, bottom);
        }

        imageShapeFunction.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(showPressedFunction != null){
            showPressedFunction.draw(canvas);
        }
        if(showProgressFunction != null){
            showProgressFunction.draw(canvas);
        }
        if(showImageFromFunction != null){
            showImageFromFunction.draw(canvas);
        }
        if(showGifFlagFunction != null){
            showGifFlagFunction.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(showPressedFunction != null){
            showPressedFunction.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!isSetImage && displayParams != null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, "：", "restore image on attached to window", " - ", displayParams.uri));
            }
            Sketch.with(getContext()).display(displayParams, SketchImageView.this).commit();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        this.isSetImage = false;
        if (displayRequest != null && !displayRequest.isFinished()) {
            displayRequest.cancel();
        }

        final Drawable oldDrawable = getDrawable();
        if (oldDrawable != null && notifyDrawable("onDetachedFromWindow", oldDrawable, false)) {
            super.setImageDrawable(null);
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        final Drawable oldDrawable = getDrawable();
        super.setImageURI(uri);
        final Drawable newDrawable = getDrawable();

        // 图片确实改变了就处理一下旧的图片
        if (oldDrawable != newDrawable) {
            notifyDrawable("setImageURI:oldDrawable", oldDrawable, false);

            // 不显示GIF角标
            if (showGifFlagFunction != null && showGifFlagFunction.isGifDrawable) {
                showGifFlagFunction.setIsGifDrawable(false);
                invalidate();
            }

            if(showImageFromFunction != null){
                showImageFromFunction.setImageFrom(null);
                invalidate();
            }

            displayParams = null;
            if(displayRequest != null && !displayRequest.isFinished()){
                displayRequest.cancel();
            }
            displayRequest = null;
        }
    }

    @Override
    public void setImageResource(int resId) {
        final Drawable oldDrawable = getDrawable();
        super.setImageResource(resId);
        final Drawable newDrawable = getDrawable();

        // 图片确实改变了就处理一下旧的图片
        if (oldDrawable != newDrawable) {
            notifyDrawable("setImageResource:oldDrawable", oldDrawable, false);

            // 不显示GIF角标
            if (showGifFlagFunction != null && showGifFlagFunction.isGifDrawable) {
                showGifFlagFunction.setIsGifDrawable(false);
                invalidate();
            }

            if(showImageFromFunction != null){
                showImageFromFunction.setImageFrom(null);
                invalidate();
            }

            displayParams = null;
            if(displayRequest != null && !displayRequest.isFinished()){
                displayRequest.cancel();
            }
            displayRequest = null;
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        final Drawable oldDrawable = getDrawable();
        super.setImageDrawable(drawable);
        final Drawable newDrawable = getDrawable();

        // 图片确实改变了
        if (oldDrawable != newDrawable) {
            notifyDrawable("setImageDrawable:oldDrawable", oldDrawable, false);
            boolean newDrawableFromSketch = notifyDrawable("setImageDrawable:newDrawable", newDrawable, true);

            // 刷新GIF标志
            if(showGifFlagFunction != null){
                boolean newDrawableIsGif = SketchUtils.isGifDrawable(newDrawable);
                if (newDrawableIsGif != showGifFlagFunction.isGifDrawable) {
                    showGifFlagFunction.setIsGifDrawable(newDrawableIsGif);
                    invalidate();
                }
            }

            if(!newDrawableFromSketch){
                if(showImageFromFunction != null){
                    showImageFromFunction.setImageFrom(null);
                    invalidate();
                }

                displayParams = null;
                if(displayRequest != null && !displayRequest.isFinished()){
                    displayRequest.cancel();
                }
                displayRequest = null;
            }
        }
    }

    @Override
    public Drawable getDrawable() {
        return super.getDrawable();
    }

    @Override
    public View getSelf() {
        return this;
    }

    @Override
    public void onDisplay() {
        this.isSetImage = true;
        if (replacedClickListener) {
            setOnClickListener(onClickListener);
            if (onClickListener == null) {
                setClickable(false);
            }
            replacedClickListener = false;
        }
    }

    @Override
    public DisplayRequest displayImage(String uri) {
        return Sketch.with(getContext()).display(uri, this).commit();
    }

    @Override
    public DisplayRequest displayResourceImage(int drawableResId) {
        return Sketch.with(getContext()).displayFromResource(drawableResId, this).commit();
    }

    @Override
    public DisplayRequest displayAssetImage(String imageFileName) {
        return Sketch.with(getContext()).displayFromAsset(imageFileName, this).commit();
    }

    @Override
    public DisplayRequest displayURIImage(Uri uri) {
        return Sketch.with(getContext()).displayFromURI(uri, this).commit();
    }

    @Override
    public DisplayRequest displayInstalledAppIcon(String packageName, int versionCode) {
        return Sketch.with(getContext()).display(DefaultLocalImagePreprocessor.createInstalledAppIconUri(packageName, versionCode), this).commit();
    }

    @Override
    public DisplayOptions getOptions() {
        return displayOptions;
    }

    @Override
    public void setOptions(DisplayOptions newDisplayOptions) {
        if (newDisplayOptions == null) {
            this.displayOptions.reset();
        } else {
            this.displayOptions.copy(newDisplayOptions);
        }
    }

    @Override
    public void setOptionsByName(Enum<?> optionsName) {
        setOptions(Sketch.getDisplayOptions(optionsName));
    }

    @Override
    public DisplayListener getDisplayListener(boolean isPauseDownload) {
        if (
        showImageFromFunction != null
                || showProgressFunction != null
                || (isPauseDownload && clickDisplayOnPauseDownload) || clickRedisplayOnFailed) {
            if (myListener == null) {
                myListener = new MyListener();
            }
            return myListener;
        } else {
            return displayListener;
        }
    }

    @Override
    public void setDisplayListener(DisplayListener displayListener) {
        this.displayListener = displayListener;
    }

    public DownloadProgressListener getDownloadProgressListener() {
        if (showProgressFunction != null) {
            if (myListener == null) {
                myListener = new MyListener();
            }
            return myListener;
        } else {
            return downloadProgressListener;
        }
    }

    public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
    }

    @Override
    public DisplayRequest getDisplayRequest() {
        return displayRequest;
    }

    @Override
    public void setDisplayRequest(DisplayRequest displayRequest) {
        this.displayRequest = displayRequest;
    }

    @Override
    public DisplayParams getDisplayParams() {
        return displayParams;
    }

    @Override
    public void setDisplayParams(DisplayParams displayParams) {
        this.displayParams = displayParams;
    }

    /**
     * 设置当暂停下载的时候点击显示图片
     *
     * @param clickDisplayOnPauseDownload true：是
     */
    public void setClickDisplayOnPauseDownload(boolean clickDisplayOnPauseDownload) {
        this.clickDisplayOnPauseDownload = clickDisplayOnPauseDownload;
    }

    /**
     * 设置当失败的时候点击重新显示图片
     *
     * @param clickRedisplayOnFailed true：是
     */
    public void setClickRedisplayOnFailed(boolean clickRedisplayOnFailed) {
        this.clickRedisplayOnFailed = clickRedisplayOnFailed;
    }

    /**
     * 设置是否显示下载进度，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     */
    public void setShowDownloadProgress(boolean showDownloadProgress) {
        this.showProgressFunction = showDownloadProgress ? new ShowProgressFunction(this, imageShapeFunction) : null;
    }

    /**
     * 设置下载进度蒙层的颜色
     */
    @SuppressWarnings("unused")
    public void setDownloadProgressColor(int downloadProgressColor) {
        if(showProgressFunction != null){
            showProgressFunction.setDownloadProgressColor(downloadProgressColor);
        }
    }

    /**
     * 是否显示按下状态
     */
    @SuppressWarnings("unused")
    public boolean isShowPressedStatus() {
        return showPressedFunction != null;
    }

    /**
     * 设置是否显示按下状态，开启后按下的时候会在ImageView表面覆盖一个黑色半透明图层，长按的时候还会有类似Android5.0的涟漪效果。此功能需要注册点击事件或设置Clickable为true
     */
    public void setShowPressedStatus(boolean showPressedStatus) {
        this.showPressedFunction = showPressedStatus ? new ShowPressedFunction(this, imageShapeFunction) : null;
    }

    /**
     * 设置按下状态的颜色
     */
    @SuppressWarnings("unused")
    public void setPressedStatusColor(int pressedStatusColor) {
        if(showPressedFunction != null){
            showPressedFunction.setPressedStatusColor(pressedStatusColor);
        }
    }

    /**
     * 是否显示图片来源
     */
    @SuppressWarnings("unused")
    public boolean isShowImageFrom() {
        return showImageFromFunction != null;
    }

    /**
     * 设置是否显示图片来源，开启后会在View的左上角显示一个纯色三角形，红色代表本次是从网络加载的，黄色代表本次是从本地加载的，绿色代表本次是从内存加载的
     */
    public void setShowImageFrom(boolean showImageFrom) {
        ShowImageFromFunction oldShowImageFromFunction = showImageFromFunction;
        showImageFromFunction = showImageFrom ? new ShowImageFromFunction(this) : null;
        if(oldShowImageFromFunction != null){
            invalidate();
        }
    }

    /**
     * 获取GIF图片标识
     */
    @SuppressWarnings("unused")
    public Drawable getGifFlagDrawable() {
        return showGifFlagFunction != null ? showGifFlagFunction.getGifFlagDrawable() : null;
    }

    /**
     * 设置GIF标识图片
     */
    @SuppressWarnings("unused")
    public void setGifFlagDrawable(Drawable gifFlagDrawable) {
        showGifFlagFunction = gifFlagDrawable != null ? new ShowGifFlagFunction(this, gifFlagDrawable) : null;
    }

    /**
     * 设置GIF标识图片
     */
    public void setGifFlagDrawable(int gifFlagResId) {
        setGifFlagDrawable(getResources().getDrawable(gifFlagResId));
    }

    /**
     * 获取图片形状，下载进度和按下效果的蒙层会适应此形状
     */
    @SuppressWarnings("unused")
    public ImageShape getImageShape() {
        return imageShapeFunction.getImageShape();
    }

    /**
     * 设置图片形状，下载进度和按下效果的蒙层会适应此形状
     */
    public void setImageShape(ImageShape imageShape) {
        imageShapeFunction.setImageShape(imageShape);
    }

    /**
     * 获取图片形状的圆角角度，只有图片形状是ROUNDED_RECT的时候此参数才有用
     */
    @SuppressWarnings("unused")
    public int getImageShapeRoundedRadius() {
        return imageShapeFunction.getRoundedRadius();
    }

    /**
     * 设置图片形状的圆角角度，只有图片形状是ROUNDED_RECT的时候此参数才有用
     */
    public void setImageShapeRoundedRadius(int radius) {
        imageShapeFunction.setRoundedRadius(radius);
    }

    /**
     * 获取图片来源
     */
    @SuppressWarnings("unused")
    public ImageFrom getImageFrom() {
        return showImageFromFunction != null ? showImageFromFunction.getImageFrom() : null;
    }

    /**
     * 修改Drawable显示状态
     *
     * @param callingStation 调用位置
     * @param drawable       Drawable
     * @param isDisplayed    是否已显示
     * @return true：drawable或其子Drawable是RecycleDrawable
     */
    private static boolean notifyDrawable(String callingStation, Drawable drawable, final boolean isDisplayed) {
        if (drawable == null) {
            return false;
        } else if (drawable instanceof BindFixedRecycleBitmapDrawable) {
            BindFixedRecycleBitmapDrawable bindFixedRecycleBitmapDrawable = (BindFixedRecycleBitmapDrawable) drawable;
            DisplayRequest displayRequest = bindFixedRecycleBitmapDrawable.getDisplayRequest();
            if (displayRequest != null && !displayRequest.isFinished()) {
                displayRequest.cancel();
            }
            bindFixedRecycleBitmapDrawable.setIsDisplayed(callingStation, isDisplayed);
            return true;
        } else if (drawable instanceof RecycleDrawable) {
            ((RecycleDrawable) drawable).setIsDisplayed(callingStation, isDisplayed);
            return true;
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            boolean result = false;
            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                result |= notifyDrawable(callingStation, layerDrawable.getDrawable(i), isDisplayed);
            }
            return result;
        } else {
            return false;
        }
    }

    private class MyListener implements DisplayListener, DownloadProgressListener, View.OnClickListener {
        @Override
        public void onStarted() {
            boolean needInvokeInvalidate = false;
            if (showImageFromFunction != null) {
                //noinspection ConstantConditions
                needInvokeInvalidate |= showImageFromFunction.onDisplayStarted();
            }
            if(showProgressFunction != null){
                needInvokeInvalidate |= showProgressFunction.onDisplayStarted();
            }
            if (needInvokeInvalidate) {
                invalidate();
            }
            if (displayListener != null) {
                displayListener.onStarted();
            }
        }

        @Override
        public void onCompleted(ImageFrom imageFrom, String mimeType) {
            boolean needInvokeInvalidate = false;
            if (showImageFromFunction != null) {
                //noinspection ConstantConditions
                needInvokeInvalidate |= showImageFromFunction.onDisplayCompleted(imageFrom, mimeType);
            }
            if(showProgressFunction != null){
                needInvokeInvalidate |= showProgressFunction.onDisplayCompleted(imageFrom, mimeType);
            }
            if (needInvokeInvalidate) {
                invalidate();
            }
            if (displayListener != null) {
                displayListener.onCompleted(imageFrom, mimeType);
            }
        }

        @Override
        public void onFailed(FailedCause failedCause) {
            boolean needInvokeInvalidate = false;
            if (showImageFromFunction != null) {
                //noinspection ConstantConditions
                needInvokeInvalidate |= showImageFromFunction.onDisplayFailed(failedCause);
            }
            if(showProgressFunction != null){
                needInvokeInvalidate |= showProgressFunction.onDisplayFailed(failedCause);
            }
            if (needInvokeInvalidate) {
                invalidate();
            }
            if (clickRedisplayOnFailed && failedCause != FailedCause.URI_NULL_OR_EMPTY && failedCause != FailedCause.IMAGE_VIEW_NULL && failedCause != FailedCause.URI_NO_SUPPORT) {
                SketchImageView.super.setOnClickListener(this);
                replacedClickListener = true;
            }
            if (displayListener != null) {
                displayListener.onFailed(failedCause);
            }
        }

        @Override
        public void onCanceled(CancelCause cancelCause) {
            if (cancelCause != null && cancelCause == CancelCause.PAUSE_DOWNLOAD && clickDisplayOnPauseDownload) {
                SketchImageView.super.setOnClickListener(this);
                replacedClickListener = true;
            }
            if (displayListener != null) {
                displayListener.onCanceled(cancelCause);
            }
        }

        @Override
        public void onUpdateDownloadProgress(int totalLength, int completedLength) {
            if(showProgressFunction != null){
                showProgressFunction.setProgress((float) completedLength / totalLength);
                invalidate();
            }
            if (downloadProgressListener != null) {
                downloadProgressListener.onUpdateDownloadProgress(totalLength, completedLength);
            }
        }

        @Override
        public void onClick(View v) {
            if (displayParams != null) {
                Sketch.with(getContext()).display(displayParams, SketchImageView.this).requestLevel(RequestLevel.NET).commit();
            }
        }
    }

    /**
     * 图片的形状
     */
    public enum ImageShape {
        RECT,
        CIRCLE,
        ROUNDED_RECT,
    }
}
