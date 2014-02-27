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
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 圆形位图处理器
 */
public class CircleBitmapProcessor implements BitmapProcessor {
	private static final String TAG = CircleBitmapProcessor.class.getSimpleName();
	
	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public BitmapProcessor copy() {
		return new CircleBitmapProcessor();
	}

	@Override
	public Bitmap process(Bitmap bitmap, ScaleType scaleType, ImageSize targetSize) {
		if(bitmap == null){
			return null;
		}
		if(scaleType == null){
			scaleType = ScaleType.CENTER_CROP;
		}
		if(targetSize == null){
			targetSize = new ImageSize(bitmap.getWidth(), bitmap.getHeight());
		}
		return cricle(bitmap, scaleType, targetSize);
	}
	
	/**
	 * Process incoming {@linkplain Bitmap} to make rounded corners according to target {@link ImageView}.<br />
	 * This method <b>doesn't display</b> result bitmap in {@link ImageView}
	 * 
	 * @param bitmap Incoming Bitmap to process
	 * @param imageView Target {@link ImageView} to display bitmap in
	 * @return Result bitmap with rounded corners
	 */
	public Bitmap cricle(Bitmap bitmap, ScaleType scaleType, ImageSize targetSize) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int viewWidth = targetSize.getWidth();
		int viewHeight = targetSize.getHeight();
		if (viewWidth <= 0) {
			viewWidth = bitmapWidth;
		}
		if (viewHeight <= 0){
			viewHeight = bitmapHeight;
		}

		int srcLeft = 0;
		int srcTop = 0;
		int srcWidth = bitmapWidth;
		int srcHeight = bitmapHeight;
		int destLeft = 0;
		int destTop = 0;
		int destWidth = viewWidth;
		int destHeight = viewHeight;
		int width = viewWidth;
		int height = viewHeight;
		float viewScale = (float) viewWidth / viewHeight;
		float bitmapScale = (float) bitmapWidth / bitmapHeight;
		switch (scaleType) {
			case CENTER_INSIDE:
				if (viewScale > bitmapScale) {
					destHeight = Math.min(viewHeight, bitmapHeight);
					destWidth = (int) (bitmapWidth / ((float) bitmapHeight / destHeight));
				} else {
					destWidth = Math.min(viewWidth, bitmapWidth);
					destHeight = (int) (bitmapHeight / ((float) bitmapWidth / destWidth));
				}
				destLeft = (viewWidth - destWidth) / 2;
				destTop = (viewHeight - destHeight) / 2;
				break;
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			default:
				if (viewScale > bitmapScale) {
					width = (int) (bitmapWidth / ((float) bitmapHeight / viewHeight));
					height = viewHeight;
				} else {
					width = viewWidth;
					height = (int) (bitmapHeight / ((float) bitmapWidth / viewWidth));
				}
				srcWidth = bitmapWidth;
				srcHeight = bitmapHeight;
				destWidth = width;
				destHeight = height;
				break;
			case CENTER_CROP:
				if(viewScale > bitmapScale){
					srcWidth = bitmapWidth;
					srcHeight = (int) (viewHeight * ((float) bitmapWidth / viewWidth));
					srcLeft = 0;
					srcTop = (bitmapHeight - srcHeight) / 2;
				}else if(viewScale < bitmapScale){
					srcWidth = (int) (viewWidth * ((float) bitmapHeight / viewHeight));
					srcHeight = bitmapHeight;
					srcLeft = (bitmapWidth - srcWidth) / 2;
					srcTop = 0;
				}
				break;
			case CENTER:
			case MATRIX:
				width = Math.min(viewWidth, bitmapWidth);
				height = Math.min(viewHeight, bitmapHeight);
				srcLeft = (bitmapWidth - width) / 2;
				srcTop = (bitmapHeight - height) / 2;
				srcWidth = width;
				srcHeight = height;
				destWidth = width;
				destHeight = height;
				break;
		}
		
		try {
			Rect srcRect = new Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight);
			Rect destRect = new Rect(destLeft, destTop, destLeft + destWidth, destTop + destHeight);
			return getCricleBitmap(bitmap, srcRect, destRect, width, height);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return bitmap;
		}
	}
	
	/**
	 * 处理圆角图片
	 * @param bitmap
	 * @param srcRect
	 * @param destRect
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap getCricleBitmap(Bitmap bitmap, Rect srcRect, Rect destRect, int width, int height) {
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final RectF destRectF = new RectF(destRect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFFFF0000);
		canvas.drawCircle(width/2, height/2, width < height?width/2:height/2, paint);
		
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, destRectF, paint);

		return output;
	}
}
