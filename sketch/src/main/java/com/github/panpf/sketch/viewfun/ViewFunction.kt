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

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.widget.ImageView
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.decode.ImageAttrs
import com.github.panpf.sketch.request.ErrorCause
import com.github.panpf.sketch.request.CancelCause

abstract class ViewFunction {
    /**
     * 依附到 [android.view.Window]
     */
    open fun onAttachedToWindow() {}

    /**
     * 发生触摸事件
     *
     * @param event [MotionEvent]. 事件
     * @return 拦截事件
     */
    open fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    /**
     * 布局
     *
     * @param changed 位置是否改变
     * @param left    左边位置
     * @param top     顶部位置
     * @param right   右边位置
     * @param bottom  底部位置
     */
    open fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {}

    /**
     * 绘制
     *
     * @param canvas [Canvas]
     */
    open fun onDraw(canvas: Canvas) {}

    /**
     * 从 [android.view.Window] 脱离
     *
     * @return true：是否需要调用父 [ImageView.setImageDrawable] 清空图片
     */
    open fun onDetachedFromWindow(): Boolean {
        return false
    }

    /**
     * [Drawable] 改变
     *
     * @param callPosition 执行这个方法的场景或位置
     * @param oldDrawable  旧图片
     * @param newDrawable  新图片
     * @return 是否需要调用 [ImageView.invalidate] 刷新 [ImageView]
     */
    open fun onDrawableChanged(
        callPosition: String,
        oldDrawable: Drawable?,
        newDrawable: Drawable?
    ): Boolean {
        return false
    }

    /**
     * 准备显示图片
     *
     * @return 是否需要调用 [ImageView.invalidate] 刷新 [ImageView]
     */
    open fun onReadyDisplay(uri: String): Boolean {
        return false
    }

    /**
     * 开始转入异步线程加载或下载图片
     *
     * @return 是否需要调用 [ImageView.invalidate] 刷新 [ImageView]
     */
    fun onDisplayStarted(): Boolean {
        return false
    }

    /**
     * 更新下载进度
     *
     * @param totalLength     总长度
     * @param completedLength 已完成长度
     * @return 是否需要调用 [ImageView.invalidate] 刷新 [ImageView]
     */
    open fun onUpdateDownloadProgress(totalLength: Int, completedLength: Int): Boolean {
        return false
    }

    /**
     * 显示完成
     *
     * @param drawable   新图片
     * @param imageFrom  图片来源
     * @param imageAttrs 图片属性
     * @return 是否需要调用 [ImageView.invalidate] 刷新 [ImageView]
     */
    open fun onDisplayCompleted(
        drawable: Drawable,
        imageFrom: ImageFrom,
        imageAttrs: ImageAttrs
    ): Boolean {
        return false
    }

    /**
     * 显示失败
     *
     * @param errorCause 错误原因
     * @return 是否需要调用 [ImageView.invalidate] 刷新 [ImageView]
     */
    open fun onDisplayError(errorCause: ErrorCause): Boolean {
        return false
    }

    /**
     * 显示取消
     *
     * @param cancelCause 取消原因
     * @return 是否需要调用 [ImageView.invalidate] 刷新 [ImageView]
     */
    open fun onDisplayCanceled(cancelCause: CancelCause): Boolean {
        return false
    }

    /**
     * size变化
     *
     * @param left   左边位置
     * @param top    顶部位置
     * @param right  右边位置
     * @param bottom 底部位置
     */
    open fun onSizeChanged(left: Int, top: Int, right: Int, bottom: Int) {}
}