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

package me.xiaopan.spear.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.widget.ImageView.ScaleType;

import me.xiaopan.spear.ImageSize;

/**
 * 圆形位图处理器
 */
public class CircleImageProcessor implements ImageProcessor {
    private static final String NAME = "CircleImageProcessor";
    private static CircleImageProcessor instance;

    public static CircleImageProcessor getInstance() {
        if(instance == null){
            synchronized (CircleImageProcessor.class){
                if(instance == null){
                    instance = new CircleImageProcessor();
                }
            }
        }
        return instance;
    }

    private CircleImageProcessor(){

    }

    @Override
    public String getFlag() {
        return NAME;
    }

    @Override
    public Bitmap process(Bitmap bitmap, ImageSize resize, ScaleType scaleType) {
        if(bitmap == null) return null;
        if(resize == null) resize = new ImageSize(bitmap.getWidth(), bitmap.getHeight());

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int newBitmapWidth = resize.getWidth();
        int newBitmapHeight = resize.getHeight();

        // 当newBitmap size大于bitmap size时，再创建一张newBitmap size的图片就不太合适了，应该以bitmap size为准
        if(newBitmapWidth > bitmapWidth && newBitmapHeight > bitmapHeight){
            Rect rect = CutImageProcessor.findMappingRect(bitmapWidth, bitmapHeight, newBitmapWidth, newBitmapHeight);
            newBitmapWidth = rect.width();
            newBitmapHeight = rect.height();
        }

        // 初始化画布
        int diameter = newBitmapWidth<newBitmapHeight?newBitmapWidth:newBitmapHeight;
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);
        
        // 绘制圆形的罩子
        canvas.drawCircle(diameter/2, diameter/2, diameter/2, paint);
        
        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect dstRect = new Rect(0, 0, diameter, diameter);
        Rect srcRect = CutImageProcessor.findMappingRect(bitmapWidth, bitmapHeight, diameter, diameter);
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        return output;
    }
}
