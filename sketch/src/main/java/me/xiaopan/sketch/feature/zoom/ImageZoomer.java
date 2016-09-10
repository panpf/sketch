/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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
import android.graphics.Rect;
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
import me.xiaopan.sketch.util.SketchUtils;

// TODO DrawerLayout包ViewPager的时候左右滑动有问题（先看看是不是DrawerLayout与ViewPager的兼容问题导致的）
// TODO 解决嵌套在别的可滑动View中时，会导致ArrayIndexOutOfBoundsException异常，初步猜测requestDisallowInterceptTouchEvent引起的
// TODO: 16/8/23 测试旋转功能
// TODO: 16/8/27 添加阅读模式，阅读模式下对于竖图默认显示fillZoomScale
// TODO: 16/9/7 初始化完成后的回调有问题，看一下是先后顺序导致的还是怎么着
public class ImageZoomer implements View.OnTouchListener, OnScaleDragGestureListener, ViewTreeObserver.OnGlobalLayoutListener {
    public static final String NAME = "ImageZoomer";

    public static final float DEFAULT_MAXIMIZE_SCALE = 1.75f;
    public static final float DEFAULT_MINIMUM_SCALE = 1.0f;

    public static final int DEFAULT_ZOOM_DURATION = 200;

    public static final int EDGE_NONE = -1;
    public static final int EDGE_LEFT = 0;
    public static final int EDGE_RIGHT = 1;
    public static final int EDGE_BOTH = 2;

    private final Matrix baseMatrix = new Matrix();
    private final Matrix drawMatrix = new Matrix();
    private final Matrix suppMatrix = new Matrix();

    // incoming
    private Context context;
    private WeakReference<ImageView> viewReference;

    // configurable options
    private int zoomDuration = DEFAULT_ZOOM_DURATION;
    private float baseRotation;
    private float minZoomScale = DEFAULT_MINIMUM_SCALE;
    private float maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
    private boolean zoomable = true;
    private boolean readMode;   // 阅读模式下，竖图将默认横向充满屏幕
    private ScaleType scaleType = ScaleType.FIT_CENTER;
    private Interpolator zoomInterpolator = new AccelerateDecelerateInterpolator();

    // listeners
    private OnViewTapListener onViewTapListener;
    private OnDragFlingListener onDragFlingListener;
    private OnScaleChangeListener onScaleChangeListener;
    private OnViewLongPressListener onViewLongPressListener;
    private ArrayList<OnMatrixChangedListener> onMatrixChangedListenerList;

    // run time required
    private int imageViewLeft, imageViewTop, imageViewRight, imageViewBottom;
    private int scrollEdge = EDGE_BOTH;
    private boolean allowParentInterceptOnEdge = true;
    private boolean blockParentIntercept;
    private GestureDetector tapGestureDetector;
    private FlingTranslateRunner currentFlingTranslateRunner;
    private ScaleDragGestureDetector scaleDragGestureDetector;
    private float lastScaleFocusX;
    private float lastScaleFocusY;
    private RectF displayRectF = new RectF();
    private float fullZoomScale; // 能够让图片完成显示的缩放比例
    private float fillZoomScale;    // 能够让图片填满宽或高的缩放比例
    private float originZoomScale;  // 能够让图片按照原始比例一比一显示的比例

