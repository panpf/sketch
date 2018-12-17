/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.zoom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.ArrayList;

import me.panpf.sketch.SLog;
import me.panpf.sketch.zoom.block.Block;

/**
 * 图片缩放器，接收触摸事件，变换 {@link Matrix}，改变图片的显示效果，代理点击和长按事件
 */
// TODO 解决嵌套在别的可滑动 View 中时，会导致 ArrayIndexOutOfBoundsException 异常，初步猜测 requestDisallowInterceptTouchEvent 引起的
public class ImageZoomer {
    public static final String NAME = "ImageZoomer";

    private ImageView imageView;
    private ScaleType scaleType;    // ImageView 原本的 ScaleType

    private Sizes sizes = new Sizes();
    private ZoomScales zoomScales = new AdaptiveTwoLevelScales();

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
    private ScrollBarHelper scrollBarHelper;
    private BlockDisplayer blockDisplayer;

    public ImageZoomer(@NonNull ImageView imageView) {
        Context appContext = imageView.getContext().getApplicationContext();

        this.imageView = imageView;

        this.tapHelper = new TapHelper(appContext, this);
        this.scaleDragHelper = new ScaleDragHelper(appContext, this);
        this.scrollBarHelper = new ScrollBarHelper(appContext, this);
        this.blockDisplayer = new BlockDisplayer(appContext, this);
    }


    /* -----------主要方法----------- */


    /**
     * 当 {@link ImageView} 的 {@link Drawable}、{@link ScaleType}、尺寸发生改变或旋转角度、阅读模式修改了需要调用此方法重置
     *
     * @return true：重置以后可以工作，false：重置以后无法工作，通常是新的 {@link Drawable} 不满足条件导致
     */
    public boolean reset(@NonNull String why) {
        recycle(why);

        sizes.resetSizes(imageView);
        if (!isWorking()) {
            return false;
        }

        // 为什么要每次都重新获取 ScaleType ？因为 reset 是可以反复执行的，在此之前 ScaleType 可能会改变
        scaleType = imageView.getScaleType();
        imageView.setScaleType(ScaleType.MATRIX);

        zoomScales.reset(imageView.getContext(), sizes, scaleType, rotateDegrees, readMode);
        scaleDragHelper.reset();
        blockDisplayer.reset();
        return true;
    }

    /**
     * 不需要缩放时回收
     */
    public void recycle(@NonNull String why) {
        if (!isWorking()) {
            return;
        }

        sizes.clean();
        zoomScales.clean();
        scaleDragHelper.recycle();
        blockDisplayer.recycle(why);

        // 清空 Matrix，这很重要
        imageView.setImageMatrix(null);

        // 恢复 ScaleType 这很重要，一定要在 clean 以后执行，要不会被 {@link FunctionCallbackView#setScaleType(ScaleType)} 方法覆盖
        imageView.setScaleType(scaleType);
        scaleType = null;
    }

    /**
     * {@link ImageZoomer} 工作中
     */
    public boolean isWorking() {
        return !sizes.isEmpty();
    }


    /* -----------ImageView 回调----------- */


    /**
     * 绘制回调
     */
    public void onDraw(@NonNull Canvas canvas) {
        if (!isWorking()) {
            return;
        }

        blockDisplayer.onDraw(canvas);
        scrollBarHelper.onDraw(canvas);    // scrollBarHelper.onDraw 必须在 blockDisplayer.onDraw 之后执行，这样才不会被覆盖掉
    }

