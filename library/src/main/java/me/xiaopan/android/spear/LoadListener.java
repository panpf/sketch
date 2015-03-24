/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.android.spear;

import android.graphics.Bitmap;

/**
 * 加载监听器
 */
public interface LoadListener {
    /**
     * 已开始
     */
    void onStarted();

    /**
     * 已完成
     * @param bitmap 图片
     * @param imageFrom 图片来源
     */
    void onCompleted(Bitmap bitmap, ImageFrom imageFrom);

    /**
     * 已失败
     * @param failCause 失败原因
     */
    void onFailed(FailCause failCause);

    /**
     * 已取消
     */
    void onCanceled(CancelCause cancelCause);
}