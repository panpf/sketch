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

package me.xiaopan.sketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

public class LoadingImageHolder implements ImageHolder{
    private int resId;
    private ImageProcessor imageProcessor;
    private Resize resize;
    private boolean imagesOfLowQuality;
    private String memoryCacheId;
    private RecycleBitmapDrawable drawable;

    public LoadingImageHolder(int resId) {
        this.resId = resId;
    }

    public LoadingImageHolder(int resId, ImageProcessor imageProcessor) {
        this.resId = resId;
        this.imageProcessor = imageProcessor;
    }

    public LoadingImageHolder(int resId, ImageProcessor imageProcessor, Resize resize) {
        this.resId = resId;
        this.resize = resize;
        this.imageProcessor = imageProcessor;
    }

    public LoadingImageHolder(int resId, ImageProcessor imageProcessor, Resize resize, boolean imagesOfLowQuality) {
        this.resId = resId;
        this.resize = resize;
        this.imageProcessor = imageProcessor;
        this.imagesOfLowQuality = imagesOfLowQuality;
    }

    protected RecycleBitmapDrawable getRecycleBitmapDrawable(Context context){
        if(drawable != null && !drawable.isRecycled()){
            return drawable;
        }

        // 从内存缓存中取
        if(memoryCacheId == null){
            memoryCacheId = generateMemoryCacheId(resId, resize, imagesOfLowQuality, imageProcessor);
        }
        MemoryCache lruMemoryCache = Sketch.with(context).getConfiguration().getPlaceholderImageMemoryCache();
        RecycleBitmapDrawable newDrawable = (RecycleBitmapDrawable) lruMemoryCache.get(memoryCacheId);
        if(newDrawable != null && !newDrawable.isRecycled()){
            this.drawable = newDrawable;
            return drawable;
        }

        if(newDrawable != null){
            lruMemoryCache.remove(memoryCacheId);
        }

        // 创建新的图片
        Bitmap bitmap;
        boolean tempImagesOfLowQuality = this.imagesOfLowQuality;
        if(Sketch.with(context).getConfiguration().isImagesOfLowQuality()){
            tempImagesOfLowQuality = true;
        }
        boolean canRecycle = false;

        Drawable resDrawable = context.getResources().getDrawable(resId);
        if(resDrawable != null && resDrawable instanceof BitmapDrawable){
            bitmap = ((BitmapDrawable) resDrawable).getBitmap();
        }else{
            bitmap = SketchUtils.drawableToBitmap(resDrawable, tempImagesOfLowQuality);
            canRecycle = true;
        }

        if(bitmap != null && !bitmap.isRecycled() && imageProcessor != null){
            Bitmap newBitmap = imageProcessor.process(bitmap, resize, tempImagesOfLowQuality);
            if(newBitmap != bitmap){
                if(canRecycle){
                    bitmap.recycle();
                }
                bitmap = newBitmap;
                canRecycle = true;
            }
        }

        if(bitmap != null && !bitmap.isRecycled()){
            newDrawable = new RecycleBitmapDrawable(bitmap);
            newDrawable.setAllowRecycle(canRecycle);
            if(canRecycle){
                lruMemoryCache.put(memoryCacheId, newDrawable);
            }
            drawable = newDrawable;
        }

        return drawable;
    }

    protected String generateMemoryCacheId(int resId, Resize resize, boolean imagesOfLowQuality, ImageProcessor imageProcessor){
        StringBuilder builder = new StringBuilder();
        builder.append(resId);
        if(resize != null){
            builder.append("_");
            resize.appendIdentifier(builder);
        }
        if(imagesOfLowQuality){
            builder.append("_LowQuality");
        }
        if(imageProcessor != null){
            builder.append("_");
            imageProcessor.appendIdentifier(builder);
        }
        return builder.toString();
    }

    @Override
    public FixedRecycleBitmapDrawable getFixedRecycleBitmapDrawable(Context context, FixedSize fixedSize){
        RecycleBitmapDrawable recycleBitmapDrawable = getRecycleBitmapDrawable(context);
        if(recycleBitmapDrawable != null){
            FixedRecycleBitmapDrawable fixedRecycleBitmapDrawable = new FixedRecycleBitmapDrawable(recycleBitmapDrawable);
            if(fixedSize != null){
                fixedRecycleBitmapDrawable.setFixedSize(fixedSize);
            }
            return fixedRecycleBitmapDrawable;
        }
        return null;
    }

    @Override
    public BindFixedRecycleBitmapDrawable getBindFixedRecycleBitmapDrawable(Context context, FixedSize fixedSize, DisplayRequest displayRequest){
        RecycleBitmapDrawable recycleBitmapDrawable = getRecycleBitmapDrawable(context);
        BindFixedRecycleBitmapDrawable bindFixedRecycleBitmapDrawable = new BindFixedRecycleBitmapDrawable(recycleBitmapDrawable, displayRequest);
        if(fixedSize != null){
            bindFixedRecycleBitmapDrawable.setFixedSize(fixedSize);
        }
        return bindFixedRecycleBitmapDrawable;
    }
}