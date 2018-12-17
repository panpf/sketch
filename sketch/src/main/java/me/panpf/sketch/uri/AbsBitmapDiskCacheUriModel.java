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

package me.panpf.sketch.uri;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.OutputStream;

import androidx.annotation.NonNull;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPoolUtils;
import me.panpf.sketch.util.SketchUtils;

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
