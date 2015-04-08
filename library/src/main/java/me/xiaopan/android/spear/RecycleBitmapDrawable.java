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

package me.xiaopan.android.spear;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class RecycleBitmapDrawable extends BitmapDrawable implements RecycleDrawable {
    private static final String NAME = "RecycleBitmapDrawable";

    private int cacheRefCount;
    private int displayRefCount;
    private boolean waitDisplay;
    private String bitmapCode;

    public RecycleBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
        this.bitmapCode = Integer.toHexString(bitmap.hashCode());
        this.waitDisplay = true;
    }

    @Override
    public void setIsDisplayed(String callingStation, boolean isDisplayed) {
        synchronized (this) {
            if (isDisplayed) {
                displayRefCount++;
                waitDisplay = false;
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
    public void cancelWaitDisplay(String callingStation){
        synchronized (this){
            waitDisplay = false;
        }
        tryRecycle("cancelDisplay", callingStation);
    }

    private synchronized void tryRecycle(String type, String callingStation) {
        if (cacheRefCount <= 0 && displayRefCount <= 0 && !waitDisplay && canRecycle()) {
            getBitmap().recycle();
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "recycled bitmap@" + getHashCode() + " - " + type + " - " + callingStation);
            }
        }else{
            if(Spear.isDebugMode()){
                Log.d(Spear.TAG, NAME + " - " + "can't recycle bitmap@" + getHashCode() + " - " + type + " - " + callingStation + " - " + ("cacheRefCount="+cacheRefCount) + "; " + ("displayRefCount="+displayRefCount) + "; " + ("waitDisplay="+waitDisplay) + "; " + ("canRecycle="+canRecycle()));
            }
        }
    }

    private String getHashCode(){
        return getBitmap() != null ? Integer.toHexString(getBitmap().hashCode()) : null;
    }

    private boolean canRecycle(){
//        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && getBitmap() != null && !getBitmap().isRecycled();
        return getBitmap() != null && !getBitmap().isRecycled();
    }

    public String getBitmapCode() {
        return bitmapCode;
    }
}