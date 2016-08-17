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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

public class SketchBitmapDrawable extends BitmapDrawable implements RecyclerDrawable {
    protected String logName = "SketchBitmapDrawable";

    private int originWidth;
    private int originHeight;
    private String mimeType;

    private int cacheRefCount;
    private int displayRefCount;
    private int waitDisplayRefCount;
    private boolean allowRecycle = true;

    public SketchBitmapDrawable(Bitmap bitmap) {
        super(bitmap);
    }

    void setLogName(String logName) {
        this.logName = logName;
    }

    @Override
    public int getOriginWidth() {
        return originWidth;
    }

    @Override
    public void setOriginWidth(int originWidth) {
        this.originWidth = originWidth;
    }

    @Override
    public int getOriginHeight() {
        return originHeight;
    }

    @Override
    public void setOriginHeight(int originHeight) {
        this.originHeight = originHeight;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public void setIsDisplayed(String callingStation, boolean displayed) {
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

    @Override
    public void setIsCached(String callingStation, boolean cached) {
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

    @Override
    public void setIsWaitDisplay(String callingStation, boolean waitDisplay) {
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

    @Override
    public boolean isRecycled() {
        Bitmap bitmap = getBitmap();
        return bitmap == null || bitmap.isRecycled();
    }

    @Override
    public void recycle() {
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    public String getInfo() {
        Bitmap bitmap = getBitmap();
        return SketchUtils.getInfo(logName, bitmap, mimeType, SketchUtils.getBitmapByteCount(bitmap));
    }

    @Override
    public boolean canRecycle() {
        return allowRecycle && getBitmap() != null && !getBitmap().isRecycled();
    }

    @Override
    public void setAllowRecycle(boolean allowRecycle) {
        this.allowRecycle = allowRecycle;
    }

    @Override
    public int getByteCount() {
        return SketchUtils.getBitmapByteCount(getBitmap());
    }

    private synchronized void tryRecycle(String type, String callingStation) {
        if (cacheRefCount <= 0 && displayRefCount <= 0 && waitDisplayRefCount <= 0 && canRecycle()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "recycled bitmap",
                        " - ", callingStation, ":", type,
                        " - ", getInfo()));
            }
            getBitmap().recycle();
        } else {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "can't recycled bitmap",
                        " - ", callingStation, ":", type,
                        " - ", getInfo(),
                        " - ", "references(",
                                "cacheRefCount=", cacheRefCount, "; ",
                                "displayRefCount=", displayRefCount, "; ",
                                "waitDisplayRefCount=", waitDisplayRefCount, "; ",
                                "canRecycle=", canRecycle(), ")"));
            }
        }
    }
}