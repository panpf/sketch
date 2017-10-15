/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.zoom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.List;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.decode.ImageSizeCalculator;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.zoom.gestures.ActionListener;
import me.xiaopan.sketch.zoom.gestures.OnScaleDragGestureListener;
import me.xiaopan.sketch.zoom.gestures.ScaleDragGestureDetector;
import me.xiaopan.sketch.zoom.gestures.ScaleDragGestureDetectorCompat;

public class ZoomManager {
    private static final String NAME = "ImageZoomer";

    private static final int EDGE_NONE = -1;
    private static final int EDGE_START = 0;
    private static final int EDGE_END = 1;
    private static final int EDGE_BOTH = 2;

    private Context context;
    private ImageZoomer imageZoomer;

    private Matrix defaultMatrix = new Matrix(); // 存储默认的缩放和位移信息
    private Matrix supportMatrix = new Matrix(); // 存储用户通过触摸事件产生的缩放、位移和外部设置的旋转信息
    private Matrix drawMatrix = new Matrix(); // 存储 configMatrix 和 userMatrix 融合后的信息，用于绘制
    private RectF tempDisplayRectF = new RectF();
    private int horScrollEdge = EDGE_NONE; // 横向滚动边界
    private int verScrollEdge = EDGE_NONE; // 竖向滚动边界
    private boolean zooming;    // 缩放中状态
    private ZoomScales zoomScales = new ZoomScales();

    private GestureDetector tapGestureDetector; // 点击手势识别器
    private LocationRunner locationRunner;  // 定位执行器
    private FlingTranslateRunner flingTranslateRunner;  // 执行飞速滚动
    private ScaleDragGestureDetector scaleDragGestureDetector;  // 缩放和拖拽手势识别器
    private boolean disallowParentInterceptTouchEvent;  // 控制滑动或缩放中到达边缘了依然禁止父类拦截事件
    private float tempLastScaleFocusX, tempLastScaleFocusY;  // 缓存最后一次缩放手势的坐标，在恢复缩放比例时使用
    private ScrollBar scrollBar;
    private OnScaleDragGestureListenerImpl scaleDragGestureListener = new OnScaleDragGestureListenerImpl();

    public ZoomManager(Context context, ImageZoomer imageZoomer) {
        this.imageZoomer = imageZoomer;
        this.context = context.getApplicationContext();

        this.tapGestureDetector = new GestureDetector(this.context, new TapListener(imageZoomer));
        this.scaleDragGestureDetector = ScaleDragGestureDetectorCompat.newInstance(this.context, scaleDragGestureListener);
        this.scaleDragGestureDetector.setActionListener(new ActionListenerImpl());
        this.scrollBar = new ScrollBar(this.context, imageZoomer);
    }


    /* -----------回调方法----------- */

    /**
     * 绘制回调
     */
    public void onDraw(Canvas canvas) {
        scrollBar.drawScrollBar(canvas);
    }

