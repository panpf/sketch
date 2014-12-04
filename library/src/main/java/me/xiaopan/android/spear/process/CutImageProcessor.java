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

package me.xiaopan.android.spear.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.android.spear.util.ImageSize;

public class CutImageProcessor implements ImageProcessor {

    @Override
    public Bitmap process(Bitmap bitmap, ImageSize resize, ImageView.ScaleType scaleType) {
        if(bitmap == null) return null;
        if(resize == null) return bitmap;
        if(scaleType == null) scaleType = ImageView.ScaleType.FIT_CENTER;

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int resizeWidth = resize.getWidth();
        int resizeHeight = resize.getHeight();

        if(scaleType == ImageView.ScaleType.CENTER){
            if(resizeWidth >= bitmapWidth && resizeHeight >= bitmapHeight){
                return bitmap;
            }else{
                Rect srcRect = findCutRect(bitmapWidth, bitmapHeight, resizeWidth, resizeHeight, Offset.CENTER);
                return cut(bitmap, srcRect.width(), srcRect.height(), srcRect);
            }
        }else if(scaleType == ImageView.ScaleType.CENTER_CROP){
            if(((float)resizeWidth/resizeHeight == (float)bitmapWidth/bitmapHeight) && resizeWidth >= bitmapWidth){
                return bitmap;
            }

            Rect srcRect = findMappingRect(bitmapWidth, bitmapHeight, resizeWidth, resizeHeight, Offset.CENTER);
            int newBitmapWidth;
            int newBitmapHeight;
            if(bitmapWidth > resizeWidth && bitmapHeight > resizeHeight){
                newBitmapWidth = resizeWidth;
                newBitmapHeight = resizeHeight;
            }else{
                newBitmapWidth = srcRect.width();
                newBitmapHeight = srcRect.height();
            }
            return cut(bitmap, newBitmapWidth, newBitmapHeight, srcRect);
        }else if(scaleType == ImageView.ScaleType.CENTER_INSIDE
                || scaleType == ImageView.ScaleType.FIT_CENTER
                || scaleType == ImageView.ScaleType.FIT_END
                || scaleType == ImageView.ScaleType.FIT_START){
            if(resizeWidth > bitmapWidth && resizeHeight > bitmapHeight){
                return bitmap;
            }else{
                float widthScale = (float)bitmapWidth/resizeWidth;
                float heightScale = (float)bitmapHeight/resizeHeight;
                float finalScale = widthScale>heightScale?widthScale:heightScale;
                return Bitmap.createScaledBitmap(bitmap, (int)(bitmapWidth/finalScale), (int)(bitmapHeight/finalScale), true);
            }
        }else if(scaleType == ImageView.ScaleType.FIT_XY
                || scaleType == ImageView.ScaleType.MATRIX){
            return bitmap;
        }else{
            return bitmap;
        }
    }

    public static Rect findMappingRect(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight, Offset offset){
        float widthScale = (float)sourceWidth/targetWidth;
        float heightScale = (float)sourceHeight/targetHeight;
        float scale = widthScale<heightScale?widthScale:heightScale;
        int srcLeft;
        int srcTop;
        int srcWidth = (int)(targetWidth*scale);
        int srcHeight = (int)(targetHeight*scale);
        if (offset == null || offset == Offset.START) {
            srcLeft = 0;
            srcTop = 0;
        } else if (offset == Offset.END) {
            if(sourceWidth > sourceHeight){
                srcLeft = sourceWidth - srcWidth;
                srcTop = 0;
            }else{
                srcLeft = 0;
                srcTop = sourceHeight - srcHeight;
            }
        } else {
            if(sourceWidth > sourceHeight){
                srcLeft = (sourceWidth - srcWidth)/2;
                srcTop = 0;
            }else{
                srcLeft = 0;
                srcTop = (sourceHeight - srcHeight)/2;
            }
        }
        return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
    }

    public static Rect findCutRect(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight, Offset offset){
        int left;
        int right;
        if(sourceWidth > targetWidth){
            if(offset == null || offset == Offset.START){
                left = 0;
            }else if(offset == Offset.END){
                left = sourceWidth-targetWidth;
            }else{
                left = (sourceWidth-targetWidth)/2;
            }
            right = left+targetWidth;
        }else{
            left = 0;
            right = sourceWidth;
        }

        int top;
        int bottom;
        if(sourceWidth > targetHeight){
            if(offset == null || offset == Offset.START){
                top = 0;
            }else if(offset == Offset.END){
                top = sourceHeight-targetHeight;
            }else{
                top = (sourceHeight-targetHeight)/2;
            }
            bottom = top+targetHeight;
        }else{
            top = 0;
            bottom = sourceHeight;
        }
        return new Rect(left, top, right, bottom);
    }

    public static Bitmap cut(Bitmap sourceBitmap, int newBitmapWidth, int newBitmapHeight, Rect srcRect){
        Bitmap newBitmap = Bitmap.createBitmap(newBitmapWidth, newBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(sourceBitmap, srcRect, new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight()), null);
        return newBitmap;
    }

    /**
     * 计算影射区域
     * @param sourceSize 原尺寸
     * @param targetSize 目标尺寸
     * @param scaleType 显示方式
     * @return 影射区域
     */
    @Deprecated
    public static Rect computeSrcRect(Point sourceSize, Point targetSize, ImageView.ScaleType scaleType){
        if(scaleType == ImageView.ScaleType.CENTER_INSIDE || scaleType == ImageView.ScaleType.MATRIX || scaleType == ImageView.ScaleType.FIT_XY){
            return new Rect(0, 0, sourceSize.x, sourceSize.y);
        }else{
            float scale;
            if(Math.abs(sourceSize.x - targetSize.x) < Math.abs(sourceSize.y - targetSize.y)){
                scale = (float) sourceSize.x/targetSize.x;
                if((int)(targetSize.y*scale) > sourceSize.y){
                    scale = (float) sourceSize.y/targetSize.y;
                }
            }else{
                scale = (float) sourceSize.y/targetSize.y;
                if((int)(targetSize.x*scale) > sourceSize.x){
                    scale = (float) sourceSize.x/targetSize.x;
                }
            }
            int srcLeft;
            int srcTop;
            int srcWidth = (int)(targetSize.x*scale);
            int srcHeight = (int)(targetSize.y*scale);
            if (scaleType == ImageView.ScaleType.FIT_START) {
                srcLeft = 0;
                srcTop = 0;
            } else if (scaleType == ImageView.ScaleType.FIT_END) {
                if(sourceSize.x > sourceSize.y){
                    srcLeft = sourceSize.x - srcWidth;
                    srcTop = 0;
                }else{
                    srcLeft = 0;
                    srcTop = sourceSize.y - srcHeight;
                }
            } else {
                if(sourceSize.x > sourceSize.y){
                    srcLeft = (sourceSize.x - srcWidth)/2;
                    srcTop = 0;
                }else{
                    srcLeft = 0;
                    srcTop = (sourceSize.y - srcHeight)/2;
                }
            }
            return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
        }
    }

    public enum Offset{
        START,
        CENTER,
        END,
    }
}
