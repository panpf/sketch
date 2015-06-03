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
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import me.xiaopan.sketch.Resize;
import me.xiaopan.sketch.ResizeCalculator;
import me.xiaopan.sketch.Sketch;

/**
 * 圆形图片处理器
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
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        builder.append(NAME);
        return builder;
    }

    @Override
    public Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if(bitmap == null || bitmap.isRecycled()){
            return null;
        }

        int targetWidth = resize != null?resize.getWidth():bitmap.getWidth();
        int targetHeight = resize != null?resize.getHeight():bitmap.getHeight();
        int newBitmapSize = targetWidth < targetHeight ? targetWidth : targetHeight;

        ResizeCalculator.Result result = sketch.getConfiguration().getResizeCalculator().calculator(bitmap.getWidth(), bitmap.getHeight(), newBitmapSize, newBitmapSize, resize != null ? resize.getScaleType() : null, forceUseResize);
        if(result == null){
            return bitmap;
        }

        // 初始化画布
        Bitmap output = Bitmap.createBitmap(result.imageWidth, result.imageHeight, lowQualityImage ? Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);
        
        // 绘制圆形的罩子
        canvas.drawCircle(result.imageWidth/2, result.imageHeight/2, (result.imageWidth<result.imageHeight?result.imageWidth:result.imageHeight)/2, paint);
        
        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, result.srcRect, result.destRect, paint);

        return output;
    }
}
