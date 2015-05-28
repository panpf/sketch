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

import android.content.Context;

import me.xiaopan.sketch.process.ImageProcessor;

public class PauseDownloadImageHolder extends LoadingImageHolder{

    public PauseDownloadImageHolder(int resId) {
        super(resId);
    }

    public PauseDownloadImageHolder(int resId, ImageProcessor imageProcessor) {
        super(resId, imageProcessor);
    }

    public PauseDownloadImageHolder(int resId, ImageProcessor imageProcessor, Resize resize) {
        super(resId, imageProcessor, resize);
    }

    public PauseDownloadImageHolder(int resId, ImageProcessor imageProcessor, Resize resize, boolean imagesOfLowQuality) {
        super(resId, imageProcessor, resize, imagesOfLowQuality);
    }

    @Override
    public synchronized BindFixedRecycleBitmapDrawable getBindFixedRecycleBitmapDrawable(Context context, FixedSize fixedSize, DisplayRequest displayRequest) {
        return super.getBindFixedRecycleBitmapDrawable(context, fixedSize, displayRequest);
    }

    @Override
    public synchronized FixedRecycleBitmapDrawable getFixedRecycleBitmapDrawable(Context context, FixedSize fixedSize) {
        return super.getFixedRecycleBitmapDrawable(context, fixedSize);
    }
}