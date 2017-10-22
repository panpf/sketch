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

package me.xiaopan.sketch.zoom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.ArrayList;

import me.xiaopan.sketch.SLog;

/**
 * 图片缩放器，接收触摸事件，变换 Matrix，改变图片的显示效果，代理点击和长按事件
 */
// TODO 解决嵌套在别的可滑动 View 中时，会导致 ArrayIndexOutOfBoundsException 异常，初步猜测 requestDisallowInterceptTouchEvent 引起的
public class ImageZoomer {
    public static final String NAME = "ImageZoomer";

    private ImageView imageView;
    private ScaleType scaleType;    // ImageView 原本的 ScaleType

    private Sizes sizes = new Sizes();
    private Scales scales = new Scales();   // 根据预览图尺寸、原始图尺寸和 ImageView 尺寸计算出的缩放比例

    private int rotateDegrees; // 旋转角度
    private int zoomDuration = 200;   // 双击缩放动画持续时间
    private boolean readMode;   // 阅读模式下，竖图将默认横向充满屏幕
    private Interpolator zoomInterpolator = new AccelerateDecelerateInterpolator();
    private boolean allowParentInterceptOnEdge = true;  // 允许父 ViewGroup 在滑动到边缘时拦截事件
    private OnDragFlingListener onDragFlingListener;
    private OnScaleChangeListener onScaleChangeListener;
    private OnRotateChangeListener onRotateChangeListener;
    private OnViewTapListener onViewTapListener;
    private OnViewLongPressListener onViewLongPressListener;
    private ArrayList<OnMatrixChangeListener> onMatrixChangeListenerList;

    private TapHelper tapHelper;
    private ScaleDragHelper scaleDragHelper;
    private ScrollBarHelper scrollBarHelper;    // 挪到 外面去

    public ImageZoomer(ImageView imageView) {
        Context appContext = imageView.getContext().getApplicationContext();

        this.imageView = imageView;
        this.scaleType = imageView.getScaleType();

        this.tapHelper = new TapHelper(appContext, this);
        this.scaleDragHelper = new ScaleDragHelper(appContext, this);
        this.scrollBarHelper = new ScrollBarHelper(appContext, this);
    }


    /* -----------ImageView 回调----------- */


    /**
     * 绘制回调
     */
    public void onDraw(Canvas canvas) {
        if (!isWorking()) {
            return;
        }

        scrollBarHelper.onDraw(canvas);
    }

    /**
     * 事件回调
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (!isWorking()) {
            return false;
        }

        boolean scaleAndDragConsumed = scaleDragHelper.onTouchEvent(event);
        boolean tapConsumed = tapHelper.onTouchEvent(event);
        return scaleAndDragConsumed || tapConsumed;
    }

    /**
     * ImageView 尺寸改变回调
     */
    public void onSizeChanged() {
        if (!isWorking()) {
            return;
        }
        reset();
    }


    /* -----------内部组件回调----------- */


    void onMatrixChanged() {
        // 在 setImageMatrix 前面执行，省了再执行一次 imageView.invalidate()
        scrollBarHelper.onMatrixChanged();

        imageView.setImageMatrix(scaleDragHelper.getDrawMatrix());

        if (onMatrixChangeListenerList != null && !onMatrixChangeListenerList.isEmpty()) {
            for (int w = 0, size = onMatrixChangeListenerList.size(); w < size; w++) {
                onMatrixChangeListenerList.get(w).onMatrixChanged(this);
            }
        }
    }


    /* -----------主要方法----------- */


    /**
     * 当 ImageView 的 drawable、scaleType、尺寸发生改变或旋转角度、阅读模式修改了需要调用此方法重置
     *
     * @return true：重置以后可以工作，false：重置以后无法工作，通常是新的 drawable 不满足条件导致
     */
    public boolean reset() {
        recycle();

        sizes.resetSizes(imageView);
        if (!isWorking()) {
            return false;
        }

        imageView.setScaleType(ScaleType.MATRIX);
        scales.reset(imageView.getContext(), sizes, scaleType, rotateDegrees, readMode);
        scaleDragHelper.reset();
        return true;
    }

    /**
     * 不需要缩放时回收
     */
    public void recycle() {
        sizes.clean();
        scales.clean();
        scaleDragHelper.recycle();

        imageView.setImageMatrix(null); // 恢复 Matrix 这很重要
        imageView.setScaleType(scaleType);  // 恢复 ScaleType 这很重要
    }


    /* -----------交互功能----------- */


