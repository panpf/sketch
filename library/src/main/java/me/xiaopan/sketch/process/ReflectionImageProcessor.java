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
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;

import me.xiaopan.sketch.Resize;
import me.xiaopan.sketch.ResizeCalculator;
import me.xiaopan.sketch.Sketch;

/**
 * 倒影图片处理器
 */
public class ReflectionImageProcessor implements ImageProcessor {
    private static final String NAME = "ReflectionImageProcessor";
	private int reflectionSpacing;
	private float reflectionScale;

	/**
	 * 创建一个倒影图片处理器
	 * @param reflectionSpacing 倒影和图片之间的距离
	 * @param reflectionScale 倒影的高度所占原图高度比例，取值为0.0到1
	 */
	public ReflectionImageProcessor(int reflectionSpacing, float reflectionScale) {
		this.reflectionSpacing = reflectionSpacing;
		this.reflectionScale = reflectionScale;
	}
	
	public ReflectionImageProcessor(){
		this(2, 0.3f);
	}

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        builder.append(NAME)
                .append(" - ")
                .append("scale").append("=").append(reflectionScale)
                .append(", ")
                .append("spacing").append("=").append(reflectionSpacing);
        return builder;
    }

    @Override
    public Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if(bitmap == null){
            return null;
        }

        ResizeCalculator.Result result = sketch.getConfiguration().getResizeCalculator().calculator(bitmap.getWidth(), bitmap.getHeight(), resize != null ? resize.getWidth() : bitmap.getWidth(), resize != null ? resize.getHeight() : bitmap.getHeight(), resize != null ? resize.getScaleType() : null, forceUseResize);
        if(result == null){
            return bitmap;
        }

        Bitmap srcBitmap;
        if(bitmap.getWidth() == result.imageWidth && bitmap.getHeight() == result.imageHeight){
            srcBitmap = bitmap;
        }else{
            srcBitmap = Bitmap.createBitmap(result.imageWidth, result.imageHeight, lowQualityImage ? Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(srcBitmap);
            canvas.drawBitmap(bitmap, result.srcRect, result.destRect, null);
        }

        // 初始化画布
        Bitmap bitmapWithReflection = Bitmap.createBitmap(result.imageWidth, (int) (result.imageHeight+reflectionSpacing+(result.imageHeight*reflectionScale)), lowQualityImage ? Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);

        // 在上半部分绘制原图
        canvas.drawBitmap(srcBitmap, 0, 0, null);

        // 在下半部分绘制倒影
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, false);
        if(srcBitmap != bitmap){
            srcBitmap.recycle();
        }
        canvas.drawBitmap(reflectionImage, 0, result.imageHeight+reflectionSpacing, null);
        reflectionImage.recycle();

        // 在下半部分绘制半透明遮罩
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, result.imageHeight+reflectionSpacing, 0, bitmapWithReflection.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, result.imageHeight + reflectionSpacing, bitmapWithReflection.getWidth(), bitmapWithReflection.getHeight(), paint);

        return bitmapWithReflection;
    }

    public float getReflectionScale() {
        return reflectionScale;
    }

    public void setReflectionScale(float reflectionScale) {
        this.reflectionScale = reflectionScale;
    }

    public int getReflectionSpacing() {
        return reflectionSpacing;
    }

    public void setReflectionSpacing(int reflectionSpacing) {
        this.reflectionSpacing = reflectionSpacing;
    }
}
