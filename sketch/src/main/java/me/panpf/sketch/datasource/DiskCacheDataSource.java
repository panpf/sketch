/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.datasource;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.decode.NotFoundGifLibraryException;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.drawable.SketchGifFactory;
import me.panpf.sketch.request.ImageFrom;

/**
 * 用于读取来自磁盘缓存的图片
 */
public class DiskCacheDataSource implements DataSource {

    private DiskCache.Entry diskCacheEntry;
    private ImageFrom imageFrom;
    private long length = -1;
    private boolean fromProcessedCache; // 标识是否来自已处理缓存，后续对已处理缓存的图片会有额外处理

    public DiskCacheDataSource(DiskCache.Entry diskCacheEntry, ImageFrom imageFrom) {
        this.diskCacheEntry = diskCacheEntry;
        this.imageFrom = imageFrom;
    }

    @NonNull
    @Override
    public InputStream getInputStream() throws IOException {
        return diskCacheEntry.newInputStream();
    }

    @Override
    public long getLength() throws IOException {
        if (length >= 0) {
            return length;
        }

        length = diskCacheEntry.getFile().length();
        return length;
    }

    @Override
    public File getFile(File outDir, String outName) {
        return diskCacheEntry.getFile();
    }

    @NonNull
    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public DiskCache.Entry getDiskCacheEntry() {
        return diskCacheEntry;
    }

    @NonNull
    @Override
    public SketchGifDrawable makeGifDrawable(@NonNull String key, @NonNull String uri, @NonNull ImageAttrs imageAttrs,
                                             @NonNull BitmapPool bitmapPool) throws IOException, NotFoundGifLibraryException {
        return SketchGifFactory.createGifDrawable(key, uri, imageAttrs, getImageFrom(), bitmapPool, diskCacheEntry.getFile());
    }

    public boolean isFromProcessedCache() {
        return fromProcessedCache;
    }

    public DiskCacheDataSource setFromProcessedCache(boolean fromProcessedCache) {
        this.fromProcessedCache = fromProcessedCache;
        return this;
    }
}
