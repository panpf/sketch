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

package me.xiaopan.sketch.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import me.xiaopan.sketch.LogType;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.MaxSize;
import me.xiaopan.sketch.util.SketchUtils;

public class CacheFileDecodeHelper implements DecodeHelper {
    protected String logName = "CacheFileDecodeHelper";

    private DiskCache.Entry diskCacheEntry;
    private LoadRequest loadRequest;

    public CacheFileDecodeHelper(DiskCache.Entry diskCacheEntry, LoadRequest loadRequest) {
        this.diskCacheEntry = diskCacheEntry;
        this.loadRequest = loadRequest;
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        InputStream inputStream;
        try {
            inputStream = diskCacheEntry.newInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        SketchUtils.close(inputStream);
        return bitmap;
    }

    @Override
    public Bitmap decodeRegion(Rect srcRect, BitmapFactory.Options options) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1) {
            return null;
        }

        InputStream inputStream;
        try {
            inputStream = diskCacheEntry.newInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        BitmapRegionDecoder regionDecoder;
        try {
            regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            SketchUtils.close(inputStream);
        }

        Bitmap bitmap = regionDecoder.decodeRegion(srcRect, options);
        regionDecoder.recycle();
        SketchUtils.close(inputStream);
        return bitmap;
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, int outWidth, int outHeight, String outMimeType, int inSampleSize) {
        if (LogType.REQUEST.isEnabled()) {
            if (bitmap != null && loadRequest.getOptions().getMaxSize() != null) {
                MaxSize maxSize = loadRequest.getOptions().getMaxSize();
                ImageSizeCalculator sizeCalculator = loadRequest.getSketch().getConfiguration().getImageSizeCalculator();
                SLog.d(LogType.REQUEST, logName, "decodeSuccess. originalSize=%dx%d, targetSize=%dx%d, " +
                                "targetSizeScale=%s, inSampleSize=%d, finalSize=%dx%d. %s",
                        outWidth, outHeight, maxSize.getWidth(), maxSize.getHeight(),
                        sizeCalculator.getTargetSizeScale(), inSampleSize, bitmap.getWidth(), bitmap.getHeight(), loadRequest.getId());
            } else {
                SLog.d(LogType.REQUEST, logName, "decodeSuccess. unchanged. %s", loadRequest.getId());
            }
        }
    }

    @Override
    public void onDecodeError() {
        if (LogType.REQUEST.isEnabled()) {
            SLog.e(LogType.REQUEST, logName, "decode failed. diskCacheKey=%s. %s", diskCacheEntry.getUri(), loadRequest.getId());
        }

        if (!diskCacheEntry.delete()) {
            if (LogType.REQUEST.isEnabled()) {
                SLog.e(LogType.REQUEST, logName, "delete image disk cache file failed. diskCacheKey=%s. %s",
                        diskCacheEntry.getUri(), loadRequest.getId());
            }
        }
    }

    @Override
    public SketchGifDrawable getGifDrawable(BitmapPool bitmapPool) {
        try {
            return new SketchGifDrawable(bitmapPool, new RandomAccessFile(diskCacheEntry.getFile().getPath(), "r").getFD());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
