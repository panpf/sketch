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
import android.graphics.drawable.TransitionDrawable;
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

public class SketchImageView extends ImageView implements SketchImageViewInterface {
    private static final String NAME = "SketchImageView";

    private static final int NONE = -1;
    private static final int FROM_FLAG_COLOR_MEMORY = 0x8800FF00;
    private static final int FROM_FLAG_COLOR_LOCAL = 0x880000FF;
    private static final int FROM_FLAG_COLOR_DISK_CACHE = 0x88FFFF00;
    private static final int FROM_FLAG_COLOR_NETWORK = 0x88FF0000;
    private static final int DEFAULT_PROGRESS_COLOR = 0x22000000;
    private static final int DEFAULT_PRESSED_STATUS_COLOR = 0x33000000;

    private Request displayRequest;
    private MyListener myListener;
    private DisplayOptions displayOptions;
    private DisplayListener displayListener;
    private ProgressListener progressListener;
    private DisplayParams displayParams;
    private View.OnClickListener onClickListener;
    private boolean replacedClickListener;
    private boolean clickDisplayOnPauseDownload;
    private boolean clickRedisplayOnFailed;
    private boolean isSetImage;

    protected int fromFlagColor = NONE;
    protected Path fromFlagPath;
    protected Paint fromFlagPaint;
    protected boolean showFromFlag;

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

    protected boolean currentIsGifDrawable;
    protected float gifDrawableLeft = -1;
    protected float gifDrawableTop = -1;
    protected Drawable gifFlagDrawable;

    protected Path imageShapeClipPath;
    protected int roundedRadius;
    protected ImageShape imageShape = ImageShape.RECT;
    protected boolean applyClip = false;
    RectF rectF;

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

