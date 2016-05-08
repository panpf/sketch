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
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import me.xiaopan.sketch.feture.LocalImagePreprocessor;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.DisplayParams;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.DownloadProgressListener;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.feture.ClickRetryFunction;
import me.xiaopan.sketch.feture.ImageShapeFunction;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.feture.RecyclerCompatFunction;
import me.xiaopan.sketch.feture.RequestFunction;
import me.xiaopan.sketch.feture.ShowGifFlagFunction;
import me.xiaopan.sketch.feture.ShowImageFromFunction;
import me.xiaopan.sketch.feture.ShowPressedFunction;
import me.xiaopan.sketch.feture.ShowProgressFunction;
import me.xiaopan.sketch.request.UriScheme;

public class SketchImageView extends ImageView implements ImageViewInterface {
    private DisplayListener wrapperDisplayListener;
    private DownloadProgressListener wrapperDownloadProgressListener;
    private MyDisplayListener displayListener;
    private MyProgressListener downloadProgressListener;

    private RequestFunction requestFunction;
    private RecyclerCompatFunction recyclerCompatFunction;

    private ShowImageFromFunction showImageFromFunction;
    private ShowProgressFunction showProgressFunction;
    private ShowPressedFunction showPressedFunction;
    private ShowGifFlagFunction showGifFlagFunction;
    private ImageShapeFunction imageShapeFunction;
    private ClickRetryFunction clickRetryFunction;

    public SketchImageView(Context context) {
        super(context);
        init();
    }

    public SketchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SketchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        requestFunction = new RequestFunction(this);
        recyclerCompatFunction = new RecyclerCompatFunction(getContext(), this, requestFunction);

        imageShapeFunction = new ImageShapeFunction(this);
        clickRetryFunction = new ClickRetryFunction(this, requestFunction, this);

        displayListener = new MyDisplayListener();
        downloadProgressListener = new MyProgressListener();

        super.setOnClickListener(clickRetryFunction);

