/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.viewfun

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.shaper.ImageShaper

/**
 * 这个类负责提供各种 function 开关和属性设置
 */
abstract class FunctionPropertyView : FunctionCallbackView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
            : super(context, attrs, defStyle)

    /**
     * 开启暂停下载的时候点击强制显示图片功能
     */
    var isClickRetryOnPauseDownloadEnabled: Boolean
        get() = functions.clickRetryFunction != null && functions.clickRetryFunction!!.isClickRetryOnPauseDownloadEnabled
        set(enabled) {
            if (isClickRetryOnPauseDownloadEnabled == enabled) {
                return
            }
            if (functions.clickRetryFunction == null) {
                functions.clickRetryFunction = ClickRetryFunction(this)
            }
            functions.clickRetryFunction!!.isClickRetryOnPauseDownloadEnabled = enabled
            updateClickable()
        }

    /**
     * 开启显示失败时点击重试功能
     */
    var isClickRetryOnDisplayErrorEnabled: Boolean
        get() = functions.clickRetryFunction != null && functions.clickRetryFunction!!.isClickRetryOnDisplayErrorEnabled
        set(enabled) {
            if (isClickRetryOnDisplayErrorEnabled == enabled) {
                return
            }
            if (functions.clickRetryFunction == null) {
                functions.clickRetryFunction = ClickRetryFunction(this)
            }
            functions.clickRetryFunction!!.isClickRetryOnDisplayErrorEnabled = enabled
            updateClickable()
        }

    /**
     * 开启显示下载进度功能，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     */
    var isShowDownloadProgressEnabled: Boolean
        get() = functions.showDownloadProgressFunction != null
        set(enabled) {
            setShowDownloadProgressEnabled(
                enabled,
                ShowDownloadProgressFunction.DEFAULT_MASK_COLOR,
                null
            )
        }

    /**
     * 是否开启了点击播放 gif 功能
     */
    val isClickPlayGifEnabled: Boolean
        get() = functions.clickPlayGifFunction != null

    /**
     * 开启显示按下状态功能，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
     */
    var isShowPressedStatusEnabled: Boolean
        get() = functions.showPressedFunction != null
        set(enabled) {
            setShowPressedStatusEnabled(enabled, ShowPressedFunction.DEFAULT_MASK_COLOR, null)
        }

    /**
     * 开启显示图片来源功能，开启后会在View的左上角显示一个纯色三角形，红色代表本次是从网络加载的，
     * 黄色代表本次是从本地加载的，绿色代表本次是从内存缓存加载的，绿色代表本次是从内存缓存加载的，紫色代表是从内存加载的
     */
    var isShowImageFromEnabled: Boolean
        get() = functions.showImageFromFunction != null
        set(enabled) {
            if (isShowImageFromEnabled == enabled) {
                return
            }
            if (enabled) {
                functions.showImageFromFunction = ShowImageFromFunction(this)
                functions.showImageFromFunction!!.onDrawableChanged(
                    "setShowImageFromEnabled",
                    null,
                    drawable
                )
            } else {
                functions.showImageFromFunction = null
            }
            invalidate()
        }

    /**
     * 获取图片来源
     */
    val imageFrom: ImageFrom?
        get() = if (functions.showImageFromFunction != null) functions.showImageFromFunction!!.imageFrom else null

    /**
     * 是否开启了显示GIF标识功能
     */
    val isShowGifFlagEnabled: Boolean
        get() = functions.showGifFlagFunction != null

    /**
     * 开启点击播放gif 功能
     *
     * @param playIconResId 播放图标资源ID
     */
    @SuppressLint("ResourceType")
    fun setClickPlayGifEnabled(@DrawableRes playIconResId: Int) {
        setClickPlayGifEnabled(if (playIconResId > 0) resources.getDrawable(playIconResId) else null)
    }

    /**
     * 开启点击播放 gif 功能
     *
     * @param playIconDrawable 播放图标
     */
    fun setClickPlayGifEnabled(playIconDrawable: Drawable?) {
        var update = false
        if (playIconDrawable != null) {
            if (functions.clickPlayGifFunction == null) {
                functions.clickPlayGifFunction = ClickPlayGifFunction(this)
                update = true
            }
            update =
                update or functions.clickPlayGifFunction!!.setPlayIconDrawable(playIconDrawable)
        } else {
            if (functions.clickPlayGifFunction != null) {
                functions.clickPlayGifFunction = null
                update = true
            }
        }
        if (update) {
            updateClickable()
            invalidate()
        }
    }

    /**
     * 开启显示下载进度功能，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     *
     * @param maskShaper 下载进度蒙层的形状
     */
    fun setShowDownloadProgressEnabled(enabled: Boolean, maskShaper: ImageShaper?) {
        setShowDownloadProgressEnabled(
            enabled,
            ShowDownloadProgressFunction.DEFAULT_MASK_COLOR,
            maskShaper
        )
    }

    /**
     * 开启显示下载进度功能，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     *
     * @param maskColor 下载进度蒙层的颜色
     */
    fun setShowDownloadProgressEnabled(enabled: Boolean, @ColorInt maskColor: Int) {
        setShowDownloadProgressEnabled(enabled, maskColor, null)
    }

    /**
     * 开启显示下载进度功能，开启后会在ImageView表面覆盖一层默认为黑色半透明的蒙层来显示进度
     *
     * @param maskColor  下载进度蒙层的颜色
     * @param maskShaper 下载进度蒙层的形状
     */
    fun setShowDownloadProgressEnabled(
        enabled: Boolean,
        @ColorInt maskColor: Int,
        maskShaper: ImageShaper?
    ) {
        var update = false
        if (enabled) {
            if (functions.showDownloadProgressFunction == null) {
                functions.showDownloadProgressFunction = ShowDownloadProgressFunction(this)
                update = true
            }
            update = update or functions.showDownloadProgressFunction!!.setMaskColor(maskColor)
            update = update or functions.showDownloadProgressFunction!!.setMaskShaper(maskShaper)
        } else {
            if (functions.showDownloadProgressFunction != null) {
                functions.showDownloadProgressFunction = null
                update = true
            }
        }
        if (update) {
            invalidate()
        }
    }

    /**
     * 开启显示按下状态功能，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
     *
     * @param maskShaper 按下状态蒙层的形状
     */
    fun setShowPressedStatusEnabled(enabled: Boolean, maskShaper: ImageShaper?) {
        setShowPressedStatusEnabled(enabled, ShowPressedFunction.DEFAULT_MASK_COLOR, maskShaper)
    }

    /**
     * 开启显示按下状态功能，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
     *
     * @param maskColor 下载进度蒙层的颜色
     */
    fun setShowPressedStatusEnabled(enabled: Boolean, @ColorInt maskColor: Int) {
        setShowPressedStatusEnabled(enabled, maskColor, null)
    }

    /**
     * 开启显示按下状态功能，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
     *
     * @param maskColor  按下状态蒙层的颜色
     * @param maskShaper 按下状态蒙层的形状
     */
    fun setShowPressedStatusEnabled(
        enabled: Boolean,
        @ColorInt maskColor: Int,
        maskShaper: ImageShaper?
    ) {
        var update = false
        if (enabled) {
            if (functions.showPressedFunction == null) {
                functions.showPressedFunction = ShowPressedFunction(this)
                update = true
            }
            update = update or functions.showPressedFunction!!.setMaskColor(maskColor)
            update = update or functions.showPressedFunction!!.setMaskShaper(maskShaper)
        } else {
            if (functions.showPressedFunction != null) {
                functions.showPressedFunction = null
                update = true
            }
        }
        if (update) {
            invalidate()
        }
    }

    /**
     * 开启显示GIF标识功能
     *
     * @param gifFlagDrawable gif标识图标
     */
    fun setShowGifFlagEnabled(gifFlagDrawable: Drawable?) {
        var update = false
        if (gifFlagDrawable != null) {
            if (functions.showGifFlagFunction == null) {
                functions.showGifFlagFunction = ShowGifFlagFunction(this)
                update = true
            }
            update = update or functions.showGifFlagFunction!!.setGifFlagDrawable(gifFlagDrawable)
        } else {
            if (functions.showGifFlagFunction != null) {
                functions.showGifFlagFunction = null
                update = true
            }
        }
        if (update) {
            invalidate()
        }
    }

    /**
     * 开启显示GIF标识功能
     *
     * @param gifFlagDrawableResId gif标识图标
     */
    @SuppressLint("ResourceType")
    fun setShowGifFlagEnabled(@DrawableRes gifFlagDrawableResId: Int) {
        setShowGifFlagEnabled(
            if (gifFlagDrawableResId > 0) resources.getDrawable(
                gifFlagDrawableResId
            ) else null
        )
    }
}