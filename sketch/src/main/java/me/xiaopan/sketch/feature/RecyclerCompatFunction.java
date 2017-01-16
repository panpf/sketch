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

import android.content.Context;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.request.DisplayParams;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.request.UriScheme;

/**
 * 由于RecyclerView在往回滚动的时候遇到可以直接使用的ItemView（位置没有变）会不走onBindViewHolder而直接走onAttachedToWindow然后显示，
 * <br>可是RequestFunction在onDetachedFromWindow的时候会主动清空Drawable导致没有重新走onBindViewHolder的ItemView会没有Drawable而显示空白
 * <br>因此RecyclerCompatFunction就判断了如果在onAttachedToWindow之前没有调用相关显示图片的方法就会根据DisplayParams恢复之前的图片
 */
public class RecyclerCompatFunction extends SketchImageView.Function {
    protected String logName = "RecyclerCompatFunction";

    private Context context;
    private RequestFunction requestFunction;
    private ImageViewInterface imageViewInterface;

    private boolean isSetImage;

    public RecyclerCompatFunction(Context context, ImageViewInterface imageViewInterface, RequestFunction requestFunction) {
        context = context.getApplicationContext();
        this.context = context;
        this.imageViewInterface = imageViewInterface;
        this.requestFunction = requestFunction;
    }

    @Override
    public void onAttachedToWindow() {
        if (isSetImage) {
            return;
        }

        DisplayParams displayParams = requestFunction.getDisplayParams();
        if (displayParams != null) {
            if (SLogType.BASE.isEnabled()) {
                SLog.w(SLogType.BASE, logName, "restore image on attached to window. %s", displayParams.info.getUri());
            }
            Sketch.with(context).display(displayParams, imageViewInterface).commit();
        }
    }

    @Override
    public boolean onReadyDisplay(UriScheme uriScheme) {
        isSetImage = true;
        return false;
    }

    @Override
    public boolean onDetachedFromWindow() {
        this.isSetImage = false;
        return false;
    }
}
