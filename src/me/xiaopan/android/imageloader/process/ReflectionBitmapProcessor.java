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
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.widget.ImageView.ScaleType;

/**
 * 倒影位图处理器
 */
public class ReflectionBitmapProcessor implements BitmapProcessor {
	private static final String NAME = ReflectionBitmapProcessor.class.getSimpleName();
	private int reflectionSpacing;
	private float reflectionScale;
	
	/**
	 * 创建一个倒影位图处理器
	 * @param reflectionSpacing 倒影和图片之间的距离
	 * @param reflectionScale 倒影的高度所占原图高度比例
	 */
	public ReflectionBitmapProcessor(int reflectionSpacing, float reflectionScale) {
		this.reflectionSpacing = reflectionSpacing;
		this.reflectionScale = reflectionScale;
	}
	
	public ReflectionBitmapProcessor(){
		this(2, 0.3f);
	}

	@Override
	public String getTag() {
		return NAME;
	}

	@Override
	public Bitmap process(Bitmap bitmap, ScaleType scaleType, ImageSize processSize) {
		// 初始化参数
		if(bitmap == null) return null;
		if(scaleType == null) scaleType = ScaleType.FIT_CENTER;
		if(processSize == null) processSize = new ImageSize(bitmap.getWidth(), bitmap.getHeight());
		
		// 初始化画布
		Bitmap bitmapWithReflection = Bitmap.createBitmap(processSize.getWidth(), processSize.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);

        // 在上半部分绘制原图
		int imageHeight = (int) (processSize.getHeight() * (1 - reflectionScale));
		Bitmap cutBitmap = cut(processSize, imageHeight, bitmap, scaleType);
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
        canvas.drawRect(0, imageHeight+reflectionSpacing, reflectionImage.getWidth(), bitmapWithReflection.getHeight(), paint);

        return bitmapWithReflection;
		
		

//		// 初始化画布
//		Bitmap bitmapWithReflection = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight() + reflectionSpacing + (int) (bitmap.getHeight()*reflectionScale), Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(bitmapWithReflection);
//
//		//创建倒影图片
//        Matrix matrix = new Matrix();
//        matrix.preScale(1, -1);
//        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);//
//
//
//		/* 将原图画到画布的上半部分，将倒影画到画布的下半部分，倒影与画布顶部的间距是原图的高度加上原图与倒影之间的间距 */
//        canvas.drawBitmap(bitmap, 0, 0, null);
//        canvas.drawBitmap(reflectionImage, 0, bitmap.getHeight() + reflectionSpacing, null);
//        reflectionImage.recycle();
//
//		/* 将倒影改成半透明，创建画笔，并设置画笔的渐变从半透明的白色到全透明的白色，然后再倒影上面画半透明效果 */
//        Paint paint = new Paint();
//        paint.setShader(new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionSpacing, 0x70ffffff, 0x00ffffff, TileMode.CLAMP));
//        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
//        canvas.drawRect(0, bitmap.getHeight() + reflectionSpacing, bitmap.getWidth(), bitmapWithReflection.getHeight() + reflectionSpacing, paint);
//
//        return bitmapWithReflection;
	}
	
	@Override
	public BitmapProcessor copy() {
		return new ReflectionBitmapProcessor(reflectionSpacing, reflectionScale);
	}
	
	public Bitmap cut(ImageSize processSize, int imageHeight, Bitmap bitmap, ScaleType scaleType){
		Bitmap cutBitmap = Bitmap.createBitmap(processSize.getWidth(), imageHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(cutBitmap);
        Rect srcRect = new ComputeRect().compute(new Point(bitmap.getWidth(), bitmap.getHeight()), new Point(cutBitmap.getWidth(), cutBitmap.getHeight()), scaleType);
        canvas.drawBitmap(bitmap, srcRect, new Rect(0, 0, cutBitmap.getWidth(), cutBitmap.getHeight()), null);
        return cutBitmap;
	}
}