    /**
     * 触摸事件回调
     */
    public boolean onTouchEvent(MotionEvent event) {
        // 定位操作不能被打断
        if (locationRunner != null) {
            if (locationRunner.isRunning()) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                    SLog.d(NAME, "disallow parent intercept touch event. location running");
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


    /* -----------主要方法----------- */

    public void reset() {
        zoomScales.reset(context, imageZoomer.getViewSize(), imageZoomer.getImageSize(), imageZoomer.getDrawableSize(),
                imageZoomer.getScaleType(), imageZoomer.getRotateDegrees(), imageZoomer.isReadMode());

        resetDefaultMatrix();
        resetSupportMatrix();
        checkAndApplyMatrix();
    }

    @SuppressWarnings("ConstantConditions")
    private void resetDefaultMatrix() {
        defaultMatrix.reset();

        Size viewSize = imageZoomer.getViewSize();
        Size imageSize = imageZoomer.getImageSize();
        Size drawableSize = imageZoomer.getDrawableSize();
        boolean readMode = imageZoomer.isReadMode();
        ScaleType scaleType = imageZoomer.getScaleType();

        final int drawableWidth = imageZoomer.getRotateDegrees() % 180 == 0 ? drawableSize.getWidth() : drawableSize.getHeight();
        final int drawableHeight = imageZoomer.getRotateDegrees() % 180 == 0 ? drawableSize.getHeight() : drawableSize.getWidth();
        final int imageWidth = imageZoomer.getRotateDegrees() % 180 == 0 ? imageSize.getWidth() : imageSize.getHeight();
        final int imageHeight = imageZoomer.getRotateDegrees() % 180 == 0 ? imageSize.getHeight() : imageSize.getWidth();

        final float widthScale = (float) viewSize.getWidth() / drawableWidth;
        final float heightScale = (float) viewSize.getHeight() / drawableHeight;
        boolean imageThanViewLarge = drawableWidth > viewSize.getWidth() || drawableHeight > viewSize.getHeight();

        if (scaleType == ScaleType.CENTER || (scaleType == ScaleType.CENTER_INSIDE && !imageThanViewLarge)) {
            ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getSizeCalculator();
            if (readMode && sizeCalculator.canUseReadModeByHeight(imageWidth, imageHeight)) {
                defaultMatrix.postScale(widthScale, widthScale);
            } else if (readMode && sizeCalculator.canUseReadModeByWidth(imageWidth, imageHeight)) {
                defaultMatrix.postScale(heightScale, heightScale);
            } else {
                defaultMatrix.postTranslate((viewSize.getWidth() - drawableWidth) / 2F, (viewSize.getHeight() - drawableHeight) / 2F);
            }
        } else if (scaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            defaultMatrix.postScale(scale, scale);
            defaultMatrix.postTranslate((viewSize.getWidth() - drawableWidth * scale) / 2F, (viewSize.getHeight() - drawableHeight * scale) / 2F);
        } else if (scaleType == ScaleType.FIT_START || scaleType == ScaleType.FIT_CENTER || scaleType == ScaleType.FIT_END ||
                (scaleType == ScaleType.CENTER_INSIDE && imageThanViewLarge)) {
            ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getSizeCalculator();
            if (readMode && sizeCalculator.canUseReadModeByHeight(imageWidth, imageHeight)) {
                defaultMatrix.postScale(widthScale, widthScale);
            } else if (readMode && sizeCalculator.canUseReadModeByWidth(imageWidth, imageHeight)) {
                defaultMatrix.postScale(heightScale, heightScale);
            } else {
                RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
                RectF mTempDst = new RectF(0, 0, viewSize.getWidth(), viewSize.getHeight());
                Matrix.ScaleToFit scaleToFit;
                if (scaleType == ScaleType.FIT_START) {
                    scaleToFit = Matrix.ScaleToFit.START;
                } else if (scaleType == ScaleType.FIT_END) {
                    scaleToFit = Matrix.ScaleToFit.END;
                } else {
                    scaleToFit = Matrix.ScaleToFit.CENTER;
                }
                defaultMatrix.setRectToRect(mTempSrc, mTempDst, scaleToFit);
            }
        } else if (scaleType == ScaleType.FIT_XY) {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewSize.getWidth(), viewSize.getHeight());
            defaultMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL);
        }
    }

    private void resetSupportMatrix() {
        supportMatrix.reset();
        supportMatrix.postRotate(imageZoomer.getRotateDegrees());
    }