    /**
     * 定位到预览图上指定的位置（不用考虑旋转角度）
     */
    @SuppressWarnings("unused")
    public boolean location(float x, float y, boolean animate) {
        if (!isWorking()) {
            SLog.w(NAME, "not working. location");
            return false;
        }

        scaleDragHelper.location(x, y, animate);
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
     * 缩放
     */
    public boolean zoom(float scale, float focalX, float focalY, boolean animate) {
        if (!isWorking()) {
            SLog.w(NAME, "not working. zoom(float, float, float, boolean)");
            return false;
        }

        if (scale < scales.minZoomScale || scale > scales.maxZoomScale) {
            SLog.w(NAME, "Scale must be within the range of %s(minScale) and %s(maxScale). %s",
                    scales.minZoomScale, scales.maxZoomScale, scale);
            return false;
        }

        scaleDragHelper.zoom(scale, focalX, focalY, animate);
        return true;
    }

    /**
     * 缩放
     */
    public boolean zoom(float scale, boolean animate) {
        if (!isWorking()) {
            SLog.w(NAME, "not working. zoom(float, boolean)");
            return false;
        }

        ImageView imageView = getImageView();
        return zoom(scale, imageView.getRight() / 2, imageView.getBottom() / 2, animate);
    }

    /**
     * 缩放
     */
    @SuppressWarnings("unused")
    public boolean zoom(float scale) {
        return zoom(scale, false);
    }

    /**
     * 获取旋转角度
     */
    public int getRotateDegrees() {
        return rotateDegrees;
    }


    /**
     * 旋转图片（会清除已经有的缩放和移动数据，旋转角度会一直存在）
     */
    // TODO: 16/9/28 支持旋转动画
    // TODO: 16/9/28 增加手势旋转功能
    // TODO: 16/10/19 研究任意角度旋转和旋转时不清空位移以及缩放信息
    public boolean rotateTo(int degrees) {
        if (!isWorking()) {
            SLog.w(NAME, "not working. rotateTo");
            return false;
        }

        if (this.rotateDegrees == degrees) {
            return false;
        }

        if (degrees % 90 != 0) {
            SLog.w(NAME, "rotate degrees must be in multiples of 90");
            return false;
        }

        degrees %= 360;
        if (degrees <= 0) {
            degrees = 360 - degrees;
        }

        this.rotateDegrees = degrees;
        reset();

        if (onRotateChangeListener != null) {
            onRotateChangeListener.onRotateChanged(this);
        }

        return true;
    }

    /**
     * 在当前旋转角度的基础上旋转一定角度
     */
    public boolean rotateBy(int degrees) {
        return rotateTo(degrees + getRotateDegrees());
    }


    /* -----------可获取信息----------- */


    /**
     * {@link ImageZoomer} 工作中
     */
    public boolean isWorking() {
        return !sizes.isEmpty();
    }

    /**
     * 获取 ImageView
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * 获取 ImageView 的尺寸
     */
    public Size getViewSize() {
        return sizes.viewSize;
    }

    /**
     * 获取图片原始尺寸
     */
    public Size getImageSize() {
        return sizes.imageSize;
    }

    /**
     * 获取预览图的尺寸
     */
    public Size getDrawableSize() {
        return sizes.drawableSize;
    }

    /**
     * 拷贝绘制 Matrix 的参数
     */
    public void getDrawMatrix(Matrix matrix) {
        matrix.set(scaleDragHelper.getDrawMatrix());
    }

    /**
     * 获取绘制区域
     */
    public void getDrawRect(RectF rectF) {
        scaleDragHelper.getDrawRect(rectF);
    }

    /**
     * 获取预览图上用户可以看到的区域（不受旋转影响）
     */
    public void getVisibleRect(Rect rect) {
        scaleDragHelper.getVisibleRect(rect);
    }

    /**
     * 获取当前缩放比例
     */
    public float getZoomScale() {
        return scaleDragHelper.getZoomScale();
    }

    /**
     * 获取 Base 缩放比例
     */
    @SuppressWarnings("unused")
    public float getBaseZoomScale() {
        return scaleDragHelper.getDefaultZoomScale();
    }

    /**
     * 获取 support 缩放比例
     */
    @SuppressWarnings("unused")
    public float getSupportZoomScale() {
        return scaleDragHelper.getSupportZoomScale();
    }

    /**
     * 获取能够让图片完整显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getFullZoomScale() {
        return scales.fullZoomScale;
    }

    /**
     * 获取能够让图片充满 ImageView 显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getFillZoomScale() {
        return scales.fillZoomScale;
    }

    /**
     * 获取能够让图片按原图比例一比一显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getOriginZoomScale() {
        return scales.originZoomScale;
    }

    /**
     * 获取最小缩放比例
     */
    @SuppressWarnings("unused")
    public float getMinZoomScale() {
        return scales.minZoomScale;
    }

    /**
     * 获取最大缩放比例
     */
    @SuppressWarnings("unused")
    public float getMaxZoomScale() {
        return scales.maxZoomScale;
    }

    /**
     * 获取双击缩放比例
     */
    @SuppressWarnings("WeakerAccess")
    public float[] getDoubleClickZoomScales() {
        return scales.doubleClickZoomScales;
    }

    /**
     * 正在缩放
     */
    public boolean isZooming() {
        return scaleDragHelper.isZooming();
    }


    /* -----------配置----------- */


    /**
     * 获取缩放动画持续时间
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public int getZoomDuration() {
        return zoomDuration;
    }

    /**
     * 设置缩放动画持续时间，单位毫秒
     */
    @SuppressWarnings("unused")
    public void setZoomDuration(int milliseconds) {
        if (milliseconds > 0) {
            this.zoomDuration = milliseconds;
        }
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
        if (scaleType == null || this.scaleType == scaleType) {
            return;
        }

        this.scaleType = scaleType;
        reset();
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
        if (this.readMode == readMode) {
            return;
        }

        this.readMode = readMode;
        reset();
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

    OnDragFlingListener getOnDragFlingListener() {
        return onDragFlingListener;
    }

    /**
     * 设置飞速滚动监听器
     */
    @SuppressWarnings("unused")
    public void setOnDragFlingListener(OnDragFlingListener onDragFlingListener) {
        this.onDragFlingListener = onDragFlingListener;
    }

    OnScaleChangeListener getOnScaleChangeListener() {
        return onScaleChangeListener;
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
                onMatrixChangeListenerList = new ArrayList<>(1);
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

    /**
     * 飞速拖拽监听器
     */
    @SuppressWarnings("WeakerAccess")
    public interface OnDragFlingListener {
        void onFling(float startX, float startY, float velocityX, float velocityY);
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