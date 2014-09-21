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
import android.graphics.RectF;
import android.widget.ImageView.ScaleType;

import me.xiaopan.android.spear.util.ImageSize;

/**
 * 圆角位图处理器
 */
public class RoundedCornerImageProcessor implements ImageProcessor {
	private int roundPixels;
	
	/**
	 * 创建一个圆角位图显示器
	 * @param roundPixels 圆角度数
	 */
	public RoundedCornerImageProcessor(int roundPixels){
		this.roundPixels = roundPixels;
	}
	
	/**
	 * 创建一个圆角位图显示器，圆角角度默认为18
	 */
	public RoundedCornerImageProcessor(){
		this(18);
	}

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
        Bitmap output = Bitmap.createBitmap(resize.getWidth(), resize.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);
        
        // 绘制圆角的罩子
        canvas.drawRoundRect(new RectF(0, 0, resize.getWidth(), resize.getHeight()), roundPixels, roundPixels, paint);
        
        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect srcRect = CutImageProcessor.computeSrcRect(new Point(bitmap.getWidth(), bitmap.getHeight()), new Point(resize.getWidth(), resize.getHeight()), scaleType);
        Rect dstRect = new Rect(0, 0, resize.getWidth(), resize.getHeight());
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        return output;
	}
	
	public int getRoundPixels() {
		return roundPixels;
	}

	public void setRoundPixels(int roundPixels) {
		this.roundPixels = roundPixels;
	}
}
