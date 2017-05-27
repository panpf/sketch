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
import android.view.MotionEvent;

import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.UriScheme;

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
    public boolean onTouchEvent(MotionEvent event) {
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
    public void onDraw(Canvas canvas) {

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
     * @return 是否需要调用invalidate()刷新ImageView
     */
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        return false;
    }


    /**
     * 准备显示图片
     *
     * @return 是否需要调用invalidate()刷新ImageView
     */
    public boolean onReadyDisplay(UriScheme uriScheme) {
        return false;
    }

    /**
     * 开始显示图片
     *
     * @return 是否需要调用invalidate()刷新ImageView
     */
    public boolean onDisplayStarted() {
        return false;
    }

    /**
     * 更新下载进度
     *
     * @param totalLength     总长度
     * @param completedLength 已完成长度
     * @return 是否需要调用invalidate()刷新ImageView
     */
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        return false;
    }

    /**
     * 显示完成
     *
     * @return 是否需要调用invalidate()刷新ImageView
     */
    public boolean onDisplayCompleted(Drawable drawable, ImageFrom imageFrom, ImageAttrs imageAttrs) {
        return false;
    }

    /**
     * 显示失败
     *
     * @return 是否需要调用invalidate()刷新ImageView
     */
    public boolean onDisplayError(ErrorCause errorCause) {
        return false;
    }

    /**
     * 显示取消
     *
     * @return 是否需要调用invalidate()刷新ImageView
     */
    public boolean onDisplayCanceled(CancelCause cancelCause) {
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
