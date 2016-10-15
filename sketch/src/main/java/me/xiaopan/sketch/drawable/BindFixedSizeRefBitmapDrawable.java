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

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.FixedSize;

public class BindFixedSizeRefBitmapDrawable extends FixedSizeRefBitmapDrawable implements BindDrawable {
    private WeakReference<DisplayRequest> weakReference;

    public BindFixedSizeRefBitmapDrawable(RefBitmapDrawable bitmapDrawable, DisplayRequest displayRequest) {
        super(bitmapDrawable, null);
        this.weakReference = new WeakReference<DisplayRequest>(displayRequest);
    }

    public BindFixedSizeRefBitmapDrawable(RefBitmapDrawable bitmapDrawable, FixedSize fixedSize, DisplayRequest displayRequest) {
        super(bitmapDrawable, fixedSize);
        this.weakReference = new WeakReference<DisplayRequest>(displayRequest);
    }

    @Override
    public DisplayRequest getRequest() {
        return weakReference.get();
    }
}