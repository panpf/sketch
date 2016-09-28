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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
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
import me.xiaopan.sketch.drawable.BindDrawable;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.feature.zoom.gestures.ActionListener;
import me.xiaopan.sketch.feature.zoom.gestures.OnScaleDragGestureListener;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetector;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetectorCompat;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.util.SketchUtils;

// TODO 解决嵌套在别的可滑动View中时，会导致ArrayIndexOutOfBoundsException异常，初步猜测requestDisallowInterceptTouchEvent引起的
public class ImageZoomer implements View.OnTouchListener, OnScaleDragGestureListener,
        ViewTreeObserver.OnGlobalLayoutListener, ActionListener {
    public static final String NAME = "ImageZoomer";

    private static final float DEFAULT_MAXIMIZE_SCALE = 1.75f;
    private static final float DEFAULT_MINIMUM_SCALE = 1.0f;
    private static final float[] DEFAULT_DOUBLE_CLICK_ZOOM_SCALES = new float[]{DEFAULT_MINIMUM_SCALE, DEFAULT_MAXIMIZE_SCALE};

    private static final int DEFAULT_ZOOM_DURATION = 200;

    private static final int EDGE_NONE = -1;
    private static final int EDGE_START = 0;
    private static final int EDGE_END = 1;
    private static final int EDGE_BOTH = 2;

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
    private float[] doubleClickZoomScales = DEFAULT_DOUBLE_CLICK_ZOOM_SCALES; // 双击缩放所使用的比例
    private boolean zooming;    // 缩放中状态

    // other properties
    private int horScrollEdge = EDGE_NONE; // 横向滚动边界
    private int verScrollEdge = EDGE_NONE; // 竖向滚动边界
    private GestureDetector tapGestureDetector; // 点击手势识别器
    private LocationRunner locationRunner;  // 定位执行器
    private FlingTranslateRunner flingTranslateRunner;  // 执行飞速滚动
    private ScaleDragGestureDetector scaleDragGestureDetector;  // 缩放和拖拽手势识别器
    private boolean disallowParentInterceptTouchEvent;  // 控制滑动或缩放中到达边缘了依然禁止父类拦截事件

    // info caches
    private float tempLastScaleFocusX, tempLastScaleFocusY;  // 缓存最后一次缩放手势的坐标，在恢复缩放比例时使用
    private final Rect tempViewBounds = new Rect(); // 缓存ImageView的left、top、right、bottom，在其变化时对比使用
    private final RectF tempDisplayRectF = new RectF();

    private final Matrix baseMatrix = new Matrix(); // 存储基础缩放、移动
    private final Matrix supportMatrix = new Matrix(); // 存储用户产生的缩放、拖拽和旋转信息
    private final Matrix tempDrawMatrix = new Matrix(); // 存储baseMatrix和supportMatrix融合后的信息，用于绘制

    private ScrollBar scrollBar;
    private final Point tempDrawableSize = new Point();
    private final Point tempImageViewSize = new Point();

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

        scrollBar = new ScrollBar(context, this);
    }

    @SuppressWarnings("unused")
    public ImageZoomer(ImageView imageView) {
        this(imageView, false);
    }

    public void draw(Canvas canvas) {
        scrollBar.drawScrollBar(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!zoomable || !isUsableDrawable()) {
            return false;
        }

        // 定位操作不能被打断
        if (locationRunner != null) {
            if (locationRunner.isRunning()) {
                return false;
            }
            locationRunner = null;
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
        ViewParent parent = imageView != null ? imageView.getParent() : null;
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
            RectF drawRectF = new RectF();
            getDrawRect(drawRectF);
            if (!drawRectF.isEmpty()) {
                zoom(minZoomScale, drawRectF.centerX(), drawRectF.centerY(), true);
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

        supportMatrix.postTranslate(dx, dy);
        checkAndApplyMatrix();

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

        float oldSuppScale = SketchUtils.getMatrixScale(supportMatrix);
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

        supportMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        checkAndApplyMatrix();
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

        Rect newViewBounds = new Rect(imageView.getLeft(), imageView.getTop(), imageView.getRight(), imageView.getBottom());
        if (!newViewBounds.equals(tempViewBounds)) {
            tempViewBounds.set(newViewBounds);
            update();
        }
    }

    /**
     * 当Drawable或者ImageView的尺寸发生变化时就需要调用此方法来更新
     */
    public void update() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        if (imageView.getScaleType() != ScaleType.MATRIX) {
            imageView.setScaleType(ScaleType.MATRIX);
        }

        resetZoomScales();
        resetBaseMatrix();
        resetSupportMatrix();

        checkAndApplyMatrix();
    }

    /** -----------私有功能----------- **/

    /**
     * 重置各种缩放比例
     */
    private void resetZoomScales() {
        fullZoomScale = fillZoomScale = originZoomScale = 1f;
        minZoomScale = DEFAULT_MINIMUM_SCALE;
        maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
        doubleClickZoomScales = DEFAULT_DOUBLE_CLICK_ZOOM_SCALES;

        Point imageViewSize = getImageViewSize();
        if (imageViewSize.x == 0 || imageViewSize.y == 0) {
            return;
        }

        Point drawableSize = getDrawableSize();
        if (drawableSize.x == 0 || drawableSize.y == 0) {
            return;
        }

        final int viewWidth = imageViewSize.x;
        final int viewHeight = imageViewSize.y;
        final int drawableWidth = drawableSize.x;
        final int drawableHeight = drawableSize.y;

        final float widthScale = (float) viewWidth / drawableWidth;
        final float heightScale = (float) viewHeight / drawableHeight;
        if (widthScale != heightScale) {
            fullZoomScale = Math.min(widthScale, heightScale);
            fillZoomScale = Math.max(widthScale, heightScale);
        } else {
            fullZoomScale = widthScale;
            fillZoomScale = widthScale;
        }
        Drawable finalDrawable = SketchUtils.getLastDrawable(getDrawable());
        if (finalDrawable instanceof SketchDrawable
                && getImageView() instanceof ImageViewInterface
                && ((ImageViewInterface) getImageView()).isSupportLargeImage()) {
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
    private void resetBaseMatrix() {
        baseMatrix.reset();

        Point imageViewSize = getImageViewSize();
        if (imageViewSize.x == 0 || imageViewSize.y == 0) {
            return;
        }

        Point drawableSize = getDrawableSize();
        if (drawableSize.x == 0 || drawableSize.y == 0) {
            return;
        }

        final int viewWidth = imageViewSize.x;
        final int viewHeight = imageViewSize.y;
        final int drawableWidth = drawableSize.x;
        final int drawableHeight = drawableSize.y;

        final float widthScale = (float) viewWidth / drawableWidth;
        final float heightScale = (float) viewHeight / drawableHeight;

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
     * 重置用户产生的Matrix信息
     */
    private void resetSupportMatrix() {
        supportMatrix.reset();
        rotateBy(baseRotation);
    }

    /**
     * 检查应用Matrix后的边界，防止超出范围
     */
    private boolean checkMatrixBounds() {
        Point imageViewSize = getImageViewSize();
        if (imageViewSize.x == 0 || imageViewSize.y == 0) {
            horScrollEdge = EDGE_NONE;
            verScrollEdge = EDGE_NONE;
            return false;
        }

        final RectF drawRectF = tempDisplayRectF;
        getDrawRect(drawRectF);
        if (drawRectF.isEmpty()) {
            horScrollEdge = EDGE_NONE;
            verScrollEdge = EDGE_NONE;
            return false;
        }

        final float displayHeight = drawRectF.height(), displayWidth = drawRectF.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = imageViewSize.y;
        if ((int) displayHeight <= viewHeight) {
            switch (scaleType) {
                case FIT_START:
                    deltaY = -drawRectF.top;
                    break;
                case FIT_END:
                    deltaY = viewHeight - displayHeight - drawRectF.top;
                    break;
                default:
                    deltaY = (viewHeight - displayHeight) / 2 - drawRectF.top;
                    break;
            }
        } else if ((int) drawRectF.top > 0) {
            deltaY = -drawRectF.top;
        } else if ((int) drawRectF.bottom < viewHeight) {
            deltaY = viewHeight - drawRectF.bottom;
        }

        final int viewWidth = imageViewSize.x;
        if ((int) displayWidth <= viewWidth) {
            switch (scaleType) {
                case FIT_START:
                    deltaX = -drawRectF.left;
                    break;
                case FIT_END:
                    deltaX = viewWidth - displayWidth - drawRectF.left;
                    break;
                default:
                    deltaX = (viewWidth - displayWidth) / 2 - drawRectF.left;
                    break;
            }
        } else if ((int) drawRectF.left > 0) {
            deltaX = -drawRectF.left;
        } else if ((int) drawRectF.right < viewWidth) {
            deltaX = viewWidth - drawRectF.right;
        }

        // Finally actually translate the matrix
        supportMatrix.postTranslate(deltaX, deltaY);

        if ((int) displayHeight <= viewHeight) {
            verScrollEdge = EDGE_BOTH;
        } else if ((int) drawRectF.top >= 0) {
            verScrollEdge = EDGE_START;
        } else if ((int) drawRectF.bottom <= viewHeight) {
            verScrollEdge = EDGE_END;
        } else {
            verScrollEdge = EDGE_NONE;
        }

        if ((int) displayWidth <= viewWidth) {
            horScrollEdge = EDGE_BOTH;
        } else if ((int) drawRectF.left >= 0) {
            horScrollEdge = EDGE_START;
        } else if ((int) drawRectF.right <= viewWidth) {
            horScrollEdge = EDGE_END;
        } else {
            horScrollEdge = EDGE_NONE;
        }

        return true;
    }

    /**
     * 检查并应用Matrix
     */
    private void checkAndApplyMatrix() {
        if (!checkMatrixBounds()) {
            return;
        }

        ImageView imageView = getImageView();
        if (!ScaleType.MATRIX.equals(imageView.getScaleType())) {
            throw new IllegalStateException("ImageView scaleType must be is MATRIX");
        }

        scrollBar.matrixChanged();
        imageView.setImageMatrix(getDrawMatrix());
        if (onMatrixChangedListenerList != null && !onMatrixChangedListenerList.isEmpty()) {
            for (int w = 0, size = onMatrixChangedListenerList.size(); w < size; w++) {
                onMatrixChangedListenerList.get(w).onMatrixChanged(this);
            }
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

    /**
     * 获取边界名称，log专用
     */
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
     * 获取ImageView的尺寸
     */
    public Point getImageViewSize() {
        ImageView imageView = getImageView();
        if (imageView != null) {
            tempImageViewSize.set(imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight(), imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom());
        } else {
            tempImageViewSize.set(0, 0);
        }
        return tempImageViewSize;
    }

    /**
     * 获取预览图
     */
    public Drawable getDrawable() {
        ImageView imageView = getImageView();
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0) {
                return drawable;
            }
        }
        return null;
    }

    /**
     * 获取预览图的尺寸
     */
    public Point getDrawableSize() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            tempDrawableSize.set(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        } else {
            tempDrawableSize.set(0, 0);
        }
        return tempDrawableSize;
    }

    /**
     * 图片是否可用，主要过滤掉占位图
     */
    public boolean isUsableDrawable(){
        Drawable drawable = getDrawable();
        return drawable != null && !(drawable instanceof BindDrawable);
    }

    /**
     * 获取绘制Matrix
     */
    private Matrix getDrawMatrix() {
        tempDrawMatrix.set(baseMatrix);
        tempDrawMatrix.postConcat(supportMatrix);
        return tempDrawMatrix;
    }

    /**
     * 拷贝绘制Matrix的参数
     */
    public void getDrawMatrix(Matrix matrix) {
        matrix.set(getDrawMatrix());
    }

    /**
     * 获取绘制区域
     */
    public void getDrawRect(RectF rectF) {
        Point drawableSize = getDrawableSize();
        if (drawableSize.x == 0 || drawableSize.y == 0) {
            rectF.setEmpty();
            return;
        }

        rectF.set(0, 0, drawableSize.x, drawableSize.y);

        getDrawMatrix().mapRect(rectF);
    }

    /**
     * 获取预览图上用户可以看到的区域
     */
    public void getVisibleRect(Rect rect) {
        Point imageViewSize = getImageViewSize();
        if (imageViewSize.x == 0 || imageViewSize.y == 0) {
            rect.setEmpty();
            return;
        }

        Point drawableSize = getDrawableSize();
        if (drawableSize.x == 0 || drawableSize.y == 0) {
            rect.setEmpty();
            return;
        }

        RectF drawRectF = new RectF();
        getDrawRect(drawRectF);
        if (drawRectF.isEmpty()) {
            rect.setEmpty();
            return;
        }

        final int viewWidth = imageViewSize.x;
        final int viewHeight = imageViewSize.y;
        final float displayWidth = drawRectF.width();
        final float displayHeight = drawRectF.height();
        final int drawableWidth = drawableSize.x;

        final float scale = displayWidth / drawableWidth;

        float left;
        float right;
        if (drawRectF.left >= 0) {
            left = 0;
        } else {
            left = Math.abs(drawRectF.left);
        }
        if (displayWidth >= viewWidth) {
            right = viewWidth + left;
        } else {
            right = drawRectF.right - drawRectF.left;
        }

        float top;
        float bottom;
        if (drawRectF.top >= 0) {
            top = 0;
        } else {
            top = Math.abs(drawRectF.top);
        }
        if (displayHeight >= viewHeight) {
            bottom = viewHeight + top;
        } else {
            bottom = drawRectF.bottom - drawRectF.top;
        }

        left /= scale;
        right /= scale;
        top /= scale;
        bottom /= scale;

        rect.set(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
    }

    /**
     * 清理
     */
    @SuppressWarnings("WeakerAccess")
    void cleanup() {
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
    @SuppressWarnings("WeakerAccess")
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
     * 定位到指定位置
     */
    @SuppressWarnings("unused")
    public void location(float x, float y) {
        cancelFling();

        if (locationRunner != null) {
            locationRunner.cancel();
        }

        locationRunner = new LocationRunner(context, this);
        locationRunner.start(x, y);
    }

    /**
     * 移动一段距离
     */
    void translateBy(float dx, float dy) {
        supportMatrix.postTranslate(dx, dy);
        checkAndApplyMatrix();
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
                supportMatrix.setScale(scale, scale, focalX, focalY);
                checkAndApplyMatrix();
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
    public boolean rotateTo(float degrees) {
        if (degrees % 90 != 0) {
            Log.w(Sketch.TAG, NAME + ". rotate degrees must be in multiples of 90");
            return false;
        }

        ImageView imageView = getImageView();
        if (imageView == null) {
            return false;
        }

        if (imageView instanceof SketchImageView) {
            SketchImageView sketchImageView = (SketchImageView) imageView;
            if (sketchImageView.isSupportLargeImage()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". large image viewer is worker, cannot use the rotate function");
                }
                return false;
            }
        }

        supportMatrix.setRotate(degrees % 360);
        checkAndApplyMatrix();
        return true;
    }

    /**
     * 增加旋转角度
     */
    public boolean rotateBy(float degrees) {
        if (degrees % 90 != 0) {
            Log.w(Sketch.TAG, NAME + ". rotate degrees must be in multiples of 90");
            return false;
        }

        ImageView imageView = getImageView();
        if (imageView == null) {
            return false;
        }

        if (imageView instanceof SketchImageView) {
            SketchImageView sketchImageView = (SketchImageView) imageView;
            if (sketchImageView.isSupportLargeImage()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". large image viewer is worker, cannot use the rotate function");
                }
                return false;
            }
        }

        supportMatrix.postRotate(degrees % 360);
        checkAndApplyMatrix();
        return true;
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
    @SuppressWarnings("WeakerAccess")
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
    @SuppressWarnings("WeakerAccess")
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
    public boolean setBaseRotation(final float degrees) {
        if (degrees % 90 != 0) {
            Log.w(Sketch.TAG, NAME + ". rotate degrees must be in multiples of 90");
            return false;
        }

        baseRotation = degrees % 360;
        update();
        rotateBy(baseRotation);
        checkAndApplyMatrix();
        return true;
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
    @SuppressWarnings("WeakerAccess")
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
    @SuppressWarnings("WeakerAccess")
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