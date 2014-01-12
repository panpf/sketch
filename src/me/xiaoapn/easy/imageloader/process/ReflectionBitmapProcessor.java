/*
 * Copyright 2013 Peng fei Pan
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

package me.xiaoapn.easy.imageloader.process;

import me.xiaoapn.easy.imageloader.task.ImageViewAware;
import me.xiaoapn.easy.imageloader.util.ImageSize;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;

public class ReflectionBitmapProcessor implements BitmapProcessor {
	private static final String TAG = ReflectionBitmapProcessor.class.getSimpleName();;
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
		this(2, 0.5f);
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Bitmap process(Bitmap bitmap, ImageViewAware imageViewAware, ImageSize targetSize) {
		return reflection(bitmap, reflectionSpacing, (int) (bitmap.getHeight()*reflectionScale));
	}
	
	/**
	 * 倒影处理
	 * @param bitmap 原图
	 * @param reflectionSpacing 原图与倒影之间的间距
	 * @return 加上倒影后的图片
	 */
	private Bitmap reflection(Bitmap bitmap, int reflectionSpacing, int reflectionHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		/* 获取倒影图片，并创建一张宽度与原图相同，但高度等于原图的高度加上间距加上倒影的高度的图片，并创建画布。画布分为上中下三部分，上：是原图；中：是原图与倒影的间距；下：是倒影 */
		Bitmap reflectionImage = reverseByVertical(bitmap);//
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height + reflectionSpacing + reflectionHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		
		/* 将原图画到画布的上半部分，将倒影画到画布的下半部分，倒影与画布顶部的间距是原图的高度加上原图与倒影之间的间距 */
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionSpacing, null);
		reflectionImage.recycle();
		
		/* 将倒影改成半透明，创建画笔，并设置画笔的渐变从半透明的白色到全透明的白色，然后再倒影上面画半透明效果 */
		Paint paint = new Paint();
		paint.setShader(new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionSpacing, 0x70ffffff, 0x00ffffff, TileMode.CLAMP));
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height+reflectionSpacing, width, bitmapWithReflection.getHeight() + reflectionSpacing, paint);
		
		return bitmapWithReflection;
	}
	
	/**
	 * 垂直翻转处理
	 * @param bitmap 原图
	 * @return 垂直翻转后的图片
	 */
	private Bitmap reverseByVertical(Bitmap bitmap){
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
	}

	@Override
	public BitmapProcessor copy() {
		return new ReflectionBitmapProcessor(reflectionSpacing, reflectionScale);
	}
}
