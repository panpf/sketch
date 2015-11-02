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

public class ImageHolder {
    private int resId;
    private Resize resize;
    private String memoryCacheId;
    private boolean lowQualityImage;
    private boolean forceUseResize;
    private ImageProcessor imageProcessor;
    private RecycleBitmapDrawable drawable;

    public ImageHolder(int resId) {
        this.resId = resId;
    }

    public boolean isForceUseResize() {
        return forceUseResize;
    }

    public ImageHolder setForceUseResize(boolean forceUseResize) {
        this.forceUseResize = forceUseResize;
        return this;
    }

    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    public ImageHolder setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
        return this;
    }

    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    public ImageHolder setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    public int getResId() {
        return resId;
    }

    public Resize getResize() {
        return resize;
    }

    public ImageHolder setResize(Resize resize) {
        this.resize = resize;
        return this;
    }

    public RecycleBitmapDrawable getRecycleBitmapDrawable(Context context){
        if(drawable != null && !drawable.isRecycled()){
            return drawable;
        }

        // 从内存缓存中取
        if(memoryCacheId == null){
            memoryCacheId = generateMemoryCacheId(resId, resize, forceUseResize, lowQualityImage, imageProcessor);
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
        boolean tempLowQualityImage = this.lowQualityImage;
        if(Sketch.with(context).getConfiguration().isLowQualityImage()){
            tempLowQualityImage = true;
        }
        boolean canRecycle = false;

        Drawable resDrawable = context.getResources().getDrawable(resId);
        if(resDrawable != null && resDrawable instanceof BitmapDrawable){
            bitmap = ((BitmapDrawable) resDrawable).getBitmap();
        }else{
            bitmap = SketchUtils.drawableToBitmap(resDrawable, tempLowQualityImage);
            canRecycle = true;
        }

        if(bitmap != null && !bitmap.isRecycled() && imageProcessor != null){
            Bitmap newBitmap = imageProcessor.process(Sketch.with(context), bitmap, resize, forceUseResize, tempLowQualityImage);
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

    protected String generateMemoryCacheId(int resId, Resize resize, boolean forceUseResize, boolean lowQualityImage, ImageProcessor imageProcessor){
        StringBuilder builder = new StringBuilder();
        builder.append(resId);
        if(resize != null){
            builder.append("_");
            resize.appendIdentifier(builder);
        }
        if(forceUseResize){
            builder.append("_");
            builder.append("forceUseResize");
        }
        if(lowQualityImage){
            builder.append("_");
            builder.append("lowQualityImage");
        }
        if(imageProcessor != null){
            builder.append("_");
            imageProcessor.appendIdentifier(builder);
        }
        return builder.toString();
    }
}