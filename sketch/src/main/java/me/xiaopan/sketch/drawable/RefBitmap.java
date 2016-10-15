package me.xiaopan.sketch.drawable;

import android.graphics.Bitmap;
import android.util.Log;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

public class RefBitmap extends SketchBitmap {

    private int cacheRefCount;
    private int displayRefCount;
    private int waitDisplayRefCount;
    private boolean allowRecycle = true;

    public RefBitmap(Bitmap bitmap, String imageId, String imageUri, int originWidth, int originHeight, String mimeType) {
        super(bitmap, imageId, imageUri, originWidth, originHeight, mimeType);
    }

    public RefBitmap(Bitmap bitmap) {
        super(bitmap);
    }

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

    public boolean isRecycled() {
        Bitmap bitmap = getBitmap();
        return bitmap == null || bitmap.isRecycled();
    }

    public void recycle() {
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    public boolean canRecycle() {
        return allowRecycle && getBitmap() != null && !getBitmap().isRecycled();
    }

    public boolean isAllowRecycle() {
        return allowRecycle;
    }

    @SuppressWarnings("unused")
    public void setAllowRecycle(boolean allowRecycle) {
        this.allowRecycle = allowRecycle;
    }

    private synchronized void tryRecycle(String type, String callingStation) {
        if (cacheRefCount <= 0 && displayRefCount <= 0 && waitDisplayRefCount <= 0 && canRecycle()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat("RefBitmap",
                        ". recycle bitmap",
                        ". ", callingStation, ":", type,
                        ". ", getInfo()));
            }
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
