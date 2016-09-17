/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.feature.zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import java.util.Arrays;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.feature.zoom.gestures.ActionListener;
import me.xiaopan.sketch.feature.zoom.gestures.OnScaleDragGestureListener;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetector;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetectorCompat;
import me.xiaopan.sketch.util.SketchUtils;

// TODO 解决嵌套在别的可滑动View中时，会导致ArrayIndexOutOfBoundsException异常，初步猜测requestDisallowInterceptTouchEvent引起的
public class ImageZoomer implements View.OnTouchListener, OnScaleDragGestureListener,
        ViewTreeObserver.OnGlobalLayoutListener, ActionListener {
    public static final String NAME = "ImageZoomer";

    public static final float DEFAULT_MAXIMIZE_SCALE = 1.75f;
    public static final float DEFAULT_MINIMUM_SCALE = 1.0f;

    public static final int DEFAULT_ZOOM_DURATION = 200;

    public static final int EDGE_NONE = -1;
    public static final int EDGE_START = 0;
    public static final int EDGE_END = 1;
    public static final int EDGE_BOTH = 2;

    // incoming
    private Context context;
    private WeakReference<ImageView> viewReference;

    // zoom configurable options
    private int zoomDuration = DEFAULT_ZOOM_DURATION;   // 双击缩放动画持续时间
    private float minZoomScale = DEFAULT_MINIMUM_SCALE;
    private float maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
    private boolean readMode;   // 阅读模式下，竖图将默认横向充满屏幕
    private boolean zoomable = true;    // 是否可以缩放
    private Interpolator zoomInterpolator = new AccelerateDecelerateInterpolator();

    // other configurable options
    private float baseRotation; // 基础旋转角度
    private boolean allowParentInterceptOnEdge = true;  // 允许父ViewGroup在滑动到边缘时拦截事件
    private ScaleType scaleType = ScaleType.FIT_CENTER; // ImageView的ScaleType

    // listeners
    private OnViewTapListener onViewTapListener;
    private OnDragFlingListener onDragFlingListener;
    private OnScaleChangeListener onScaleChangeListener;
    private OnViewLongPressListener onViewLongPressListener;
    private ArrayList<OnMatrixChangedListener> onMatrixChangedListenerList;

    // zoom properties
    private float fullZoomScale; // 能够让图片完成显示的缩放比例
    private float fillZoomScale;    // 能够让图片填满宽或高的缩放比例
    private float originZoomScale;  // 能够让图片按照原始比例一比一显示的比例
    private float[] doubleClickZoomScales = new float[]{DEFAULT_MINIMUM_SCALE, DEFAULT_MAXIMIZE_SCALE}; // 双击缩放所使用的比例
    private boolean zooming;    // 缩放中状态

    // other properties
    private int horScrollEdge = EDGE_NONE; // 横向滚动边界
    private int verScrollEdge = EDGE_NONE; // 竖向滚动边界
    private GestureDetector tapGestureDetector; // 点击手势识别器
    private FlingTranslateRunner flingTranslateRunner;  // 执行飞速滚动
    private ScaleDragGestureDetector scaleDragGestureDetector;  // 缩放和拖拽手势识别器
    private boolean disallowParentInterceptTouchEvent;  // 控制滑动或缩放中到达边缘了依然禁止父类拦截事件

    // info caches
    private float tempLastScaleFocusX, tempLastScaleFocusY;  // 缓存最后一次缩放手势的坐标，在恢复缩放比例时使用
    private final Rect tempViewBounds = new Rect(); // 缓存ImageView的left、top、right、bottom，在其变化时对比使用
    private final RectF tempDisplayRectF = new RectF();

    private final Matrix baseMatrix = new Matrix(); // 存储基础缩放、移动、旋转信息
    private final Matrix scaleAndDragMatrix = new Matrix(); // 存储用户产生的缩放和拖拽信息
    private final Matrix tempDrawMatrix = new Matrix(); // 存储baseMatrix和scaleAndDragMatrix融合后的信息，用于绘制

    private ScrollBarManager scrollBarManager;

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
        scaleDragGestureDetector.setActionListener(this);
        baseRotation = 0.0f;

        // listening to the ImageView size changes
        ViewTreeObserver observer = imageView.getViewTreeObserver();
        if (observer != null) {
            observer.addOnGlobalLayoutListener(this);
        }

        scrollBarManager = new ScrollBarManager(context, this);
    }

    @SuppressWarnings("unused")
    public ImageZoomer(ImageView imageView) {
        this(imageView, false);
    }

    public void draw(Canvas canvas) {
        scrollBarManager.drawScrollBar(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!zoomable || !hasDrawable()) {
            return false;
        }

        // 缩放、拖拽手势处理
        boolean beforeInScaling = scaleDragGestureDetector.isScaling();
        boolean beforeInDragging = scaleDragGestureDetector.isDragging();

        boolean scaleDragHandled = scaleDragGestureDetector.onTouchEvent(event);

        boolean afterInScaling = scaleDragGestureDetector.isScaling();
        boolean afterInDragging = scaleDragGestureDetector.isDragging();

        disallowParentInterceptTouchEvent = !beforeInScaling && !afterInScaling && beforeInDragging && afterInDragging;

        // 点击手势处理
        boolean tapHandled = tapGestureDetector != null && tapGestureDetector.onTouchEvent(event);

        return scaleDragHandled || tapHandled;
    }

    @Override
    public void onActionDown(MotionEvent ev) {
        tempLastScaleFocusX = 0;
        tempLastScaleFocusY = 0;

        // 上来就禁止父View拦截事件
        ImageView imageView = getImageView();
        ViewParent parent = imageView.getParent();
        if (parent != null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". disallow parent intercept touch event. onActionDown");
            }
            parent.requestDisallowInterceptTouchEvent(true);
        }

        // 取消快速滚动
        cancelFling();
    }

    @Override
    public void onActionUp(MotionEvent ev) {
        float currentScale = SketchUtils.formatFloat(getZoomScale(), 2);
        if (currentScale < SketchUtils.formatFloat(minZoomScale, 2)) {
            // 如果当前缩放倍数小于最小倍数就回滚至最小倍数
            RectF displayRectF = new RectF();
            checkMatrixBounds();
            getDisplayRect(displayRectF);
            if (!displayRectF.isEmpty()) {
                zoom(minZoomScale, displayRectF.centerX(), displayRectF.centerY(), true);
            }
        } else if (currentScale > SketchUtils.formatFloat(maxZoomScale, 2)) {
            // 如果当前缩放倍数大于最大倍数就回滚至最大倍数
            if (tempLastScaleFocusX != 0 && tempLastScaleFocusY != 0) {
                zoom(maxZoomScale, tempLastScaleFocusX, tempLastScaleFocusY, true);
            }
        }
    }

    @Override
    public void onActionCancel(MotionEvent ev) {
        onActionUp(ev);
    }

    @Override
    public void onDrag(float dx, float dy) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        // 缩放中不能拖拽
        if (scaleDragGestureDetector.isScaling()) {
            return;
        }

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". " + String.format("drag. dx: %s, dy: %s", dx, dy));
        }

        scaleAndDragMatrix.postTranslate(dx, dy);
        checkAndDisplayMatrix();

        // 滑动到边缘时父类可以拦截触摸事件
        ViewParent parent = imageView.getParent();
        if (parent != null) {
            if (!allowParentInterceptOnEdge || scaleDragGestureDetector.isScaling() || disallowParentInterceptTouchEvent) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". disallow parent intercept touch event. onDrag. allowParentInterceptOnEdge=" + allowParentInterceptOnEdge + ", scaling=" + scaleDragGestureDetector.isScaling() + ", tempDisallowParentInterceptTouchEvent=" + disallowParentInterceptTouchEvent);
                }
                parent.requestDisallowInterceptTouchEvent(true);
                return;
            }

            // 暂时不处理顶部和底部边界
            if (horScrollEdge == EDGE_BOTH || (horScrollEdge == EDGE_START && dx >= 1f) || (horScrollEdge == EDGE_END && dx <= -1f)
//                    || verScrollEdge == EDGE_BOTH || (verScrollEdge == EDGE_START && dy >= 1f) || (verScrollEdge == EDGE_END && dy <= -1f)
                    ) {
                if (Sketch.isDebugMode()) {
                    String scrollEdgeName = String.format("%s-%s", getScrollEdgeName(horScrollEdge), getScrollEdgeName(verScrollEdge));
                    Log.d(Sketch.TAG, NAME + ". allow parent intercept touch event. onDrag. scrollEdge=" + scrollEdgeName);
                }
                parent.requestDisallowInterceptTouchEvent(false);
            } else {
                if (Sketch.isDebugMode()) {
                    String scrollEdgeName = String.format("%s_%s", getScrollEdgeName(horScrollEdge), getScrollEdgeName(verScrollEdge));
                    Log.w(Sketch.TAG, NAME + ". disallow parent intercept touch event. onDrag. scrollEdge=" + scrollEdgeName);
                }
            }
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        flingTranslateRunner = new FlingTranslateRunner(context, this);
        flingTranslateRunner.fling((int) velocityX, (int) velocityY);

        if (onDragFlingListener != null) {
            onDragFlingListener.onFling(startX, startY, velocityX, velocityY);
        }
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". " + String.format("scale. scaleFactor: %s, dx: %s, dy: %s", scaleFactor, focusX, focusY));
        }

        tempLastScaleFocusX = focusX;
        tempLastScaleFocusY = focusY;

        float oldSuppScale = SketchUtils.getMatrixScale(scaleAndDragMatrix);
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

        scaleAndDragMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        checkAndDisplayMatrix();
        if (onScaleChangeListener != null) {
            onScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
        }
    }

    @Override
    public boolean onScaleBegin() {
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". " + "scale begin");
        }

        setZooming(true);
        return true;
    }

    @Override
    public void onScaleEnd() {
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". " + "scale end");
        }

        float currentScale = SketchUtils.formatFloat(getZoomScale(), 2);
        boolean overMinZoomScale = currentScale < SketchUtils.formatFloat(minZoomScale, 2);
        boolean overMaxZoomScale = currentScale > SketchUtils.formatFloat(maxZoomScale, 2);
        if (!overMinZoomScale && !overMaxZoomScale) {
            setZooming(false);
            if (onMatrixChangedListenerList != null && !onMatrixChangedListenerList.isEmpty()) {
                for (int w = 0, size = onMatrixChangedListenerList.size(); w < size; w++) {
                    onMatrixChangedListenerList.get(w).onMatrixChanged(this);
                }
            }
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

            if (top != tempViewBounds.top || bottom != tempViewBounds.bottom || left != tempViewBounds.left || right != tempViewBounds.right) {
                tempViewBounds.set(left, top, right, bottom);

                resetZoomScales();
                updateBaseMatrix();
                resetMatrix();
            }
        } else {
            resetZoomScales();
            updateBaseMatrix();
            resetMatrix();
        }
    }

    /** -----------私有功能----------- **/

    /**
     * 重置最小和最大缩放比例
     */
    private void resetZoomScales() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            fullZoomScale = fillZoomScale = originZoomScale = 1f;
            minZoomScale = DEFAULT_MINIMUM_SCALE;
            maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
            doubleClickZoomScales = new float[]{DEFAULT_MINIMUM_SCALE, DEFAULT_MAXIMIZE_SCALE};
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            fullZoomScale = fillZoomScale = originZoomScale = 1f;
            minZoomScale = DEFAULT_MINIMUM_SCALE;
            maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
            doubleClickZoomScales = new float[]{DEFAULT_MINIMUM_SCALE, DEFAULT_MAXIMIZE_SCALE};
            return;
        }

        final int viewWidth = getImageViewWidth();
        final int viewHeight = getImageViewHeight();
        if (viewWidth == 0 || viewHeight == 0) {
            fullZoomScale = fillZoomScale = originZoomScale = 1f;
            minZoomScale = DEFAULT_MINIMUM_SCALE;
            maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
            doubleClickZoomScales = new float[]{DEFAULT_MINIMUM_SCALE, DEFAULT_MAXIMIZE_SCALE};
            return;
        }

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth == 0 || drawableHeight == 0) {
            fullZoomScale = fillZoomScale = originZoomScale = 1f;
            minZoomScale = DEFAULT_MINIMUM_SCALE;
            maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
            doubleClickZoomScales = new float[]{DEFAULT_MINIMUM_SCALE, DEFAULT_MAXIMIZE_SCALE};
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

        minZoomScale = fullZoomScale;
        maxZoomScale = Math.max(originZoomScale, fillZoomScale);

        float oneLevelZoomScale;
        float twoLevelZoomScale;
        if (canUseReadMode(drawableWidth, drawableHeight, viewHeight)) {
            if (fullZoomScale < fillZoomScale) {
                oneLevelZoomScale = fullZoomScale;
                twoLevelZoomScale = fillZoomScale;
            } else {
                oneLevelZoomScale = fillZoomScale;
                twoLevelZoomScale = fullZoomScale;
            }
        } else {
            oneLevelZoomScale = fullZoomScale;

            // 二级缩放比例将在原始比例和充满比例中产生
            if (originZoomScale > fillZoomScale && (fillZoomScale * 1.2f) >= originZoomScale) {
                // 如果原始比例仅仅比充满比例大一点点，还是用充满比例作为二级缩放比例比较好
                twoLevelZoomScale = fillZoomScale;
            } else {
                // 否则的话谁大用谁作为二级缩放比例
                twoLevelZoomScale = Math.max(fillZoomScale, originZoomScale);
            }

            // 二级缩放比例和一级缩放比例的差距不能太小，最小得是一级缩放比例的1.5倍
            twoLevelZoomScale = Math.max(twoLevelZoomScale, oneLevelZoomScale * 1.5f);

            maxZoomScale = twoLevelZoomScale;
        }
        doubleClickZoomScales = new float[]{oneLevelZoomScale, twoLevelZoomScale};
        Arrays.sort(doubleClickZoomScales);
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
            horScrollEdge = EDGE_NONE;
            verScrollEdge = EDGE_NONE;
            return false;
        }

        final RectF displayRectF = tempDisplayRectF;
        getDisplayRect(displayRectF);
        if (displayRectF.isEmpty()) {
            horScrollEdge = EDGE_NONE;
            verScrollEdge = EDGE_NONE;
            return false;
        }

        final float displayHeight = displayRectF.height(), displayWidth = displayRectF.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getImageViewHeight();
        if ((int) displayHeight <= viewHeight) {
            switch (scaleType) {
                case FIT_START:
                    deltaY = -displayRectF.top;
                    break;
                case FIT_END:
                    deltaY = viewHeight - displayHeight - displayRectF.top;
                    break;
                default:
                    deltaY = (viewHeight - displayHeight) / 2 - displayRectF.top;
                    break;
            }
        } else if ((int) displayRectF.top > 0) {
            deltaY = -displayRectF.top;
        } else if ((int) displayRectF.bottom < viewHeight) {
            deltaY = viewHeight - displayRectF.bottom;
        }

        final int viewWidth = getImageViewWidth();
        if ((int) displayWidth <= viewWidth) {
            switch (scaleType) {
                case FIT_START:
                    deltaX = -displayRectF.left;
                    break;
                case FIT_END:
                    deltaX = viewWidth - displayWidth - displayRectF.left;
                    break;
                default:
                    deltaX = (viewWidth - displayWidth) / 2 - displayRectF.left;
                    break;
            }
        } else if ((int) displayRectF.left > 0) {
            deltaX = -displayRectF.left;
        } else if ((int) displayRectF.right < viewWidth) {
            deltaX = viewWidth - displayRectF.right;
        }

        // Finally actually translate the matrix
        scaleAndDragMatrix.postTranslate(deltaX, deltaY);

        if ((int) displayHeight <= viewHeight) {
            verScrollEdge = EDGE_BOTH;
        } else if ((int) displayRectF.top >= 0) {
            verScrollEdge = EDGE_START;
        } else if ((int) displayRectF.bottom <= viewHeight) {
            verScrollEdge = EDGE_END;
        } else {
            verScrollEdge = EDGE_NONE;
        }

        if ((int) displayWidth <= viewWidth) {
            horScrollEdge = EDGE_BOTH;
        } else if ((int) displayRectF.left >= 0) {
            horScrollEdge = EDGE_START;
        } else if ((int) displayRectF.right <= viewWidth) {
            horScrollEdge = EDGE_END;
        } else {
            horScrollEdge = EDGE_NONE;
        }

        return true;
    }

    /**
     * 重置
     */
    private void resetMatrix() {
        scaleAndDragMatrix.reset();
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

            scrollBarManager.matrixChanged();

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
        if (flingTranslateRunner != null) {
            flingTranslateRunner.cancelFling();
            flingTranslateRunner = null;
        }
    }

    /**
     * 是否可以使用阅读模式
     */
    private boolean canUseReadMode(int drawableWidth, int drawableHeight, int viewHeight){
        return readMode && drawableHeight > drawableWidth && drawableHeight > (viewHeight * 1.3f);
    }

    private String getScrollEdgeName(int scrollEdge){
        if (scrollEdge == EDGE_NONE) {
            return "NONE";
        } else if (scrollEdge == EDGE_START) {
            return "START";
        } else if (scrollEdge == EDGE_END) {
            return "END";
        } else if (scrollEdge == EDGE_BOTH) {
            return "BOTH";
        } else {
            return "UNKNOWN";
        }
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
        tempDrawMatrix.set(baseMatrix);
        tempDrawMatrix.postConcat(scaleAndDragMatrix);
        return tempDrawMatrix;
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

    /**
     * 获取最小缩放比例
     */
    @SuppressWarnings("unused")
    public float getMinZoomScale() {
        return minZoomScale;
    }

    /**
     * 获取最大缩放比例
     */
    @SuppressWarnings("unused")
    public float getMaxZoomScale() {
        return maxZoomScale;
    }

    /**
     * 获取双击缩放比例
     */
    public float[] getDoubleClickZoomScales() {
        return doubleClickZoomScales;
    }

    /**
     * 正在缩放
     */
    public boolean isZooming() {
        return zooming;
    }

    /**
     * 设置正在缩放状态
     */
    void setZooming(boolean zooming) {
        this.zooming = zooming;
    }

    // TODO: 16/9/17 获取旋转角度


    /** -----------交互功能----------- **/
    /**
     * 移动到指定位置
     */
    // TODO: 16/8/27 加上动画支持
    @SuppressWarnings("unused")
    public void translateTo(float x, float y) {
        scaleAndDragMatrix.setTranslate(x, y);
        applyMatrix(getDrawMatrix());
    }

    /**
     * 移动一段距离
     */
    public void translateBy(float dx, float dy) {
        scaleAndDragMatrix.postTranslate(dx, dy);
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
                Log.w(Sketch.TAG, NAME + ". Scale must be within the range of " + minZoomScale + "(minScale) and " + maxZoomScale + "(maxScale). " + scale);
                return;
            }

            if (animate) {
                imageView.post(new ZoomRunner(this, getZoomScale(), scale, focalX, focalY));
            } else {
                scale /= SketchUtils.getMatrixScale(baseMatrix);
                scaleAndDragMatrix.setScale(scale, scale, focalX, focalY);
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
    public boolean rotationTo(float degrees) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return false;
        }

        if (imageView instanceof SketchImageView) {
            SketchImageView sketchImageView = (SketchImageView) imageView;
            if (sketchImageView.isSupportLargeImage()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". large image viewer is worker, cannot use the rotation function");
                }
                return false;
            }
        }

        scaleAndDragMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
        return true;
    }

    /**
     * 增加旋转角度
     */
    public boolean rotationBy(float degrees) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return false;
        }

        if (imageView instanceof SketchImageView) {
            SketchImageView sketchImageView = (SketchImageView) imageView;
            if (sketchImageView.isSupportLargeImage()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". large image viewer is worker, cannot use the rotation function");
                }
                return false;
            }
        }

        scaleAndDragMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
        return true;
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

        scaleAndDragMatrix.set(displayMatrix);
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

            resetZoomScales();
            updateBaseMatrix();
            resetMatrix();
        } else {
            resetZoomScales();
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