/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.viewfun;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.shaper.ImageShaper;
import me.panpf.sketch.zoom.ImageZoomer;

/**
 * 这个类负责提供各种 function 开关和属性设置
 */
public abstract class FunctionPropertyView extends FunctionCallbackView {

    public FunctionPropertyView(Context context) {
        super(context);
    }

    public FunctionPropertyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FunctionPropertyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * 是否开启了暂停下载的时候点击强制显示图片功能
     */
    @SuppressWarnings("unused")
    public boolean isClickRetryOnPauseDownloadEnabled() {
        return getFunctions().clickRetryFunction != null && getFunctions().clickRetryFunction.isClickRetryOnPauseDownloadEnabled();
    }

    /**
     * 开启暂停下载的时候点击强制显示图片功能
     */
    public void setClickRetryOnPauseDownloadEnabled(boolean enabled) {
        if (isClickRetryOnPauseDownloadEnabled() == enabled) {
            return;
        }

        if (getFunctions().clickRetryFunction == null) {
            getFunctions().clickRetryFunction = new ClickRetryFunction(this);
        }
        getFunctions().clickRetryFunction.setClickRetryOnPauseDownloadEnabled(enabled);
        updateClickable();
    }


    /**
     * 是否开启了显示失败时点击重试功能
     */
    @SuppressWarnings("unused")
    public boolean isClickRetryOnDisplayErrorEnabled() {
        return getFunctions().clickRetryFunction != null && getFunctions().clickRetryFunction.isClickRetryOnDisplayErrorEnabled();
    }

    /**
     * 开启显示失败时点击重试功能
     */
    public void setClickRetryOnDisplayErrorEnabled(boolean enabled) {
        if (isClickRetryOnDisplayErrorEnabled() == enabled) {
            return;
        }

        if (getFunctions().clickRetryFunction == null) {
            getFunctions().clickRetryFunction = new ClickRetryFunction(this);
        }
        getFunctions().clickRetryFunction.setClickRetryOnDisplayErrorEnabled(enabled);
        updateClickable();
    }


    /**
     * 是否开启了点击播放 gif 功能
     */
    @SuppressWarnings("unused")
    public boolean isClickPlayGifEnabled() {
        return getFunctions().clickPlayGifFunction != null;
    }

    /**
     * 开启点击播放gif 功能
     *
     * @param playIconResId 播放图标资源ID
     */
    @SuppressLint("ResourceType")
    public void setClickPlayGifEnabled(@DrawableRes int playIconResId) {
        setClickPlayGifEnabled(playIconResId > 0 ? getResources().getDrawable(playIconResId) : null);
    }

    /**
     * 开启点击播放 gif 功能
     *
     * @param playIconDrawable 播放图标
     */
    public void setClickPlayGifEnabled(@Nullable Drawable playIconDrawable) {
        boolean update = false;

        if (playIconDrawable != null) {
            if (getFunctions().clickPlayGifFunction == null) {
                getFunctions().clickPlayGifFunction = new ClickPlayGifFunction(this);
                update = true;
            }

            update |= getFunctions().clickPlayGifFunction.setPlayIconDrawable(playIconDrawable);
        } else {
            if (getFunctions().clickPlayGifFunction != null) {
                getFunctions().clickPlayGifFunction = null;
                update = true;
            }
        }

        if (update) {
            updateClickable();
            invalidate();
        }
    }

    /**
     * 是否开启了显示下载进度功能
     */
    @SuppressWarnings("unused")
    public boolean isShowDownloadProgressEnabled() {
        return getFunctions().showDownloadProgressFunction != null;
    }

    /**
     * 开启显示下载进度功能，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     */
    public void setShowDownloadProgressEnabled(boolean enabled) {
        setShowDownloadProgressEnabled(enabled, ShowDownloadProgressFunction.DEFAULT_MASK_COLOR, null);
    }

    /**
     * 开启显示下载进度功能，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     *
     * @param maskShaper 下载进度蒙层的形状
     */
    @SuppressWarnings("unused")
    public void setShowDownloadProgressEnabled(boolean enabled, @Nullable ImageShaper maskShaper) {
        setShowDownloadProgressEnabled(enabled, ShowDownloadProgressFunction.DEFAULT_MASK_COLOR, maskShaper);
    }

    /**
     * 开启显示下载进度功能，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     *
     * @param maskColor 下载进度蒙层的颜色
     */
    @SuppressWarnings("unused")
    public void setShowDownloadProgressEnabled(boolean enabled, @ColorInt int maskColor) {
        setShowDownloadProgressEnabled(enabled, maskColor, null);
    }

    /**
     * 开启显示下载进度功能，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     *
     * @param maskColor  下载进度蒙层的颜色
     * @param maskShaper 下载进度蒙层的形状
     */
    @SuppressWarnings("unused")
    public void setShowDownloadProgressEnabled(boolean enabled, @ColorInt int maskColor, @Nullable ImageShaper maskShaper) {
        boolean update = false;

        if (enabled) {
            if (getFunctions().showDownloadProgressFunction == null) {
                getFunctions().showDownloadProgressFunction = new ShowDownloadProgressFunction(this);
                update = true;
            }
            update |= getFunctions().showDownloadProgressFunction.setMaskColor(maskColor);
            update |= getFunctions().showDownloadProgressFunction.setMaskShaper(maskShaper);
        } else {
            if (getFunctions().showDownloadProgressFunction != null) {
                getFunctions().showDownloadProgressFunction = null;
                update = true;
            }
        }

        if (update) {
            invalidate();
        }
    }