    public ImageZoomer(ImageView imageView, boolean provideTouchEvent) {
        context = imageView.getContext();
        viewReference = new WeakReference<ImageView>(imageView);

        // from ImageView get ScaleType
        scaleType = imageView.getScaleType();
        if (scaleType == ScaleType.MATRIX) {
            scaleType = ScaleType.FIT_CENTER;
        } else {
            imageView.setScaleType(ScaleType.MATRIX);
        }

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
        if (!zoomable || !hasDrawable()) {
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
                float currentScale = SketchUtils.formatFloat(getZoomScale(), 2);
                if (currentScale < SketchUtils.formatFloat(minZoomScale, 2)) {
                    // 如果当前缩放倍数小于最小倍数就回滚至最小倍数
                    RectF displayRectF = new RectF();
                    checkMatrixBounds();
                    getDisplayRect(displayRectF);
                    if (!displayRectF.isEmpty()) {
                        zoom(minZoomScale, displayRectF.centerX(), displayRectF.centerY(), true);
                        handled = true;
                    }
                } else if (currentScale > SketchUtils.formatFloat(maxZoomScale, 2)) {
                    // 如果当前缩放倍数大于最大倍数就回滚至最大倍数
                    if (lastScaleFocusX != 0 && lastScaleFocusY != 0) {
                        zoom(maxZoomScale, lastScaleFocusX, lastScaleFocusY, true);
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

        if (onDragFlingListener != null) {
            onDragFlingListener.onFling(startX, startY, velocityX, velocityY);
        }
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        lastScaleFocusX = focusX;
        lastScaleFocusY = focusY;

        float oldSuppScale = SketchUtils.getMatrixScale(suppMatrix);
        float newSuppScale = oldSuppScale * scaleFactor;

        if (scaleFactor > 1.0f) {
            // 放大的时候，如果当前已经超过最大缩放比例，就调慢缩放速度
            // 这样就能模拟出超过最大缩放比例时很难再继续放大有种拉橡皮筋的感觉
            float maxSuppScale = maxZoomScale / SketchUtils.getMatrixScale(baseMatrix);
            if (oldSuppScale >= maxSuppScale) {
                float addScale = newSuppScale - oldSuppScale;
                addScale *= 0.4;
                newSuppScale = oldSuppScale + addScale;
                scaleFactor = newSuppScale / oldSuppScale;
            }
        } else if (scaleFactor < 1.0f) {
            // 缩小的时候，如果当前已经小于最小缩放比例，就调慢缩放速度
            // 这样就能模拟出小于最小缩放比例时很难再继续缩小有种拉橡皮筋的感觉
            float minSuppScale = minZoomScale / SketchUtils.getMatrixScale(baseMatrix);
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
    public void onGlobalLayout() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        if (zoomable) {
            final int top = imageView.getTop();
            final int right = imageView.getRight();
            final int bottom = imageView.getBottom();
            final int left = imageView.getLeft();

            if (top != imageViewTop || bottom != imageViewBottom || left != imageViewLeft || right != imageViewRight) {
                resetMinAndMaxZoomScale();
                updateBaseMatrix();
                resetMatrix();

                imageViewTop = top;
                imageViewRight = right;
                imageViewBottom = bottom;
                imageViewLeft = left;
            }
        } else {
            resetMinAndMaxZoomScale();
            updateBaseMatrix();
            resetMatrix();
        }
    }

    /** -----------私有功能----------- **/

    /**
     * 重置最小和最大缩放比例
     */
    private void resetMinAndMaxZoomScale() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            fullZoomScale = fillZoomScale = originZoomScale = 1f;
            minZoomScale = DEFAULT_MINIMUM_SCALE;
            maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            fullZoomScale = fillZoomScale = originZoomScale = 1f;
            minZoomScale = DEFAULT_MINIMUM_SCALE;
            maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
            return;
        }

        final int viewWidth = getImageViewWidth();
        final int viewHeight = getImageViewHeight();
        if (viewWidth == 0 || viewHeight == 0) {
            fullZoomScale = fillZoomScale = originZoomScale = 1f;
            minZoomScale = DEFAULT_MINIMUM_SCALE;
            maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
            return;
        }

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth == 0 || drawableHeight == 0) {
            fullZoomScale = fillZoomScale = originZoomScale = 1f;
            minZoomScale = DEFAULT_MINIMUM_SCALE;
            maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
            return;
        }

        final float widthScale = (float) viewWidth / drawableWidth;
        final float heightScale = (float) viewHeight / drawableHeight;
        if (widthScale != heightScale) {
            fullZoomScale = Math.min(widthScale, heightScale);
            fillZoomScale = Math.max(widthScale, heightScale);
        } else {
            fullZoomScale = widthScale;
            fillZoomScale = widthScale;
        }
        Drawable finalDrawable = SketchUtils.getLastDrawable(drawable);
        if (finalDrawable instanceof SketchDrawable) {
            SketchDrawable sketchDrawable = (SketchDrawable) finalDrawable;
            originZoomScale = Math.max((float) sketchDrawable.getOriginWidth() / drawableWidth,
                    (float) sketchDrawable.getOriginHeight() / drawableHeight);
        } else {
            originZoomScale = 1f;
        }

        if (canUseReadMode(drawableWidth, drawableHeight, viewHeight)) {
            if (originZoomScale > fillZoomScale) {
                minZoomScale = fillZoomScale;
                maxZoomScale = originZoomScale;
            } else {
                minZoomScale = originZoomScale;
                maxZoomScale = fillZoomScale;
            }
        } else {
            minZoomScale = fullZoomScale;

            // 最大缩放比例将在原始比例和充满比例中产生
            if (originZoomScale > fillZoomScale && (fillZoomScale * 1.2f) >= originZoomScale) {
                // 如果原始比例仅仅比充满比例大一点点，还是用充满比例作为最大缩放比例比较好
                maxZoomScale = fillZoomScale;
            } else {
                // 否则的话谁大用谁作为最大缩放比例
                maxZoomScale = Math.max(fillZoomScale, originZoomScale);
            }

            // 最大缩放比例和最小缩放比例的差距不能太小，最小得是最小缩放比例的1.5倍
            maxZoomScale = Math.max(maxZoomScale, minZoomScale * 1.5f);
        }
    }

    /**
     * 更新基础Matrix
     */
    private void updateBaseMatrix() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return;
        }

