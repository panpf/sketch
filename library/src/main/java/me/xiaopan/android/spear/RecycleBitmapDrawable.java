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

import java.io.InputStream;

public class RecycleBitmapDrawable extends BitmapDrawable implements RecycleDrawable {
    private static final String NAME = "RecycleBitmapDrawable";

    private int cacheRefCount;
    private int displayRefCount;
    private int waitDisplayRefCount;

    public RecycleBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    public RecycleBitmapDrawable(Resources res, String filepath) {
        super(res, filepath);
    }

    public RecycleBitmapDrawable(Resources res, InputStream is) {
        super(res, is);
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
        if(Spear.isDebugMode()){
            Log.d(Spear.TAG, NAME + " - " + "bitmap@" + getHashCode() + " - " + (isDisplayed ? "display" : "unbind") + " - " + callingStation);
        }
        tryRecycle(callingStation);
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
        if(Spear.isDebugMode()){
            Log.d(Spear.TAG, NAME + " - " + "bitmap@" + getHashCode() + " - " + (isCached ? "putCache" : "removedFromCache") + " - " + callingStation);
        }
        tryRecycle(callingStation);
    }

    @Override
    public void setIsWaitDisplay(String callingStation, boolean isWaitDisplay){
        synchronized (this){
            if(isWaitDisplay){
                waitDisplayRefCount++;
            }else{
                if(waitDisplayRefCount > 0){
                    waitDisplayRefCount--;
                }
            }
        }
        if(Spear.isDebugMode()){
            Log.d(Spear.TAG, NAME + " - " + "bitmap@" + getHashCode() + " - " + (isWaitDisplay ? "WaitDisplay" : "Display") + " - " + callingStation);
        }
        tryRecycle(callingStation);
    }

    private synchronized void tryRecycle(String callingStation) {
        if (cacheRefCount <= 0 && displayRefCount <= 0 && waitDisplayRefCount <= 0 && canRecycle()) {
            getBitmap().recycle();
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "recycle bitmap@" + getHashCode() + " - " + callingStation);
            }
        }
    }

    private String getHashCode(){
        return Integer.toHexString(getBitmap().hashCode());
    }

    private boolean canRecycle(){
//        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && !getBitmap().isRecycled();
        return !getBitmap().isRecycled();
    }
}