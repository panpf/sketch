/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.state;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.drawable.RefBitmapDrawable;
import me.xiaopan.sketch.drawable.ShapeBitmapDrawable;
import me.xiaopan.sketch.SketchMonitor;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.process.ResizeImageProcessor;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketch.request.ShapeSize;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.shaper.ImageShaper;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 可以利用Options中配置的ImageProcessor和resize修改原图片，同样支持ShapeSize和ImageShaper
 */
@SuppressWarnings("unused")
public class MakerStateImage implements StateImage {
    private int resId;

    public MakerStateImage(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    @Override
    public Drawable getDrawable(Context context, ImageViewInterface imageViewInterface, DisplayOptions displayOptions) {
        Drawable drawable = makeDrawable(Sketch.with(context), displayOptions);

        ShapeSize shapeSize = displayOptions.getShapeSize();
        ImageShaper imageShaper = displayOptions.getImageShaper();
        if ((shapeSize != null || imageShaper != null) && drawable != null
                && drawable instanceof BitmapDrawable) {
            drawable = new ShapeBitmapDrawable(context, (BitmapDrawable) drawable, shapeSize, imageShaper);
        }

        return drawable;
    }

    private Drawable makeDrawable(Sketch sketch, DisplayOptions displayOptions) {
        Configuration configuration = sketch.getConfiguration();

        ImageProcessor processor = displayOptions.getImageProcessor();
        Resize resize = displayOptions.getResize();
        BitmapPool bitmapPool = configuration.getBitmapPool();

        // 不需要处理的时候直接取出图片返回
        if (processor == null && resize == null) {
            return configuration.getContext().getResources().getDrawable(resId);
        }

        // 从内存缓存中取
        String memoryCacheKey = SketchUtils.makeStateImageRequestId(String.valueOf(resId), displayOptions);
        MemoryCache memoryCache = configuration.getMemoryCache();
        RefBitmap cachedRefBitmap = memoryCache.get(memoryCacheKey);
        if (cachedRefBitmap != null) {
            if (cachedRefBitmap.isRecycled()) {
                memoryCache.remove(memoryCacheKey);
            } else {
                return new RefBitmapDrawable(cachedRefBitmap);
            }
        }

        // 读取图片
        Bitmap bitmap;
        boolean allowRecycle = false;
        boolean tempLowQualityImage = configuration.isGlobalLowQualityImage() || displayOptions.isLowQualityImage();
        //noinspection deprecation
        Drawable drawable = configuration.getContext().getResources().getDrawable(resId);
        if (drawable != null && drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = SketchUtils.drawableToBitmap(drawable, tempLowQualityImage, bitmapPool);
            allowRecycle = true;
        }
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        // 处理图片
        //noinspection ConstantConditions
        if (processor == null && resize != null) {
            processor = sketch.getConfiguration().getResizeImageProcessor();
            if (processor == null) {
                processor = new ResizeImageProcessor();
            }
        }
        Bitmap newBitmap;
        try {
            newBitmap = processor.process(sketch, bitmap, resize,
                    displayOptions.isForceUseResize(), tempLowQualityImage);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            SketchMonitor sketchMonitor = sketch.getConfiguration().getMonitor();
            sketchMonitor.onProcessImageError(e, UriScheme.DRAWABLE.createUri(String.valueOf(resId)), processor);
            if (allowRecycle) {
                SketchUtils.freeBitmapToPool(bitmap, bitmapPool);
            }
            return null;
        }

        // bitmap变化了，说明创建了一张全新的图片，那么就要回收掉旧的图片
        if (newBitmap != bitmap) {
            if (allowRecycle) {
                SketchUtils.freeBitmapToPool(bitmap, bitmapPool);
            }

            // 新图片不能用说你处理部分出现异常了，直接返回null即可
            if (newBitmap == null || newBitmap.isRecycled()) {
                return null;
            }

            bitmap = newBitmap;
            allowRecycle = true;
        }

        // 允许回收说明是创建了一张新的图片，不能回收说明还是从res中获取的BitmapDrawable可以直接使用
        if (allowRecycle) {
            RefBitmap newRefBitmap = new RefBitmap(bitmap, bitmapPool, memoryCacheKey,
                    String.valueOf(resId), bitmap.getWidth(), bitmap.getHeight(), null);
            memoryCache.put(memoryCacheKey, newRefBitmap);
            return new RefBitmapDrawable(newRefBitmap);
        } else {
            return drawable;
        }
    }
}