/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.drawable;

import android.graphics.Bitmap;

import me.panpf.sketch.SLog;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.cache.BitmapPoolUtils;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.util.SketchUtils;

/**
 * 引用 {@link Bitmap}，能够计算缓存引用、显示引用以及等待显示引用
 */
public class SketchRefBitmap extends SketchBitmap {
    private static final String NAME = "SketchRefBitmap";

    private int memoryCacheRefCount;  // 内存缓存引用
    private int displayRefCount;    // 真正显示引用
    private int waitingUseRefCount; // 等待使用引用

    private BitmapPool bitmapPool;

    public SketchRefBitmap(Bitmap bitmap, String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool) {
        super(bitmap, key, uri, imageAttrs);
        this.bitmapPool = bitmapPool;
    }

    @Override
    public String getInfo() {
        if (isRecycled()) {
            return String.format("%s(Recycled,%s)", NAME, getKey());
        } else {
            ImageAttrs imageAttrs = getAttrs();
            return SketchUtils.makeImageInfo(NAME, imageAttrs.getWidth(), imageAttrs.getHeight(),
                    imageAttrs.getMimeType(), imageAttrs.getExifOrientation(), bitmap, getByteCount(), getKey());
        }
    }

    /**
     * 已回收？
     */
    public synchronized boolean isRecycled() {
        return bitmap == null || bitmap.isRecycled();
    }

    /**
     * 设置显示引用
     *
     * @param callingStation 调用位置
     * @param displayed      显示
     */
    public synchronized void setIsDisplayed(String callingStation, boolean displayed) {
        if (displayed) {
            displayRefCount++;
            referenceChanged(callingStation);
        } else if (displayRefCount > 0) {
            displayRefCount--;
            referenceChanged(callingStation);
        }
    }

    /**
     * 设置缓存引用
     *
     * @param callingStation 调用位置
     * @param cached         缓存
     */
    public synchronized void setIsCached(String callingStation, boolean cached) {
        if (cached) {
            memoryCacheRefCount++;
            referenceChanged(callingStation);
        } else if (memoryCacheRefCount > 0) {
            memoryCacheRefCount--;
            referenceChanged(callingStation);
        }
    }

    /**
     * 设置等待使用引用
     *
     * @param callingStation 调用位置
     * @param waitingUse     等待使用
     */
    public synchronized void setIsWaitingUse(String callingStation, boolean waitingUse) {
        if (waitingUse) {
            waitingUseRefCount++;
            referenceChanged(callingStation);
        } else if (waitingUseRefCount > 0) {
            waitingUseRefCount--;
            referenceChanged(callingStation);
        }
    }

    /**
     * 引用变化时执行此方法
     *
     * @param callingStation 调用位置
     */
    private void referenceChanged(String callingStation) {
        if (isRecycled()) {
            SLog.e(NAME, "Recycled. %s. %s", callingStation, getKey());
            return;
        }

        if (memoryCacheRefCount == 0 && displayRefCount == 0 && waitingUseRefCount == 0) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Free. %s. %s", callingStation, getInfo());
            }

            BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool);
            bitmap = null;
        } else {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Can't free. %s. references(%d,%d,%d). %s",
                        callingStation, memoryCacheRefCount, displayRefCount, waitingUseRefCount, getInfo());
            }
        }
    }
}