        final int viewWidth = getImageViewWidth();
        final int viewHeight = getImageViewHeight();
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        final float widthScale = (float) viewWidth / drawableWidth;
        final float heightScale = (float) viewHeight / drawableHeight;

        baseMatrix.reset();

        if (scaleType == ScaleType.CENTER) {
            baseMatrix.postTranslate((viewWidth - drawableWidth) / 2F, (viewHeight - drawableHeight) / 2F);
        } else if (scaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            baseMatrix.postScale(scale, scale);
            baseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F, (viewHeight - drawableHeight * scale) / 2F);

        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            baseMatrix.postScale(scale, scale);
            baseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F, (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);

            if ((int) baseRotation % 180 != 0) {
                //noinspection SuspiciousNameCombination
                mTempSrc = new RectF(0, 0, drawableHeight, drawableWidth);
            }

            switch (scaleType) {
                case FIT_CENTER:
                    if (canUseReadMode(drawableWidth, drawableHeight, viewHeight)) {
                        baseMatrix.postScale(widthScale, widthScale);
                    } else {
                        baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);
                    }
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

    /**
     * 检查应用Matrix后的边界，防止超出范围
     */
    boolean checkMatrixBounds() {
        final ImageView imageView = getImageView();
        if (null == imageView) {
            return false;
        }

        getDisplayRect(displayRectF);
        if (displayRectF.isEmpty()) {
            return false;
        }

        final float height = displayRectF.height(), width = displayRectF.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getImageViewHeight();
        if (height <= viewHeight) {
            switch (scaleType) {
                case FIT_START:
                    deltaY = -displayRectF.top;
                    break;
                case FIT_END:
                    deltaY = viewHeight - height - displayRectF.top;
                    break;
                default:
                    deltaY = (viewHeight - height) / 2 - displayRectF.top;
                    break;
            }
        } else if (displayRectF.top > 0) {
            deltaY = -displayRectF.top;
        } else if (displayRectF.bottom < viewHeight) {
            deltaY = viewHeight - displayRectF.bottom;
        }

        final int viewWidth = getImageViewWidth();
        if (width <= viewWidth) {
            switch (scaleType) {
                case FIT_START:
                    deltaX = -displayRectF.left;
                    break;
                case FIT_END:
                    deltaX = viewWidth - width - displayRectF.left;
                    break;
                default:
                    deltaX = (viewWidth - width) / 2 - displayRectF.left;
                    break;
            }
            scrollEdge = EDGE_BOTH;
        } else if (displayRectF.left > 0) {
            scrollEdge = EDGE_LEFT;
            deltaX = -displayRectF.left;
        } else if (displayRectF.right < viewWidth) {
            deltaX = viewWidth - displayRectF.right;
            scrollEdge = EDGE_RIGHT;
        } else {
            scrollEdge = EDGE_NONE;
        }

        // Finally actually translate the matrix
        suppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    /**
     * 重置
     */
    private void resetMatrix() {
        suppMatrix.reset();
        rotationBy(baseRotation);
        applyMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    /**
     * 应用Matrix
     */
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

    /**
     * 检查并应用Matrix
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            applyMatrix(getDrawMatrix());
        }
    }

    /**
     * 取消飞速滚动
     */
    private void cancelFling() {
        if (currentFlingTranslateRunner != null) {
            currentFlingTranslateRunner.cancelFling();
            currentFlingTranslateRunner = null;
        }
    }

    /**
     * 是否可以使用阅读模式
     */
    private boolean canUseReadMode(int drawableWidth, int drawableHeight, int viewHeight){
        return readMode && drawableHeight > drawableWidth && drawableHeight > viewHeight;
    }


    /** -----------可获取信息----------- **/

    /**
     * 获取ImageView
     */
    public ImageView getImageView() {
        if (viewReference == null) {
            return null;
        }

        ImageView imageView = viewReference.get();
        if (imageView == null) {
            Log.w(Sketch.TAG, NAME + ". ImageView no longer exists. You should not use this ImageZoomer any more.");
            cleanup();
        }

        return imageView;
    }

    /**
     * 获取ImageView的宽度
     */
    public int getImageViewWidth() {
        ImageView imageView = getImageView();
        if (imageView != null) {
            return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
        } else {
            return 0;
        }
    }

    /**
     * 获取ImageView的高度
     */
    public int getImageViewHeight() {
        ImageView imageView = getImageView();
        if (imageView != null) {
            return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
        } else {
            return 0;
        }
    }

    /**
     * 是否有图片
     */
    public boolean hasDrawable() {
        ImageView imageView = getImageView();
        return imageView != null && imageView.getDrawable() != null;
    }

    /**
     * 获取图片的宽度
     */
    public int getDrawableWidth() {
        ImageView imageView = viewReference.get();
        return imageView != null && imageView.getDrawable() != null ? imageView.getDrawable().getIntrinsicWidth() : 0;
    }

    /**
     * 获取图片的高度
     */
    public int getDrawableHeight() {
        ImageView imageView = viewReference.get();
        return imageView != null && imageView.getDrawable() != null ? imageView.getDrawable().getIntrinsicHeight() : 0;
    }

    /**
     * 获取绘制Matrix
     */
    private Matrix getDrawMatrix() {
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(suppMatrix);
        return drawMatrix;
    }

    /**
     * 拷贝绘制Matrix的参数
     */
    public void getDrawMatrix(Matrix matrix) {
        matrix.set(getDrawMatrix());
    }

    /**
     * 获取显示边界
     */
    public void getDisplayRect(RectF rectF) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            rectF.setEmpty();
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            rectF.setEmpty();
            return;
        }

        rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        getDrawMatrix().mapRect(rectF);
    }