    /**
     * 事件回调
     */
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!isWorking()) {
            return false;
        }

        boolean scaleAndDragConsumed = scaleDragHelper.onTouchEvent(event);
        boolean tapConsumed = tapHelper.onTouchEvent(event);
        return scaleAndDragConsumed || tapConsumed;
    }


    /* -----------内部组件回调----------- */


    void onMatrixChanged() {
        // 在 setImageMatrix 前面执行，省了再执行一次 imageView.invalidate()
        scrollBarHelper.onMatrixChanged();
        blockDisplayer.onMatrixChanged();

        imageView.setImageMatrix(scaleDragHelper.getDrawMatrix());

        if (onMatrixChangeListenerList != null && !onMatrixChangeListenerList.isEmpty()) {
            for (int w = 0, size = onMatrixChangeListenerList.size(); w < size; w++) {
                onMatrixChangeListenerList.get(w).onMatrixChanged(this);
            }
        }
    }


    /* -----------交互功能----------- */


    /**
     * 定位到预览图上指定的位置，不用考虑缩放和旋转
     *
     * @param x       预览图上指定位置的 x 坐标
     * @param y       预览图上指定位置的 y 坐标
     * @param animate 是否使用动画
     * @return true：定位成功；false：定位失败，通常是 {@link ImageZoomer} 尚未开始工作
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
     * 定位到预览图上指定的位置，不用考虑缩放和旋转
     *
     * @param x 预览图上指定位置的 x 坐标
     * @param y 预览图上指定位置的 y 坐标
     * @return true：定位成功；false：定位失败，通常是 {@link ImageZoomer} 尚未开始工作
     */
    @SuppressWarnings("unused")
    public boolean location(float x, float y) {
        return location(x, y, false);
    }

    /**
     * 缩放，不用考虑缩放和旋转
     *
     * @param scale   缩放比例
     * @param focalX  缩放中心点在预览图上的 x 坐标
     * @param focalY  缩放中心点在预览图上的 y 坐标
     * @param animate 是否使用动画
     * @return true：缩放成功；false：缩放失败，通常是 {@link ImageZoomer} 尚未开始工作或者缩放比例小于最小缩放比例或大于最大缩放比例
     */
    public boolean zoom(float scale, float focalX, float focalY, boolean animate) {
        if (!isWorking()) {
            SLog.w(NAME, "not working. zoom(float, float, float, boolean)");
            return false;
        }

        if (scale < zoomScales.getMinZoomScale() || scale > zoomScales.getMaxZoomScale()) {
            SLog.w(NAME, "Scale must be within the range of %s(minScale) and %s(maxScale). %s",
                    zoomScales.getMinZoomScale(), zoomScales.getMaxZoomScale(), scale);
            return false;
        }

        scaleDragHelper.zoom(scale, focalX, focalY, animate);
        return true;
    }

    /**
     * 缩放，不用考虑缩放和旋转，默认缩放中心点是 {@link ImageView} 的中心
     *
     * @param scale   缩放比例
     * @param animate 是否使用动画
     * @return true：缩放成功；false：缩放失败，通常是 {@link ImageZoomer} 尚未开始工作或者缩放比例小于最小缩放比例或大于最大缩放比例
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
     * 缩放，不用考虑缩放和旋转，默认缩放中心点是 {@link ImageView} 的中心，默认不使用动画
     *
     * @param scale 缩放比例
     * @return true：缩放成功；false：缩放失败，通常是 {@link ImageZoomer} 尚未开始工作或者缩放比例小于最小缩放比例或大于最大缩放比例
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
     * 旋转图片，旋转会清除已经存在的缩放和移动数据
     *
     * @param degrees 旋转角度，只能是 90°、180°、270°、360°
     * @return true：旋转成功；false：旋转失败，通常是 {@link ImageZoomer} 尚未开始工作或者旋转角度不是 90 的倍数
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
        reset("rotateTo");

        if (onRotateChangeListener != null) {
            onRotateChangeListener.onRotateChanged(this);
        }

        return true;
    }

    /**
     * 在当前旋转角度的基础上旋转一定角度，旋转会清除已经存在的缩放和移动数据
     *
     * @param degrees 旋转角度，只能是 90°、180°、270°、360°
     * @return true：旋转成功；false：旋转失败，通常是 {@link ImageZoomer} 尚未开始工作或者旋转角度不是 90 的倍数
     */
    @SuppressWarnings("unused")
    public boolean rotateBy(int degrees) {
        return rotateTo(degrees + getRotateDegrees());
    }


    /* -----------可获取信息----------- */


    @NonNull
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * 获取分块显示器
     */
    @SuppressWarnings("unused")
    @NonNull
    public BlockDisplayer getBlockDisplayer() {
        return blockDisplayer;
    }

    /**
     * 获取 {@link ImageView} 的尺寸
     */
    @NonNull
    public Size getViewSize() {
        return sizes.viewSize;
    }

    /**
     * 获取图片原始尺寸
     */
    @NonNull
    public Size getImageSize() {
        return sizes.imageSize;
    }

    /**
     * 获取预览图的尺寸
     */
    @NonNull
    public Size getDrawableSize() {
        return sizes.drawableSize;
    }

    /**
     * 拷贝绘制 {@link Matrix} 的参数
     */
    public void getDrawMatrix(@NonNull Matrix matrix) {
        matrix.set(scaleDragHelper.getDrawMatrix());
    }

    /**
     * 获取绘制区域
     */
    public void getDrawRect(@NonNull RectF rectF) {
        scaleDragHelper.getDrawRect(rectF);
    }

    /**
     * 获取预览图上用户可以看到的区域（不受旋转影响）
     */
    public void getVisibleRect(@NonNull Rect rect) {
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
        return zoomScales.getFullZoomScale();
    }

    /**
     * 获取能够让图片充满 {@link ImageView} 显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getFillZoomScale() {
        return zoomScales.getFillZoomScale();
    }

    /**
     * 获取能够让图片按原图比例一比一显示的缩放比例
     */
    @SuppressWarnings("unused")
    public float getOriginZoomScale() {
        return zoomScales.getOriginZoomScale();
    }

    /**
     * 获取最小缩放比例
     */
    @SuppressWarnings("unused")
    public float getMinZoomScale() {
        return zoomScales.getMinZoomScale();
    }

    /**
     * 获取最大缩放比例
     */
    @SuppressWarnings("unused")
    public float getMaxZoomScale() {
        return zoomScales.getMaxZoomScale();
    }

    /**
     * 获取双击缩放比例
     */
    @NonNull
    @SuppressWarnings("WeakerAccess")
    public float[] getDoubleClickZoomScales() {
        return zoomScales.getZoomScales();
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
    @NonNull
    @SuppressWarnings("WeakerAccess")
    public Interpolator getZoomInterpolator() {
        return zoomInterpolator;
    }

    /**
     * 设置缩放动画插值器
     */
    @SuppressWarnings("unused")
    public void setZoomInterpolator(@NonNull Interpolator interpolator) {
        zoomInterpolator = interpolator;
    }

    /**
     * 获取 {@link ScaleType}
     */
    @NonNull
    public ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * 设置 {@link ScaleType}
     */
    public void setScaleType(@NonNull ScaleType scaleType) {
        //noinspection ConstantConditions
        if (scaleType == null || this.scaleType == scaleType) {
            return;
        }

        this.scaleType = scaleType;
        reset("setScaleType");
    }

    /**
     * 是否开启了阅读模式，开启后尺寸类似长微博或清明上河图的图片将默认充满屏幕显示
     */
    public boolean isReadMode() {
        return readMode;
    }

    /**
     * 开启阅读模式，开启后尺寸类似长微博或清明上河图的图片将默认充满屏幕显示
     */
    @SuppressWarnings("unused")
    public void setReadMode(boolean readMode) {
        if (this.readMode == readMode) {
            return;
        }

        this.readMode = readMode;
        reset("setReadMode");
    }


    /**
     * 是否允许父类在滑动到边缘的时候拦截事件（默认 true）
     */
    @SuppressWarnings("unused")
    public boolean isAllowParentInterceptOnEdge() {
        return allowParentInterceptOnEdge;
    }

    /**
     * 设置是否允许父类在滑动到边缘的时候拦截事件（默认 true）
     */
    @SuppressWarnings("unused")
    public void setAllowParentInterceptOnEdge(boolean allowParentInterceptOnEdge) {
        this.allowParentInterceptOnEdge = allowParentInterceptOnEdge;
    }

    @Nullable
    OnViewTapListener getOnViewTapListener() {
        return onViewTapListener;
    }

    /**
     * 设置单击监听器
     */
    @SuppressWarnings("unused")
    public void setOnViewTapListener(@Nullable OnViewTapListener onViewTapListener) {
        this.onViewTapListener = onViewTapListener;
    }

    @Nullable
    OnDragFlingListener getOnDragFlingListener() {
        return onDragFlingListener;
    }

    /**
     * 设置飞速滚动监听器
     */
    @SuppressWarnings("unused")
    public void setOnDragFlingListener(@Nullable OnDragFlingListener onDragFlingListener) {
        this.onDragFlingListener = onDragFlingListener;
    }

    @Nullable
    OnScaleChangeListener getOnScaleChangeListener() {
        return onScaleChangeListener;
    }

    /**
     * 设置缩放监听器
     */
    @SuppressWarnings("unused")
    public void setOnScaleChangeListener(@Nullable OnScaleChangeListener onScaleChangeListener) {
        this.onScaleChangeListener = onScaleChangeListener;
    }

    /**
     * 添加 {@link Matrix} 变化监听器
     */
    public void addOnMatrixChangeListener(@NonNull OnMatrixChangeListener listener) {
        //noinspection ConstantConditions
        if (listener != null) {
            if (onMatrixChangeListenerList == null) {
                onMatrixChangeListenerList = new ArrayList<>(1);
            }
            onMatrixChangeListenerList.add(listener);
        }
    }

    /**
     * 移除 {@link Matrix} 变化监听器
     */
    @SuppressWarnings("unused")
    public boolean removeOnMatrixChangeListener(@NonNull OnMatrixChangeListener listener) {
        //noinspection ConstantConditions
        return listener != null &&
                onMatrixChangeListenerList != null && onMatrixChangeListenerList.size() > 0 &&
                onMatrixChangeListenerList.remove(listener);
    }

    /**
     * 获取长按监听器
     */
    @Nullable
    OnViewLongPressListener getOnViewLongPressListener() {
        return onViewLongPressListener;
    }

    /**
     * 设置长按监听器
     */
    @SuppressWarnings("unused")
    public void setOnViewLongPressListener(@Nullable OnViewLongPressListener onViewLongPressListener) {
        this.onViewLongPressListener = onViewLongPressListener;
    }

    /**
     * 设置旋转监听器
     */
    @SuppressWarnings("unused")
    public void setOnRotateChangeListener(@Nullable OnRotateChangeListener onRotateChangeListener) {
        this.onRotateChangeListener = onRotateChangeListener;
    }

    @NonNull
    public ZoomScales getZoomScales() {
        return zoomScales;
    }

    public void setZoomScales(@Nullable ZoomScales zoomScales) {
        if (zoomScales != null) {
            this.zoomScales = zoomScales;
        } else {
            this.zoomScales = new AdaptiveTwoLevelScales();
        }
        reset("setZoomScales");
    }

    @Nullable
    public Block getBlockByDrawablePoint(int drawableX, int drawableY) {
        return blockDisplayer.getBlockByDrawablePoint(drawableX, drawableY);
    }

    @Nullable
    public Block getBlockByImagePoint(int imageX, int imageY) {
        return blockDisplayer.getBlockByImagePoint(imageX, imageY);
    }

    /**
     * view 的触摸点转换成 drawable 上对应的点
     */
    @Nullable
    public Point touchPointToDrawablePoint(int touchX, int touchY) {
        RectF drawRect = new RectF();
        getDrawRect(drawRect);

        if (drawRect.contains(touchX, touchY)) {
            final float zoomScale = getZoomScale();
            int drawableX = (int) ((Math.abs(drawRect.left) + touchX) / zoomScale);
            int drawableY = (int) ((Math.abs(drawRect.top) + touchY) / zoomScale);
            return new Point(drawableX, drawableY);
        } else {
            return null;
        }
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
        void onViewTap(@NonNull View view, float x, float y);
    }

    /**
     * {@link Matrix} 变化监听器
     */
    public interface OnMatrixChangeListener {
        void onMatrixChanged(@NonNull ImageZoomer imageZoomer);
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
        void onViewLongPress(@NonNull View view, float x, float y);
    }

    /**
     * 旋转监听器
     */
    public interface OnRotateChangeListener {
        void onRotateChanged(@NonNull ImageZoomer imageZoomer);
    }
}