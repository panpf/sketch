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
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.ImageView.ScaleType;

/**
 * 裁剪位图处理器
 */
public class TailorBitmapProcessor implements BitmapProcessor {
	private static final String NAME = TailorBitmapProcessor.class.getSimpleName();
	
	@Override
	public String getTag() {
		return NAME;
	}

	@Override
	public Bitmap process(Bitmap bitmap, ScaleType scaleType, ImageSize processSize) {
		// 初始化参数
		if(bitmap == null) return null;
		if(scaleType == null) scaleType = ScaleType.FIT_CENTER;

		if(processSize != null){
            Bitmap newBitmap = Bitmap.createBitmap(processSize.getWidth(), processSize.getHeight(), Bitmap.Config.ARGB_8888);
            Rect srcRect = BitmapProcessorUtils.computeSrcRect(new Point(bitmap.getWidth(), bitmap.getHeight()), new Point(newBitmap.getWidth(), newBitmap.getHeight()), scaleType);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(bitmap, srcRect, new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight()), null);
            return newBitmap;
        }else{
            return bitmap;
        }
	}
	
	@Override
	public BitmapProcessor copy() {
		return new TailorBitmapProcessor();
	}
}