    /**
     * 获取预览图片上用户真实看到区域
     */
    public void getVisibleRect(Rect rect) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            rect.setEmpty();
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0) {
            rect.setEmpty();
            return;
        }

        RectF displayRectF = new RectF();
        checkMatrixBounds();
        getDisplayRect(displayRectF);
        if (displayRectF.isEmpty()) {
            rect.setEmpty();
            return;
        }

        int viewWidth = getImageViewWidth();
        int viewHeight = getImageViewHeight();
        float displayWidth = displayRectF.width();
        float displayHeight = displayRectF.height();
        int drawableWidth = drawable.getIntrinsicWidth();

        float scale = displayWidth / drawableWidth;

        float left;
        float right;
        if (displayRectF.left >= 0) {
            left = 0;
        } else {
            left = Math.abs(displayRectF.left);
        }
        if (displayWidth >= viewWidth) {
            right = viewWidth + left;
        } else {
            right = displayRectF.right - displayRectF.left;
        }

        float top;
        float bottom;
        if (displayRectF.top >= 0) {
            top = 0;
        } else {
            top = Math.abs(displayRectF.top);
        }
        if (displayHeight >= viewHeight) {
            bottom = viewHeight + top;
        } else {
            bottom = displayRectF.bottom - displayRectF.top;
        }

        left /= scale;
        right /= scale;
        top /= scale;
        bottom /= scale;

        rect.set(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
    }

    @SuppressWarnings("unused")
    public Bitmap getVisibleRectangleBitmap() {
        ImageView imageView = getImageView();
        return imageView == null ? null : imageView.getDrawingCache();
    }

    /**
     * 清理
     */
    public void cleanup() {
        if (viewReference == null) {
            return; // cleanup already done
        }

        final ImageView imageView = viewReference.get();
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
        onDragFlingListener = null;
        onScaleChangeListener = null;

        // Finally, clear ImageView
        viewReference = null;
    }

    /**
     * 获取当前缩放比例
     */
    public float getZoomScale() {
        return SketchUtils.getMatrixScale(getDrawMatrix());
    }

    /**
     * 获取能够让图片完整显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getFullZoomScale() {
        return fullZoomScale;
    }

    /**
     * 获取能够让图片充满ImageView显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getFillZoomScale() {
        return fillZoomScale;
    }

    /**
     * 获取能够让图片按原图比例一比一显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getOriginZoomScale() {
        return originZoomScale;
    }


    /** -----------交互功能----------- **/

    /**
     * 移动到指定位置
     */
    // TODO: 16/8/27 加上动画支持
    @SuppressWarnings("unused")
    public void translateTo(float x, float y) {
        suppMatrix.setTranslate(x, y);
        applyMatrix(getDrawMatrix());
    }

    /**
     * 移动一段距离
     */
    public void translateBy(float dx, float dy) {
        suppMatrix.postTranslate(dx, dy);
        applyMatrix(getDrawMatrix());
    }

    /**
     * 设置缩放比例
     *
     * @param scale   新的缩放比例
     * @param focalX  缩放中心的X坐标
     * @param focalY  缩放中心的Y坐标
     * @param animate 是否显示缩放动画
     */
    public void zoom(float scale, float focalX, float focalY, boolean animate) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            if (scale < minZoomScale || scale > maxZoomScale) {
                Log.w(Sketch.TAG, NAME + ". Scale must be within the range of " + minZoomScale + "(minScale) and " + maxZoomScale + "(maxScale)");
                return;
            }

            if (animate) {
                imageView.post(new ZoomRunner(this, getZoomScale(), scale, focalX, focalY));
            } else {
                scale /= SketchUtils.getMatrixScale(baseMatrix);
                suppMatrix.setScale(scale, scale, focalX, focalY);
                checkAndDisplayMatrix();
            }
        }
    }

    /**
     * 设置缩放比例
     *
     * @param scale   新的缩放比例
     * @param animate 是否显示缩放动画
     */
    public void zoom(float scale, boolean animate) {
        ImageView imageView = getImageView();
        if (imageView != null && imageView.getRight() > 0 && imageView.getBottom() > 0) {
            zoom(scale, (imageView.getRight()) / 2, (imageView.getBottom()) / 2, animate);
        }
    }

    /**
     * 设置缩放比例
     *
     * @param scale 新的缩放比例
     */
    @SuppressWarnings("unused")
    public void zoom(float scale) {
        zoom(scale, false);
    }


    /**
     * 设置旋转角度
     */
    @SuppressWarnings("unused")
    public void rotationTo(float degrees) {
        suppMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    /**
     * 增加旋转角度
     */
    public void rotationBy(float degrees) {
        suppMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    /**
     * 设置一个显示Matrix
     */
    @SuppressWarnings("unused")
    public boolean setDisplayMatrix(Matrix displayMatrix) {
        if (displayMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }

        ImageView imageView = getImageView();
        if (imageView == null || imageView.getDrawable() == null) {
            return false;
        }

        suppMatrix.set(displayMatrix);
        applyMatrix(getDrawMatrix());
        checkMatrixBounds();

        return true;
    }

    /**
     * 当Drawable或者ImageView的尺寸发生变化时就需要调用此方法来更新
     */
    public void update() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        if (zoomable) {
            imageView.setScaleType(ScaleType.MATRIX);

            resetMinAndMaxZoomScale();
            updateBaseMatrix();
            resetMatrix();
        } else {
            resetMinAndMaxZoomScale();
            resetMatrix();
        }
    }


    /** -----------配置----------- **/

    /**
     * 是否允许缩放
     */
    @SuppressWarnings("unused")
    public boolean isZoomable() {
        return zoomable;
    }

    /**
     * 设置是否允许缩放
     */
    @SuppressWarnings("unused")
    public void setZoomable(boolean zoomable) {
        this.zoomable = zoomable;
        update();
    }

    /**
     * 火球缩放动画持续时间
     */
    public int getZoomDuration() {
        return zoomDuration;
    }

    /**
     * 设置缩放动画持续时间，单位毫秒
     */
    @SuppressWarnings("unused")
    public void setZoomDuration(int milliseconds) {
        this.zoomDuration = milliseconds > 0 ? milliseconds : DEFAULT_ZOOM_DURATION;
    }

    /**
     * 获取缩放动画插值器
     */
    public Interpolator getZoomInterpolator() {
        return zoomInterpolator;
    }

    /**
     * 设置缩放动画插值器
     */
    @SuppressWarnings("unused")
    public void setZoomInterpolator(Interpolator interpolator) {
        zoomInterpolator = interpolator;
    }

    /**
     * 获取最大缩放比例
     */
    public float getMaxZoomScale() {
        return maxZoomScale;
    }

    /**
     * 设置最大缩放比例
     */
    @SuppressWarnings("unused")
    public void setMaxZoomScale(float maxZoomScale) {
        if (maxZoomScale <= minZoomScale) {
            throw new IllegalArgumentException(
                    "maxZoomScale zoom has to be is greater than minZoomScale zoom. Call setMaxZoomScale() with a more appropriate value");
        }
        this.maxZoomScale = maxZoomScale;
    }

    /**
     * 获取最小缩放比例
     */
    public float getMinZoomScale() {
        return minZoomScale;
    }

    /**
     * 设置最小缩放比例
     */
    @SuppressWarnings("unused")
    public void setMinZoomScale(float minZoomScale) {
        if (minZoomScale >= maxZoomScale) {
            throw new IllegalArgumentException(
                    "minZoomScale zoom has to be less than maxZoomScale zoom. Call setMinZoomScale() with a more appropriate value");
        }
        this.minZoomScale = minZoomScale;
    }


    /**
     * 设置基础旋转角度
     */
    @SuppressWarnings("unused")
    public void setBaseRotation(final float degrees) {
        baseRotation = degrees % 360;
        update();
        rotationBy(baseRotation);
        checkAndDisplayMatrix();
    }

    /**
     * 获取ScaleType
     */
    public ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * 设置ScaleType
     */
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != null && scaleType != ScaleType.MATRIX && scaleType != this.scaleType) {
            this.scaleType = scaleType;
            update();
        }
    }

    /**
     * 是否开启了阅读模式
     */
    public boolean isReadMode() {
        return readMode;
    }

    /**
     * 设置阅读模式，开启后竖图将默认宽度充满屏幕
     */
    public void setReadMode(boolean readMode) {
        if (this.readMode != readMode) {
            this.readMode = readMode;
            update();
        }
    }


    /**
     * 是否允许父类在滑动到边缘的时候拦截事件（默认true）
     */
    @SuppressWarnings("unused")
    public boolean isAllowParentInterceptOnEdge() {
        return allowParentInterceptOnEdge;
    }

    /**
     * 设置是否允许父类在滑动到边缘的时候拦截事件（默认true）
     */
    @SuppressWarnings("unused")
    public void setAllowParentInterceptOnEdge(boolean allowParentInterceptOnEdge) {
        this.allowParentInterceptOnEdge = allowParentInterceptOnEdge;
    }


    /**
     * 获取单击监听器
     */
    OnViewTapListener getOnViewTapListener() {
        return onViewTapListener;
    }

    /**
     * 设置单击监听器
     */
    @SuppressWarnings("unused")
    public void setOnViewTapListener(OnViewTapListener onViewTapListener) {
        this.onViewTapListener = onViewTapListener;
    }

    /**
     * 设置飞速滚动监听器
     */
    @SuppressWarnings("unused")
    public void setOnDragFlingListener(OnDragFlingListener onDragFlingListener) {
        this.onDragFlingListener = onDragFlingListener;
    }

    /**
     * 设置缩放监听器
     */
    @SuppressWarnings("unused")
    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        this.onScaleChangeListener = onScaleChangeListener;
    }

    /**
     * 添加Matrix变化监听器
     */
    public void addOnMatrixChangeListener(OnMatrixChangedListener listener) {
        if (listener != null) {
            if (onMatrixChangedListenerList == null) {
                onMatrixChangedListenerList = new ArrayList<OnMatrixChangedListener>(1);
            }
            onMatrixChangedListenerList.add(listener);
        }
    }

    /**
     * 获取长按监听器
     */
    OnViewLongPressListener getOnViewLongPressListener() {
        return onViewLongPressListener;
    }

    /**
     * 设置长按监听器
     */
    public void setOnViewLongPressListener(OnViewLongPressListener onViewLongPressListener) {
        this.onViewLongPressListener = onViewLongPressListener;
    }


    /** -----------监听----------- **/

    /**
     * 飞速拖拽监听器
     */
    public interface OnDragFlingListener {
        boolean onFling(float startX, float startY, float velocityX, float velocityY);
    }

    /**
     * 单击监听器
     */
    public interface OnViewTapListener {
        void onViewTap(View view, float x, float y);
    }

    /**
     * Matrix变化监听器
     */
    public interface OnMatrixChangedListener {
        void onMatrixChanged(ImageZoomer imageZoomer);
    }

    /**
     * 缩放监听器
     */
    public interface OnScaleChangeListener {
        void onScaleChange(float scaleFactor, float focusX, float focusY);
    }

    /**
     * 长按监听器
     */
    public interface OnViewLongPressListener {
        void onViewLongPress(View view, float x, float y);
    }
}