        initFromFlag();
        initGifFlag();
        initImageShapePath();
    }

    protected void initFromFlag(){
        if(!showFromFlag){
            return;
        }

        if(fromFlagPath == null){
            fromFlagPath = new Path();
        }else{
            fromFlagPath.reset();
        }
        int x = getWidth()/10;
        int y = getWidth()/10;
        int left2 = getPaddingLeft();
        int top2 = getPaddingTop();
        fromFlagPath.moveTo(left2, top2);
        fromFlagPath.lineTo(left2 + x, top2);
        fromFlagPath.lineTo(left2, top2 + y);
        fromFlagPath.close();
    }

    protected void initGifFlag(){
        if(gifFlagDrawable != null){
            gifDrawableLeft = getWidth()-getPaddingRight() - gifFlagDrawable.getIntrinsicWidth();
            gifDrawableTop = getHeight()-getPaddingBottom() - gifFlagDrawable.getIntrinsicHeight();
        }
    }

    protected void initImageShapePath(){
        if(imageShape == ImageShape.RECT){
            imageShapeClipPath = null;
        }else if(imageShape == ImageShape.CIRCLE){
            if(imageShapeClipPath == null){
                imageShapeClipPath = new Path();
            }else{
                imageShapeClipPath.reset();
            }
            int xRadius = (getWidth()-getPaddingLeft()-getPaddingRight())/2;
            int yRadius = (getHeight()-getPaddingTop()-getPaddingBottom())/2;
            imageShapeClipPath.addCircle(xRadius, yRadius, xRadius < yRadius ? xRadius : yRadius, Path.Direction.CW);
        }else if(imageShape == ImageShape.ROUNDED_RECT){
            if(imageShapeClipPath == null){
                imageShapeClipPath = new Path();
            }else{
                imageShapeClipPath.reset();
            }
            if(rectF == null){
                rectF = new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            }else{
                rectF.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            }
            imageShapeClipPath.addRoundRect(rectF, roundedRadius, roundedRadius, Path.Direction.CW);
        }else{
            imageShapeClipPath = null;
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        imageShapeClipPath = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPressedStatus(canvas);
        drawDownloadProgress(canvas);
        drawFromFlag(canvas);
        drawGifFlag(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(showPressedStatus && isClickable()){
            if(gestureDetector == null){
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
        if(!isSetImage && displayParams != null){
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, "：", "restore image on attached to window", " - ", displayParams.uri));
            }
            Sketch.with(getContext()).display(displayParams, SketchImageView.this).commit();
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

    /**
     * @deprecated Use the new displayURIImage(Uri) method
     * @param uri The Uri of an image
     */
    @Override
    @Deprecated
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
    }

    /**
     * @deprecated Use the new displayResourceImage(int) method
     * @param resId the resource identifier of the drawable
     */
    @Override
    @Deprecated
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    @Override
    public void setImageDrawable(Drawable newDrawable) {
        // refresh gif flag
        if(gifFlagDrawable != null){
            boolean newDrawableIsGif = isGifImage(newDrawable);
            if(newDrawableIsGif != currentIsGifDrawable){
                currentIsGifDrawable = newDrawableIsGif;
                invalidate();
            }
        }

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
    public Drawable getDrawable() {
        return super.getDrawable();
    }

    @Override
    public View getSelf() {
        return this;
    }

    protected void drawPressedStatus(Canvas canvas){
        if(allowShowPressedStatus || animationRunning || showRect){
            applyClip = imageShapeClipPath != null;
            if(applyClip){
                canvas.save();
                try{
                    canvas.clipPath(imageShapeClipPath);
                }catch (UnsupportedOperationException e){
                    Log.e(NAME, "The current environment doesn't support clipPath has shut down automatically hardware acceleration");
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    e.printStackTrace();
                }
            }

            if(pressedStatusPaint == null){
                pressedStatusPaint = new Paint();
                pressedStatusPaint.setColor(pressedStatusColor);
                pressedStatusPaint.setAntiAlias(true);
            }
            if(allowShowPressedStatus || animationRunning){
                canvas.drawCircle(touchX, touchY, rippleRadius, pressedStatusPaint);
            }else if(showRect){
                canvas.drawRect(getPaddingLeft(), getPaddingTop(), getWidth()-getPaddingRight(), getHeight()-getPaddingBottom(), pressedStatusPaint);
            }

            if(applyClip){
                canvas.restore();
            }
        }
    }

    protected void drawDownloadProgress(Canvas canvas){
        if(showDownloadProgress && progress != NONE){
            applyClip = imageShapeClipPath != null;
            if(applyClip){
                canvas.save();
                try{
                    canvas.clipPath(imageShapeClipPath);
                }catch (UnsupportedOperationException e){
                    Log.e(NAME, "The current environment doesn't support clipPath has shut down automatically hardware acceleration");
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    e.printStackTrace();
                }
            }

            if(progressPaint == null){
                progressPaint = new Paint();
                progressPaint.setColor(downloadProgressColor);
                progressPaint.setAntiAlias(true);
            }
            canvas.drawRect(getPaddingLeft(), getPaddingTop() + (progress * getHeight()), getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingTop() - getPaddingBottom(), progressPaint);

            if(applyClip){
                canvas.restore();
            }
        }
    }

    protected void drawFromFlag(Canvas canvas){
        if(showFromFlag && fromFlagColor != NONE){
            if(fromFlagPath == null){
                fromFlagPath = new Path();
                int x = getWidth()/10;
                int y = getWidth()/10;
                int left2 = getPaddingLeft();
                int top2 = getPaddingTop();
                fromFlagPath.moveTo(left2, top2);
                fromFlagPath.lineTo(left2 + x, top2);
                fromFlagPath.lineTo(left2, top2 + y);
                fromFlagPath.close();
            }
            if(fromFlagPaint == null){
                fromFlagPaint = new Paint();
                fromFlagPaint.setAntiAlias(true);
            }
            fromFlagPaint.setColor(fromFlagColor);
            canvas.drawPath(fromFlagPath, fromFlagPaint);
        }
    }

    protected void drawGifFlag(Canvas canvas){
        if(currentIsGifDrawable && gifFlagDrawable != null){
            if(gifDrawableLeft == -1){
                gifDrawableLeft = getWidth()-getPaddingRight() - gifFlagDrawable.getIntrinsicWidth();
                gifDrawableTop = getHeight()-getPaddingBottom() - gifFlagDrawable.getIntrinsicHeight();
            }
            canvas.save();
            canvas.translate(gifDrawableLeft, gifDrawableTop);
            gifFlagDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public void onDisplay() {
        this.isSetImage = true;
        if(replacedClickListener){
            setOnClickListener(onClickListener);
            if(onClickListener == null){
                setClickable(false);
            }
            replacedClickListener = false;
        }
    }

    @Override
    public Request displayImage(String uri){
        return Sketch.with(getContext()).display(uri, this).commit();
    }

    @Override
    public Request displayResourceImage(int drawableResId){
        return Sketch.with(getContext()).displayFromResource(drawableResId, this).commit();
    }

    @Override
    public Request displayAssetImage(String imageFileName){
        return Sketch.with(getContext()).displayFromAsset(imageFileName, this).commit();
    }

    @Override
    public Request displayURIImage(Uri uri){
        return Sketch.with(getContext()).displayFromURI(uri, this).commit();
    }

    @Override
    public DisplayOptions getDisplayOptions() {
        return displayOptions;
    }

    @Override
    public void setDisplayOptions(DisplayOptions displayOptions) {
        if(displayOptions == null){
            this.displayOptions = null;
        }else if(this.displayOptions == null){
            this.displayOptions = new DisplayOptions(displayOptions);
        }else{
            this.displayOptions.copyOf(displayOptions);
        }
    }

    @Override
    public void setDisplayOptions(Enum<?> optionsName) {
        setDisplayOptions((DisplayOptions) Sketch.getOptions(optionsName));
    }

    @Override
    public DisplayListener getDisplayListener(boolean isPauseDownload){
        if(showFromFlag || showDownloadProgress || (isPauseDownload && clickDisplayOnPauseDownload) || clickRedisplayOnFailed){
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
    }

    /**
     * 设置当暂停下载的时候点击显示图片
     * @param clickDisplayOnPauseDownload true：是
     */
    public void setClickDisplayOnPauseDownload(boolean clickDisplayOnPauseDownload) {
        this.clickDisplayOnPauseDownload = clickDisplayOnPauseDownload;
    }

    /**
     * 设置当失败的时候点击重新显示图片
     * @param clickRedisplayOnFailed true：是
     */
    public void setClickRedisplayOnFailed(boolean clickRedisplayOnFailed) {
        this.clickRedisplayOnFailed = clickRedisplayOnFailed;
    }

    /**
     * 设置是否显示按下状态，开启后按下的时候会在ImageView表面覆盖一个黑色半透明图层，长按的时候还会有类似Android5.0的涟漪效果。此功能需要注册点击事件或设置Clickable为true
     * @param showPressedStatus 是否显示点击状态
     */
    public void setShowPressedStatus(boolean showPressedStatus) {
        this.showPressedStatus = showPressedStatus;
    }

    /**
     * 设置是否显示下载进度
     * @param showDownloadProgress 是否显示进度
     */
    public void setShowDownloadProgress(boolean showDownloadProgress) {
        this.showDownloadProgress = showDownloadProgress;
    }

    /**
     * 设置按下状态的颜色
     * @param pressedStatusColor 按下状态的颜色
     */
    public void setPressedStatusColor(int pressedStatusColor) {
        this.pressedStatusColor = pressedStatusColor;
        if(pressedStatusPaint != null){
            pressedStatusPaint.setColor(pressedStatusColor);
        }
    }

    /**
     * 设置下载进度的颜色
     * @param downloadProgressColor 下载进度的颜色
     */
    public void setDownloadProgressColor(int downloadProgressColor) {
        this.downloadProgressColor = downloadProgressColor;
        if(progressPaint != null){
            progressPaint.setColor(downloadProgressColor);
        }
    }

    public int getDownloadProgressColor() {
        return downloadProgressColor;
    }

    public int getFromFlagColor() {
        return fromFlagColor;
    }

    public int getPressedStatusColor() {
        return pressedStatusColor;
    }

    public boolean isShowFromFlag() {
        return showFromFlag;
    }

    public boolean isShowPressedStatus() {
        return showPressedStatus;
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
     * 获取GIF图片标识
     * @return GIF图片标识
     */
    public Drawable getGifFlagDrawable() {
        return gifFlagDrawable;
    }

    /**
     * 设置GIF图片标识
     * @param gifFlagDrawable GIF图片标识
     */
    public void setGifFlagDrawable(Drawable gifFlagDrawable) {
        this.gifFlagDrawable = gifFlagDrawable;
        if(this.gifFlagDrawable != null){
            this.gifFlagDrawable.setBounds(0 , 0, this.gifFlagDrawable.getIntrinsicWidth(), this.gifFlagDrawable.getIntrinsicHeight());
        }
    }

    /**
     * 设置GIF图片标识
     * @param gifFlagResId GIF图片标识
     */
    public void setGifFlagDrawable(int gifFlagResId) {
        this.gifFlagDrawable = getResources().getDrawable(gifFlagResId);
        if(this.gifFlagDrawable != null){
            this.gifFlagDrawable.setBounds(0 , 0, this.gifFlagDrawable.getIntrinsicWidth(), this.gifFlagDrawable.getIntrinsicHeight());
        }
    }

    /**
     * 获取图片形状
     * @return 图片形状
     */
    public ImageShape getImageShape() {
        return imageShape;
    }

    /**
     * 设置图片形状
     * @param imageShape 图片形状
     */
    public void setImageShape(ImageShape imageShape) {
        this.imageShape = imageShape;
        if(getWidth() != 0){
            initImageShapePath();
        }
    }

    /**
     * 获取圆角半径
     * @return 圆角半径
     */
    public int getRoundedRadiud() {
        return roundedRadius;
    }

    /**
     * 设置圆角半径
     * @param radius 圆角半径
     */
    public void setRoundedRadius(int radius) {
        this.roundedRadius = radius;
        if(getWidth() != 0){
            initImageShapePath();
        }
    }

    private static boolean isGifImage(Drawable newDrawable){
        if(newDrawable == null){
            return false;
        }

        if(newDrawable instanceof TransitionDrawable){
            TransitionDrawable transitionDrawable = (TransitionDrawable) newDrawable;
            if(transitionDrawable.getNumberOfLayers() >= 2){
                newDrawable = transitionDrawable.getDrawable(1);
            }
        }
        return newDrawable instanceof RecycleDrawableInterface && ImageFormat.GIF.getMimeType().equals(((RecycleDrawableInterface) newDrawable).getMimeType());
    }

    /**
     * 修改Drawable显示状态
     * @param callingStation 调用位置
     * @param drawable Drawable
     * @param isDisplayed 是否已显示
     */
    private static void notifyDrawable(String callingStation, Drawable drawable, final boolean isDisplayed) {
        if(drawable instanceof BindFixedRecycleBitmapDrawable){
            BindFixedRecycleBitmapDrawable bindFixedRecycleBitmapDrawable = (BindFixedRecycleBitmapDrawable) drawable;
            DisplayRequest displayRequest = bindFixedRecycleBitmapDrawable.getDisplayRequest();
            if(displayRequest != null && !displayRequest.isFinished()){
                displayRequest.cancel();
            }
            bindFixedRecycleBitmapDrawable.setIsDisplayed(callingStation, isDisplayed);
        }else if (drawable instanceof RecycleDrawableInterface) {
            ((RecycleDrawableInterface) drawable).setIsDisplayed(callingStation, isDisplayed);
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
        public void onCompleted(ImageFrom imageFrom, String mimeType) {
            if(showFromFlag){
                if(imageFrom != null){
                    switch (imageFrom){
                        case MEMORY_CACHE: fromFlagColor = FROM_FLAG_COLOR_MEMORY; break;
                        case DISK_CACHE: fromFlagColor = FROM_FLAG_COLOR_DISK_CACHE; break;
                        case NETWORK: fromFlagColor = FROM_FLAG_COLOR_NETWORK; break;
                        case LOCAL: fromFlagColor = FROM_FLAG_COLOR_LOCAL; break;
                        default: fromFlagColor = NONE; break;
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
                displayListener.onCompleted(imageFrom, mimeType);
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
            if(clickRedisplayOnFailed && failCause != FailCause.URI_NULL_OR_EMPTY && failCause != FailCause.IMAGE_VIEW_NULL && failCause != FailCause.URI_NO_SUPPORT){
                SketchImageView.super.setOnClickListener(this);
                replacedClickListener = true;
            }
            if (displayListener != null){
                displayListener.onFailed(failCause);
            }
        }

        @Override
        public void onCanceled(CancelCause cancelCause) {
            if(cancelCause != null && cancelCause == CancelCause.PAUSE_DOWNLOAD && clickDisplayOnPauseDownload){
                SketchImageView.super.setOnClickListener(this);
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
                Sketch.with(getContext()).display(displayParams, SketchImageView.this).requestLevel(RequestLevel.NET).commit();
            }
        }
    }

    /**
     * 图片形状
     */
    public enum ImageShape{
        RECT,
        CIRCLE,
        ROUNDED_RECT,
    }

    private class PressedStatusManager extends GestureDetector.SimpleOnGestureListener implements Runnable{
        private boolean showPress;
        private Scroller scroller;
        private Runnable cancelRunnable;

        public PressedStatusManager() {
            scroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            animationRunning = scroller.computeScrollOffset();
            if(animationRunning){
                rippleRadius = scroller.getCurrX();
                post(this);
            }
            invalidate();
        }

        @Override
        public boolean onDown(MotionEvent event) {
            if(!scroller.isFinished()){
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
            if(!showPress){
                showRect = true;
                invalidate();
                if(cancelRunnable == null){
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

        private void startAnimation(int duration){
            if(scroller == null){
                scroller = new Scroller(getContext(), new DecelerateInterpolator());
            }
            scroller.startScroll(0, 0, computeRippleRadius(), 0, duration);
            post(this);
        }

        /**
         * 计算涟漪的半径
         * @return 涟漪的半径
         */
        private int computeRippleRadius(){
            // 先计算按下点到四边的距离
            int toLeftDistance = touchX - getPaddingLeft();
            int toTopDistance = touchY - getPaddingTop();
            int toRightDistance = Math.abs(getWidth() - getPaddingRight() - touchX);
            int toBottomDistance = Math.abs(getHeight() - getPaddingBottom() - touchY);

            // 当按下位置在第一或第四象限的时候，比较按下位置在左上角到右下角这条线上距离谁最远就以谁为半径，否则在左下角到右上角这条线上比较
            int centerX = getWidth()/2;
            int centerY = getHeight()/2;
            if((touchX < centerX && touchY < centerY) || (touchX > centerX && touchY > centerY)) {
                int toLeftTopDistance = (int) Math.sqrt((toLeftDistance * toLeftDistance) + (toTopDistance * toTopDistance));
                int toRightBottomDistance = (int) Math.sqrt((toRightDistance * toRightDistance) + (toBottomDistance * toBottomDistance));
                return toLeftTopDistance > toRightBottomDistance ? toLeftTopDistance : toRightBottomDistance;
            }else{
                int toLeftBottomDistance = (int) Math.sqrt((toLeftDistance * toLeftDistance) + (toBottomDistance * toBottomDistance));
                int toRightTopDistance = (int) Math.sqrt((toRightDistance * toRightDistance) + (toTopDistance * toTopDistance));
                return toLeftBottomDistance > toRightTopDistance ? toLeftBottomDistance : toRightTopDistance;
            }
        }
    }
}
