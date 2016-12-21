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
import android.util.Log;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 引用Bitmap，能够计算缓存引用、显示引用以及等待显示引用
 */
public class RefBitmap extends SketchBitmap {
    private static final String LOG_NAME = "RefBitmap";

    private int cacheRefCount;
    private int displayRefCount;
    private int waitDisplayRefCount;

    private BitmapPool bitmapPool;

    public RefBitmap(Bitmap bitmap, BitmapPool bitmapPool, String imageId, String imageUri, int originWidth, int originHeight, String mimeType) {
        super(bitmap, imageId, imageUri, originWidth, originHeight, mimeType);
        this.bitmapPool = bitmapPool;
    }

    public String getBitmapInfo() {
        if (isRecycled()) {
            return "Recycled";
        }
        return String.format("%s,%dx%d,%s,%s,%d",
                Integer.toHexString(bitmap.hashCode()),
                bitmap.getWidth(), bitmap.getHeight(),
                getMimeType(),
                bitmap.getConfig() != null ? bitmap.getConfig().name() : null,
                SketchUtils.getBitmapByteSize(bitmap));
    }

    @Override
    public String getInfo() {
        if (isRecycled()) {
            return "Recycled";
        }
        return String.format("%s(%s,%dx%d,%s,%s,%d)",
                LOG_NAME,
                Integer.toHexString(bitmap.hashCode()),
                bitmap.getWidth(), bitmap.getHeight(),
                getMimeType(),
                bitmap.getConfig() != null ? bitmap.getConfig().name() : null,
                SketchUtils.getBitmapByteSize(bitmap));
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
            cacheRefCount++;
            referenceChanged(callingStation);
        } else if (cacheRefCount > 0) {
            cacheRefCount--;
            referenceChanged(callingStation);
        }
    }

    /**
     * 设置等待缓存怒引用
     *
     * @param callingStation 调用位置
     * @param waitDisplay    等待显示
     */
    public synchronized void setIsWaitDisplay(String callingStation, boolean waitDisplay) {
        if (waitDisplay) {
            waitDisplayRefCount++;
            referenceChanged(callingStation);
        } else if (waitDisplayRefCount > 0) {
            waitDisplayRefCount--;
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
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, String.format("%s. Recycled. %s. %s", LOG_NAME, callingStation, getImageId()));
            }
            return;
        }

        if (cacheRefCount == 0 && displayRefCount == 0 && waitDisplayRefCount == 0) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, String.format("%s. Free. %s. bitmap(%s). %s", LOG_NAME, callingStation, getBitmapInfo(), getImageId()));
            }

            SketchUtils.freeBitmapToPool(bitmap, bitmapPool);
            bitmap = null;
        } else {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, String.format("%s. Can't free. %s. bitmap(%s). references(%d,%d,%d). %s", LOG_NAME,
                        callingStation, getBitmapInfo(), cacheRefCount, displayRefCount, waitDisplayRefCount, getImageId()));
            }
        }
    }
}
