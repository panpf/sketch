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
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.widget.ImageView.ScaleType;

import me.xiaopan.android.spear.util.ImageSize;

/**
 * 圆形位图处理器
 */
public class CircleImageProcessor implements ImageProcessor {

    @Override
    public Bitmap process(Bitmap bitmap, ImageSize resize, ScaleType scaleType) {
        if(bitmap == null){
            return null;
        }
        if (scaleType == null){
            scaleType = ScaleType.FIT_CENTER;
        }
        if (resize == null){
            resize = new ImageSize(bitmap.getWidth(), bitmap.getHeight());
        }

        // 如果新的尺寸大于等于原图的尺寸，就重新定义新的尺寸
        if((resize.getWidth() * resize.getHeight()) >= (bitmap.getWidth() * bitmap.getHeight())){
            Rect rect = CutImageProcessor.computeSrcRect(new Point(bitmap.getWidth(), bitmap.getHeight()), new Point(resize.getWidth(), resize.getHeight()), scaleType);
            resize = new ImageSize(rect.width(), rect.height());
        }

        // 初始化画布
        int slidLength = resize.getWidth() > resize.getHeight() ? resize.getHeight() : resize.getWidth();
        Bitmap output = Bitmap.createBitmap(slidLength, slidLength, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);
        
        // 绘制圆形的罩子
        canvas.drawCircle(slidLength / 2, slidLength / 2, slidLength / 2, paint);
        
        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect srcRect = CutImageProcessor.computeSrcRect(new Point(bitmap.getWidth(), bitmap.getHeight()), new Point(slidLength, slidLength), scaleType);
        Rect dstRect = new Rect(0, 0, slidLength, slidLength);
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        return output;
    }
}
