/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.drawable;

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

import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.decode.NotFoundGifLibraryException;
import me.panpf.sketch.request.ImageFrom;

public class SketchGifFactory {
    private static int existGifLibrary = 0;

    public static boolean isExistGifLibrary() {
        if (existGifLibrary == 0) {
            synchronized (SketchGifFactory.class) {
                if (existGifLibrary == 0) {
                    try {
                        Class.forName("me.panpf.sketch.gif.BuildConfig");
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

    public static void assetExistGifLibrary() throws NotFoundGifLibraryException {
        if (!isExistGifLibrary()) {
            throw new NotFoundGifLibraryException();
        }
    }

    @SuppressWarnings("unused")
    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, AssetFileDescriptor afd) throws IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, afd);
    }

    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, AssetManager assets, String assetName) throws IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, assets, assetName);
    }

    @SuppressWarnings("unused")
    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, ByteBuffer buffer) throws IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, buffer);
    }

    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, byte[] bytes) throws IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, bytes);
    }

    @SuppressWarnings("unused")
    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, FileDescriptor fd) throws IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, fd);
    }

    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, File file) throws IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, file);
    }

    @SuppressWarnings("unused")
    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, String filePath) throws IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, filePath);
    }

    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, Resources res, int id) throws Resources.NotFoundException, IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, res, id);
    }

    public static SketchGifDrawable createGifDrawable(String key, String imageUri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, ContentResolver resolver, Uri uri) throws IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, imageUri, imageAttrs, imageFrom, bitmapPool, resolver, uri);
    }

    @SuppressWarnings("unused")
    public static SketchGifDrawable createGifDrawable(String key, String uri, ImageAttrs imageAttrs, ImageFrom imageFrom,
                                                      BitmapPool bitmapPool, InputStream stream) throws IOException, NotFoundGifLibraryException {
        assetExistGifLibrary();
        return new SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, stream);
    }
}
