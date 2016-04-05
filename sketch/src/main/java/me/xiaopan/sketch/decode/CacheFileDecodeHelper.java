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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import me.xiaopan.sketch.LoadRequest;
import me.xiaopan.sketch.RecycleGifDrawable;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

public class CacheFileDecodeHelper implements DecodeHelper {
    private static final String NAME = "CacheFileDecodeHelper";
    private File file;
    private LoadRequest loadRequest;

    public CacheFileDecodeHelper(File file, LoadRequest loadRequest) {
        this.file = file;
        this.loadRequest = loadRequest;
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        if (!file.canRead()) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "can not read", " - ", file.getPath()));
            }
            return null;
        }

        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
        if (Sketch.isDebugMode()) {
            StringBuilder stringBuilder = new StringBuilder(NAME)
                    .append(" - ").append("decodeSuccess");
            if (bitmap != null && loadRequest.getMaxSize() != null) {
                stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                stringBuilder.append(", ").append("inSampleSize").append("=").append(inSampleSize);
                stringBuilder.append(", ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
            } else {
                stringBuilder.append(" - ").append("unchanged");
            }
            stringBuilder.append(" - ").append(loadRequest.getName());
            Log.d(Sketch.TAG, stringBuilder.toString());
        }
    }

    @Override
    public void onDecodeFailed() {
        if (Sketch.isDebugMode()) {
            StringBuilder logContent = new StringBuilder(NAME);
            logContent.append(" - ").append("decode failed");
            logContent.append(", ").append("filePath").append("=").append(file.getPath());
            if (file.exists()) {
                logContent.append(",  ").append("fileLength").append("=").append(file.length());
            }
            logContent.append(",  ").append("imageUri").append("=").append(loadRequest.getUri());
            Log.e(Sketch.TAG, logContent.toString());
        }
        if (!file.delete()) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "delete damaged disk cache file failed", " - ", file.getPath()));
            }
        }
    }

    @Override
    public RecycleGifDrawable getGifDrawable() {
        try {
            return new RecycleGifDrawable(new RandomAccessFile(file.getPath(), "r").getFD());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
