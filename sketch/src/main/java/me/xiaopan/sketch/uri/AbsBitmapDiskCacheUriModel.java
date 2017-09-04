/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.uri;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.OutputStream;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.util.SketchUtils;

public abstract class AbsBitmapDiskCacheUriModel extends AbsDiskCacheUriModel<Bitmap> {

    @Override
    protected final void outContent(@NonNull Bitmap bitmap, @NonNull OutputStream outputStream) throws Exception {
        bitmap.compress(SketchUtils.bitmapConfigToCompressFormat(bitmap.getConfig()), 100, outputStream);
    }

    @Override
    protected final void closeContent(@NonNull Bitmap bitmap, @NonNull Context context) {
        BitmapPoolUtils.freeBitmapToPool(bitmap, Sketch.with(context).getConfiguration().getBitmapPool());
    }
}