        clickRetryFunction.updateClickable();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        clickRetryFunction.setWrapperClickListener(l);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(showImageFromFunction != null){
            showImageFromFunction.onLayout(changed, left, top, right, bottom);
        }
        if(showProgressFunction != null){
            showProgressFunction.onLayout(changed, left, top, right, bottom);
        }
        if(showGifFlagFunction != null){
            showGifFlagFunction.onLayout(changed, left, top, right, bottom);
        }
        if(showPressedFunction != null){
            showPressedFunction.onLayout(changed, left, top, right, bottom);
        }
        if(imageShapeFunction != null){
            imageShapeFunction.onLayout(changed, left, top, right, bottom);
        }
        if(clickRetryFunction != null){
            clickRetryFunction.onLayout(changed, left, top, right, bottom);
        }
        if(requestFunction != null){
            requestFunction.onLayout(changed,left, top, right, bottom);
        }
        if(recyclerCompatFunction != null){
            recyclerCompatFunction.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(showPressedFunction != null){
            showPressedFunction.onDraw(canvas);
        }
        if(showProgressFunction != null){
            showProgressFunction.onDraw(canvas);
        }
        if(showImageFromFunction != null){
            showImageFromFunction.onDraw(canvas);
        }
        if(showGifFlagFunction != null){
            showGifFlagFunction.onDraw(canvas);
        }
        if(imageShapeFunction != null){
            imageShapeFunction.onDraw(canvas);
        }
        if(clickRetryFunction != null){
            clickRetryFunction.onDraw(canvas);
        }
        if(requestFunction != null){
            requestFunction.onDraw(canvas);
        }
        if(recyclerCompatFunction != null){
            recyclerCompatFunction.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(showPressedFunction != null){
            showPressedFunction.onTouchEvent(event);
        }
        if(showProgressFunction != null){
            showProgressFunction.onTouchEvent(event);
        }
        if(showImageFromFunction != null){
            showImageFromFunction.onTouchEvent(event);
        }
        if(showGifFlagFunction != null){
            showGifFlagFunction.onTouchEvent(event);
        }
        if(imageShapeFunction != null){
            imageShapeFunction.onTouchEvent(event);
        }
        if(clickRetryFunction != null){
            clickRetryFunction.onTouchEvent(event);
        }
        if(requestFunction != null){
            requestFunction.onTouchEvent(event);
        }
        if(recyclerCompatFunction != null){
            recyclerCompatFunction.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if(requestFunction != null){
            requestFunction.onAttachedToWindow();
        }
        if(recyclerCompatFunction != null){
            recyclerCompatFunction.onAttachedToWindow();
        }
        if(showPressedFunction != null){
            showPressedFunction.onAttachedToWindow();
        }
        if(showProgressFunction != null){
            showProgressFunction.onAttachedToWindow();
        }
        if(showGifFlagFunction != null){
            showGifFlagFunction.onAttachedToWindow();
        }
        if(showImageFromFunction != null){
            showImageFromFunction.onAttachedToWindow();
        }
        if(imageShapeFunction != null){
            imageShapeFunction.onAttachedToWindow();
        }
        if(clickRetryFunction != null){
            clickRetryFunction.onAttachedToWindow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        boolean needSetImageNull = false;

        if(requestFunction != null){
            //noinspection ConstantConditions
            needSetImageNull |= requestFunction.onDetachedFromWindow();
        }
        if(recyclerCompatFunction != null){
            needSetImageNull |= recyclerCompatFunction.onDetachedFromWindow();
        }
        if(showPressedFunction != null){
            needSetImageNull |= showPressedFunction.onDetachedFromWindow();
        }
        if(showProgressFunction != null){
            needSetImageNull |= showProgressFunction.onDetachedFromWindow();
        }
        if(showGifFlagFunction != null){
            needSetImageNull |= showGifFlagFunction.onDetachedFromWindow();
        }
        if(showImageFromFunction != null){
            needSetImageNull |= showImageFromFunction.onDetachedFromWindow();
        }
        if(imageShapeFunction != null){
            needSetImageNull |= imageShapeFunction.onDetachedFromWindow();
        }
        if(clickRetryFunction != null){
            needSetImageNull |= clickRetryFunction.onDetachedFromWindow();
        }

        if(needSetImageNull){
            super.setImageDrawable(null);
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        final Drawable oldDrawable = getDrawable();
        super.setImageURI(uri);
        final Drawable newDrawable = getDrawable();

        setDrawable(oldDrawable, newDrawable);
    }

    @Override
    public void setImageResource(int resId) {
        final Drawable oldDrawable = getDrawable();
        super.setImageResource(resId);
        final Drawable newDrawable = getDrawable();

        setDrawable(oldDrawable, newDrawable);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        final Drawable oldDrawable = getDrawable();
        super.setImageDrawable(drawable);
        final Drawable newDrawable = getDrawable();

        setDrawable(oldDrawable, newDrawable);
    }

    private void setDrawable(Drawable oldDrawable, Drawable newDrawable){
        // 图片确实改变了
        if (oldDrawable != newDrawable) {
            boolean needInvokeInvalidate = false;

            if(requestFunction != null){
                //noinspection ConstantConditions
                needInvokeInvalidate |= requestFunction.onDrawableChanged("setImageURI", oldDrawable, newDrawable);
            }
            if(showGifFlagFunction != null){
                needInvokeInvalidate |= showGifFlagFunction.onDrawableChanged("setImageURI", oldDrawable, newDrawable);
            }
            if(showImageFromFunction != null){
                needInvokeInvalidate |= showImageFromFunction.onDrawableChanged("setImageURI", oldDrawable, newDrawable);
            }
            if(showPressedFunction != null){
                needInvokeInvalidate |= showPressedFunction.onDrawableChanged("setImageURI", oldDrawable, newDrawable);
            }
            if(showProgressFunction != null){
                needInvokeInvalidate |= showProgressFunction.onDrawableChanged("setImageURI", oldDrawable, newDrawable);
            }
            if(imageShapeFunction != null){
                needInvokeInvalidate |= imageShapeFunction.onDrawableChanged("setImageURI", oldDrawable, newDrawable);
            }
            if(clickRetryFunction != null){
                needInvokeInvalidate |= clickRetryFunction.onDrawableChanged("setImageURI", oldDrawable, newDrawable);
            }
            if(recyclerCompatFunction != null){
                needInvokeInvalidate |= recyclerCompatFunction.onDrawableChanged("setImageURI", oldDrawable, newDrawable);
            }

            if(needInvokeInvalidate){
                invalidate();
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
    public void onDisplay(UriScheme uriScheme) {
        boolean needInvokeInvalidate = false;

        if(requestFunction != null){
            //noinspection ConstantConditions
            needInvokeInvalidate |= requestFunction.onDisplay(uriScheme);
        }
        if(recyclerCompatFunction != null){
            needInvokeInvalidate |= recyclerCompatFunction.onDisplay(uriScheme);
        }
        if(showPressedFunction != null){
            needInvokeInvalidate |= showPressedFunction.onDisplay(uriScheme);
        }
        if(showProgressFunction != null){
            needInvokeInvalidate |= showProgressFunction.onDisplay(uriScheme);
        }
        if(showGifFlagFunction != null){
            needInvokeInvalidate |= showGifFlagFunction.onDisplay(uriScheme);
        }
        if(showImageFromFunction != null){
            needInvokeInvalidate |= showImageFromFunction.onDisplay(uriScheme);
        }
        if(imageShapeFunction != null){
            needInvokeInvalidate |= imageShapeFunction.onDisplay(uriScheme);
        }
        if(clickRetryFunction != null){
            needInvokeInvalidate |= clickRetryFunction.onDisplay(uriScheme);
        }

        if(needInvokeInvalidate){
            invalidate();
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
        return Sketch.with(getContext()).display(LocalImagePreprocessor.createInstalledAppIconUri(packageName, versionCode), this).commit();
    }

    @Override
    public DisplayOptions getOptions() {
        return requestFunction.getDisplayOptions();
    }

    @Override
    public void setOptions(DisplayOptions newDisplayOptions) {
        if (newDisplayOptions == null) {
            this.requestFunction.getDisplayOptions().reset();
        } else {
            this.requestFunction.getDisplayOptions().copy(newDisplayOptions);
        }
    }

    @Override
    public void setOptionsByName(Enum<?> optionsName) {
        setOptions(Sketch.getDisplayOptions(optionsName));
    }

    @Override
    public DisplayListener getDisplayListener() {
        return displayListener;
    }

    @Override
    public void setDisplayListener(DisplayListener displayListener) {
        this.wrapperDisplayListener = displayListener;
    }

    @Override
    public DownloadProgressListener getDownloadProgressListener() {
        if (showProgressFunction != null || wrapperDisplayListener != null) {
            return downloadProgressListener;
        } else {
            return null;
        }
    }

    @Override
    public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.wrapperDownloadProgressListener = downloadProgressListener;
    }

    @Override
    public DisplayParams getDisplayParams() {
        return requestFunction.getDisplayParams();
    }

    @Override
    public void setDisplayParams(DisplayParams displayParams) {
        requestFunction.setDisplayParams(displayParams);
    }

    /**
     * 设置当暂停下载的时候点击显示图片
     */
    public void setClickRetryOnPauseDownload(boolean clickRetryOnPauseDownload) {
        clickRetryFunction.setClickRetryOnPauseDownload(clickRetryOnPauseDownload);
    }

    /**
     * 设置当失败的时候点击重新显示图片
     */
    public void setClickRetryOnFailed(boolean clickRetryOnFailed) {
        clickRetryFunction.setClickRetryOnFailed(clickRetryOnFailed);
    }

    /**
     * 是否显示下载进度
     */
    @SuppressWarnings("unused")
    public boolean isShowDownloadProgress(){
        return showProgressFunction != null;
    }

    /**
     * 设置是否显示下载进度，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     */
    public void setShowDownloadProgress(boolean showDownloadProgress) {
        if(showDownloadProgress){
            if(showProgressFunction == null){
                showProgressFunction = new ShowProgressFunction(this, imageShapeFunction);
            }
        }else{
            showProgressFunction = null;
        }
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
        if(showPressedStatus){
            if(showPressedFunction == null){
                showPressedFunction = new ShowPressedFunction(this, imageShapeFunction);
            }
        }else{
            showPressedFunction = null;
        }
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

        if(showImageFrom){
            if(showImageFromFunction == null){
                showImageFromFunction = new ShowImageFromFunction(this, requestFunction);
            }
        }else{
            showImageFromFunction = null;
        }

        if(oldShowImageFromFunction != null){
            invalidate();
        }
    }

    /**
     * 是否显示GIF标识
     */
    @SuppressWarnings("unused")
    public boolean isShowGifFlag() {
        return showGifFlagFunction != null;
    }

    /**
     * 设置是否显示GIF标识
     */
    @SuppressWarnings("unused")
    public void setShowGifFlag(Drawable gifFlagDrawable) {
        if(gifFlagDrawable != null){
            if(showGifFlagFunction == null){
                showGifFlagFunction = new ShowGifFlagFunction(this, gifFlagDrawable);
                invalidate();
            }else if(gifFlagDrawable != showGifFlagFunction.getGifFlagDrawable()){
                invalidate();
            }
        }else{
            showGifFlagFunction = null;
            invalidate();
        }
    }

    /**
     * 设置GIF标识图片
     */
    public void setShowGifFlag(int gifFlagDrawableResId) {
        setShowGifFlag(getResources().getDrawable(gifFlagDrawableResId));
    }

    /**
     * 获取图片形状，下载进度和按下效果的蒙层会适应此形状
     */
    @SuppressWarnings("unused")
    public ImageShape getImageShape() {
        return imageShapeFunction != null ? imageShapeFunction.getImageShape() : null;
    }

    /**
     * 设置图片形状，下载进度和按下效果的蒙层会适应此形状
     */
    public void setImageShape(ImageShape imageShape) {
        if(imageShapeFunction != null){
            imageShapeFunction.setImageShape(imageShape);
        }
    }

    /**
     * 获取图片形状的圆角角度，只有图片形状是ROUNDED_RECT的时候此参数才有用
     */
    @SuppressWarnings("unused")
    public int getImageShapeRoundedRadius() {
        return imageShapeFunction != null ? imageShapeFunction.getRoundedRadius() : 0;
    }

    /**
     * 设置图片形状的圆角角度，只有图片形状是ROUNDED_RECT的时候此参数才有用
     */
    public void setImageShapeRoundedRadius(int radius) {
        if(imageShapeFunction != null){
            imageShapeFunction.setRoundedRadius(radius);
        }
    }

    /**
     * 获取图片来源
     */
    @SuppressWarnings("unused")
    public ImageFrom getImageFrom() {
        return showImageFromFunction != null ? showImageFromFunction.getImageFrom() : null;
    }

    private class MyDisplayListener implements DisplayListener {
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
            if(showGifFlagFunction != null){
                needInvokeInvalidate |= showGifFlagFunction.onDisplayStarted();
            }
            if(showPressedFunction != null){
                needInvokeInvalidate |= showPressedFunction.onDisplayStarted();
            }
            if(imageShapeFunction != null){
                needInvokeInvalidate |= imageShapeFunction.onDisplayStarted();
            }
            if(clickRetryFunction != null){
                needInvokeInvalidate |= clickRetryFunction.onDisplayStarted();
            }
            if(requestFunction != null){
                needInvokeInvalidate |= requestFunction.onDisplayStarted();
            }
            if(recyclerCompatFunction != null){
                needInvokeInvalidate |= recyclerCompatFunction.onDisplayStarted();
            }
            if (needInvokeInvalidate) {
                invalidate();
            }

            if (wrapperDisplayListener != null) {
                wrapperDisplayListener.onStarted();
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
            if(showGifFlagFunction != null){
                needInvokeInvalidate |= showGifFlagFunction.onDisplayCompleted(imageFrom, mimeType);
            }
            if(showPressedFunction != null){
                needInvokeInvalidate |= showPressedFunction.onDisplayCompleted(imageFrom, mimeType);
            }
            if(imageShapeFunction != null){
                needInvokeInvalidate |= imageShapeFunction.onDisplayCompleted(imageFrom, mimeType);
            }
            if(clickRetryFunction != null){
                needInvokeInvalidate |= clickRetryFunction.onDisplayCompleted(imageFrom, mimeType);
            }
            if(requestFunction != null){
                needInvokeInvalidate |= requestFunction.onDisplayCompleted(imageFrom, mimeType);
            }
            if(recyclerCompatFunction != null){
                needInvokeInvalidate |= recyclerCompatFunction.onDisplayCompleted(imageFrom, mimeType);
            }
            if (needInvokeInvalidate) {
                invalidate();
            }

            if (wrapperDisplayListener != null) {
                wrapperDisplayListener.onCompleted(imageFrom, mimeType);
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
            if(showGifFlagFunction != null){
                needInvokeInvalidate |= showGifFlagFunction.onDisplayFailed(failedCause);
            }
            if(showPressedFunction != null){
                needInvokeInvalidate |= showPressedFunction.onDisplayFailed(failedCause);
            }
            if(imageShapeFunction != null){
                needInvokeInvalidate |= imageShapeFunction.onDisplayFailed(failedCause);
            }
            if(clickRetryFunction != null){
                needInvokeInvalidate |= clickRetryFunction.onDisplayFailed(failedCause);
            }
            if(requestFunction != null){
                needInvokeInvalidate |= requestFunction.onDisplayFailed(failedCause);
            }
            if(recyclerCompatFunction != null){
                needInvokeInvalidate |= recyclerCompatFunction.onDisplayFailed(failedCause);
            }
            if (needInvokeInvalidate) {
                invalidate();
            }

            if (wrapperDisplayListener != null) {
                wrapperDisplayListener.onFailed(failedCause);
            }
        }

        @Override
        public void onCanceled(CancelCause cancelCause) {
            boolean needInvokeInvalidate = false;
            if (showImageFromFunction != null) {
                //noinspection ConstantConditions
                needInvokeInvalidate |= showImageFromFunction.onCanceled(cancelCause);
            }
            if(showProgressFunction != null){
                needInvokeInvalidate |= showProgressFunction.onCanceled(cancelCause);
            }
            if(showGifFlagFunction != null){
                needInvokeInvalidate |= showGifFlagFunction.onCanceled(cancelCause);
            }
            if(showPressedFunction != null){
                needInvokeInvalidate |= showPressedFunction.onCanceled(cancelCause);
            }
            if(imageShapeFunction != null){
                needInvokeInvalidate |= imageShapeFunction.onCanceled(cancelCause);
            }
            if(clickRetryFunction != null){
                needInvokeInvalidate |= clickRetryFunction.onCanceled(cancelCause);
            }
            if(requestFunction != null){
                needInvokeInvalidate |= requestFunction.onCanceled(cancelCause);
            }
            if(recyclerCompatFunction != null){
                needInvokeInvalidate |= recyclerCompatFunction.onCanceled(cancelCause);
            }
            if (needInvokeInvalidate) {
                invalidate();
            }

            if (wrapperDisplayListener != null) {
                wrapperDisplayListener.onCanceled(cancelCause);
            }
        }
    }

    private class MyProgressListener implements DownloadProgressListener{

        @Override
        public void onUpdateDownloadProgress(int totalLength, int completedLength) {
            boolean needInvokeInvalidate = false;
            if (showImageFromFunction != null) {
                //noinspection ConstantConditions
                needInvokeInvalidate |= showImageFromFunction.onUpdateDownloadProgress(totalLength, completedLength);
            }
            if(showProgressFunction != null){
                needInvokeInvalidate |= showProgressFunction.onUpdateDownloadProgress(totalLength, completedLength);
            }
            if(showPressedFunction != null){
                needInvokeInvalidate |= showPressedFunction.onUpdateDownloadProgress(totalLength, completedLength);
            }
            if(showGifFlagFunction != null){
                needInvokeInvalidate |= showGifFlagFunction.onUpdateDownloadProgress(totalLength, completedLength);
            }
            if(imageShapeFunction != null){
                needInvokeInvalidate |= imageShapeFunction.onUpdateDownloadProgress(totalLength, completedLength);
            }
            if(clickRetryFunction != null){
                needInvokeInvalidate |= clickRetryFunction.onUpdateDownloadProgress(totalLength, completedLength);
            }
            if(requestFunction != null){
                needInvokeInvalidate |= requestFunction.onUpdateDownloadProgress(totalLength, completedLength);
            }
            if(recyclerCompatFunction != null){
                needInvokeInvalidate |= recyclerCompatFunction.onUpdateDownloadProgress(totalLength, completedLength);
            }
            if (needInvokeInvalidate) {
                invalidate();
            }

            if (wrapperDownloadProgressListener != null) {
                wrapperDownloadProgressListener.onUpdateDownloadProgress(totalLength, completedLength);
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
