/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

package me.panpf.sketch.decode;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.datasource.DiskCacheDataSource;
import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.request.LoadOptions;
import me.panpf.sketch.request.LoadRequest;
import me.panpf.sketch.util.DiskLruCache;
import me.panpf.sketch.util.SketchUtils;

/**
 * Cache transform results, no need to transform again next time, can speed up the loading speed
 */
public class TransformCacheManager {

    public boolean canUse(@NonNull LoadOptions loadOptions) {
        if (!loadOptions.isCacheProcessedImageInDisk()) {
            return false;
        }

        if (loadOptions.getMaxSize() != null || loadOptions.getResize() != null) {
            return true;
        }

        if (loadOptions.getProcessor() != null) {
            return true;
        }

        if (loadOptions.isThumbnailMode() && loadOptions.getResize() != null) {
            return true;
        }

        //noinspection RedundantIfStatement
        if (!loadOptions.isCorrectImageOrientationDisabled()) {
            return true;
        }

        return false;
    }

    public boolean canUseByInSampleSize(int inSampleSize) {
        return inSampleSize >= 8;
    }

    public boolean checkDiskCache(@NonNull LoadRequest request) {
        DiskCache diskCache = request.getConfiguration().getDiskCache();
        String processedImageDiskCacheKey = request.getTransformCacheKey();
        String diskCacheKey = request.getDiskCacheKey();
        if (diskCacheKey.equals(processedImageDiskCacheKey)) {
            return false;
        }

        ReentrantLock editLock = diskCache.getEditLock(processedImageDiskCacheKey);
        editLock.lock();

        try {
            return diskCache.exist(processedImageDiskCacheKey);
        } finally {
            editLock.unlock();
        }
    }

    @Nullable
    public DiskCacheDataSource getDiskCache(@NonNull LoadRequest request) {
        DiskCache diskCache = request.getConfiguration().getDiskCache();
        String processedImageDiskCacheKey = request.getTransformCacheKey();
        String diskCacheKey = request.getDiskCacheKey();
        if (diskCacheKey.equals(processedImageDiskCacheKey)) {
            return null;
        }

        ReentrantLock editLock = diskCache.getEditLock(processedImageDiskCacheKey);
        editLock.lock();

        DiskCache.Entry diskCacheEntry;
        try {
            diskCacheEntry = diskCache.get(processedImageDiskCacheKey);
        } finally {
            editLock.unlock();
        }

        if (diskCacheEntry == null) {
            return null;
        }

        return new DiskCacheDataSource(diskCacheEntry, ImageFrom.DISK_CACHE).setFromProcessedCache(true);
    }

    public void saveToDiskCache(@NonNull LoadRequest request, @NonNull Bitmap bitmap) {
        DiskCache diskCache = request.getConfiguration().getDiskCache();
        String processedImageDiskCacheKey = request.getTransformCacheKey();
        String diskCacheKey = request.getDiskCacheKey();
        if (diskCacheKey.equals(processedImageDiskCacheKey)) {
            return;
        }

        ReentrantLock editLock = diskCache.getEditLock(processedImageDiskCacheKey);
        editLock.lock();

        try {
            DiskCache.Entry diskCacheEntry = diskCache.get(processedImageDiskCacheKey);

            if (diskCacheEntry != null) {
                diskCacheEntry.delete();
            }

            DiskCache.Editor diskCacheEditor = diskCache.edit(processedImageDiskCacheKey);
            if (diskCacheEditor != null) {
                BufferedOutputStream outputStream = null;
                try {
                    outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
                    bitmap.compress(SketchUtils.bitmapConfigToCompressFormat(bitmap.getConfig()), 100, outputStream);
                    diskCacheEditor.commit();
                } catch (DiskLruCache.EditorChangedException e) {
                    e.printStackTrace();
                    diskCacheEditor.abort();
                } catch (IOException e) {
                    e.printStackTrace();
                    diskCacheEditor.abort();
                } catch (DiskLruCache.ClosedException e) {
                    e.printStackTrace();
                    diskCacheEditor.abort();
                } catch (DiskLruCache.FileNotExistException e) {
                    e.printStackTrace();
                    diskCacheEditor.abort();
                } finally {
                    SketchUtils.close(outputStream);
                }
            }
        } finally {
            editLock.unlock();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "TransformCache";
    }
}
