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

package me.xiaopan.android.spear.cache.memory;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import me.xiaopan.android.spear.util.LruCache;
import me.xiaopan.android.spear.util.RecyclingBitmapDrawable;

public class BitmapLruCache extends LruCache<String, BitmapDrawable> {

    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, BitmapDrawable value) {
        int bitmapSize;
        Bitmap bitmap = value.getBitmap();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            bitmapSize =  bitmap.getByteCount();
        }else{
            bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
        }
        return bitmapSize == 0 ? 1 : bitmapSize;
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
        if(RecyclingBitmapDrawable.class.isInstance(oldValue)){
            ((RecyclingBitmapDrawable) oldValue).setIsCached(false);
        }
    }
}
