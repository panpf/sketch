/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package me.xiaopan.sketch.feature.zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.feature.zoom.gestures.OnScaleDragGestureListener;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetector;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetectorCompat;
import me.xiaopan.sketch.util.MatrixUtils;

// TODO DrawerLayout包ViewPager的时候左右滑动有问题（先看看是不是DrawerLayout与ViewPager的兼容问题导致的）
// TODO 解决嵌套在别的可滑动View中时，会导致ArrayIndexOutOfBoundsException异常，初步猜测requestDisallowInterceptTouchEvent引起的
// TODO: 16/8/23 测试旋转功能
public class ImageZoomer implements View.OnTouchListener, OnScaleDragGestureListener, ViewTreeObserver.OnGlobalLayoutListener, FlingTranslateRunner.FlingTranslateListener {
    public static final String NAME = "ImageZoomer";

    public static final float DEFAULT_MAX_SCALE = 3.0f;
    public static final float DEFAULT_MID_SCALE = 1.75f;
    public static final float DEFAULT_MIN_SCALE = 1.0f;
    public static final int DEFAULT_ZOOM_DURATION = 200;
    public static final int EDGE_NONE = -1;
    public static final int EDGE_LEFT = 0;
    public static final int EDGE_RIGHT = 1;
    public static final int EDGE_BOTH = 2;

    // incoming
    private Context context;
    private WeakReference<ImageView> imageViewWeakReference;

    // configurable options
    private int zoomDuration = DEFAULT_ZOOM_DURATION;
    private float baseRotation;
    private float minScale = DEFAULT_MIN_SCALE;
    private float midScale = DEFAULT_MID_SCALE;
    private float maxScale = DEFAULT_MAX_SCALE;
    private boolean zoomEnabled = true;
    private ScaleType scaleType = ScaleType.FIT_CENTER;
    private Interpolator zoomInterpolator = new AccelerateDecelerateInterpolator();

    // listeners
    private OnViewTapListener onViewTapListener;
    private OnSingleFlingListener onSingleFlingListener;
    private OnScaleChangeListener onScaleChangeListener;
    private ArrayList<OnMatrixChangedListener> onMatrixChangedListenerList;

    // run time required
    private int imageViewLeft, imageViewTop, imageViewRight, imageViewBottom;
    private int scrollEdge = EDGE_BOTH;
    private boolean allowParentInterceptOnEdge = true;
    private boolean blockParentIntercept;
    private GestureDetector tapGestureDetector;
    private FlingTranslateRunner currentFlingTranslateRunner;
    private ScaleDragGestureDetector scaleDragGestureDetector;
    private final Matrix baseMatrix = new Matrix();
    private final Matrix drawMatrix = new Matrix();
    private final Matrix suppMatrix = new Matrix();
    private float lastScaleFocusX;
    private float lastScaleFocusY;

    public ImageZoomer(ImageView imageView, boolean provideTouchEvent) {
        context = imageView.getContext();
        imageViewWeakReference = new WeakReference<ImageView>(imageView);

        // from ImageView get ScaleType
        scaleType = imageView.getScaleType();
        if (scaleType == ScaleType.MATRIX) {
            scaleType = ScaleType.FIT_CENTER;
        }
        imageView.setScaleType(ScaleType.MATRIX);

        // initialize ImageView
        imageView.setDrawingCacheEnabled(true);
        if (!provideTouchEvent) {
            imageView.setOnTouchListener(this);
        }

        // initialize
        tapGestureDetector = new GestureDetector(context, new TapListener(this));
        scaleDragGestureDetector = ScaleDragGestureDetectorCompat.newInstance(imageView.getContext(), this);
        baseRotation = 0.0f;

        // listening to the ImageView size changes
        ViewTreeObserver observer = imageView.getViewTreeObserver();
        if (observer != null) {
            observer.addOnGlobalLayoutListener(this);
        }
    }

    @SuppressWarnings("unused")
    public ImageZoomer(ImageView imageView) {
        this(imageView, false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!zoomEnabled || !hasDrawable()) {
            return false;
        }

        boolean handled = false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastScaleFocusX = 0;
                lastScaleFocusY = 0;

                // 上来就禁止父View拦截事件
                ViewParent parent = v.getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }

                // 取消快速滚动
                cancelFling();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float currentScale = getScale();
                if (currentScale < minScale) {
                    // 如果当前缩放倍数小于最小倍数就回滚至最小倍数
                    RectF rect = getDisplayRect();
                    if (rect != null) {
                        v.post(new ZoomRunner(this, currentScale, minScale, rect.centerX(), rect.centerY()));
                        handled = true;
                    }
                } else if (currentScale > maxScale) {
                    // 如果当前缩放倍数大于最大倍数就回滚至最大倍数
                    if (lastScaleFocusX != 0 && lastScaleFocusY != 0) {
                        v.post(new ZoomRunner(this, currentScale, maxScale, lastScaleFocusX, lastScaleFocusY));
                        handled = true;
                    }
                }
                break;
        }

        // 缩放、拖拽手势探测器处理
        if (scaleDragGestureDetector != null) {
            boolean wasScaling = scaleDragGestureDetector.isScaling();
            boolean wasDragging = scaleDragGestureDetector.isDragging();

            handled = scaleDragGestureDetector.onTouchEvent(event);

            boolean didntScale = !wasScaling && !scaleDragGestureDetector.isScaling();
            boolean didntDrag = !wasDragging && !scaleDragGestureDetector.isDragging();

            blockParentIntercept = didntScale && didntDrag;
        }

        // 点击手势探测器处理
        if (tapGestureDetector != null && tapGestureDetector.onTouchEvent(event)) {
            handled = true;
        }

        return handled;
    }

    @Override
    public void onDrag(float dx, float dy) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        // Do not drag if we are already scaling
        if (scaleDragGestureDetector != null && scaleDragGestureDetector.isScaling()) {
            return;
        }

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". " + String.format("onDrag: dx: %.2f. dy: %.2f", dx, dy));
        }

        suppMatrix.postTranslate(dx, dy);
        checkAndDisplayMatrix();

        /**
         * Here we decide whether to let the ImageView's parent to start taking
         * over the touch event.
         *
         * First we check whether this function is enabled. We never want the
         * parent to take over if we're scaling. We then check the edge we're
         * on, and the direction of the scroll (i.e. if we're pulling against
         * the edge, aka 'overscrolling', let the parent take over).
         */
        ViewParent parent = imageView.getParent();
        if (parent != null) {
            if (allowParentInterceptOnEdge && !scaleDragGestureDetector.isScaling() && !blockParentIntercept) {
                if (scrollEdge == EDGE_BOTH || (scrollEdge == EDGE_LEFT && dx >= 1f) || (scrollEdge == EDGE_RIGHT && dx <= -1f)) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        currentFlingTranslateRunner = new FlingTranslateRunner(context, this);
        currentFlingTranslateRunner.fling((int) velocityX, (int) velocityY);

        if (onSingleFlingListener != null) {
            onSingleFlingListener.onFling(startX, startY, velocityX, velocityY);
        }
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        lastScaleFocusX = focusX;
        lastScaleFocusY = focusY;

        float oldSuppScale = MatrixUtils.getMatrixScale(suppMatrix);
        float newSuppScale = oldSuppScale * scaleFactor;

        if (scaleFactor > 1.0f) {
            // 放大的时候，如果当前已经超过最大缩放比例，就调慢缩放速度
            // 这样就能模拟出超过最大缩放比例时很难再继续放大有种拉橡皮筋的感觉
            float maxSuppScale = maxScale / MatrixUtils.getMatrixScale(baseMatrix);
            if (oldSuppScale >= maxSuppScale) {
                float addScale = newSuppScale - oldSuppScale;
                addScale *= 0.4;
                newSuppScale = oldSuppScale + addScale;
                scaleFactor = newSuppScale / oldSuppScale;
            }
        } else if (scaleFactor < 1.0f) {
            // 缩小的时候，如果当前已经小于最小缩放比例，就调慢缩放速度
            // 这样就能模拟出小于最小缩放比例时很难再继续缩小有种拉橡皮筋的感觉
            float minSuppScale = minScale / MatrixUtils.getMatrixScale(baseMatrix);
            if (oldSuppScale <= minSuppScale) {
                float addScale = newSuppScale - oldSuppScale;
                addScale *= 0.4;
                newSuppScale = oldSuppScale + addScale;
                scaleFactor = newSuppScale / oldSuppScale;
            }
        }

        suppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        checkAndDisplayMatrix();
        if (onScaleChangeListener != null) {
            onScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
        }
    }

    @Override
    public void onFlingTranslate(float dx, float dy) {
        suppMatrix.postTranslate(dx, dy);
        applyMatrix(getDrawMatrix());
    }

    /**
     * 获取缩放倍数
     */
    public float getScale() {
        return MatrixUtils.getMatrixScale(getDrawMatrix());
    }

    /**
     * 设置缩放倍数
     * @param scale 新的缩放倍数
     * @param animate 是否显示动画
     */
    public void setScale(float scale, boolean animate) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            setScale(scale, (imageView.getRight()) / 2, (imageView.getBottom()) / 2, animate);
        }
    }

    /**
     * 设置缩放倍数
     * @param scale 新的缩放倍数
     * @param focalX 缩放中心的X坐标
     * @param focalY 缩放中心的Y坐标
     * @param animate 是否显示动画
     */
    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            if (scale < minScale || scale > maxScale) {
                Log.w(Sketch.TAG, NAME + ". Scale must be within the range of " + minScale + "(minScale) and " + maxScale + "(maxScale)");
                return;
            }

            if (animate) {
                imageView.post(new ZoomRunner(this, getScale(), scale, focalX, focalY));
            } else {
                scale /= MatrixUtils.getMatrixScale(baseMatrix);
                suppMatrix.setScale(scale, scale, focalX, focalY);
                checkAndDisplayMatrix();
            }
        }
    }

    private boolean hasDrawable() {
        ImageView imageView = getImageView();
        return imageView != null && imageView.getDrawable() != null;
    }

    public ImageView getImageView() {
        if (imageViewWeakReference == null) {
            return null;
        }

        ImageView imageView = imageViewWeakReference.get();
        if (imageView == null) {
            Log.i(Sketch.TAG, NAME + ". ImageView no longer exists. You should not use this ImageAmplifier any more.");
            cleanup();
        }

        return imageView;
    }

    @SuppressWarnings("unused")
    public void setScale(float scale) {
        setScale(scale, false);
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType != null && scaleType != ScaleType.MATRIX && scaleType != this.scaleType) {
            this.scaleType = scaleType;
            update();
        }
    }

    public int getDrawableWidth() {
        ImageView imageView = imageViewWeakReference.get();
        if (imageView == null) {
            return 0;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return 0;
        }

        return drawable.getIntrinsicWidth();
    }

    public int getDrawableHeight() {
        ImageView imageView = imageViewWeakReference.get();
        if (imageView == null) {
            return 0;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return 0;
        }

        return drawable.getIntrinsicHeight();
    }

    private Matrix getDrawMatrix() {
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(suppMatrix);
        return drawMatrix;
    }

    public void getDrawMatrix(Matrix matrix) {
        matrix.set(getDrawMatrix());
    }

    @SuppressWarnings("unused")
    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if (finalMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }

        ImageView imageView = getImageView();
        if (imageView == null || imageView.getDrawable() == null) {
            return false;
        }

        suppMatrix.set(finalMatrix);
        applyMatrix(getDrawMatrix());
        checkMatrixBounds();

        return true;
    }

    @SuppressWarnings("unused")
    public void setBaseRotation(final float degrees) {
        baseRotation = degrees % 360;
        update();
        setRotationBy(baseRotation);
        checkAndDisplayMatrix();
    }

    @SuppressWarnings("unused")
    public boolean canZoom() {
        return zoomEnabled;
    }

    public void cleanup() {
        if (imageViewWeakReference == null) {
            return; // cleanup already done
        }

        final ImageView imageView = imageViewWeakReference.get();
        if (imageView != null) {
            // Remove this as a global layout listener
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            if (observer != null && observer.isAlive()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    observer.removeGlobalOnLayoutListener(this);
                }
            }

            // Remove the ImageView's reference to this
            imageView.setOnTouchListener(null);

            // make sure a pending fling runnable won't be run
            cancelFling();
        }

        if (tapGestureDetector != null) {
            tapGestureDetector.setOnDoubleTapListener(null);
        }

        // Clear listeners too
        onMatrixChangedListenerList = null;
        onViewTapListener = null;
        onSingleFlingListener = null;
        onScaleChangeListener = null;

        // Finally, clear ImageView
        imageViewWeakReference = null;
    }

    @Override
    public void onGlobalLayout() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        if (zoomEnabled) {
            final int top = imageView.getTop();
            final int right = imageView.getRight();
            final int bottom = imageView.getBottom();
            final int left = imageView.getLeft();

            if (top != imageViewTop || bottom != imageViewBottom || left != imageViewLeft || right != imageViewRight) {
                resetScaleMultiple();
                updateBaseMatrix(imageView.getDrawable());
                resetMatrix();

                imageViewTop = top;
                imageViewRight = right;
                imageViewBottom = bottom;
                imageViewLeft = left;
            }
        } else {
            resetScaleMultiple();
            updateBaseMatrix(imageView.getDrawable());
            resetMatrix();
        }
    }

    public void update() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        if (zoomEnabled) {
            imageView.setScaleType(ScaleType.MATRIX);

            resetScaleMultiple();
            updateBaseMatrix(imageView.getDrawable());
            resetMatrix();
        } else {
            resetScaleMultiple();
            resetMatrix();
        }
    }

    /**
     * 重置中、大缩放倍数
     */
    private void resetScaleMultiple(){
        ImageView imageView = getImageView();
        if (imageView == null) {
            minScale = DEFAULT_MIN_SCALE;
            midScale = DEFAULT_MID_SCALE;
            maxScale = DEFAULT_MAX_SCALE;
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if(drawable == null){
            minScale = DEFAULT_MIN_SCALE;
            midScale = DEFAULT_MID_SCALE;
            maxScale = DEFAULT_MAX_SCALE;
            return;
        }

        int viewWidth = getImageViewWidth();
        int viewHeight = getImageViewHeight();
        if (viewWidth == 0 || viewHeight == 0) {
            minScale = DEFAULT_MIN_SCALE;
            midScale = DEFAULT_MID_SCALE;
            maxScale = DEFAULT_MAX_SCALE;
            return;
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth == 0 || drawableHeight == 0) {
            minScale = DEFAULT_MIN_SCALE;
            midScale = DEFAULT_MID_SCALE;
            maxScale = DEFAULT_MAX_SCALE;
            return;
        }

        float widthScale = (float) viewWidth / drawableWidth;
        float heightScale = (float) viewHeight / drawableHeight;
        if (widthScale != heightScale) {
            minScale = Math.min(widthScale, heightScale);
            midScale = Math.max(widthScale, heightScale);
        } else {
            minScale = widthScale;
            midScale = widthScale * 2;
        }

        if (drawable instanceof SketchDrawable) {
            // 根据图片的原始大小计算最大缩放比例，保证一比一显示
            SketchDrawable sketchDrawable = (SketchDrawable) drawable;
            int originDrawableWidth = sketchDrawable.getOriginWidth();
            int originDrawableHeight = sketchDrawable.getOriginHeight();
            maxScale = Math.max((float) originDrawableWidth / drawableWidth, (float) originDrawableHeight / drawableHeight);

            // 最大缩放比例不能小于中间缩放比例的2倍
            maxScale = Math.max(maxScale, midScale * 2);
        } else {
            maxScale = midScale * 2;
        }
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return null;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return null;
        }

        RectF displayRect = new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        matrix.mapRect(displayRect);
        return displayRect;
    }

    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    /**
     * 获取预览图片上用户真实看到区域
     */
    public void getVisibleRect(RectF rectF) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            rectF.setEmpty();
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0) {
            rectF.setEmpty();
            return;
        }

        RectF displayRect = getDisplayRect();
        int viewWidth = getImageViewWidth();
        int viewHeight = getImageViewHeight();
        float displayWidth = displayRect.width();
        float displayHeight = displayRect.height();
        int drawableWidth = drawable.getIntrinsicWidth();

        float scale = displayWidth / drawableWidth;

        float left;
        float right;
        if (displayRect.left >= 0) {
            left = 0;
        } else {
            left = Math.abs(displayRect.left);
        }
        if (displayWidth >= viewWidth) {
            right = viewWidth + left;
        } else {
            right = displayRect.right - displayRect.left;
        }

        float top;
        float bottom;
        if (displayRect.top >= 0) {
            top = 0;
        } else {
            top = Math.abs(displayRect.top);
        }
        if (displayHeight >= viewHeight) {
            bottom = viewHeight + top;
        } else {
            bottom = displayRect.bottom - displayRect.top;
        }

        left /= scale;
        right /= scale;
        top /= scale;
        bottom /= scale;

        rectF.set(left, top, right, bottom);
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            applyMatrix(getDrawMatrix());
        }
    }

    private boolean checkMatrixBounds() {
        final ImageView imageView = getImageView();
        if (null == imageView) {
            return false;
        }

        final RectF rect = getDisplayRect(getDrawMatrix());
        if (null == rect) {
            return false;
        }

        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getImageViewHeight();
        if (height <= viewHeight) {
            switch (scaleType) {
                case FIT_START:
                    deltaY = -rect.top;
                    break;
                case FIT_END:
                    deltaY = viewHeight - height - rect.top;
                    break;
                default:
                    deltaY = (viewHeight - height) / 2 - rect.top;
                    break;
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getImageViewWidth();
        if (width <= viewWidth) {
            switch (scaleType) {
                case FIT_START:
                    deltaX = -rect.left;
                    break;
                case FIT_END:
                    deltaX = viewWidth - width - rect.left;
                    break;
                default:
                    deltaX = (viewWidth - width) / 2 - rect.left;
                    break;
            }
            scrollEdge = EDGE_BOTH;
        } else if (rect.left > 0) {
            scrollEdge = EDGE_LEFT;
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            scrollEdge = EDGE_RIGHT;
        } else {
            scrollEdge = EDGE_NONE;
        }

        // Finally actually translate the matrix
        suppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private void applyMatrix(Matrix matrix) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            if (!ScaleType.MATRIX.equals(imageView.getScaleType())) {
                throw new IllegalStateException("ImageView scaleType must be is MATRIX");
            }

            imageView.setImageMatrix(matrix);

            if (onMatrixChangedListenerList != null && !onMatrixChangedListenerList.isEmpty()) {
                for (int w = 0, size = onMatrixChangedListenerList.size(); w < size; w++) {
                    onMatrixChangedListenerList.get(w).onMatrixChanged(this);
                }
            }
        }
    }

    public float getMaximumScale() {
        return maxScale;
    }

    @SuppressWarnings("unused")
    public void setMaximumScale(float maximumScale) {
        checkZoomLevels(minScale, midScale, maximumScale);
        maxScale = maximumScale;
    }

    public float getMediumScale() {
        return midScale;
    }

    @SuppressWarnings("unused")
    public void setMediumScale(float mediumScale) {
        checkZoomLevels(minScale, mediumScale, maxScale);
        midScale = mediumScale;
    }

    public float getMinimumScale() {
        return minScale;
    }

    @SuppressWarnings("unused")
    public void setMinimumScale(float minimumScale) {
        checkZoomLevels(minimumScale, midScale, maxScale);
        minScale = minimumScale;
    }

    @SuppressWarnings("unused")
    public void setRotationTo(float degrees) {
        suppMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public void setRotationBy(float degrees) {
        suppMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    @SuppressWarnings("unused")
    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.onSingleFlingListener = onSingleFlingListener;
    }

    @SuppressWarnings("unused")
    public void setAllowParentInterceptOnEdge(boolean allowParentInterceptOnEdge) {
        this.allowParentInterceptOnEdge = allowParentInterceptOnEdge;
    }

    @SuppressWarnings("unused")
    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        this.onScaleChangeListener = onScaleChangeListener;
    }

    public void addOnMatrixChangeListener(OnMatrixChangedListener listener) {
        if (listener != null) {
            if (onMatrixChangedListenerList == null) {
                onMatrixChangedListenerList = new ArrayList<OnMatrixChangedListener>(1);
            }
            onMatrixChangedListenerList.add(listener);
        }
    }

    @SuppressWarnings("unused")
    public void setZoomable(boolean zoomable) {
        zoomEnabled = zoomable;
        update();
    }

    @SuppressWarnings("unused")
    public void setZoomTransitionDuration(int milliseconds) {
        if (milliseconds < 0)
            milliseconds = DEFAULT_ZOOM_DURATION;
        this.zoomDuration = milliseconds;
    }

    @SuppressWarnings("unused")
    public Bitmap getVisibleRectangleBitmap() {
        ImageView imageView = getImageView();
        return imageView == null ? null : imageView.getDrawingCache();
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays it.s
     */
    private void resetMatrix() {
        suppMatrix.reset();
        setRotationBy(baseRotation);
        applyMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    @SuppressWarnings("unused")
    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    int getZoomDuration() {
        return zoomDuration;
    }

    OnViewTapListener getOnViewTapListener() {
        return onViewTapListener;
    }

    @SuppressWarnings("unused")
    public void setOnViewTapListener(OnViewTapListener onViewTapListener) {
        this.onViewTapListener = onViewTapListener;
    }

    Interpolator getZoomInterpolator() {
        return zoomInterpolator;
    }

    /**
     * Set the zoom interpolator
     *
     * @param interpolator the zoom interpolator
     */
    @SuppressWarnings("unused")
    public void setZoomInterpolator(Interpolator interpolator) {
        zoomInterpolator = interpolator;
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param d - Drawable being displayed
     */
    private void updateBaseMatrix(Drawable d) {
        ImageView imageView = getImageView();
        if (null == imageView || null == d) {
            return;
        }

        final float viewWidth = getImageViewWidth();
        final float viewHeight = getImageViewHeight();
        final int drawableWidth = d.getIntrinsicWidth();
        final int drawableHeight = d.getIntrinsicHeight();

        baseMatrix.reset();

        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;

        if (scaleType == ScaleType.CENTER) {
            baseMatrix.postTranslate((viewWidth - drawableWidth) / 2F, (viewHeight - drawableHeight) / 2F);
        } else if (scaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            baseMatrix.postScale(scale, scale);
            baseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            baseMatrix.postScale(scale, scale);
            baseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);

            if ((int) baseRotation % 180 != 0) {
                //noinspection SuspiciousNameCombination
                mTempSrc = new RectF(0, 0, drawableHeight, drawableWidth);
            }

            switch (scaleType) {
                case FIT_CENTER:
                    baseMatrix
                            .setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);
                    break;

                case FIT_START:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.START);
                    break;

                case FIT_END:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.END);
                    break;

                case FIT_XY:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL);
                    break;

                default:
                    break;
            }
        }
    }

    private void cancelFling() {
        if (currentFlingTranslateRunner != null) {
            currentFlingTranslateRunner.cancelFling();
            currentFlingTranslateRunner = null;
        }
    }

    private static void checkZoomLevels(float minZoom, float midZoom, float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException(
                    "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException(
                    "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
    }

    public int getImageViewWidth() {
        ImageView imageView = getImageView();
        if (imageView != null) {
            return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
        } else {
            return 0;
        }
    }

    public int getImageViewHeight() {
        ImageView imageView = getImageView();
        if (imageView != null) {
            return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
        } else {
            return 0;
        }
    }

    public interface OnSingleFlingListener {
        boolean onFling(float startX, float startY, float velocityX, float velocityY);
    }

    public interface OnViewTapListener {
        void onViewTap(View view, float x, float y);
    }

    public interface OnMatrixChangedListener {
        void onMatrixChanged(ImageZoomer imageZoomer);
    }

    public interface OnScaleChangeListener {
        void onScaleChange(float scaleFactor, float focusX, float focusY);
    }
}