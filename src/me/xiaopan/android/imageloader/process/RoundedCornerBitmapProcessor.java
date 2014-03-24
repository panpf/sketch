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
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView.ScaleType;

/**
 * 圆角位图处理器
 */
public class RoundedCornerBitmapProcessor implements BitmapProcessor {
	private static final String NAME = RoundedCornerBitmapProcessor.class.getSimpleName();
	private int roundPixels;
	
	/**
	 * 创建一个圆角位图显示器
	 * @param roundPixels 圆角度数
	 */
	public RoundedCornerBitmapProcessor(int roundPixels){
		this.roundPixels = roundPixels;
	}
	
	/**
	 * 创建一个圆角位图显示器，圆角角度默认为18
	 */
	public RoundedCornerBitmapProcessor(){
		this(18);
	}
	
	@Override
	public String getTag() {
		return NAME;
	}

	@Override
	public BitmapProcessor copy() {
		return new RoundedCornerBitmapProcessor(roundPixels);
	}

	@Override
	public Bitmap process(Bitmap bitmap, ScaleType scaleType, ImageSize targetSize) {
		if(bitmap == null) return null;
		if(scaleType == null) scaleType = ScaleType.FIT_CENTER;
		if(targetSize == null) targetSize = new ImageSize(bitmap.getWidth(), bitmap.getHeight());

        Bitmap output = Bitmap.createBitmap(targetSize.getWidth(), targetSize.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);
        canvas.drawRoundRect(new RectF(0, 0, targetSize.getWidth(), targetSize.getHeight()), roundPixels, roundPixels, paint);

        float scale;
        if(Math.abs(bitmap.getWidth() - targetSize.getWidth()) < Math.abs(bitmap.getHeight() - targetSize.getHeight())){
            scale = (float) bitmap.getWidth()/targetSize.getWidth();
            if((int)(targetSize.getHeight()*scale) > bitmap.getHeight()){
                scale = (float) bitmap.getHeight()/targetSize.getHeight();
            }
        }else{
            scale = (float) bitmap.getHeight()/targetSize.getHeight();
            if((int)(targetSize.getWidth()*scale) > bitmap.getWidth()){
                scale = (float) bitmap.getWidth()/targetSize.getWidth();
            }
        }
        int srcWidth = (int)(targetSize.getWidth()*scale);
        int srcHeight = (int)(targetSize.getHeight()*scale);
        int srcLeft;
        int srcTop;
        if (scaleType == ScaleType.FIT_START) {
            srcLeft = 0;
            srcTop = 0;
        } else if (scaleType == ScaleType.FIT_END) {
            if(bitmap.getWidth() > bitmap.getHeight()){
                srcLeft = bitmap.getWidth() - srcWidth;
                srcTop = 0;
            }else{
                srcLeft = 0;
                srcTop = bitmap.getHeight() - srcHeight;
            }
        } else {
            if(bitmap.getWidth() > bitmap.getHeight()){
                srcLeft = (bitmap.getWidth() - srcWidth)/2;
                srcTop = 0;
            }else{
                srcLeft = 0;
                srcTop = (bitmap.getHeight() - srcHeight)/2;
            }
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight), new RectF(0, 0, targetSize.getWidth(), targetSize.getHeight()), paint);

        return output;
	}
	
	public int getRoundPixels() {
		return roundPixels;
	}

	public void setRoundPixels(int roundPixels) {
		this.roundPixels = roundPixels;
	}
}
