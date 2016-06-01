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
import android.graphics.Point;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.drawable.RecycleGifDrawable;
import me.xiaopan.sketch.request.LoadRequest;
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
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            inputStream = diskCacheEntry.newInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SketchUtils.close(inputStream);
        }

        return bitmap;
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
        if (Sketch.isDebugMode()) {
            StringBuilder builder = new StringBuilder(logName)
                    .append(" - ").append("decodeSuccess");
            if (bitmap != null && loadRequest.getOptions().getMaxSize() != null) {
                builder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                builder.append(", ").append("targetSize").append("=").append(loadRequest.getOptions().getMaxSize().getWidth()).append("x").append(loadRequest.getOptions().getMaxSize().getHeight());
                builder.append(", ").append("targetSizeScaleInSampleSize").append("=").append(loadRequest.getSketch().getConfiguration().getImageSizeCalculator().getTargetSizeScaleInSampleSize());
                builder.append(", ").append("inSampleSize").append("=").append(inSampleSize);
                builder.append(", ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
            } else {
                builder.append(" - ").append("unchanged");
            }
            builder.append(" - ").append(loadRequest.getAttrs().getId());
            Log.d(Sketch.TAG, builder.toString());
        }
    }

    @Override
    public void onDecodeFailed() {
        if (Sketch.isDebugMode()) {
            Log.e(Sketch.TAG, SketchUtils.concat(logName, " - ", "decode failed", " - ", "diskCacheKey", "=", diskCacheEntry.getUri(), ", ", " - ", loadRequest.getAttrs().getId()));
        }
        if (!diskCacheEntry.delete()) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName, " - ", "delete damaged disk cache file failed", " - ", "diskCacheKey", "=", diskCacheEntry.getUri(), ", ", " - ", loadRequest.getAttrs().getId()));
            }
        }
    }

    @Override
    public RecycleGifDrawable getGifDrawable() {
        try {
            return new RecycleGifDrawable(new RandomAccessFile(diskCacheEntry.getFile().getPath(), "r").getFD());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
