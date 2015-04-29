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

package me.xiaopan.spear;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;

public class RecycleBitmapDrawable extends BitmapDrawable implements RecycleDrawableInterface {
    private static final String NAME = "RecycleBitmapDrawable";

    private int cacheRefCount;
    private int displayRefCount;
    private int waitDisplayRefCount;
    private String mimeType;

    public RecycleBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    @Override
    public void setIsDisplayed(String callingStation, boolean isDisplayed) {
        synchronized (this) {
            if (isDisplayed) {
                displayRefCount++;
            } else {
                if(displayRefCount > 0){
                    displayRefCount--;
                }
            }
        }
        tryRecycle((isDisplayed ? "display" : "hide"), callingStation);
    }

    @Override
    public void setIsCached(String callingStation, boolean isCached) {
        synchronized (this) {
            if (isCached) {
                cacheRefCount++;
            } else {
                if(cacheRefCount > 0){
                    cacheRefCount--;
                }
            }
        }
        tryRecycle((isCached ? "putToCache" : "removedFromCache"), callingStation);
    }

    @Override
    public void setIsWaitDisplay(String callingStation, boolean isWaitDisplay) {
        synchronized (this) {
            if (isWaitDisplay) {
                waitDisplayRefCount++;
            } else {
                if(waitDisplayRefCount > 0){
                    waitDisplayRefCount--;
                }
            }
        }
        tryRecycle((isWaitDisplay ? "waitDisplay" : "displayed"), callingStation);
    }

    @Override
    public int getSize() {
        Bitmap bitmap = getBitmap();
        if(bitmap == null){
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return  bitmap.getByteCount();
        }else{
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    @Override
    public boolean isRecycled() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && bitmap.isRecycled();
    }

    @Override
    public String getHashCodeByLog() {
        Bitmap bitmap = getBitmap();
        if(bitmap != null){
            return Integer.toHexString(bitmap.hashCode());
        }else{
            return null;
        }
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
    public void recycle() {
        Bitmap bitmap = getBitmap();
        if(bitmap != null){
            bitmap.recycle();
        }
    }

    private synchronized void tryRecycle(String type, String callingStation) {
        if (cacheRefCount <= 0 && displayRefCount <= 0 && waitDisplayRefCount <= 0 && canRecycle()) {
            getBitmap().recycle();
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "recycled bitmap@" + getHashCodeByLog() + " - " + type + " - " + callingStation);
            }
        }else{
            if(Spear.isDebugMode()){
                Log.d(Spear.TAG, NAME + " - " + "can't recycle bitmap@" + getHashCodeByLog() + " - " + type + " - " + callingStation + " - " + ("cacheRefCount="+cacheRefCount) + "; " + ("displayRefCount="+displayRefCount) + "; " + ("waitDisplayRefCount="+waitDisplayRefCount) + "; " + ("canRecycle="+canRecycle()));
            }
        }
    }

    private boolean canRecycle(){
//        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && getBitmap() != null && !getBitmap().isRecycled();
        return getBitmap() != null && !getBitmap().isRecycled();
    }
}