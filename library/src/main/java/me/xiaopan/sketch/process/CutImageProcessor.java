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

package me.xiaopan.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.widget.ImageView;

import me.xiaopan.sketch.Resize;

public class CutImageProcessor implements ImageProcessor {
    private static final String NAME = "CutImageProcessor";

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }

    @Override
    public Bitmap process(Bitmap bitmap, Resize resize, boolean imagesOfLowQuality) {
        if(bitmap == null){
            return null;
        }
        if(resize == null){
            return bitmap;
        }
        ImageView.ScaleType scaleType = resize.getScaleType();
        if(scaleType == null){
            scaleType = ImageView.ScaleType.FIT_CENTER;
        }

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int newBitmapWidth = resize.getWidth();
        int newBitmapHeight = resize.getHeight();

        Rect srcRect = null;
        if(scaleType == ImageView.ScaleType.CENTER){
            if(!(newBitmapWidth >= bitmapWidth && newBitmapHeight >= bitmapHeight)){
                srcRect = findCutRect(bitmapWidth, bitmapHeight, newBitmapWidth, newBitmapHeight);
                newBitmapWidth = srcRect.width();
                newBitmapHeight = srcRect.height();
            }
        }else if(scaleType == ImageView.ScaleType.CENTER_CROP){
            if(!(((float)newBitmapWidth/newBitmapHeight == (float)bitmapWidth/bitmapHeight) && newBitmapWidth >= bitmapWidth)){
                srcRect = findMappingRect(bitmapWidth, bitmapHeight, newBitmapWidth, newBitmapHeight);
                if(bitmapWidth <= newBitmapWidth || bitmapHeight <= newBitmapHeight){
                    newBitmapWidth = srcRect.width();
                    newBitmapHeight = srcRect.height();
                }
            }
        }else if(scaleType == ImageView.ScaleType.CENTER_INSIDE || scaleType == ImageView.ScaleType.FIT_CENTER || scaleType == ImageView.ScaleType.FIT_END || scaleType == ImageView.ScaleType.FIT_START){
            if(!(newBitmapWidth > bitmapWidth && newBitmapHeight > bitmapHeight)){
                float widthScale = (float)bitmapWidth/newBitmapWidth;
                float heightScale = (float)bitmapHeight/newBitmapHeight;
                float finalScale = widthScale>heightScale?widthScale:heightScale;
                newBitmapWidth = (int)(bitmapWidth/finalScale);
                newBitmapHeight = (int)(bitmapHeight/finalScale);
                srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
            }
        }else if(scaleType == ImageView.ScaleType.FIT_XY || scaleType == ImageView.ScaleType.MATRIX){
        }

        if(srcRect == null){
            return bitmap;
        }else{
            Bitmap.Config newBitmapConfig = bitmap.getConfig();
            if(newBitmapConfig == null){
                newBitmapConfig = imagesOfLowQuality? Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888;
            }
            Bitmap newBitmap = Bitmap.createBitmap(newBitmapWidth, newBitmapHeight, newBitmapConfig);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(bitmap, srcRect, new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight()), null);
            return newBitmap;
        }
    }

    public static Rect findMappingRect(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight){
        float widthScale = (float)sourceWidth/targetWidth;
        float heightScale = (float)sourceHeight/targetHeight;
        float finalScale = widthScale<heightScale?widthScale:heightScale;
        int srcWidth = (int)(targetWidth*finalScale);
        int srcHeight = (int)(targetHeight*finalScale);
        int srcLeft = (sourceWidth - srcWidth)/2;
        int srcTop = (sourceHeight - srcHeight)/2;
        return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
    }

    public static Rect findCutRect(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight){
        int left;
        int right;
        if(sourceWidth > targetWidth){
            left = (sourceWidth-targetWidth)/2;
            right = left+targetWidth;
        }else{
            left = 0;
            right = sourceWidth;
        }

        int top;
        int bottom;
        if(sourceHeight > targetHeight){
            top = (sourceHeight-targetHeight)/2;
            bottom = top+targetHeight;
        }else{
            top = 0;
            bottom = sourceHeight;
        }
        return new Rect(left, top, right, bottom);
    }
}
