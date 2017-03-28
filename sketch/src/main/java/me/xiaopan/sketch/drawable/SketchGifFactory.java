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

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import me.xiaopan.sketch.cache.BitmapPool;

public class SketchGifFactory {
    private static int existGifLibrary = 0;

    public static boolean isExistGifLibrary() {
        if (existGifLibrary == 0) {
            synchronized (SketchGifFactory.class) {
                if (existGifLibrary == 0) {
                    try {
                        Class.forName("me.xiaopan.sketch.gif.BuildConfig");
                        Class.forName("pl.droidsonroids.gif.GifDrawable");
                        existGifLibrary = 1;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        existGifLibrary = -1;
                    }
                }
            }
        }
        return existGifLibrary == 1;
    }

    @SuppressWarnings("unused")
    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, AssetFileDescriptor afd) throws IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, uri, imageAttrs, bitmapPool, afd);
    }

    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, AssetManager assets, String assetName) throws IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, uri, imageAttrs, bitmapPool, assets, assetName);
    }

    @SuppressWarnings("unused")
    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, ByteBuffer buffer) throws IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, uri, imageAttrs, bitmapPool, buffer);
    }

    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, byte[] bytes) throws IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, uri, imageAttrs, bitmapPool, bytes);
    }

    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, FileDescriptor fd) throws IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, uri, imageAttrs, bitmapPool, fd);
    }

    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, File file) throws IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, uri, imageAttrs, bitmapPool, file);
    }

    @SuppressWarnings("unused")
    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, String filePath) throws IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, uri, imageAttrs, bitmapPool, filePath);
    }

    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, Resources res, int id) throws Resources.NotFoundException, IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, uri, imageAttrs, bitmapPool, res, id);
    }

    public static SketchGifDrawable createGifDrawable(String key, String imageUri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, ContentResolver resolver, Uri uri) throws IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, imageUri, imageAttrs, bitmapPool, resolver, uri);
    }

    @SuppressWarnings("unused")
    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs,
                                                      BitmapPool bitmapPool, InputStream stream) throws IOException {
        if (!isExistGifLibrary()) {
            return null;
        }

        return new SketchGifDrawableImpl(key, uri, imageAttrs, bitmapPool, stream);
    }
}
