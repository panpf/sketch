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
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.drawable.LoadingDrawable;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.feature.zoom.gestures.ActionListener;
import me.xiaopan.sketch.feature.zoom.gestures.OnScaleDragGestureListener;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetector;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetectorCompat;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.util.SketchUtils;

// TODO 解决嵌套在别的可滑动View中时，会导致ArrayIndexOutOfBoundsException异常，初步猜测requestDisallowInterceptTouchEvent引起的
@SuppressWarnings("SuspiciousNameCombination")
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
    private int rotateDegrees; // 旋转角度
    private boolean allowParentInterceptOnEdge = true;  // 允许父ViewGroup在滑动到边缘时拦截事件
    private ScaleType scaleType = ScaleType.FIT_CENTER; // ImageView的ScaleType

    // listeners
    private OnViewTapListener onViewTapListener;
    private OnDragFlingListener onDragFlingListener;
    private OnScaleChangeListener onScaleChangeListener;
    private OnRotateChangeListener onRotateChangeListener;
    private OnViewLongPressListener onViewLongPressListener;
    private ArrayList<OnMatrixChangeListener> onMatrixChangeListenerList;

    // zoom properties
    private float fullZoomScale; // 能够看到图片全貌的缩放比例
    private float fillZoomScale;    // 能够让图片填满宽或高的缩放比例
    private float originZoomScale;  // 能够让图片按照真实尺寸一比一显示的缩放比例
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
    private ScrollBar scrollBar;    // 绘制滚动条

    // info caches
    private float tempLastScaleFocusX, tempLastScaleFocusY;  // 缓存最后一次缩放手势的坐标，在恢复缩放比例时使用
    private final Rect tempViewBounds = new Rect(); // 缓存ImageView的left、top、right、bottom，在其变化时对比使用
    private final RectF tempDisplayRectF = new RectF();

    // Matrix
    private final Matrix baseMatrix = new Matrix(); // 存储基础缩放、移动
    private final Matrix supportMatrix = new Matrix(); // 存储用户产生的缩放、拖拽和旋转信息
    private final Matrix drawMatrix = new Matrix(); // 存储baseMatrix和supportMatrix融合后的信息，用于绘制

    // drawable and view info
    private Drawable drawable;
    private final Point drawableSize = new Point();
    private final Point imageViewSize = new Point();

    public ImageZoomer(ImageView imageView, boolean provideTouchEvent) {
        context = imageView.getContext().getApplicationContext();

        // initialize
        tapGestureDetector = new GestureDetector(context, new TapListener(this));
        scaleDragGestureDetector = ScaleDragGestureDetectorCompat.newInstance(context, this);
        scaleDragGestureDetector.setActionListener(this);

        scrollBar = new ScrollBar(context, this);

        init(imageView, provideTouchEvent);
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
        if (!zoomable || !isWorking() || !isUsableDrawable()) {
            return false;
        }

        // 定位操作不能被打断
        if (locationRunner != null) {
            if (locationRunner.isRunning()) {
                if (SLogType.ZOOM.isEnabled()) {
                    SLog.w(SLogType.ZOOM, NAME, "disallow parent intercept touch event. location running");
                }
                requestDisallowInterceptTouchEvent(true);
                return true;
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
        if (SLogType.ZOOM.isEnabled()) {
            SLog.w(SLogType.ZOOM, NAME, "disallow parent intercept touch event. action down");
        }
        requestDisallowInterceptTouchEvent(true);

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

        if (SLogType.ZOOM.isEnabled()) {
            SLog.d(SLogType.ZOOM, NAME, "drag. dx: %s, dy: %s", dx, dy);
        }

        supportMatrix.postTranslate(dx, dy);
        checkAndApplyMatrix();

        if (!allowParentInterceptOnEdge || scaleDragGestureDetector.isScaling() || disallowParentInterceptTouchEvent) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, NAME, "disallow parent intercept touch event. onDrag. allowParentInterceptOnEdge=%s, scaling=%s, tempDisallowParentInterceptTouchEvent=%s",
                        allowParentInterceptOnEdge, scaleDragGestureDetector.isScaling(), disallowParentInterceptTouchEvent);
            }
            requestDisallowInterceptTouchEvent(true);
            return;
        }

        // 滑动到边缘时父类可以拦截触摸事件，但暂时不处理顶部和底部边界
