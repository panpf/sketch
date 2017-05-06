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

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.drawable.SketchGifFactory;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.MaxSize;

public class ProcessedCacheDataSource implements DataSource {
    protected String logName = "ProcessedCacheFileDataSource";

    private DiskCache.Entry diskCacheEntry;
    private LoadRequest loadRequest;

    public ProcessedCacheDataSource(DiskCache.Entry diskCacheEntry, LoadRequest loadRequest) {
        this.diskCacheEntry = diskCacheEntry;
        this.loadRequest = loadRequest;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return diskCacheEntry.newInputStream();
    }

    @Override
    public SketchGifDrawable makeGifDrawable(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool) {
        try {
            return SketchGifFactory.createGifDrawable(key, uri, imageAttrs, bitmapPool, new RandomAccessFile(diskCacheEntry.getFile().getPath(), "r").getFD());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ImageFrom getImageFrom() {
        return ImageFrom.DISK_CACHE;
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, int outWidth, int outHeight, String outMimeType, int inSampleSize) {
        if (SLogType.REQUEST.isEnabled()) {
            if (bitmap != null && loadRequest.getOptions().getMaxSize() != null) {
                MaxSize maxSize = loadRequest.getOptions().getMaxSize();
                ImageSizeCalculator sizeCalculator = loadRequest.getConfiguration().getImageSizeCalculator();
                SLog.d(SLogType.REQUEST, logName, "decodeSuccess. originalSize=%dx%d, targetSize=%dx%d, " +
                                "targetSizeScale=%s, inSampleSize=%d, finalSize=%dx%d. %s",
                        outWidth, outHeight, maxSize.getWidth(), maxSize.getHeight(),
                        sizeCalculator.getTargetSizeScale(), inSampleSize, bitmap.getWidth(), bitmap.getHeight(), loadRequest.getKey());
            } else {
                SLog.d(SLogType.REQUEST, logName, "decodeSuccess. unchanged. %s", loadRequest.getKey());
            }
        }
    }

    @Override
    public void onDecodeError() {
        if (SLogType.REQUEST.isEnabled()) {
            SLog.e(SLogType.REQUEST, logName, "decode failed. diskCacheKey=%s. %s", diskCacheEntry.getUri(), loadRequest.getKey());
        }

        if (!diskCacheEntry.delete()) {
            if (SLogType.REQUEST.isEnabled()) {
                SLog.e(SLogType.REQUEST, logName, "delete image disk cache file failed. diskCacheKey=%s. %s",
                        diskCacheEntry.getUri(), loadRequest.getKey());
            }
        }
    }
}
