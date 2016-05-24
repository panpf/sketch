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

package me.xiaopan.sketch.drawable;

import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.FixedSize;
import me.xiaopan.sketch.request.ImageViewInterface;

public class BindFixedRecycleBitmapDrawable extends FixedRecycleBitmapDrawable {
    private WeakReference<DisplayRequest> displayRequestWeakReference;

    public BindFixedRecycleBitmapDrawable(RecycleBitmapDrawable recycleBitmapDrawable, DisplayRequest displayRequest) {
        super(recycleBitmapDrawable, null);
        this.displayRequestWeakReference = new WeakReference<DisplayRequest>(displayRequest);
    }

    public BindFixedRecycleBitmapDrawable(RecycleBitmapDrawable recycleBitmapDrawable, FixedSize fixedSize, DisplayRequest displayRequest) {
        super(recycleBitmapDrawable, fixedSize);
        this.displayRequestWeakReference = new WeakReference<DisplayRequest>(displayRequest);
    }

    /**
     * 从ImageViewInterface上查找DisplayRequest
     */
    public static DisplayRequest findDisplayRequest(ImageViewInterface imageViewInterface) {
        if (imageViewInterface != null) {
            final Drawable drawable = imageViewInterface.getDrawable();
            if (drawable != null && drawable instanceof BindFixedRecycleBitmapDrawable) {
                return ((BindFixedRecycleBitmapDrawable) drawable).getDisplayRequest();
            }
        }
        return null;
    }

    /**
     * 获取显示请求
     *
     * @return 显示请求
     */
    public DisplayRequest getDisplayRequest() {
        return displayRequestWeakReference.get();
    }
}