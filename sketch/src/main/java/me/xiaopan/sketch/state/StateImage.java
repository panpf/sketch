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

package me.xiaopan.sketch.state;

import android.content.Context;
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ImageViewInterface;

/**
 * 专门用于加载中、失败、暂停图片
 */
public interface StateImage {
    Drawable getDrawable(Context context, ImageViewInterface imageViewInterface, DisplayOptions displayOptions);
}
