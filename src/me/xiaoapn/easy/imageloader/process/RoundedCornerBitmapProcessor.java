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
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 圆角位图处理器
 */
public class RoundedCornerBitmapProcessor implements BitmapProcessor {
	private static final String TAG = RoundedCornerBitmapProcessor.class.getSimpleName();
	private int roundPixels;
	
	/**
	 * 创建一个圆角位图显示器
	 * @param roundPixels 圆角角度
	 * @param animationGenerator 动画生成器
	 */
	public RoundedCornerBitmapProcessor(int roundPixels){
		this.roundPixels = roundPixels;
	}
	
	/**
	 * 创建一个圆角位图显示器，圆角角度默认为18并且动画生成器使用AlphaAnimationGenerator
	 */
	public RoundedCornerBitmapProcessor(){
		this(18);
	}
	
	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public BitmapProcessor copy() {
		return new RoundedCornerBitmapProcessor(roundPixels);
	}

	@Override
	public Bitmap process(Bitmap bitmap, ImageViewAware imageViewAware, ImageSize targetSize) {
		ImageView imageView = imageViewAware.getImageView();
		if(imageView != null){
			return roundCorners(bitmap, imageView.getScaleType(), targetSize, roundPixels);
		}else{
			return bitmap;
		}
	}
	
	/**
	 * Process incoming {@linkplain Bitmap} to make rounded corners according to target {@link ImageView}.<br />
	 * This method <b>doesn't display</b> result bitmap in {@link ImageView}
	 * 
	 * @param bitmap Incoming Bitmap to process
	 * @param imageView Target {@link ImageView} to display bitmap in
	 * @param roundPixels
	 * @return Result bitmap with rounded corners
	 */
	public Bitmap roundCorners(Bitmap bitmap, ScaleType scaleType, ImageSize targetSize, int roundPixels) {
		Bitmap roundBitmap;

		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int imageViewWidth = targetSize.getWidth();
		int imageViewHeight = targetSize.getHeight();
		if (imageViewWidth <= 0) {
			imageViewWidth = bitmapWidth;
		}
		if (imageViewHeight <= 0){
			imageViewHeight = bitmapHeight;
		}

		int width, height;
		Rect srcRect;
		Rect destRect;
		switch (scaleType) {
			case CENTER_INSIDE:
				float vRation = (float) imageViewWidth / imageViewHeight;
				float bRation = (float) bitmapWidth / bitmapHeight;
				int destWidth;
				int destHeight;
				if (vRation > bRation) {
					destHeight = Math.min(imageViewHeight, bitmapHeight);
					destWidth = (int) (bitmapWidth / ((float) bitmapHeight / destHeight));
				} else {
					destWidth = Math.min(imageViewWidth, bitmapWidth);
					destHeight = (int) (bitmapHeight / ((float) bitmapWidth / destWidth));
				}
				int x = (imageViewWidth - destWidth) / 2;
				int y = (imageViewHeight - destHeight) / 2;
				srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
				destRect = new Rect(x, y, x + destWidth, y + destHeight);
				width = imageViewWidth;
				height = imageViewHeight;
				break;
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			default:
				vRation = (float) imageViewWidth / imageViewHeight;
				bRation = (float) bitmapWidth / bitmapHeight;
				if (vRation > bRation) {
					width = (int) (bitmapWidth / ((float) bitmapHeight / imageViewHeight));
					height = imageViewHeight;
				} else {
					width = imageViewWidth;
					height = (int) (bitmapHeight / ((float) bitmapWidth / imageViewWidth));
				}
				srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER_CROP:
				vRation = (float) imageViewWidth / imageViewHeight;
				bRation = (float) bitmapWidth / bitmapHeight;
				int srcWidth;
				int srcHeight;
				if (vRation > bRation) {
					srcWidth = bitmapWidth;
					srcHeight = (int) (imageViewHeight * ((float) bitmapWidth / imageViewWidth));
					x = 0;
					y = (bitmapHeight - srcHeight) / 2;
				} else {
					srcWidth = (int) (imageViewWidth * ((float) bitmapHeight / imageViewHeight));
					srcHeight = bitmapHeight;
					x = (bitmapWidth - srcWidth) / 2;
					y = 0;
				}
				width = imageViewWidth;
				height = imageViewHeight;
				srcRect = new Rect(x, y, x + srcWidth, y + srcHeight);
				destRect = new Rect(0, 0, width, height);
				break;
			case FIT_XY:
				width = imageViewWidth;
				height = imageViewHeight;
				srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER:
			case MATRIX:
				width = Math.min(imageViewWidth, bitmapWidth);
				height = Math.min(imageViewHeight, bitmapHeight);
				x = (bitmapWidth - width) / 2;
				y = (bitmapHeight - height) / 2;
				srcRect = new Rect(x, y, x + width, y + height);
				destRect = new Rect(0, 0, width, height);
				break;
		}

		try {
			roundBitmap = getRoundedCornerBitmap(bitmap, roundPixels, srcRect, destRect, width, height);
		} catch (OutOfMemoryError e) {
			roundBitmap = bitmap;
		}

		return roundBitmap;
	}
	
	/**
	 * 处理圆角图片
	 * @param bitmap
	 * @param roundPixels
	 * @param srcRect
	 * @param destRect
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixels, Rect srcRect, Rect destRect, int width, int height) {
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final RectF destRectF = new RectF(destRect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFF000000);
		canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, destRectF, paint);

		return output;
	}
	
	public int getRoundPixels() {
		return roundPixels;
	}

	public void setRoundPixels(int roundPixels) {
		this.roundPixels = roundPixels;
	}
}
