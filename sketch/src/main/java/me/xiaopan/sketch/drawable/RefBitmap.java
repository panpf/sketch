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
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 引用Bitmap，能够计算缓存引用、显示引用以及等待显示引用
 */
public class RefBitmap extends SketchBitmap {

    private int cacheRefCount;
    private int displayRefCount;
    private int waitDisplayRefCount;

    private boolean allowRecycle = true;

    public RefBitmap(Bitmap bitmap, String imageId, String imageUri, int originWidth, int originHeight, String mimeType) {
        super(bitmap, imageId, imageUri, originWidth, originHeight, mimeType);
    }

    /**
     * 设置显示引用
     *
     * @param callingStation 调用位置
     * @param displayed      显示
     */
    public synchronized void setIsDisplayed(String callingStation, boolean displayed) {
        synchronized (this) {
            if (displayed) {
                displayRefCount++;
            } else {
                if (displayRefCount > 0) {
                    displayRefCount--;
                }
            }
        }
        tryRecycle((displayed ? "display" : "hide"), callingStation);
    }

    /**
     * 设置缓存引用
     *
     * @param callingStation 调用位置
     * @param cached         缓存
     */
    public synchronized void setIsCached(String callingStation, boolean cached) {
        synchronized (this) {
            if (cached) {
                cacheRefCount++;
            } else {
                if (cacheRefCount > 0) {
                    cacheRefCount--;
                }
            }
        }
        tryRecycle((cached ? "putToCache" : "removedFromCache"), callingStation);
    }

    /**
     * 设置等待缓存怒引用
     *
     * @param callingStation 调用位置
     * @param waitDisplay    等待显示
     */
    public synchronized void setIsWaitDisplay(String callingStation, boolean waitDisplay) {
        synchronized (this) {
            if (waitDisplay) {
                waitDisplayRefCount++;
            } else {
                if (waitDisplayRefCount > 0) {
                    waitDisplayRefCount--;
                }
            }
        }
        tryRecycle((waitDisplay ? "waitDisplay" : "displayed"), callingStation);
    }

    /**
     * 已回收
     */
    public synchronized boolean isRecycled() {
        Bitmap bitmap = getBitmap();
        return bitmap == null || bitmap.isRecycled();
    }

    /**
     * 回收Bitmap
     */
    public synchronized void recycle() {
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    /**
     * 可以回收？只有三种引用都为0并且允许回收才可以回收
     */
    public synchronized boolean canRecycle() {
        return allowRecycle && getBitmap() != null && !getBitmap().isRecycled();
    }

    /**
     * 允许回收（默认允许）
     */
    @SuppressWarnings("unused")
    public synchronized boolean isAllowRecycle() {
        return allowRecycle;
    }

    /**
     * 设置允许回收
     */
    @SuppressWarnings("unused")
    public synchronized void setAllowRecycle(boolean allowRecycle) {
        this.allowRecycle = allowRecycle;
    }

    /**
     * 尝试回收
     *
     * @param type           类型
     * @param callingStation 调用位置
     */
    private synchronized void tryRecycle(String type, String callingStation) {
        if (cacheRefCount <= 0 && displayRefCount <= 0 && waitDisplayRefCount <= 0 && canRecycle()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat("RefBitmap",
                        ". recycle bitmap",
                        ". ", callingStation, ":", type,
                        ". ", getInfo()));
            }

            // TODO: 2016/12/11 放入bitmap pool
            getBitmap().recycle();
        } else {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat("RefBitmap",
                        ". can't recycle bitmap",
                        ". ", callingStation,
                        ". ", type,
                        ". ", getInfo(),
                        ". ", "references(",
                        "cacheRefCount=", cacheRefCount, ", ",
                        "displayRefCount=", displayRefCount, ", ",
                        "waitDisplayRefCount=", waitDisplayRefCount, ", ",
                        "canRecycle=", canRecycle(), ")"));
            }
        }
    }
}