    /**
     * 检查 Matrix 边界并应用
     */
    private void checkAndApplyMatrix() {
        if (!checkMatrixBounds()) {
            return;
        }

        ImageView imageView = imageZoomer.getImageView();
        if (!ScaleType.MATRIX.equals(imageView.getScaleType())) {
            throw new IllegalStateException("ImageView scaleType must be is MATRIX");
        }

        scrollBar.matrixChanged();
        imageView.setImageMatrix(getDrawMatrix());

        List<ImageZoomer.OnMatrixChangeListener> onMatrixChangeListenerList = imageZoomer.getOnMatrixChangeListenerList();
        if (onMatrixChangeListenerList != null && !onMatrixChangeListenerList.isEmpty()) {
            for (int w = 0, size = onMatrixChangeListenerList.size(); w < size; w++) {
                onMatrixChangeListenerList.get(w).onMatrixChanged(imageZoomer);
            }
        }
    }

    /**
     * 检查应用 Matrix 后的边界，防止超出范围
     */
    private boolean checkMatrixBounds() {
        final RectF drawRectF = tempDisplayRectF;
        getDrawRect(drawRectF);
        if (drawRectF.isEmpty()) {
            horScrollEdge = EDGE_NONE;
            verScrollEdge = EDGE_NONE;
            return false;
        }

        final float displayHeight = drawRectF.height(), displayWidth = drawRectF.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = imageZoomer.getViewSize().getHeight();
        if ((int) displayHeight <= viewHeight) {
            switch (imageZoomer.getScaleType()) {
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

        final int viewWidth = imageZoomer.getViewSize().getWidth();
        if ((int) displayWidth <= viewWidth) {
            switch (imageZoomer.getScaleType()) {
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

    public void recycle() {
        cancelFling();
    }

    private void cancelFling() {
        if (flingTranslateRunner != null) {
            flingTranslateRunner.cancelFling();
            flingTranslateRunner = null;
        }
    }

    /**
     * 移动一段距离
     */
    void translateBy(float dx, float dy) {
        supportMatrix.postTranslate(dx, dy);
        checkAndApplyMatrix();
    }

    private void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ImageView imageView = imageZoomer.getImageView();
        ViewParent parent = imageView.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }


    /* -----------交互方法----------- */


    /**
     * 定位到预览图上指定的位置（不用考虑旋转角度）
     */
    @SuppressWarnings("unused")
    public void location(float x, float y, boolean animate) {
        Size imageViewSize = imageZoomer.getViewSize();
        Size drawableSize = imageZoomer.getDrawableSize();

        // 旋转定位点
        PointF pointF = new PointF(x, y);
        SketchUtils.rotatePoint(pointF, imageZoomer.getRotateDegrees(), drawableSize);
        x = pointF.x;
        y = pointF.y;

        cancelFling();

        if (locationRunner != null) {
            locationRunner.cancel();
        }

        final int imageViewWidth = imageViewSize.getWidth();
        final int imageViewHeight = imageViewSize.getHeight();

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

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
            SLog.d(NAME, "location. start=%dx%d, end=%dx%d", startX, startY, endX, endY);
        }

        if (animate) {
            locationRunner = new LocationRunner(context, this);
            locationRunner.location(startX, startY, endX, endY);
        } else {
            translateBy(-(endX - startX), -(endY - startY));
        }
    }

    /**
     * 缩放
     */
    public void zoom(float scale, float focalX, float focalY, boolean animate) {
        if (animate) {
            new ZoomRunner(this, scaleDragGestureListener, getZoomScale(), scale, focalX, focalY).zoom();
        } else {
            float baseScale = getDefaultZoomScale();
            float supportZoomScale = getSupportZoomScale();
            float finalScale = scale / baseScale;
            float addScale = finalScale / supportZoomScale;
            supportMatrix.postScale(addScale, addScale, focalX, focalY);
            checkAndApplyMatrix();
        }
    }

//    /**
//     * 旋转图片（会清除已经有的缩放和移动数据，旋转角度会一直存在）
//     */
//    public void rotateDegreesChanged() {
//        resetSizes();
//        resetSupportMatrix();
//        resetBaseMatrix();
//        resetZoomScales();
//        checkAndApplyMatrix();
//    }


    /* -----------可获取信息----------- */


    public ImageZoomer getImageZoomer() {
        return imageZoomer;
    }

    /**
     * 获取边界名称，log 专用
     */
    private String getScrollEdgeName(int scrollEdge) {
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
     * 获取绘制Matrix
     */
    public Matrix getDrawMatrix() {
        drawMatrix.set(defaultMatrix);
        drawMatrix.postConcat(supportMatrix);
        return drawMatrix;
    }

    /**
     * 获取默认缩放比例
     */
    public float getDefaultZoomScale() {
        return SketchUtils.getMatrixScale(defaultMatrix);
    }

    /**
     * 获取 support 缩放比例
     */
    public float getSupportZoomScale() {
        return SketchUtils.getMatrixScale(supportMatrix);
    }

    /**
     * 获取最小缩放比例
     */
    @SuppressWarnings("unused")
    public float getMinZoomScale() {
        return zoomScales.minZoomScale;
    }

    /**
     * 获取最大缩放比例
     */
    @SuppressWarnings("unused")
    public float getMaxZoomScale() {
        return zoomScales.maxZoomScale;
    }

    /**
     * 获取双击缩放比例
     */
    @SuppressWarnings("WeakerAccess")
    public float[] getDoubleClickZoomScales() {
        return zoomScales.doubleClickZoomScales;
    }

    /**
     * 获取能够让图片完整显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getFullZoomScale() {
        return zoomScales.fullZoomScale;
    }

    /**
     * 获取能够让图片充满ImageView显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getFillZoomScale() {
        return zoomScales.fillZoomScale;
    }

    /**
     * 获取能够让图片按原图比例一比一显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getOriginZoomScale() {
        return zoomScales.originZoomScale;
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

    /**
     * 获取当前缩放比例
     */
    public float getZoomScale() {
        return SketchUtils.getMatrixScale(getDrawMatrix());
    }

    /**
     * 获取绘制区域
     */
    public void getDrawRect(RectF rectF) {
        if (!imageZoomer.isWorking()) {
            if (SLog.isLoggable(SLog.LEVEL_VERBOSE | SLog.TYPE_ZOOM)) {
                SLog.v(NAME, "not working. getDrawRect");
            }
            rectF.setEmpty();
            return;
        }

        Size drawableSize = imageZoomer.getDrawableSize();
        rectF.set(0, 0, drawableSize.getWidth(), drawableSize.getHeight());

        getDrawMatrix().mapRect(rectF);
    }

    /**
     * 获取预览图上用户可以看到的区域（不受旋转影响）
     */
    public void getVisibleRect(Rect rect) {
        if (!imageZoomer.isWorking()) {
            if (SLog.isLoggable(SLog.LEVEL_VERBOSE | SLog.TYPE_ZOOM)) {
                SLog.v(NAME, "not working. getVisibleRect");
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

        Size viewSize = imageZoomer.getViewSize();
        Size drawableSize = imageZoomer.getDrawableSize();
        final float displayWidth = drawRectF.width();
        final float displayHeight = drawRectF.height();
        final int drawableWidth = imageZoomer.getRotateDegrees() % 180 == 0 ? drawableSize.getWidth() : drawableSize.getHeight();
        final int drawableHeight = imageZoomer.getRotateDegrees() % 180 == 0 ? drawableSize.getHeight() : drawableSize.getWidth();

        final float widthScale = displayWidth / drawableWidth;
        final float heightScale = displayHeight / drawableHeight;

        float left;
        float right;
        if (drawRectF.left >= 0) {
            left = 0;
        } else {
            left = Math.abs(drawRectF.left);
        }
        if (displayWidth >= viewSize.getWidth()) {
            right = viewSize.getWidth() + left;
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
        if (displayHeight >= viewSize.getHeight()) {
            bottom = viewSize.getHeight() + top;
        } else {
            bottom = drawRectF.bottom - drawRectF.top;
        }

        left /= widthScale;
        right /= widthScale;
        top /= heightScale;
        bottom /= heightScale;

        rect.set(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));

        // 将可见区域转回原始角度
        SketchUtils.reverseRotateRect(rect, imageZoomer.getRotateDegrees(), drawableSize);
    }

    private class OnScaleDragGestureListenerImpl implements OnScaleDragGestureListener {
        @Override
        public void onDrag(float dx, float dy) {
            ImageView imageView = imageZoomer.getImageView();
            if (imageView == null) {
                return;
            }

            // 缩放中不能拖拽
            if (scaleDragGestureDetector.isScaling()) {
                return;
            }

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(NAME, "drag. dx: %s, dy: %s", dx, dy);
            }

            supportMatrix.postTranslate(dx, dy);
            checkAndApplyMatrix();

            if (!imageZoomer.isAllowParentInterceptOnEdge() || scaleDragGestureDetector.isScaling() || disallowParentInterceptTouchEvent) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                    SLog.d(NAME, "disallow parent intercept touch event. onDrag. allowParentInterceptOnEdge=%s, scaling=%s, tempDisallowParentInterceptTouchEvent=%s",
                            imageZoomer.isAllowParentInterceptOnEdge(), scaleDragGestureDetector.isScaling(), disallowParentInterceptTouchEvent);
                }
                requestDisallowInterceptTouchEvent(true);
                return;
            }

            // 滑动到边缘时父类可以拦截触摸事件，但暂时不处理顶部和底部边界
//        if (horScrollEdge == EDGE_BOTH || (horScrollEdge == EDGE_START && dx >= 1f) || (horScrollEdge == EDGE_END && dx <= -1f)
//                    || verScrollEdge == EDGE_BOTH || (verScrollEdge == EDGE_START && dy >= 1f) || (verScrollEdge == EDGE_END && dy <= -1f)) {
            if (horScrollEdge == EDGE_BOTH || (horScrollEdge == EDGE_START && dx >= 1f) || (horScrollEdge == EDGE_END && dx <= -1f)) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                    SLog.d(NAME, "allow parent intercept touch event. onDrag. scrollEdge=%s-%s",
                            getScrollEdgeName(horScrollEdge), getScrollEdgeName(verScrollEdge));
                }
                requestDisallowInterceptTouchEvent(false);
            } else {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                    SLog.d(NAME, "disallow parent intercept touch event. onDrag. scrollEdge=%s-%s",
                            getScrollEdgeName(horScrollEdge), getScrollEdgeName(verScrollEdge));
                }
                requestDisallowInterceptTouchEvent(true);
            }
        }

        @Override
        public void onFling(float startX, float startY, float velocityX, float velocityY) {
            flingTranslateRunner = new FlingTranslateRunner(context, ZoomManager.this);
            flingTranslateRunner.fling((int) velocityX, (int) velocityY);

            ImageZoomer.OnDragFlingListener onDragFlingListener = imageZoomer.getOnDragFlingListener();
            if (onDragFlingListener != null) {
                onDragFlingListener.onFling(startX, startY, velocityX, velocityY);
            }
        }

        @Override
        public void onScale(float scaleFactor, float focusX, float focusY) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(NAME, "scale. scaleFactor: %s, dx: %s, dy: %s", scaleFactor, focusX, focusY);
            }

