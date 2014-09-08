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

package me.xiaopan.android.spear.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import me.xiaopan.android.spear.request.DisplayRequest;

public class AsyncDrawable extends BitmapDrawable {
    private WeakReference<DisplayRequest> displayRequestWeakReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, DisplayRequest displayRequest) {
        super(res, bitmap);
        displayRequestWeakReference = new WeakReference<DisplayRequest>(displayRequest);
    }

    public DisplayRequest getDisplayRequest() {
        return displayRequestWeakReference.get();
    }

    /**
     * 获取与给定ImageView所持有的DisplayRequst
     * @param imageView ImageView
     * @return 给定ImageView所持有的DisplayRequst
     */
    public static DisplayRequest getDisplayRequestByAsyncDrawable(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                return ((AsyncDrawable) drawable).getDisplayRequest();
            }
        }
        return null;
    }
}