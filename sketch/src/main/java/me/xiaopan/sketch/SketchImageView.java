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
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import me.xiaopan.sketch.util.SketchUtils;

public class SketchImageView extends ImageView implements ImageViewInterface {
    private static final String NAME = "SketchImageView";

    private static final int NONE = -1;
    private static final int DEFAULT_PROGRESS_COLOR = 0x22000000;
    private static final int DEFAULT_PRESSED_STATUS_COLOR = 0x33000000;

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

//    protected Path imageFromPath;
//    protected Paint imageFromPaint;
//    protected boolean showImageFrom;
//    protected ImageFrom imageFrom;
    private ShowImageFromFunction showImageFromFunction;

    protected int downloadProgressColor = DEFAULT_PROGRESS_COLOR;
    protected Paint progressPaint;
    protected float progress = NONE;
    protected boolean showDownloadProgress;

    protected int touchX;
    protected int touchY;
    protected int pressedStatusColor = DEFAULT_PRESSED_STATUS_COLOR;
    protected int rippleRadius;
    protected boolean allowShowPressedStatus;
    protected boolean showPressedStatus;
    protected boolean animationRunning;
    protected Paint pressedStatusPaint;
    protected GestureDetector gestureDetector;
    protected boolean showRect;

//    protected boolean isGifDrawable;
//    protected float gifDrawableLeft = -1;
//    protected float gifDrawableTop = -1;
//    protected Drawable gifFlagDrawable;
    private ShowGifFlagFunction showGifFlagFunction;

    protected Path maskShapeClipPath;
    protected int maskRoundedRadius;
    protected MaskShape maskShape = MaskShape.RECT;
    protected boolean applyMaskClip = false;
    protected RectF maskRectF;

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

//        initFromFlag();
        if(showImageFromFunction != null){
            showImageFromFunction.onLayout(changed, left, top, right, bottom);
        }
//        initGifFlag();
        if(showGifFlagFunction != null){
            showGifFlagFunction.onLayout(changed, left, top, right, bottom);
        }
        initImageShapePath();
    }

//    protected void initFromFlag() {
//        if (!showImageFrom) {
//            return;
//        }
//
//        if (imageFromPath == null) {
//            imageFromPath = new Path();
//        } else {
//            imageFromPath.reset();
//        }
//        int x = getWidth() / 10;
//        int y = getWidth() / 10;
//        int left2 = getPaddingLeft();
//        int top2 = getPaddingTop();
//        imageFromPath.moveTo(left2, top2);
//        imageFromPath.lineTo(left2 + x, top2);
//        imageFromPath.lineTo(left2, top2 + y);
//        imageFromPath.close();
//    }

