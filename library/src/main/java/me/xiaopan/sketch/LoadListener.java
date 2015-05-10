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

package me.xiaopan.sketch;

import android.graphics.drawable.Drawable;

public interface LoadListener {
    /**
     * load start
     */
    void onStarted();

    /**
     * load complete
     * @param drawable RecycleBitmapDrawable:normal image; RecycleGifDrawable:gif image; null:exception
     * @param imageFrom image from
     * @param mimeType image type
     */
    void onCompleted(Drawable drawable, ImageFrom imageFrom, String mimeType);

    /**
     * load fail
     * @param failCause fail cause
     */
    void onFailed(FailCause failCause);

    /**
     * cancel
     */
    void onCanceled(CancelCause cancelCause);
}