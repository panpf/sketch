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

import me.xiaopan.sketch.LoadRequest;
import me.xiaopan.sketch.RecycleGifDrawable;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

public class DrawableDecodeHelper implements DecodeHelper {
    private static final String NAME = "DrawableDecodeHelper";
    private int drawableId;
    private LoadRequest loadRequest;

    public DrawableDecodeHelper(int drawableId, LoadRequest loadRequest) {
        this.drawableId = drawableId;
        this.loadRequest = loadRequest;
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        return BitmapFactory.decodeResource(loadRequest.getAttrs().getSketch().getConfiguration().getContext().getResources(), drawableId, options);
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
        if (Sketch.isDebugMode()) {
            StringBuilder builder = new StringBuilder(NAME)
                    .append(" - ").append("decodeSuccess");
            if (bitmap != null && loadRequest.getOptions().getMaxSize() != null) {
                builder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                builder.append(", ").append("targetSize").append("=").append(loadRequest.getOptions().getMaxSize().getWidth()).append("x").append(loadRequest.getOptions().getMaxSize().getHeight());
                builder.append(", ").append("targetSizeScaleInSampleSize").append("=").append(loadRequest.getAttrs().getSketch().getConfiguration().getImageSizeCalculator().getTargetSizeScaleInSampleSize());
                builder.append(",  ").append("inSampleSize").append("=").append(inSampleSize);
                builder.append(",  ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
            } else {
                builder.append(" - ").append("unchanged");
            }
            builder.append(" - ").append(loadRequest.getAttrs().getName());
            Log.d(Sketch.TAG, builder.toString());
        }
    }

    @Override
    public void onDecodeFailed() {
        if (Sketch.isDebugMode()) {
            Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "decode failed", " - ", String.valueOf(drawableId)));
        }
    }

    @Override
    public RecycleGifDrawable getGifDrawable() {
        try {
            return new RecycleGifDrawable(loadRequest.getAttrs().getSketch().getConfiguration().getContext().getResources(), drawableId);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