    /**
     * 是否开启了显示按下状态功能
     */
    @SuppressWarnings("unused")
    public boolean isShowPressedStatusEnabled() {
        return getFunctions().showPressedFunction != null;
    }

    /**
     * 开启显示按下状态功能，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
     */
    public void setShowPressedStatusEnabled(boolean enabled) {
        setShowPressedStatusEnabled(enabled, ShowPressedFunction.DEFAULT_MASK_COLOR, null);
    }

    /**
     * 开启显示按下状态功能，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
     *
     * @param maskShaper 按下状态蒙层的形状
     */
    @SuppressWarnings("unused")
    public void setShowPressedStatusEnabled(boolean enabled, ImageShaper maskShaper) {
        setShowPressedStatusEnabled(enabled, ShowPressedFunction.DEFAULT_MASK_COLOR, maskShaper);
    }

    /**
     * 开启显示按下状态功能，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
     *
     * @param maskColor 下载进度蒙层的颜色
     */
    @SuppressWarnings("unused")
    public void setShowPressedStatusEnabled(boolean enabled, @ColorInt int maskColor) {
        setShowPressedStatusEnabled(enabled, maskColor, null);
    }

    /**
     * 开启显示按下状态功能，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
     *
     * @param maskColor  按下状态蒙层的颜色
     * @param maskShaper 按下状态蒙层的形状
     */
    @SuppressWarnings("unused")
    public void setShowPressedStatusEnabled(boolean enabled, @ColorInt int maskColor, ImageShaper maskShaper) {
        boolean update = false;

        if (enabled) {
            if (getFunctions().showPressedFunction == null) {
                getFunctions().showPressedFunction = new ShowPressedFunction(this);
                update = true;
            }
            update |= getFunctions().showPressedFunction.setMaskColor(maskColor);
            update |= getFunctions().showPressedFunction.setMaskShaper(maskShaper);
        } else {
            if (getFunctions().showPressedFunction != null) {
                getFunctions().showPressedFunction = null;
                update = true;
            }
        }

        if (update) {
            invalidate();
        }
    }


    /**
     * 是否开启了显示图片来源功能
     */
    @SuppressWarnings("unused")
    public boolean isShowImageFromEnabled() {
        return getFunctions().showImageFromFunction != null;
    }

    /**
     * 开启显示图片来源功能，开启后会在View的左上角显示一个纯色三角形，红色代表本次是从网络加载的，
     * 黄色代表本次是从本地加载的，绿色代表本次是从内存缓存加载的，绿色代表本次是从内存缓存加载的，紫色代表是从内存加载的
     */
    public void setShowImageFromEnabled(boolean enabled) {
        if (isShowImageFromEnabled() == enabled) {
            return;
        }

        if (enabled) {
            getFunctions().showImageFromFunction = new ShowImageFromFunction(this);
            getFunctions().showImageFromFunction.onDrawableChanged("setShowImageFromEnabled", null, getDrawable());
        } else {
            getFunctions().showImageFromFunction = null;
        }

        invalidate();
    }

    /**
     * 获取图片来源
     */
    @Nullable
    @SuppressWarnings("unused")
    public ImageFrom getImageFrom() {
        return getFunctions().showImageFromFunction != null ? getFunctions().showImageFromFunction.getImageFrom() : null;
    }


    /**
     * 是否开启了显示GIF标识功能
     */
    @SuppressWarnings("unused")
    public boolean isShowGifFlagEnabled() {
        return getFunctions().showGifFlagFunction != null;
    }

    /**
     * 开启显示GIF标识功能
     *
     * @param gifFlagDrawable gif标识图标
     */
    @SuppressWarnings("unused")
    public void setShowGifFlagEnabled(Drawable gifFlagDrawable) {
        boolean update = false;

        if (gifFlagDrawable != null) {
            if (getFunctions().showGifFlagFunction == null) {
                getFunctions().showGifFlagFunction = new ShowGifFlagFunction(this);
                update = true;
            }

            update |= getFunctions().showGifFlagFunction.setGifFlagDrawable(gifFlagDrawable);
        } else {
            if (getFunctions().showGifFlagFunction != null) {
                getFunctions().showGifFlagFunction = null;
                update = true;
            }
        }

        if (update) {
            invalidate();
        }
    }

    /**
     * 开启显示GIF标识功能
     *
     * @param gifFlagDrawableResId gif标识图标
     */
    @SuppressLint("ResourceType")
    public void setShowGifFlagEnabled(@DrawableRes int gifFlagDrawableResId) {
        setShowGifFlagEnabled(gifFlagDrawableResId > 0 ? getResources().getDrawable(gifFlagDrawableResId) : null);
    }

    /**
     * 是否开启了手势缩放功能
     */
    @Override
    public boolean isZoomEnabled() {
        return getFunctions().zoomFunction != null;
    }

    /**
     * 开启手势缩放功能
     */
    public void setZoomEnabled(boolean enabled) {
        if (enabled == isZoomEnabled()) {
            return;
        }

        if (enabled) {
            ImageZoomFunction zoomFunction = new ImageZoomFunction(this);
            zoomFunction.onDrawableChanged("setZoomEnabled", null, getDrawable());

            getFunctions().zoomFunction = zoomFunction;
        } else {
            getFunctions().zoomFunction.recycle("setZoomEnabled");

            getFunctions().zoomFunction = null;
        }
    }

    /**
     * 获取缩放功能控制对象
     *
     * @return null：没有开启缩放功能，请先执行 {@link #setZoomEnabled(boolean)} 开启
     */
    @Nullable
    public ImageZoomer getZoomer() {
        return getFunctions().zoomFunction != null ? getFunctions().zoomFunction.getZoomer() : null;
    }
}
