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

package me.xiaopan.sketch.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.drawable.FixedSizeRefBitmapDrawable;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.drawable.RefBitmapDrawable;
import me.xiaopan.sketch.feature.ExceptionMonitor;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.process.ResizeImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 可修改drawable资源
 */
public class MakerDrawableModeImage implements ModeImage {
    private int resId;

    private Resize resize;
    private boolean lowQualityImage;
    private boolean forceUseResize;
    private ImageProcessor imageProcessor;

    private String memoryCacheId;

    public MakerDrawableModeImage(int resId, ImageProcessor imageProcessor, Resize resize, boolean forceUseResize) {
        this.resId = resId;

        this.imageProcessor = imageProcessor;
        this.resize = resize;
        this.forceUseResize = forceUseResize;

        if (imageProcessor == null && resize == null) {
            throw new IllegalArgumentException("imageProcessor is null and resize is null");
        }
    }

    public MakerDrawableModeImage(int resId, ImageProcessor imageProcessor) {
        this(resId, imageProcessor, null, false);
    }

    @SuppressWarnings("unused")
    public MakerDrawableModeImage(int resId, Resize resize, boolean forceUseResize) {
        this(resId, null, resize, forceUseResize);
    }

    @SuppressWarnings("unused")
    public MakerDrawableModeImage(int resId, Resize resize) {
        this(resId, null, resize, false);
    }

    @SuppressWarnings("unused")
    public boolean isForceUseResize() {
        return forceUseResize;
    }

    @SuppressWarnings("unused")
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    @SuppressWarnings("unused")
    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    @SuppressWarnings("unused")
    public MakerDrawableModeImage setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    @SuppressWarnings("unused")
    public int getResId() {
        return resId;
    }

    public Resize getResize() {
        return resize;
    }

    protected String generateMemoryCacheId(int resId, Resize resize, boolean forceUseResize, boolean lowQualityImage, ImageProcessor imageProcessor) {
        StringBuilder builder = new StringBuilder();
        builder.append(resId);
        if (resize != null) {
            resize.appendIdentifier("_", builder);
        }
        if (forceUseResize) {
            builder.append("_").append("forceUseResize");
        }
        if (lowQualityImage) {
            builder.append("_").append("lowQualityImage");
        }
        if (imageProcessor != null) {
            imageProcessor.appendIdentifier("_", builder);
        }
        return builder.toString();
    }

    @Override
    public Drawable getDrawable(Context context, FixedSize fixedSize) {
        BitmapDrawable bitmapDrawable = makeDrawable(Sketch.with(context));
        if (bitmapDrawable != null && fixedSize != null) {
            return new FixedSizeRefBitmapDrawable(bitmapDrawable, fixedSize);
        } else {
            return bitmapDrawable;
        }
    }

    private BitmapDrawable makeDrawable(Sketch sketch) {
        // 从内存缓存中取
        if (memoryCacheId == null) {
            memoryCacheId = generateMemoryCacheId(resId, resize, forceUseResize, lowQualityImage, imageProcessor);
        }
        Configuration configuration = sketch.getConfiguration();
        MemoryCache modeImageMemoryCache = configuration.getModeImageMemoryCache();
        RefBitmap cachedRefBitmap = modeImageMemoryCache.get(memoryCacheId);
        if (cachedRefBitmap != null) {
            if (cachedRefBitmap.isRecycled()) {
                modeImageMemoryCache.remove(memoryCacheId);
            } else {
                return new RefBitmapDrawable(cachedRefBitmap);
            }
        }

        // 读取图片
        Bitmap bitmap;
        boolean allowRecycle = false;
        boolean tempLowQualityImage = configuration.isGlobalLowQualityImage() || this.lowQualityImage;
        Drawable drawable = configuration.getContext().getResources().getDrawable(resId);
        if (drawable != null && drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = SketchUtils.drawableToBitmap(drawable, tempLowQualityImage);
            allowRecycle = true;
        }
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        // 处理图片
        ImageProcessor finalImageProcessor = imageProcessor;
        if (finalImageProcessor == null && resize != null) {
            finalImageProcessor = sketch.getConfiguration().getResizeImageProcessor();
        }
        if (finalImageProcessor == null) {
            finalImageProcessor = new ResizeImageProcessor();
        }
        Bitmap newBitmap;
        try {
            newBitmap = finalImageProcessor.process(sketch, bitmap, resize, forceUseResize, tempLowQualityImage);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            ExceptionMonitor exceptionMonitor = sketch.getConfiguration().getExceptionMonitor();
            exceptionMonitor.onProcessImageFailed(e, UriScheme.DRAWABLE.createUri(String.valueOf(resId)), imageProcessor);
            if (allowRecycle) {
                bitmap.recycle();
            }
            return null;
        }

        // bitmap变化了，说明创建了一张全新的图片，那么就要回收掉旧的图片
        if (newBitmap != bitmap) {
            if (allowRecycle) {
                bitmap.recycle();
            }

            // 新图片不能用说你处理部分出现异常了，直接返回null即可
            if (newBitmap == null || newBitmap.isRecycled()) {
                return null;
            }

            bitmap = newBitmap;
            allowRecycle = true;
        }

        // 允许收说明是创建了一张新的图片，不能回收说明还是从res中获取的BitmapDrawable可以直接使用
        if (allowRecycle) {
            RefBitmap newRefBitmap = new RefBitmap(bitmap, memoryCacheId, String.valueOf(resId), bitmap.getWidth(), bitmap.getHeight(), null);
            newRefBitmap.setAllowRecycle(true);
            modeImageMemoryCache.put(memoryCacheId, newRefBitmap);
            return new RefBitmapDrawable(newRefBitmap);
        } else {
            return (BitmapDrawable) drawable;
        }
    }
}