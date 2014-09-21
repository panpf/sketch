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
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.widget.ImageView.ScaleType;

import me.xiaopan.android.spear.util.ImageSize;

/**
 * 倒影位图处理器
 */
public class ReflectionImageProcessor implements ImageProcessor {
	private int reflectionSpacing;
	private float reflectionScale;
	
	/**
	 * 创建一个倒影位图处理器
	 * @param reflectionSpacing 倒影和图片之间的距离
	 * @param reflectionScale 倒影的高度所占原图高度比例
	 */
	public ReflectionImageProcessor(int reflectionSpacing, float reflectionScale) {
		this.reflectionSpacing = reflectionSpacing;
		this.reflectionScale = reflectionScale;
	}
	
	public ReflectionImageProcessor(){
		this(2, 0.3f);
	}

    @Override
    public Bitmap process(Bitmap bitmap, ImageSize resize, ScaleType scaleType) {
        if(bitmap == null){
            return null;
        }
        if (scaleType == null){
            scaleType = ScaleType.FIT_CENTER;
        }

        if(resize == null){
            return fullHandle(bitmap);
        }

        // 如果新的尺寸大于等于原图的尺寸，就重新定义新的尺寸
        if((resize.getWidth() * resize.getHeight()) >= (bitmap.getWidth() * bitmap.getHeight())){
            Rect rect = CutImageProcessor.computeSrcRect(new Point(bitmap.getWidth(), bitmap.getHeight()), new Point(resize.getWidth(), resize.getHeight()), scaleType);
            resize = new ImageSize(rect.width(), rect.height());
        }
        return cutHandle(bitmap, scaleType, resize);
	}
	
    private Bitmap fullHandle(Bitmap bitmap){
        // 初始化画布
        Bitmap bitmapWithReflection = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight() + (int)(bitmap.getHeight()*reflectionScale) + reflectionSpacing, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);

        // 在上半部分绘制原图
        canvas.drawBitmap(bitmap, 0, 0, null);

        // 在下半部分绘制倒影图片
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        canvas.drawBitmap(reflectionImage, 0, bitmap.getHeight()+reflectionSpacing, null);
        reflectionImage.recycle();

        // 在下半部分绘制半透明遮罩
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, bitmap.getHeight()+reflectionSpacing, 0, bitmapWithReflection.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, bitmap.getHeight()+reflectionSpacing, bitmapWithReflection.getWidth(), bitmapWithReflection.getHeight(), paint);

        return bitmapWithReflection;
    }

    private Bitmap cutHandle(Bitmap bitmap, ScaleType scaleType, ImageSize processSize){
        // 初始化画布
        Bitmap bitmapWithReflection = Bitmap.createBitmap(processSize.getWidth(), processSize.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);

        // 从原图中裁剪出需要的区域
        int imageHeight = (int) (processSize.getHeight() * (1 - reflectionScale));
        Bitmap cutBitmap = Bitmap.createBitmap(processSize.getWidth(), imageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(cutBitmap);
        Rect srcRect = CutImageProcessor.computeSrcRect(new Point(bitmap.getWidth(), bitmap.getHeight()), new Point(cutBitmap.getWidth(), cutBitmap.getHeight()), scaleType);
        canvas2.drawBitmap(bitmap, srcRect, new Rect(0, 0, cutBitmap.getWidth(), cutBitmap.getHeight()), null);

        // 在上半部分绘制原图
        canvas.drawBitmap(cutBitmap, new Rect(0, 0, cutBitmap.getWidth(), cutBitmap.getHeight()), new Rect(0, 0, bitmapWithReflection.getWidth(), imageHeight), null);
        bitmap.recycle();

        // 在下半部分绘制倒影图片
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(cutBitmap, 0, 0, cutBitmap.getWidth(), cutBitmap.getHeight(), matrix, false);
        cutBitmap.recycle();
        canvas.drawBitmap(reflectionImage, 0, imageHeight+reflectionSpacing, null);
        reflectionImage.recycle();

        // 在下半部分绘制半透明遮罩
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, imageHeight+reflectionSpacing, 0, bitmapWithReflection.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, imageHeight+reflectionSpacing, bitmapWithReflection.getWidth(), bitmapWithReflection.getHeight(), paint);

        return bitmapWithReflection;
    }
}
