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

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import pl.droidsonroids.gif.GifDrawable;

public class RecycleGifDrawable extends GifDrawable implements RecycleDrawable {
    private static final String NAME = "RecycleGifDrawable";

    private int cacheRefCount;
    private int displayRefCount;
    private boolean waitDisplay;

    public RecycleGifDrawable(AssetFileDescriptor afd) throws IOException {
        super(afd);
        this.waitDisplay = true;
    }

    public RecycleGifDrawable(AssetManager assets, String assetName) throws IOException {
        super(assets, assetName);
        this.waitDisplay = true;
    }

    public RecycleGifDrawable(ByteBuffer buffer) throws IOException {
        super(buffer);
        this.waitDisplay = true;
    }

    public RecycleGifDrawable(byte[] bytes) throws IOException {
        super(bytes);
        this.waitDisplay = true;
    }

    public RecycleGifDrawable(FileDescriptor fd) throws IOException {
        super(fd);
        this.waitDisplay = true;
    }

    public RecycleGifDrawable(File file) throws IOException {
        super(file);
        this.waitDisplay = true;
    }

    public RecycleGifDrawable(String filePath) throws IOException {
        super(filePath);
        this.waitDisplay = true;
    }

    public RecycleGifDrawable(Resources res, int id) throws Resources.NotFoundException, IOException {
        super(res, id);
        this.waitDisplay = true;
    }

    public RecycleGifDrawable(ContentResolver resolver, Uri uri) throws IOException {
        super(resolver, uri);
        this.waitDisplay = true;
    }

    public RecycleGifDrawable(InputStream stream) throws IOException {
        super(stream);
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

    @Override
    public int getSize() {
        return (int) getAllocationByteCount();
    }

    private synchronized void tryRecycle(String type, String callingStation) {
        if (cacheRefCount <= 0 && displayRefCount <= 0 && !waitDisplay && canRecycle()) {
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "recycled gif drawable@" + getHashCode() + " - " + type + " - " + callingStation);
            }
            recycle();
        }else{
            if(Spear.isDebugMode()){
                Log.d(Spear.TAG, NAME + " - " + "can't recycle gif drawable@" + getHashCode() + " - " + type + " - " + callingStation + " - " + ("cacheRefCount="+cacheRefCount) + "; " + ("displayRefCount="+displayRefCount) + "; " + ("waitDisplay="+waitDisplay) + "; " + ("canRecycle="+canRecycle()));
            }
        }
    }

    private String getHashCode(){
        return Integer.toHexString(hashCode());
    }

    private boolean canRecycle(){
//        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && !isRecycled();
        return !isRecycled();
    }
}