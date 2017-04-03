/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.util.SketchUtils;
import pl.droidsonroids.gif.GifDrawable;

/**
 * 增加了从BitmapPool中寻找可复用Bitmap的功能以及图片的信息
 */
// SketchGifDrawableImpl类配置了混淆时忽略警告，以后内部类有变化时需要同步调整混淆配置，并打包验证
public class SketchGifDrawableImpl extends GifDrawable implements SketchGifDrawable {
    protected String logName = "SketchGifDrawable";

    private String key;
    private String uri;
    private ImageAttrs imageAttrs;
    private ImageFrom imageFrom;

    private BitmapPool bitmapPool;

    private Map<AnimationListener, pl.droidsonroids.gif.AnimationListener> listenerMap;

    SketchGifDrawableImpl(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          AssetFileDescriptor afd) throws IOException {
        super(afd);
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    SketchGifDrawableImpl(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          AssetManager assets, String assetName) throws IOException {
        super(assets, assetName);
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    SketchGifDrawableImpl(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          ByteBuffer buffer) throws IOException {
        super(buffer);
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    SketchGifDrawableImpl(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          byte[] bytes) throws IOException {
        super(bytes);
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    SketchGifDrawableImpl(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          FileDescriptor fd) throws IOException {
        super(fd);
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    SketchGifDrawableImpl(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          File file) throws IOException {
        super(file);
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    SketchGifDrawableImpl(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          String filePath) throws IOException {
        super(filePath);
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    SketchGifDrawableImpl(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          Resources res, int id) throws Resources.NotFoundException, IOException {
        super(res, id);
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    SketchGifDrawableImpl(String key, String imageUri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          ContentResolver resolver, Uri uri) throws IOException {
        super(resolver, uri);
        this.key = key;
        this.uri = imageUri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    SketchGifDrawableImpl(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool,
                          InputStream stream) throws IOException {
        super(stream);
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
        this.bitmapPool = bitmapPool;
    }

    @Override
    protected Bitmap makeBitmap(int width, int height, Bitmap.Config config) {
        if (bitmapPool != null) {
            return bitmapPool.getOrMake(width, height, config);
        }
        return super.makeBitmap(width, height, config);
    }

    @Override
    protected void recycleBitmap() {
        if (mBuffer == null) {
            return;
        }

        if (bitmapPool != null) {
            BitmapPoolUtils.freeBitmapToPool(mBuffer, bitmapPool);
        } else {
            super.recycleBitmap();
        }
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public int getOriginWidth() {
        return imageAttrs.getOriginWidth();
    }

    @Override
    public int getOriginHeight() {
        return imageAttrs.getOriginHeight();
    }

    @Override
    public String getMimeType() {
        return imageAttrs.getMimeType();
    }

    @Override
    public int getOrientation() {
        return imageAttrs.getOrientation();
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @Override
    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    @Override
    public String getInfo() {
        return SketchUtils.makeImageInfo(logName, mBuffer, imageAttrs.getMimeType(), getAllocationByteCount());
    }

    @Override
    public int getByteCount() {
        return (int) getAllocationByteCount();
    }

    @Override
    public Bitmap.Config getBitmapConfig() {
        return mBuffer != null ? mBuffer.getConfig() : null;
    }

    @Override
    public void addAnimationListener(@NonNull final AnimationListener listener) {
        if (listenerMap == null) {
            listenerMap = new HashMap<AnimationListener, pl.droidsonroids.gif.AnimationListener>();
        }

        // 这个内部类配置了混淆时忽略警告，以后有变化时需要同步调整混淆配置，并打包验证
        pl.droidsonroids.gif.AnimationListener animationListener = new pl.droidsonroids.gif.AnimationListener() {
            @Override
            public void onAnimationCompleted(int loopNumber) {
                listener.onAnimationCompleted(loopNumber);
            }
        };
        addAnimationListener(animationListener);
        listenerMap.put(listener, animationListener);
    }

    @Override
    public boolean removeAnimationListener(AnimationListener listener) {
        if (listenerMap == null || listenerMap.isEmpty()) {
            return false;
        }

        pl.droidsonroids.gif.AnimationListener animationListener = listenerMap.remove(listener);
        return animationListener != null && removeAnimationListener(animationListener);
    }

    @Override
    public void followPageVisible(boolean userVisible, boolean fromDisplayCompleted) {
        if (userVisible) {
            start();
        } else {
            if (fromDisplayCompleted) {
                // 图片加载完了，但是页面还不可见的时候就停留着在第一帧
                seekToFrame(0);
                stop();
            } else {
                stop();
            }
        }
    }
}
