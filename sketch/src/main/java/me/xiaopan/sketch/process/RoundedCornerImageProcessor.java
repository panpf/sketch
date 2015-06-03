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
import android.graphics.RectF;

import me.xiaopan.sketch.Resize;
import me.xiaopan.sketch.ResizeCalculator;
import me.xiaopan.sketch.Sketch;

/**
 * 圆角图片处理器
 */
public class RoundedCornerImageProcessor implements ImageProcessor {
    private static final String NAME = "RoundedCornerImageProcessor";

    private int roundPixels;

	/**
	 * 创建一个圆角图片显示器
	 * @param roundPixels 圆角度数
	 */
	public RoundedCornerImageProcessor(int roundPixels){
		this.roundPixels = roundPixels;
	}
	
	/**
	 * 创建一个圆角图片显示器，圆角角度默认为18
	 */
	public RoundedCornerImageProcessor(){
		this(18);
	}

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME).append(" - ").append("roundPixels").append("=").append(roundPixels);
    }

    @Override
    public Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if(bitmap == null || bitmap.isRecycled()){
            return null;
        }

        ResizeCalculator.Result result = sketch.getConfiguration().getResizeCalculator().calculator(bitmap.getWidth(), bitmap.getHeight(), resize != null ? resize.getWidth() : bitmap.getWidth(), resize != null ? resize.getHeight() : bitmap.getHeight(), resize != null ? resize.getScaleType() : null, forceUseResize);
        if(result == null){
            return bitmap;
        }

        Bitmap output = Bitmap.createBitmap(result.imageWidth, result.imageHeight, lowQualityImage?Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);

        // 绘制圆角的罩子
        canvas.drawRoundRect(new RectF(0, 0, result.imageWidth, result.imageHeight), roundPixels, roundPixels, paint);

        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, result.srcRect, result.destRect, paint);
        return output;
	}

    public int getRoundPixels() {
		return roundPixels;
	}

    public void setRoundPixels(int roundPixels) {
        this.roundPixels = roundPixels;
    }
}