//        if (horScrollEdge == EDGE_BOTH || (horScrollEdge == EDGE_START && dx >= 1f) || (horScrollEdge == EDGE_END && dx <= -1f)
//                    || verScrollEdge == EDGE_BOTH || (verScrollEdge == EDGE_START && dy >= 1f) || (verScrollEdge == EDGE_END && dy <= -1f)) {
        if (horScrollEdge == EDGE_BOTH || (horScrollEdge == EDGE_START && dx >= 1f) || (horScrollEdge == EDGE_END && dx <= -1f)) {
            SLog.i(SLogType.ZOOM, NAME, "allow parent intercept touch event. onDrag. scrollEdge=%s-%s",
                    getScrollEdgeName(horScrollEdge), getScrollEdgeName(verScrollEdge));
            requestDisallowInterceptTouchEvent(false);
        } else {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, NAME, "disallow parent intercept touch event. onDrag. scrollEdge=%s-%s",
                        getScrollEdgeName(horScrollEdge), getScrollEdgeName(verScrollEdge));
            }
            requestDisallowInterceptTouchEvent(true);
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
        if (SLogType.ZOOM.isEnabled()) {
            SLog.d(SLogType.ZOOM, NAME, "scale. scaleFactor: %s, dx: %s, dy: %s", scaleFactor, focusX, focusY);
        }

        tempLastScaleFocusX = focusX;
        tempLastScaleFocusY = focusY;

        float oldSuppScale = getSupportZoomScale();
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
            onScaleChangeListener.onScaleChanged(scaleFactor, focusX, focusY);
        }
    }

    @Override
    public boolean onScaleBegin() {
        if (SLogType.ZOOM.isEnabled()) {
            SLog.d(SLogType.ZOOM, NAME, "scale begin");
        }

        setZooming(true);
        return true;
    }

    @Override
    public void onScaleEnd() {
        if (SLogType.ZOOM.isEnabled()) {
            SLog.d(SLogType.ZOOM, NAME, "scale end");
        }

        float currentScale = SketchUtils.formatFloat(getZoomScale(), 2);
        boolean overMinZoomScale = currentScale < SketchUtils.formatFloat(minZoomScale, 2);
        boolean overMaxZoomScale = currentScale > SketchUtils.formatFloat(maxZoomScale, 2);
        if (!overMinZoomScale && !overMaxZoomScale) {
            setZooming(false);
            if (onMatrixChangeListenerList != null && !onMatrixChangeListenerList.isEmpty()) {
                for (int w = 0, size = onMatrixChangeListenerList.size(); w < size; w++) {
                    onMatrixChangeListenerList.get(w).onMatrixChanged(this);
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

    private void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }
        ViewParent parent = imageView.getParent();
        if (parent == null) {
            return;
        }
        parent.requestDisallowInterceptTouchEvent(disallowIntercept);
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

        resetSizes();
        resetZoomScales();
        resetBaseMatrix();
        resetSupportMatrix();

        checkAndApplyMatrix();
    }

    /** -----------私有功能----------- **/

    /**
     * 重置基础信息
     */
    private void resetSizes() {
        drawable = null;
        drawableSize.set(0, 0);
        imageViewSize.set(0, 0);

        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        final int imageViewWidth = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
        final int imageViewHeight = imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
        imageViewSize.set(imageViewWidth, imageViewHeight);

        Drawable newDrawable = imageView.getDrawable();
        if (newDrawable == null) {
            return;
        }

        final int drawableWidth = newDrawable.getIntrinsicWidth();
        final int drawableHeight = newDrawable.getIntrinsicHeight();
        if (drawableWidth == 0 || drawableHeight == 0) {
            return;
        }

        drawable = newDrawable;
        drawableSize.set(drawableWidth, drawableHeight);
    }

    /**
     * 重置最小、最大以及双击缩放比例
     */
    private void resetZoomScales() {
        fullZoomScale = fillZoomScale = originZoomScale = 1f;
        minZoomScale = DEFAULT_MINIMUM_SCALE;
        maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
        doubleClickZoomScales = DEFAULT_DOUBLE_CLICK_ZOOM_SCALES;

        if (!isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. resetZoomScales");
            }
            return;
        }

        final int viewWidth = imageViewSize.x;
        final int viewHeight = imageViewSize.y;
        final int previewWidth = rotateDegrees % 180 == 0 ? drawableSize.x : drawableSize.y;
        final int previewHeight = rotateDegrees % 180 == 0 ? drawableSize.y : drawableSize.x;
        final int imageWidth;
        final int imageHeight;
        ImageView imageView = getImageView();
        Drawable finalDrawable = SketchUtils.getLastDrawable(getDrawable());
        if (finalDrawable instanceof SketchDrawable && imageView instanceof ImageViewInterface &&
                ((ImageViewInterface) imageView).isSupportLargeImage()) {
            SketchDrawable sketchDrawable = (SketchDrawable) finalDrawable;
            imageWidth = rotateDegrees % 180 == 0 ? sketchDrawable.getOriginWidth() : sketchDrawable.getOriginHeight();
            imageHeight = rotateDegrees % 180 == 0 ? sketchDrawable.getOriginHeight() : sketchDrawable.getOriginWidth();
        } else {
            imageWidth = previewWidth;
            imageHeight = previewHeight;
        }

        final float widthScale = (float) viewWidth / previewWidth;
        final float heightScale = (float) viewHeight / previewHeight;
        boolean imageThanViewLarge = previewWidth > viewWidth || previewHeight > viewHeight;

        // 小的是完整显示比例，大的是充满比例
        fullZoomScale = Math.min(widthScale, heightScale);
        fillZoomScale = Math.max(widthScale, heightScale);
        originZoomScale = Math.max((float) imageWidth / previewWidth, (float) imageHeight / previewHeight);

        if (scaleType == ScaleType.CENTER || (scaleType == ScaleType.CENTER_INSIDE && !imageThanViewLarge)) {
            minZoomScale = 1.0f;
            maxZoomScale = Math.max(originZoomScale, fillZoomScale);
        } else if (scaleType == ScaleType.CENTER_CROP) {
            minZoomScale = fillZoomScale;
            // 由于CENTER_CROP的时候最小缩放比例就是充满比例，所以最大缩放比例一定要比充满比例大的多
            maxZoomScale = Math.max(originZoomScale, fillZoomScale * 1.5f);
        } else if (scaleType == ScaleType.FIT_START || scaleType == ScaleType.FIT_CENTER || scaleType == ScaleType.FIT_END ||
                (scaleType == ScaleType.CENTER_INSIDE && imageThanViewLarge)) {
            minZoomScale = fullZoomScale;

            ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getImageSizeCalculator();
            if (readMode && (sizeCalculator.canUseReadModeByHeight(imageWidth, imageHeight) ||
                    sizeCalculator.canUseReadModeByWidth(imageWidth, imageHeight))) {
                // 阅读模式下保证阅读效果最重要
                maxZoomScale = Math.max(originZoomScale, fillZoomScale);
            } else {
                // 如果原始比例仅仅比充满比例大一点点，还是用充满比例作为最大缩放比例比较好，否则谁大用谁
                if (originZoomScale > fillZoomScale && (fillZoomScale * 1.2f) >= originZoomScale) {
                    maxZoomScale = fillZoomScale;
                } else {
                    maxZoomScale = Math.max(originZoomScale, fillZoomScale);
                }

                // 最大缩放比例和最小缩放比例的差距不能太小，最小得是最小缩放比例的1.5倍
                maxZoomScale = Math.max(maxZoomScale, minZoomScale * 1.5f);
            }
        } else if (scaleType == ScaleType.FIT_XY) {
            minZoomScale = fullZoomScale;
            maxZoomScale = fullZoomScale;
        } else {
            // 基本不会走到这儿
            minZoomScale = fullZoomScale;
            maxZoomScale = fullZoomScale;
        }

        // 这样的情况基本不会出现，不过还是加层保险
        if (minZoomScale > maxZoomScale) {
            minZoomScale = minZoomScale + maxZoomScale;
            maxZoomScale = minZoomScale - maxZoomScale;
            minZoomScale = minZoomScale - maxZoomScale;
        }

        // 双击缩放比例始终由最小缩放比例和最大缩放比例组成
        doubleClickZoomScales = new float[]{minZoomScale, maxZoomScale};
    }

    /**
     * 重置基础Matrix
     */
    private void resetBaseMatrix() {
        baseMatrix.reset();

        if (!isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. resetBaseMatrix");
            }
            return;
        }

        final int viewWidth = imageViewSize.x;
        final int viewHeight = imageViewSize.y;
        final int previewWidth = rotateDegrees % 180 == 0 ? drawableSize.x : drawableSize.y;
        final int previewHeight = rotateDegrees % 180 == 0 ? drawableSize.y : drawableSize.x;
        final int imageWidth;
        final int imageHeight;
        ImageView imageView = getImageView();
        Drawable finalDrawable = SketchUtils.getLastDrawable(getDrawable());
        if (finalDrawable instanceof SketchDrawable && imageView instanceof ImageViewInterface &&
                ((ImageViewInterface) imageView).isSupportLargeImage()) {
            SketchDrawable sketchDrawable = (SketchDrawable) finalDrawable;
            imageWidth = rotateDegrees % 180 == 0 ? sketchDrawable.getOriginWidth() : sketchDrawable.getOriginHeight();
            imageHeight = rotateDegrees % 180 == 0 ? sketchDrawable.getOriginHeight() : sketchDrawable.getOriginWidth();
        } else {
            imageWidth = previewWidth;
            imageHeight = previewHeight;
        }

        final float widthScale = (float) viewWidth / previewWidth;
        final float heightScale = (float) viewHeight / previewHeight;
        boolean imageThanViewLarge = previewWidth > viewWidth || previewHeight > viewHeight;

        if (scaleType == ScaleType.CENTER || (scaleType == ScaleType.CENTER_INSIDE && !imageThanViewLarge)) {
            ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getImageSizeCalculator();
            if (readMode && sizeCalculator.canUseReadModeByHeight(imageWidth, imageHeight)) {
                baseMatrix.postScale(widthScale, widthScale);
            } else if (readMode && sizeCalculator.canUseReadModeByWidth(imageWidth, imageHeight)) {
                baseMatrix.postScale(heightScale, heightScale);
            } else {
                baseMatrix.postTranslate((viewWidth - previewWidth) / 2F, (viewHeight - previewHeight) / 2F);
            }
        } else if (scaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            baseMatrix.postScale(scale, scale);
            baseMatrix.postTranslate((viewWidth - previewWidth * scale) / 2F, (viewHeight - previewHeight * scale) / 2F);
        } else if (scaleType == ScaleType.FIT_START || scaleType == ScaleType.FIT_CENTER || scaleType == ScaleType.FIT_END ||
                (scaleType == ScaleType.CENTER_INSIDE && imageThanViewLarge)) {
            ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getImageSizeCalculator();
            if (readMode && sizeCalculator.canUseReadModeByHeight(imageWidth, imageHeight)) {
                baseMatrix.postScale(widthScale, widthScale);
            } else if (readMode && sizeCalculator.canUseReadModeByWidth(imageWidth, imageHeight)) {
                baseMatrix.postScale(heightScale, heightScale);
            } else {
                RectF mTempSrc = new RectF(0, 0, previewWidth, previewHeight);
                RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);
                Matrix.ScaleToFit scaleToFit;
                if (scaleType == ScaleType.FIT_START) {
                    scaleToFit = Matrix.ScaleToFit.START;
                } else if (scaleType == ScaleType.FIT_END) {
                    scaleToFit = Matrix.ScaleToFit.END;
                } else {
                    scaleToFit = Matrix.ScaleToFit.CENTER;
                }
                baseMatrix.setRectToRect(mTempSrc, mTempDst, scaleToFit);
            }
        } else if (scaleType == ScaleType.FIT_XY) {
            RectF mTempSrc = new RectF(0, 0, previewWidth, previewHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);
            baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL);
        }
    }

    /**
     * 重置用户产生的Matrix信息
     */
    private void resetSupportMatrix() {
        supportMatrix.reset();
        supportMatrix.postRotate(rotateDegrees);
    }

    /**
     * 检查应用Matrix后的边界，防止超出范围
     */
    private boolean checkMatrixBounds() {
        if (!isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. checkMatrixBounds");
            }
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

        if (onMatrixChangeListenerList != null && !onMatrixChangeListenerList.isEmpty()) {
            for (int w = 0, size = onMatrixChangeListenerList.size(); w < size; w++) {
                onMatrixChangeListenerList.get(w).onMatrixChanged(this);
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

    /**
     * 初始化
     */
    void init(ImageView imageView, boolean provideTouchEvent){
        viewReference = new WeakReference<ImageView>(imageView);

        // from ImageView get ScaleType
        scaleType = imageView.getScaleType();
        if (scaleType == ScaleType.MATRIX) {
            scaleType = ScaleType.FIT_CENTER;
        } else {
            imageView.setScaleType(ScaleType.MATRIX);
        }
        if (!provideTouchEvent) {
            imageView.setOnTouchListener(this);
        }

        // listening to the ImageView size changes
        ViewTreeObserver observer = imageView.getViewTreeObserver();
        if (observer != null) {
            observer.addOnGlobalLayoutListener(this);
        }
    }

    /**
     * 清理
     */
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

            // 恢复Matrix
            imageView.setImageMatrix(null);
        }

        scaleType = ScaleType.FIT_CENTER;
        drawable = null;
        imageViewSize.set(0, 0);
        drawableSize.set(0, 0);

        // Finally, clear ImageView
        viewReference = null;
    }

    /**
     * 设置正在缩放状态
     */
    void setZooming(boolean zooming) {
        this.zooming = zooming;
    }

    /** -----------可获取信息----------- **/

    public boolean isWorking() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return false;
        }

        final int imageViewWidth = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
        final int imageViewHeight = imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
        if (imageViewWidth <= 0
                || imageViewHeight <= 0
                || imageViewWidth != imageViewSize.x
                || imageViewHeight != imageViewSize.y) {
            return false;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null || drawable != this.drawable) {
            return false;
        }

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        return drawableWidth > 0 &&
                drawableHeight > 0 &&
                drawableWidth == drawableSize.x &&
                drawableHeight == drawableSize.y;
    }

    /**
     * 获取ImageView
     */
    public ImageView getImageView() {
        if (viewReference == null) {
            return null;
        }

        ImageView imageView = viewReference.get();
        if (imageView == null) {
            SLog.w(SLogType.ZOOM, NAME, "ImageView no longer exists. You should not use this ImageZoomer any more");
            cleanup();
        }

        return imageView;
    }

    /**
     * 获取ImageView的尺寸
     */
    public Point getImageViewSize() {
        return imageViewSize;
    }

    /**
     * 获取预览图
     */
    public Drawable getDrawable() {
        return drawable;
    }

    /**
     * 获取预览图的尺寸
     */
    public Point getDrawableSize() {
        return drawableSize;
    }

    /**
     * 图片是否可用，主要过滤掉占位图
     */
    public boolean isUsableDrawable(){
        return drawable != null && !(drawable instanceof LoadingDrawable);
    }

    /**
     * 获取绘制Matrix
     */
    private Matrix getDrawMatrix() {
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(supportMatrix);
        return drawMatrix;
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
        if (!isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. getDrawRect");
            }
            rectF.setEmpty();
            return;
        }

        Point drawableSize = getDrawableSize();
        rectF.set(0, 0, drawableSize.x, drawableSize.y);

        getDrawMatrix().mapRect(rectF);
    }

    /**
     * 获取预览图上用户可以看到的区域（不受旋转影响）
     */
    public void getVisibleRect(Rect rect) {
        if (!isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. getVisibleRect");
            }
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
        final int drawableWidth = rotateDegrees % 180 == 0 ? drawableSize.x : drawableSize.y;
        final int drawableHeight = rotateDegrees % 180 == 0 ? drawableSize.y : drawableSize.x;

        final float widthScale = displayWidth / drawableWidth;
        final float heightScale = displayHeight / drawableHeight;

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

        left /= widthScale;
        right /= widthScale;
        top /= heightScale;
        bottom /= heightScale;

        rect.set(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));

        // 将可见区域转回原始角度
        SketchUtils.reverseRotateRect(rect, rotateDegrees, drawableSize);
    }

    /**
     * 获取当前缩放比例
     */
    public float getZoomScale() {
        return SketchUtils.getMatrixScale(getDrawMatrix());
    }

    /**
     * 获取Base缩放比例
     */
    public float getBaseZoomScale() {
        return SketchUtils.getMatrixScale(baseMatrix);
    }

    /**
     * 获取support缩放比例
     */
    public float getSupportZoomScale() {
        return SketchUtils.getMatrixScale(supportMatrix);
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
     * 获取旋转角度
     */
    public int getRotateDegrees(){
        return rotateDegrees;
    }


    /** -----------交互功能----------- **/
    /**
     * 定位到预览图上指定的位置（不用考虑旋转角度）
     */
    @SuppressWarnings("unused")
    public boolean location(float x, float y, boolean animate) {
        if (!isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. location");
            }
            return false;
        }

        // 旋转定位点
        PointF pointF = new PointF(x, y);
        SketchUtils.rotatePoint(pointF, rotateDegrees, drawableSize);
        x = pointF.x;
        y = pointF.y;

        cancelFling();

        if (locationRunner != null) {
            locationRunner.cancel();
        }

        final int imageViewWidth = imageViewSize.x;
        final int imageViewHeight = imageViewSize.y;

        // 充满的时候是无法移动的，因此先放到最大
        final float scale = SketchUtils.formatFloat(getZoomScale(), 2);
        final float fullZoomScale = SketchUtils.formatFloat(getFullZoomScale(), 2);
        if (scale == fullZoomScale) {
            float[] zoomScales = getDoubleClickZoomScales();
            zoom(zoomScales[zoomScales.length - 1], x, y, false);
        }

        RectF drawRectF = new RectF();
        getDrawRect(drawRectF);

        // 传进来的位置是预览图上的位置，需要乘以当前的缩放倍数才行
        final float currentScale = getZoomScale();
        final int scaleLocationX = (int) (x * currentScale);
        final int scaleLocationY = (int) (y * currentScale);
        final int trimScaleLocationX = Math.min(Math.max(scaleLocationX, 0), (int) drawRectF.width());
        final int trimScaleLocationY = Math.min(Math.max(scaleLocationY, 0), (int) drawRectF.height());

        // 让定位点显示在屏幕中间
        final int centerLocationX = trimScaleLocationX - (imageViewWidth / 2);
        final int centerLocationY = trimScaleLocationY - (imageViewHeight / 2);
        final int trimCenterLocationX = Math.max(centerLocationX, 0);
        final int trimCenterLocationY = Math.max(centerLocationY, 0);

        // 当前显示区域的left和top就是开始位置
        final int startX = Math.abs((int) drawRectF.left);
        final int startY = Math.abs((int) drawRectF.top);
        //noinspection UnnecessaryLocalVariable
        final int endX = trimCenterLocationX;
        //noinspection UnnecessaryLocalVariable
        final int endY = trimCenterLocationY;

        if (SLogType.ZOOM.isEnabled()) {
            SLog.d(SLogType.ZOOM, ImageZoomer.NAME, "location. start=%dx%d, end=%dx%d", startX, startY, endX, endY);
        }

        if (animate) {
            locationRunner = new LocationRunner(context, this);
            locationRunner.location(startX, startY, endX, endY);
        } else {
            translateBy(-(endX - startX), -(endY - startY));
        }
        return true;
    }

    /**
     * 定位到预览图上指定的位置（不用考虑旋转角度）
     */
    @SuppressWarnings("unused")
    public boolean location(float x, float y) {
        return location(x, y, false);
    }

    /**
     * 移动一段距离
     */
    void translateBy(float dx, float dy) {
        supportMatrix.postTranslate(dx, dy);
        checkAndApplyMatrix();
    }

    /**
     * 缩放
     */
    public boolean zoom(float scale, float focalX, float focalY, boolean animate) {
        if (!isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. zoom");
            }
            return false;
        }

        if (scale < minZoomScale || scale > maxZoomScale) {
            SLog.w(SLogType.ZOOM, NAME, "Scale must be within the range of %s(minScale) and %s(maxScale). %s",
                    minZoomScale, maxZoomScale, scale);
            return false;
        }

        if (animate) {
            new ZoomRunner(this, getZoomScale(), scale, focalX, focalY).zoom();
        } else {
            float baseScale = getBaseZoomScale();
            float supportZoomScale = getSupportZoomScale();
            float finalScale = scale / baseScale;
            float addScale = finalScale / supportZoomScale;
            supportMatrix.postScale(addScale, addScale, focalX, focalY);
            checkAndApplyMatrix();
        }

        return true;
    }

    /**
     * 缩放
     */
    public boolean zoom(float scale, boolean animate) {
        if (!isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. zoom");
            }
            return false;
        }

        ImageView imageView = getImageView();
        return zoom(scale, (imageView.getRight()) / 2, (imageView.getBottom()) / 2, animate);
    }

    /**
     * 缩放
     */
    @SuppressWarnings("unused")
    public boolean zoom(float scale) {
        return zoom(scale, false);
    }


    /**
     * 旋转图片（会清除已经有的缩放和移动数据，旋转角度会一直存在）
     */
    // TODO: 16/9/28 支持旋转动画
    // TODO: 16/9/28 增加手势旋转功能
    // TODO: 16/10/19 研究任意角度旋转和旋转时不清空位移以及缩放信息
    public boolean rotateTo(int degrees) {
        if (!isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. rotateTo");
            }
            return false;
        }

        if (degrees % 90 != 0) {
            SLog.w(SLogType.ZOOM, NAME, "rotate degrees must be in multiples of 90");
            return false;
        }
        degrees %= 360;
        if (degrees <= 0) {
            degrees = 360 - degrees;
        }

        rotateDegrees = degrees;
        resetSizes();
        resetSupportMatrix();
        resetBaseMatrix();
        resetZoomScales();
        checkAndApplyMatrix();

        if (onRotateChangeListener != null) {
            onRotateChangeListener.onRotateChanged(this);
        }

        return true;
    }

    /**
     * 在当前旋转角度的基础上旋转一定角度
     */
    public boolean rotateBy(int degrees) {
        return rotateTo(degrees + rotateDegrees);
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
    public void addOnMatrixChangeListener(OnMatrixChangeListener listener) {
        if (listener != null) {
            if (onMatrixChangeListenerList == null) {
                onMatrixChangeListenerList = new ArrayList<OnMatrixChangeListener>(1);
            }
            onMatrixChangeListenerList.add(listener);
        }
    }

    @SuppressWarnings("unused")
    public boolean removeOnMatrixChangeListener(OnMatrixChangeListener listener) {
        return listener != null &&
                onMatrixChangeListenerList != null && onMatrixChangeListenerList.size() > 0 &&
                onMatrixChangeListenerList.remove(listener);
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
    @SuppressWarnings("unused")
    public void setOnViewLongPressListener(OnViewLongPressListener onViewLongPressListener) {
        this.onViewLongPressListener = onViewLongPressListener;
    }

    /**
     * 设置旋转监听器
     */
    @SuppressWarnings("unused")
    public void setOnRotateChangeListener(OnRotateChangeListener onRotateChangeListener) {
        this.onRotateChangeListener = onRotateChangeListener;
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
    public interface OnMatrixChangeListener {
        void onMatrixChanged(ImageZoomer imageZoomer);
    }

    /**
     * 缩放监听器
     */
    @SuppressWarnings("WeakerAccess")
    public interface OnScaleChangeListener {
        void onScaleChanged(float scaleFactor, float focusX, float focusY);
    }

    /**
     * 长按监听器
     */
    public interface OnViewLongPressListener {
        void onViewLongPress(View view, float x, float y);
    }

    /**
     * 旋转监听器
     */
    public interface OnRotateChangeListener {
        void onRotateChanged(ImageZoomer imageZoomer);
    }
}