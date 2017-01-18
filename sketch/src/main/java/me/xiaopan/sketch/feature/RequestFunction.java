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

package me.xiaopan.sketch.feature;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.drawable.LoadingDrawable;
import me.xiaopan.sketch.drawable.RefDrawable;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.DisplayParams;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 请求基本功能，更新图片显示引用计数和在onDetachedFromWindow的时候取消请求并清空图片
 */
public class RequestFunction extends SketchImageView.Function {
    private ImageViewInterface imageViewInterface;

    private DisplayOptions displayOptions = new DisplayOptions();
    private DisplayParams displayParams;

    private boolean oldDrawableFromSketch;
    private boolean newDrawableFromSketch;

    public RequestFunction(ImageViewInterface imageViewInterface) {
        this.imageViewInterface = imageViewInterface;
    }

    /**
     * 修改Drawable显示状态
     *
     * @param callingStation 调用位置
     * @param drawable       Drawable
     * @param isDisplayed    是否已显示
     * @return true：drawable或其子Drawable是SketchDrawable
     */
    private static boolean notifyDrawable(String callingStation, Drawable drawable, final boolean isDisplayed) {
        boolean isSketchDrawable = false;
        if (drawable != null) {
            if (drawable instanceof LayerDrawable) {
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                    isSketchDrawable |= notifyDrawable(callingStation, layerDrawable.getDrawable(i), isDisplayed);
                }
            } else {
                if (!isDisplayed && drawable instanceof LoadingDrawable) {
                    LoadingDrawable loadingDrawable = (LoadingDrawable) drawable;
                    DisplayRequest displayRequest = loadingDrawable.getRequest();
                    if (displayRequest != null && !displayRequest.isFinished()) {
                        displayRequest.cancel(CancelCause.BE_REPLACED_ON_SET_DRAWABLE);
                    }
                }

                if (drawable instanceof RefDrawable) {
                    ((RefDrawable) drawable).setIsDisplayed(callingStation, isDisplayed);
                } else if (drawable instanceof SketchGifDrawable) {
                    if (!isDisplayed) {
                        ((SketchGifDrawable) drawable).recycle();
                    }
                }

                isSketchDrawable = drawable instanceof SketchDrawable;
            }
        }
        return isSketchDrawable;
    }

    @Override
    public boolean onDetachedFromWindow() {
        // 主动取消请求
        DisplayRequest potentialRequest = SketchUtils.findDisplayRequest(imageViewInterface);
        if (potentialRequest != null && !potentialRequest.isFinished()) {
            potentialRequest.cancel(CancelCause.ON_DETACHED_FROM_WINDOW);
        }

        // 如果当前图片是来自Sketch，那么就有可能在这里被主动回收，因此要主动设置ImageView的drawable为null
        final Drawable oldDrawable = imageViewInterface.getDrawable();
        return oldDrawable != null && notifyDrawable("onDetachedFromWindow", oldDrawable, false);
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        // 当Drawable改变的时候新Drawable的显示引用计数加1，旧Drawable的显示引用计数减1，一定要先处理newDrawable
        newDrawableFromSketch = notifyDrawable(callPosition + ":newDrawable", newDrawable, true);
        oldDrawableFromSketch = notifyDrawable(callPosition + ":oldDrawable", oldDrawable, false);

        // 如果新Drawable不是来自Sketch，那么就要清空显示参数，防止被RecyclerCompatFunction在onAttachedToWindow的时候错误的恢复成上一张图片
        if (!newDrawableFromSketch) {
            displayParams = null;
        }

        return false;
    }

    public DisplayParams getDisplayParams() {
        return displayParams;
    }

    public void setDisplayParams(DisplayParams displayParams) {
        this.displayParams = displayParams;
    }

    @SuppressWarnings("unused")
    public boolean isOldDrawableFromSketch() {
        return oldDrawableFromSketch;
    }

    public boolean isNewDrawableFromSketch() {
        return newDrawableFromSketch;
    }

    public DisplayOptions getDisplayOptions() {
        return displayOptions;
    }
}
