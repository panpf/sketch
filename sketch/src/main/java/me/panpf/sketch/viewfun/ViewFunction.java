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

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.MotionEvent;
import android.widget.ImageView;

import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.uri.UriModel;

public abstract class ViewFunction {
    /**
     * 依附到 {@link android.view.Window}
     */
    public void onAttachedToWindow() {

    }

    /**
     * 发生触摸事件
     *
     * @param event {@link MotionEvent}. 事件
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
     * @param canvas {@link Canvas}
     */
    public void onDraw(@NonNull Canvas canvas) {

    }

    /**
     * 从 {@link android.view.Window} 脱离
     *
     * @return true：是否需要调用父 {@link ImageView#setImageDrawable(Drawable)} 清空图片
     */
    public boolean onDetachedFromWindow() {
        return false;
    }

    /**
     * {@link Drawable} 改变
     *
     * @param callPosition 执行这个方法的场景或位置
     * @param oldDrawable  旧图片
     * @param newDrawable  新图片
     * @return 是否需要调用 {@link ImageView#invalidate()} 刷新 {@link ImageView}
     */
    public boolean onDrawableChanged(@NonNull String callPosition, @Nullable Drawable oldDrawable, @Nullable Drawable newDrawable) {
        return false;
    }


    /**
     * 准备显示图片
     *
     * @param uriModel {@link UriModel}
     * @return 是否需要调用 {@link ImageView#invalidate()} 刷新 {@link ImageView}
     */
    public boolean onReadyDisplay(@Nullable UriModel uriModel) {
        return false;
    }

    /**
     * 开始转入异步线程加载或下载图片
     *
     * @return 是否需要调用 {@link ImageView#invalidate()} 刷新 {@link ImageView}
     */
    public boolean onDisplayStarted() {
        return false;
    }

    /**
     * 更新下载进度
     *
     * @param totalLength     总长度
     * @param completedLength 已完成长度
     * @return 是否需要调用 {@link ImageView#invalidate()} 刷新 {@link ImageView}
     */
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        return false;
    }

    /**
     * 显示完成
     *
     * @param drawable   新图片
     * @param imageFrom  图片来源
     * @param imageAttrs 图片属性
     * @return 是否需要调用 {@link ImageView#invalidate()} 刷新 {@link ImageView}
     */
    public boolean onDisplayCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
        return false;
    }

    /**
     * 显示失败
     *
     * @param errorCause 错误原因
     * @return 是否需要调用 {@link ImageView#invalidate()} 刷新 {@link ImageView}
     */
    public boolean onDisplayError(@NonNull ErrorCause errorCause) {
        return false;
    }

    /**
     * 显示取消
     *
     * @param cancelCause 取消原因
     * @return 是否需要调用 {@link ImageView#invalidate()} 刷新 {@link ImageView}
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
