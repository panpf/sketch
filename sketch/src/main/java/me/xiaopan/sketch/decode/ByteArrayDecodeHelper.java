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

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.drawable.ImageInfo;
import me.xiaopan.sketch.drawable.SketchGifDrawableImpl;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.MaxSize;

public class ByteArrayDecodeHelper implements DecodeHelper {
    protected String logName = "ByteArrayDecodeHelper";

    private byte[] data;
    private LoadRequest loadRequest;

    public ByteArrayDecodeHelper(byte[] data, LoadRequest loadRequest) {
        this.data = data;
        this.loadRequest = loadRequest;
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    @Override
    public Bitmap decodeRegion(Rect srcRect, BitmapFactory.Options options) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1) {
            return null;
        }

        BitmapRegionDecoder regionDecoder;
        try {
            regionDecoder = BitmapRegionDecoder.newInstance(data, 0, data.length, false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Bitmap bitmap = regionDecoder.decodeRegion(srcRect, options);
        regionDecoder.recycle();
        return bitmap;
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, int outWidth, int outHeight, String outMimeType, int inSampleSize) {
        if (SLogType.REQUEST.isEnabled()) {
            if (bitmap != null && loadRequest.getOptions().getMaxSize() != null) {
                MaxSize maxSize = loadRequest.getOptions().getMaxSize();
                ImageSizeCalculator sizeCalculator = loadRequest.getSketch().getConfiguration().getImageSizeCalculator();
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
            SLog.e(SLogType.REQUEST, logName, "decode failed. %s", loadRequest.getKey());
        }
    }

    @Override
    public SketchGifDrawableImpl makeGifDrawable(ImageInfo imageInfo, BitmapPool bitmapPool) {
        try {
            return new SketchGifDrawableImpl(imageInfo, bitmapPool, data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