            tempLastScaleFocusX = focusX;
            tempLastScaleFocusY = focusY;

            float oldSuppScale = getSupportZoomScale();
            float newSuppScale = oldSuppScale * scaleFactor;

            if (scaleFactor > 1.0f) {
                // 放大的时候，如果当前已经超过最大缩放比例，就调慢缩放速度
                // 这样就能模拟出超过最大缩放比例时很难再继续放大有种拉橡皮筋的感觉
                float maxSuppScale = zoomScales.maxZoomScale / SketchUtils.getMatrixScale(defaultMatrix);
                if (oldSuppScale >= maxSuppScale) {
                    float addScale = newSuppScale - oldSuppScale;
                    addScale *= 0.4;
                    newSuppScale = oldSuppScale + addScale;
                    scaleFactor = newSuppScale / oldSuppScale;
                }
            } else if (scaleFactor < 1.0f) {
                // 缩小的时候，如果当前已经小于最小缩放比例，就调慢缩放速度
                // 这样就能模拟出小于最小缩放比例时很难再继续缩小有种拉橡皮筋的感觉
                float minSuppScale = zoomScales.minZoomScale / SketchUtils.getMatrixScale(defaultMatrix);
                if (oldSuppScale <= minSuppScale) {
                    float addScale = newSuppScale - oldSuppScale;
                    addScale *= 0.4;
                    newSuppScale = oldSuppScale + addScale;
                    scaleFactor = newSuppScale / oldSuppScale;
                }
            }

            supportMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndApplyMatrix();

            ImageZoomer.OnScaleChangeListener onScaleChangeListener = imageZoomer.getOnScaleChangeListener();
            if (onScaleChangeListener != null) {
                onScaleChangeListener.onScaleChanged(scaleFactor, focusX, focusY);
            }
        }

        @Override
        public boolean onScaleBegin() {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(NAME, "scale begin");
            }

            setZooming(true);
            return true;
        }

        @Override
        public void onScaleEnd() {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(NAME, "scale end");
            }

            float currentScale = SketchUtils.formatFloat(getZoomScale(), 2);
            boolean overMinZoomScale = currentScale < SketchUtils.formatFloat(zoomScales.minZoomScale, 2);
            boolean overMaxZoomScale = currentScale > SketchUtils.formatFloat(zoomScales.maxZoomScale, 2);
            if (!overMinZoomScale && !overMaxZoomScale) {
                setZooming(false);

                List<ImageZoomer.OnMatrixChangeListener> onMatrixChangeListenerList = imageZoomer.getOnMatrixChangeListenerList();
                if (onMatrixChangeListenerList != null && !onMatrixChangeListenerList.isEmpty()) {
                    for (int w = 0, size = onMatrixChangeListenerList.size(); w < size; w++) {
                        onMatrixChangeListenerList.get(w).onMatrixChanged(imageZoomer);
                    }
                }
            }
        }
    }

    private class ActionListenerImpl implements ActionListener {
        @Override
        public void onActionDown(MotionEvent ev) {
            tempLastScaleFocusX = 0;
            tempLastScaleFocusY = 0;

            // 上来就禁止父View拦截事件
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(NAME, "disallow parent intercept touch event. action down");
            }
            requestDisallowInterceptTouchEvent(true);

            // 取消快速滚动
            cancelFling();
        }

        @Override
        public void onActionUp(MotionEvent ev) {
            float currentScale = SketchUtils.formatFloat(getZoomScale(), 2);
            if (currentScale < SketchUtils.formatFloat(zoomScales.minZoomScale, 2)) {
                // 如果当前缩放倍数小于最小倍数就回滚至最小倍数
                RectF drawRectF = new RectF();
                getDrawRect(drawRectF);
                if (!drawRectF.isEmpty()) {
                    zoom(zoomScales.minZoomScale, drawRectF.centerX(), drawRectF.centerY(), true);
                }
            } else if (currentScale > SketchUtils.formatFloat(zoomScales.maxZoomScale, 2)) {
                // 如果当前缩放倍数大于最大倍数就回滚至最大倍数
                if (tempLastScaleFocusX != 0 && tempLastScaleFocusY != 0) {
                    zoom(zoomScales.maxZoomScale, tempLastScaleFocusX, tempLastScaleFocusY, true);
                }
            }
        }

        @Override
        public void onActionCancel(MotionEvent ev) {
            onActionUp(ev);
        }
    }
}
