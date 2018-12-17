/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.uri;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.datasource.ByteArrayDataSource;
import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.datasource.DiskCacheDataSource;
import me.panpf.sketch.request.DownloadResult;
import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.util.DiskLruCache;
import me.panpf.sketch.util.SketchUtils;

/**
 * 为需要磁盘缓存的 UriModel 封装好 getDataSource 部分
 */
public abstract class AbsDiskCacheUriModel<Content> extends UriModel {

    private static final String NAME = "AbsDiskCacheUriModel";

    @NonNull
    @Override
    public final DataSource getDataSource(@NonNull Context context, @NonNull String uri, @Nullable DownloadResult downloadResult) throws GetDataSourceException {
        DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();
        String diskCacheKey = getDiskCacheKey(uri);

        DiskCache.Entry cacheEntry = diskCache.get(diskCacheKey);
        if (cacheEntry != null) {
            return new DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE);
        }

        ReentrantLock diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
        diskCacheEditLock.lock();
        try {
            cacheEntry = diskCache.get(diskCacheKey);
            if (cacheEntry != null) {
                return new DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE);
            } else {
                return readContent(context, uri, diskCacheKey);
            }
        } finally {
            diskCacheEditLock.unlock();
        }
    }

    @NonNull
    private DataSource readContent(Context context, String uri, String diskCacheKey) throws GetDataSourceException {
        Content content = getContent(context, uri);

        DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();
        DiskCache.Editor diskCacheEditor = diskCache.edit(diskCacheKey);
        OutputStream outputStream;
        if (diskCacheEditor != null) {
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } catch (IOException e) {
                diskCacheEditor.abort();
                closeContent(content, context);

                String cause = String.format("Open output stream exception. %s", uri);
                SLog.e(NAME, e, cause);
                throw new GetDataSourceException(cause, e);
            }
        } else {
            outputStream = new ByteArrayOutputStream();
        }

        try {
            outContent(content, outputStream);
        } catch (Throwable tr) {
            if (diskCacheEditor != null) {
                diskCacheEditor.abort();
            }
            String cause = String.format("Output data exception. %s", uri);
            SLog.e(NAME, tr, cause);
            throw new GetDataSourceException(cause, tr);
        } finally {
            SketchUtils.close(outputStream);
            closeContent(content, context);
        }

        if (diskCacheEditor != null) {
            try {
                diskCacheEditor.commit();
            } catch (IOException | DiskLruCache.EditorChangedException | DiskLruCache.ClosedException | DiskLruCache.FileNotExistException e) {
                diskCacheEditor.abort();
                String cause = String.format("Commit disk cache exception. %s", uri);
                SLog.e(NAME, e, cause);
                throw new GetDataSourceException(cause, e);
            }
        }

        if (diskCacheEditor == null) {
            return new ByteArrayDataSource(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        } else {
            DiskCache.Entry cacheEntry = diskCache.get(diskCacheKey);
            if (cacheEntry != null) {
                return new DiskCacheDataSource(cacheEntry, ImageFrom.LOCAL);
            } else {
                String cause = String.format("Not found disk cache after save. %s", uri);
                SLog.e(NAME, cause);
                throw new GetDataSourceException(cause);
            }
        }
    }

    @NonNull
    protected abstract Content getContent(@NonNull Context context, @NonNull String uri) throws GetDataSourceException;

    protected abstract void outContent(@NonNull Content content, @NonNull OutputStream outputStream) throws Exception;

    protected abstract void closeContent(@NonNull Content content, @NonNull Context context);
}
