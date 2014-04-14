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

package me.xiaopan.android.imageloader.process;

import me.xiaopan.android.imageloader.util.ImageSize;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.widget.ImageView.ScaleType;

/**
 * 圆形位图处理器
 */
public class CircleBitmapProcessor implements BitmapProcessor {
    private static final String NAME = CircleBitmapProcessor.class.getSimpleName();

    @Override
    public String getTag() {
        return NAME;
    }

    @Override
    public BitmapProcessor copy() {
        return new CircleBitmapProcessor();
    }

    @Override
    public Bitmap process(Bitmap bitmap, ScaleType scaleType, ImageSize processSize) {
    	// 初始化参数
    	if (bitmap == null) return null;
        if (scaleType == null) scaleType = ScaleType.FIT_CENTER;
        if (processSize == null) processSize = new ImageSize(bitmap.getWidth(), bitmap.getHeight());
        int slidLlength = processSize.getWidth() > processSize.getHeight() ? processSize.getHeight() : processSize.getWidth();

        // 初始化画布
        Bitmap output = Bitmap.createBitmap(slidLlength, slidLlength, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);
        
        // 绘制圆形的罩子
        canvas.drawCircle(slidLlength / 2, slidLlength / 2, slidLlength / 2, paint);
        
        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect srcRect = new ComputeRect().compute(new Point(bitmap.getWidth(),  bitmap.getHeight()), new Point(slidLlength,  slidLlength), scaleType);
        Rect dstRect = new Rect(0, 0, slidLlength, slidLlength);
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        return output;
    }
}
