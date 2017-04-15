/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

import android.graphics.Bitmap;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 引用Bitmap，能够计算缓存引用、显示引用以及等待显示引用
 */
public class RefBitmap extends SketchBitmap {
    private static final String LOG_NAME = "RefBitmap";

    private int memoryCacheRefCount;  // 内存缓存引用
    private int displayRefCount;    // 真正显示引用
    private int waitingUseRefCount; // 等待使用引用

    private BitmapPool bitmapPool;

    public RefBitmap(Bitmap bitmap, String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool) {
        super(bitmap, key, uri, imageAttrs);
        this.bitmapPool = bitmapPool;
    }

    public String getBitmapInfo() {
        if (isRecycled()) {
            return String.format("%s,%s", LOG_NAME, getKey());
        }
        return String.format("%s,%dx%d,%s,%s,%d,%s",
                Integer.toHexString(bitmap.hashCode()),
                bitmap.getWidth(), bitmap.getHeight(),
                getAttrs().getMimeType(),
                bitmap.getConfig() != null ? bitmap.getConfig().name() : null,
                SketchUtils.getByteCount(bitmap),
                getKey());
    }

    @Override
    public String getInfo() {
        if (isRecycled()) {
            return String.format("%s(Recycled,%s)", LOG_NAME, getKey());
        }
        return String.format("%s(%s,%dx%d,%s,%s,%d,%s)",
                LOG_NAME,
                Integer.toHexString(bitmap.hashCode()),
                bitmap.getWidth(), bitmap.getHeight(),
                getAttrs().getMimeType(),
                bitmap.getConfig() != null ? bitmap.getConfig().name() : null,
                SketchUtils.getByteCount(bitmap),
                getKey());
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
            if (SLogType.CACHE.isEnabled()) {
                SLog.e(SLogType.CACHE, LOG_NAME, "Recycled. %s. %s", callingStation, getKey());
            }
            return;
        }

        if (memoryCacheRefCount == 0 && displayRefCount == 0 && waitingUseRefCount == 0) {
            if (SLogType.CACHE.isEnabled()) {
                SLog.w(SLogType.CACHE, LOG_NAME, "Free. %s. bitmap(%s)", callingStation, getBitmapInfo());
            }

            BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool);
            bitmap = null;
        } else {
            if (SLogType.CACHE.isEnabled()) {
                SLog.d(SLogType.CACHE, LOG_NAME, "Can't free. %s. references(%d,%d,%d). bitmap(%s)",
                        callingStation, memoryCacheRefCount, displayRefCount, waitingUseRefCount, getBitmapInfo());
            }
        }
    }
}
