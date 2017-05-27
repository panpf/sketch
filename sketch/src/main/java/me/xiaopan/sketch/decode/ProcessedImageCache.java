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

package me.xiaopan.sketch.decode;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 对读到内存后又再次处理过的图片进行缓存，下次就不用再处理了，可加快加载速度
 */
public class ProcessedImageCache implements Identifier {

    /**
     * 判断是否可以使用此功能
     */
    public boolean canUse(LoadOptions loadOptions) {
        if (!loadOptions.isCacheProcessedImageInDisk()) {
            return false;
        }

        if (loadOptions.getMaxSize() != null || loadOptions.getResize() != null) {
            return true;
        }

        if (loadOptions.getImageProcessor() != null) {
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

    /**
     * 此缩放比例是否可以使用缓存到本地磁盘功能
     */
    public boolean canUseCacheProcessedImageInDisk(int inSampleSize) {
        return inSampleSize >= 8;
    }

    public boolean checkDiskCache(DiskCache diskCache, String processedImageDiskCacheKey) {
        ReentrantLock editLock = diskCache.getEditLock(processedImageDiskCacheKey);
        editLock.lock();

        boolean exist = diskCache.exist(processedImageDiskCacheKey);

        editLock.unlock();
        return exist;
    }

    /**
     * 开启了缓存已处理图片功能，如果磁盘缓存中已经有了缓存就直接读取
     */
    public ProcessedCacheDataSource getDiskCache(DiskCache diskCache, String processedImageDiskCacheKey) {
        ReentrantLock editLock = diskCache.getEditLock(processedImageDiskCacheKey);
        editLock.lock();

        DiskCache.Entry diskCacheEntry = diskCache.get(processedImageDiskCacheKey);

        editLock.unlock();

        if (diskCacheEntry != null) {
            return new ProcessedCacheDataSource(diskCacheEntry);
        }

        return null;
    }

    /**
     * 保存bitmap到磁盘缓存
     */
    public void saveToDiskCache(DiskCache diskCache, String processedImageDiskCacheKey, Bitmap bitmap) {
        ReentrantLock editLock = diskCache.getEditLock(processedImageDiskCacheKey);
        editLock.lock();

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

        editLock.unlock();
    }

    @Override
    public String getKey() {
        return "ProcessedImageCache";
    }
}
