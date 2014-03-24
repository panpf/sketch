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
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
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
		this(2, 0.5f);
	}

	@Override
	public String getTag() {
		return NAME;
	}

	@Override
	public Bitmap process(Bitmap bitmap, ScaleType scaleType, ImageSize targetSize) {
		if(bitmap == null) return null;

		//创建倒影图片
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);//

		//并创建一张宽度与原图相同，但高度等于原图的高度加上间距加上倒影的高度的图片，并创建画布。画布分为上中下三部分，上：是原图；中：是原图与倒影的间距；下：是倒影 */
        Bitmap bitmapWithReflection = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight() + reflectionSpacing + (int) (bitmap.getHeight()*reflectionScale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);

		/* 将原图画到画布的上半部分，将倒影画到画布的下半部分，倒影与画布顶部的间距是原图的高度加上原图与倒影之间的间距 */
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(reflectionImage, 0, bitmap.getHeight() + reflectionSpacing, null);
        reflectionImage.recycle();

		/* 将倒影改成半透明，创建画笔，并设置画笔的渐变从半透明的白色到全透明的白色，然后再倒影上面画半透明效果 */
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionSpacing, 0x70ffffff, 0x00ffffff, TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, bitmap.getHeight() + reflectionSpacing, bitmap.getWidth(), bitmapWithReflection.getHeight() + reflectionSpacing, paint);

        return bitmapWithReflection;
	}
	
	@Override
	public BitmapProcessor copy() {
		return new ReflectionBitmapProcessor(reflectionSpacing, reflectionScale);
	}
}