//    protected void initGifFlag() {
//        if (gifFlagDrawable != null) {
//            gifDrawableLeft = getWidth() - getPaddingRight() - gifFlagDrawable.getIntrinsicWidth();
//            gifDrawableTop = getHeight() - getPaddingBottom() - gifFlagDrawable.getIntrinsicHeight();
//        }
//    }

    protected void initImageShapePath() {
        if (maskShape == MaskShape.RECT) {
            maskShapeClipPath = null;
        } else if (maskShape == MaskShape.CIRCLE) {
            if (maskShapeClipPath == null) {
                maskShapeClipPath = new Path();
            } else {
                maskShapeClipPath.reset();
            }
            int xRadius = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;
            int yRadius = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
            maskShapeClipPath.addCircle(xRadius, yRadius, xRadius < yRadius ? xRadius : yRadius, Path.Direction.CW);
        } else if (maskShape == MaskShape.ROUNDED_RECT) {
            if (maskShapeClipPath == null) {
                maskShapeClipPath = new Path();
            } else {
                maskShapeClipPath.reset();
            }
            if (maskRectF == null) {
                maskRectF = new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            } else {
                maskRectF.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            }
            maskShapeClipPath.addRoundRect(maskRectF, maskRoundedRadius, maskRoundedRadius, Path.Direction.CW);
        } else {
            maskShapeClipPath = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPressedStatus(canvas);
        drawDownloadProgress(canvas);
//        drawFromFlag(canvas);
        if(showImageFromFunction != null){
            showImageFromFunction.draw(canvas);
        }
//        drawGifFlag(canvas);
        if(showGifFlagFunction != null){
            showGifFlagFunction.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (showPressedStatus && isClickable()) {
            if (gestureDetector == null) {
                gestureDetector = new GestureDetector(getContext(), new PressedStatusManager());
            }
            gestureDetector.onTouchEvent(event);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    allowShowPressedStatus = false;
                    invalidate();
                    break;
            }
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
//            if (gifFlagDrawable != null && isGifDrawable) {
//                isGifDrawable = false;
//                invalidate();
//            }
            if (showGifFlagFunction != null && showGifFlagFunction.isGifDrawable) {
                showGifFlagFunction.setIsGifDrawable(false);
                invalidate();
            }

//            imageFrom = null;
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
//            if (gifFlagDrawable != null && isGifDrawable) {
//                isGifDrawable = false;
//                invalidate();
//            }
            if (showGifFlagFunction != null && showGifFlagFunction.isGifDrawable) {
                showGifFlagFunction.setIsGifDrawable(false);
                invalidate();
            }

//            imageFrom = null;
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
//            boolean newDrawableIsGif = SketchUtils.isGifDrawable(newDrawable);
//            if (newDrawableIsGif != isGifDrawable) {
//                isGifDrawable = newDrawableIsGif;
//                if(gifFlagDrawable != null){
//                    invalidate();
//                }
//            }
            if(showGifFlagFunction != null){
                boolean newDrawableIsGif = SketchUtils.isGifDrawable(newDrawable);
                if (newDrawableIsGif != showGifFlagFunction.isGifDrawable) {
                    showGifFlagFunction.setIsGifDrawable(newDrawableIsGif);
                    invalidate();
                }
            }

            if(!newDrawableFromSketch){
//                imageFrom = null;
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

    protected void drawPressedStatus(Canvas canvas) {
        if (allowShowPressedStatus || animationRunning || showRect) {
            applyMaskClip = maskShapeClipPath != null;
            if (applyMaskClip) {
                canvas.save();
                try {
                    canvas.clipPath(maskShapeClipPath);
                } catch (UnsupportedOperationException e) {
                    Log.e(NAME, "The current environment doesn't support clipPath has shut down automatically hardware acceleration");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    e.printStackTrace();
                }
            }

            if (pressedStatusPaint == null) {
                pressedStatusPaint = new Paint();
                pressedStatusPaint.setColor(pressedStatusColor);
                pressedStatusPaint.setAntiAlias(true);
            }
            if (allowShowPressedStatus || animationRunning) {
                canvas.drawCircle(touchX, touchY, rippleRadius, pressedStatusPaint);
            } else if (showRect) {
                canvas.drawRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(), pressedStatusPaint);
            }

            if (applyMaskClip) {
                canvas.restore();
            }
        }
    }

    protected void drawDownloadProgress(Canvas canvas) {
        if (showDownloadProgress && progress != NONE) {
            applyMaskClip = maskShapeClipPath != null;
            if (applyMaskClip) {
                canvas.save();
                try {
                    canvas.clipPath(maskShapeClipPath);
                } catch (UnsupportedOperationException e) {
                    Log.e(NAME, "The current environment doesn't support clipPath has shut down automatically hardware acceleration");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    e.printStackTrace();
                }
            }

            if (progressPaint == null) {
                progressPaint = new Paint();
                progressPaint.setColor(downloadProgressColor);
                progressPaint.setAntiAlias(true);
            }
            canvas.drawRect(getPaddingLeft(), getPaddingTop() + (progress * getHeight()), getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingTop() - getPaddingBottom(), progressPaint);

            if (applyMaskClip) {
                canvas.restore();
            }
        }
    }

//    protected void drawFromFlag(Canvas canvas) {
//        if (showImageFrom && imageFrom != null) {
//            if (imageFromPath == null) {
//                imageFromPath = new Path();
//                int x = getWidth() / 10;
//                int y = getWidth() / 10;
//                int left2 = getPaddingLeft();
//                int top2 = getPaddingTop();
//                imageFromPath.moveTo(left2, top2);
//                imageFromPath.lineTo(left2 + x, top2);
//                imageFromPath.lineTo(left2, top2 + y);
//                imageFromPath.close();
//            }
//            if (imageFromPaint == null) {
//                imageFromPaint = new Paint();
//                imageFromPaint.setAntiAlias(true);
//            }
//            switch (imageFrom) {
//                case MEMORY_CACHE:
//                    imageFromPaint.setColor(FROM_FLAG_COLOR_MEMORY);
//                    break;
//                case DISK_CACHE:
//                    imageFromPaint.setColor(FROM_FLAG_COLOR_DISK_CACHE);
//                    break;
//                case NETWORK:
//                    imageFromPaint.setColor(FROM_FLAG_COLOR_NETWORK);
//                    break;
//                case LOCAL:
//                    imageFromPaint.setColor(FROM_FLAG_COLOR_LOCAL);
//                    break;
//                default:
//                    return;
//            }
//            canvas.drawPath(imageFromPath, imageFromPaint);
//        }
//    }

//    protected void drawGifFlag(Canvas canvas) {
//        if (isGifDrawable && gifFlagDrawable != null) {
//            if (gifDrawableLeft == -1) {
//                gifDrawableLeft = getWidth() - getPaddingRight() - gifFlagDrawable.getIntrinsicWidth();
//                gifDrawableTop = getHeight() - getPaddingBottom() - gifFlagDrawable.getIntrinsicHeight();
//            }
//            canvas.save();
//            canvas.translate(gifDrawableLeft, gifDrawableTop);
//            gifFlagDrawable.draw(canvas);
//            canvas.restore();
//        }
//    }

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
//                showImageFrom
        showImageFromFunction != null
                || showDownloadProgress || (isPauseDownload && clickDisplayOnPauseDownload) || clickRedisplayOnFailed) {
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
        if (showDownloadProgress) {
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
     * 设置是否显示按下状态，开启后按下的时候会在ImageView表面覆盖一个黑色半透明图层，长按的时候还会有类似Android5.0的涟漪效果。此功能需要注册点击事件或设置Clickable为true
     *
     * @param showPressedStatus 是否显示点击状态
     */
    public void setShowPressedStatus(boolean showPressedStatus) {
        this.showPressedStatus = showPressedStatus;
    }

    /**
     * 设置是否显示下载进度
     *
     * @param showDownloadProgress 是否显示进度
     */
    public void setShowDownloadProgress(boolean showDownloadProgress) {
        this.showDownloadProgress = showDownloadProgress;
    }

    /**
     * 设置按下状态的颜色
     *
     * @param pressedStatusColor 按下状态的颜色
     */
    @SuppressWarnings("unused")
    public void setPressedStatusColor(int pressedStatusColor) {
        this.pressedStatusColor = pressedStatusColor;
        if (pressedStatusPaint != null) {
            pressedStatusPaint.setColor(pressedStatusColor);
        }
    }

    /**
     * 设置下载进度的颜色
     *
     * @param downloadProgressColor 下载进度的颜色
     */
    @SuppressWarnings("unused")
    public void setDownloadProgressColor(int downloadProgressColor) {
        this.downloadProgressColor = downloadProgressColor;
        if (progressPaint != null) {
            progressPaint.setColor(downloadProgressColor);
        }
    }

    @SuppressWarnings("unused")
    public int getDownloadProgressColor() {
        return downloadProgressColor;
    }

    @SuppressWarnings("unused")
    public int getPressedStatusColor() {
        return pressedStatusColor;
    }

    @SuppressWarnings("unused")
    public boolean isShowPressedStatus() {
        return showPressedStatus;
    }

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
     *
     * @return GIF图片标识
     */
    @SuppressWarnings("unused")
    public Drawable getGifFlagDrawable() {
//        return gifFlagDrawable;
        return showGifFlagFunction != null ? showGifFlagFunction.getGifFlagDrawable() : null;
    }

    /**
     * 设置GIF标识图片
     */
    @SuppressWarnings("unused")
    public void setGifFlagDrawable(Drawable gifFlagDrawable) {
//        this.gifFlagDrawable = gifFlagDrawable;
//        if (this.gifFlagDrawable != null) {
//            this.gifFlagDrawable.setBounds(0, 0, this.gifFlagDrawable.getIntrinsicWidth(), this.gifFlagDrawable.getIntrinsicHeight());
//        }
        if(gifFlagDrawable != null){
            showGifFlagFunction = new ShowGifFlagFunction(this, gifFlagDrawable);
        }else{
            showGifFlagFunction = null;
        }
    }

    /**
     * 设置GIF标识图片
     */
    public void setGifFlagDrawable(int gifFlagResId) {
//        this.gifFlagDrawable = getResources().getDrawable(gifFlagResId);
//        if (this.gifFlagDrawable != null) {
//            this.gifFlagDrawable.setBounds(0, 0, this.gifFlagDrawable.getIntrinsicWidth(), this.gifFlagDrawable.getIntrinsicHeight());
//        }
        setGifFlagDrawable(getResources().getDrawable(gifFlagResId));
    }

    /**
     * 获取下载进度、按下效果蒙层的形状
     */
    @SuppressWarnings("unused")
    public MaskShape getMaskShape() {
        return maskShape;
    }

    /**
     * 设置下载进度、按下效果蒙层的形状
     */
    public void setMaskShape(MaskShape maskShape) {
        this.maskShape = maskShape;
        if (getWidth() != 0) {
            initImageShapePath();
        }
    }

    /**
     * 获取下载进度、按下效果蒙层的圆角半径
     */
    @SuppressWarnings("unused")
    public int getMaskRoundedRadius() {
        return maskRoundedRadius;
    }

    /**
     * 设置下载进度、按下效果蒙层的圆角半径
     */
    public void setMaskRoundedRadius(int radius) {
        this.maskRoundedRadius = radius;
        if (getWidth() != 0) {
            initImageShapePath();
        }
    }

    /**
     * 获取图片来源
     * @return 图片来源；null：不显示图片来源
     */
    @SuppressWarnings("unused")
    public ImageFrom getImageFrom() {
//        return imageFrom;
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
//            if (showImageFromFunction) {
//                imageFrom = null;
//            }
            boolean needInvokeInvalidate = false;
            if (showImageFromFunction != null) {
                needInvokeInvalidate |= showImageFromFunction.onDisplayStarted();
            }
            if (showDownloadProgress) {
                progress = 0;
            }
//            if (showImageFromFunction || showDownloadProgress) {
//                invalidate();
//            }
            if (needInvokeInvalidate || showDownloadProgress) {
                invalidate();
            }
            if (displayListener != null) {
                displayListener.onStarted();
            }
        }

        @Override
        public void onCompleted(ImageFrom imageFrom, String mimeType) {
//            if (showImageFrom) {
//                SketchImageView.this.imageFrom = imageFrom;
//            }
            boolean needInvokeInvalidate = false;
            if (showImageFromFunction != null) {
                needInvokeInvalidate |= showImageFromFunction.onDisplayCompleted(imageFrom, mimeType);
            }
            if (showDownloadProgress) {
                progress = NONE;
            }
//            if (showImageFrom || showDownloadProgress) {
//                invalidate();
//            }
            if (needInvokeInvalidate || showDownloadProgress) {
                invalidate();
            }
            if (displayListener != null) {
                displayListener.onCompleted(imageFrom, mimeType);
            }
        }

        @Override
        public void onFailed(FailedCause failedCause) {
//            if (showImageFrom) {
//                imageFrom = null;
//            }
            boolean needInvokeInvalidate = false;
            if (showImageFromFunction != null) {
                needInvokeInvalidate |= showImageFromFunction.onDisplayFailed(failedCause);
            }
            if (showDownloadProgress) {
                progress = NONE;
            }
//            if (showDownloadProgress || showImageFrom) {
//                invalidate();
//            }
            if (showDownloadProgress || needInvokeInvalidate) {
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
            if (showDownloadProgress) {
                progress = (float) completedLength / totalLength;
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
     * 蒙层的形状
     */
    public enum MaskShape {
        RECT,
        CIRCLE,
        ROUNDED_RECT,
    }

    private class PressedStatusManager extends GestureDetector.SimpleOnGestureListener implements Runnable {
        private boolean showPress;
        private Scroller scroller;
        private Runnable cancelRunnable;

        public PressedStatusManager() {
            scroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            animationRunning = scroller.computeScrollOffset();
            if (animationRunning) {
                rippleRadius = scroller.getCurrX();
                post(this);
            }
            invalidate();
        }

        @Override
        public boolean onDown(MotionEvent event) {
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
                removeCallbacks(this);
                animationRunning = false;
                invalidate();
            }

            touchX = (int) event.getX();
            touchY = (int) event.getY();
            showPress = false;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            allowShowPressedStatus = true;
            showPress = true;
            startAnimation(1000);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!showPress) {
                showRect = true;
                invalidate();
                if (cancelRunnable == null) {
                    cancelRunnable = new Runnable() {
                        @Override
                        public void run() {
                            showRect = false;
                            invalidate();
                        }
                    };
                }
                postDelayed(cancelRunnable, 200);
            }
            return super.onSingleTapUp(e);
        }

        private void startAnimation(int duration) {
            if (scroller == null) {
                scroller = new Scroller(getContext(), new DecelerateInterpolator());
            }
            scroller.startScroll(0, 0, computeRippleRadius(), 0, duration);
            post(this);
        }

        /**
         * 计算涟漪的半径
         *
         * @return 涟漪的半径
         */
        private int computeRippleRadius() {
            // 先计算按下点到四边的距离
            int toLeftDistance = touchX - getPaddingLeft();
            int toTopDistance = touchY - getPaddingTop();
            int toRightDistance = Math.abs(getWidth() - getPaddingRight() - touchX);
            int toBottomDistance = Math.abs(getHeight() - getPaddingBottom() - touchY);

            // 当按下位置在第一或第四象限的时候，比较按下位置在左上角到右下角这条线上距离谁最远就以谁为半径，否则在左下角到右上角这条线上比较
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            if ((touchX < centerX && touchY < centerY) || (touchX > centerX && touchY > centerY)) {
                int toLeftTopDistance = (int) Math.sqrt((toLeftDistance * toLeftDistance) + (toTopDistance * toTopDistance));
                int toRightBottomDistance = (int) Math.sqrt((toRightDistance * toRightDistance) + (toBottomDistance * toBottomDistance));
                return toLeftTopDistance > toRightBottomDistance ? toLeftTopDistance : toRightBottomDistance;
            } else {
                int toLeftBottomDistance = (int) Math.sqrt((toLeftDistance * toLeftDistance) + (toBottomDistance * toBottomDistance));
                int toRightTopDistance = (int) Math.sqrt((toRightDistance * toRightDistance) + (toTopDistance * toTopDistance));
                return toLeftBottomDistance > toRightTopDistance ? toLeftBottomDistance : toRightTopDistance;
            }
        }
    }
}
