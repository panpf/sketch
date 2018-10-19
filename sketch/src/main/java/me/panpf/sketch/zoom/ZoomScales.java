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

package me.panpf.sketch.zoom;

import android.content.Context;
import android.widget.ImageView;

public interface ZoomScales {

    /**
     * 重置
     */
    void reset(final Context context, final Sizes sizes, final ImageView.ScaleType scaleType, final float rotateDegrees, final boolean readMode);

    /**
     * 最小缩放比例
     */
    float getMinZoomScale();

    /**
     * 最大缩放比例
     */
    float getMaxZoomScale();

    /**
     * 最大初始缩放比例
     */
    float getInitZoomScale();

    /**
     * 能够看到图片全貌的缩放比例
     */
    float getFullZoomScale();

    /**
     * 获取能够宽或高能够填满屏幕的缩放比例
     */
    float getFillZoomScale();

    /**
     * 能够让图片按照真实尺寸一比一显示的缩放比例
     */
    float getOriginZoomScale();

    /**
     * 双击缩放所的比例组
     */
    float[] getZoomScales();

    /**
     * 清理一下
     */
    void clean();
}
