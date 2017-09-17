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

package me.xiaopan.sketch.viewfun;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.uri.UriModel;

public abstract class ViewFunction {
    /**
     * 依附到Window
     */
    public void onAttachedToWindow() {

    }

    /**
     * 发生触摸事件
     *
     * @return 拦截事件
     */
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return false;
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
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    /**
     * 绘制
     *
     * @param canvas Canvas
     */
    public void onDraw(@NonNull Canvas canvas) {

    }

    /**
     * 从Window脱离
     *
     * @return true：是否需要调用父setImageDrawable清空图片
     */
    public boolean onDetachedFromWindow() {
        return false;
    }

    /**
     * drawable改变
     *
     * @return 是否需要调用 invalidate() 刷新 ImageView
     */
    public boolean onDrawableChanged(@NonNull String callPosition, @Nullable Drawable oldDrawable, @Nullable Drawable newDrawable) {
        return false;
    }


    /**
     * 准备显示图片
     *
     * @return 是否需要调用 invalidate() 刷新 ImageView
     */
    public boolean onReadyDisplay(@Nullable UriModel uriModel) {
        return false;
    }

    /**
     * 准备加载图片
     *
     * @return 是否需要调用 invalidate() 刷新 ImageView
     */
    public boolean onDisplayReadyLoad() {
        return false;
    }

    /**
     * 更新下载进度
     *
     * @param totalLength     总长度
     * @param completedLength 已完成长度
     * @return 是否需要调用 invalidate() 刷新 ImageView
     */
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        return false;
    }

    /**
     * 显示完成
     *
     * @return 是否需要调用 invalidate() 刷新 ImageView
     */
    public boolean onDisplayCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
        return false;
    }

    /**
     * 显示失败
     *
     * @return 是否需要调用 invalidate() 刷新 ImageView
     */
    public boolean onDisplayError(@NonNull ErrorCause errorCause) {
        return false;
    }

    /**
     * 显示取消
     *
     * @return 是否需要调用 invalidate() 刷新 ImageView
     */
    public boolean onDisplayCanceled(@NonNull CancelCause cancelCause) {
        return false;
    }

    /**
     * size变化
     *
     * @param left   左边位置
     * @param top    顶部位置
     * @param right  右边位置
     * @param bottom 底部位置
     */
    public void onSizeChanged(int left, int top, int right, int bottom) {

    }
}
