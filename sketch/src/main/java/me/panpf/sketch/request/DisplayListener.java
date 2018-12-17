/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.request;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import me.panpf.sketch.decode.ImageAttrs;

/**
 * 显示监听器，所有的方法都会在主线中执行，所以不必考虑异步线程中刷新 UI 的问题
 */
public interface DisplayListener extends Listener {
    /**
     * 开始转入异步线程加载图片
     */
    @Override
    void onStarted();

    void onCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs);
}