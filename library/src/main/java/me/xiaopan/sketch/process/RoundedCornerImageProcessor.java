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
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import me.xiaopan.sketch.FixedSize;
import me.xiaopan.sketch.Resize;

/**
 * 圆角位图处理器
 */
public class RoundedCornerImageProcessor implements ImageProcessor {
    private static final String NAME = "RoundedCornerImageProcessor";

    private int roundPixels;
    private boolean forceUseResizeInCenterCrop = true;
    private FixedSize fixedSize;

	/**
	 * 创建一个圆角位图显示器
	 * @param roundPixels 圆角度数
	 */
	public RoundedCornerImageProcessor(int roundPixels){
		this.roundPixels = roundPixels;
	}
	
	/**
	 * 创建一个圆角位图显示器，圆角角度默认为18
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
        builder.append(NAME);
        builder.append(" - ");
        builder.append("roundPixels=").append(roundPixels);
        builder.append(", forceUseResizeInCenterCrop=").append(forceUseResizeInCenterCrop);
        if(fixedSize != null){
            builder.append(", fixedSize=").append(fixedSize.getWidth()).append("x").append(fixedSize.getHeight());
        }
        return builder;
    }

    @Override
    public Bitmap process(Bitmap bitmap, Resize resize, boolean imagesOfLowQuality) {
        if(bitmap == null){
            return null;
        }

        if(fixedSize != null){
            return fixedSize(bitmap, fixedSize, imagesOfLowQuality);
        }

        ImageView.ScaleType scaleType = null;
        if(resize != null){
            scaleType = resize.getScaleType();
        }
        if(scaleType == null){
            scaleType = ImageView.ScaleType.FIT_CENTER;
        }

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int newBitmapWidth;
        int newBitmapHeight;
        if(resize != null){
            newBitmapWidth = resize.getWidth();
            newBitmapHeight = resize.getHeight();
        }else{
            newBitmapWidth = bitmap.getWidth();
            newBitmapHeight = bitmap.getHeight();
        }

        Rect srcRect = null;
        if(scaleType == ImageView.ScaleType.CENTER){
            if(newBitmapWidth >= bitmapWidth && newBitmapHeight >= bitmapHeight){
                srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
                newBitmapWidth = bitmapWidth;
                newBitmapHeight = bitmapHeight;
            }else{
                srcRect = CutImageProcessor.findCutRect(bitmapWidth, bitmapHeight, newBitmapWidth, newBitmapHeight);
                newBitmapWidth = srcRect.width();
                newBitmapHeight = srcRect.height();
            }
        }else if(scaleType == ImageView.ScaleType.CENTER_CROP){
            if(!forceUseResizeInCenterCrop && ((float)newBitmapWidth/newBitmapHeight == (float)bitmapWidth/bitmapHeight && newBitmapWidth >= bitmapWidth)){
                srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
                newBitmapWidth = bitmapWidth;
                newBitmapHeight = bitmapHeight;
            }else{
                srcRect = CutImageProcessor.findMappingRect(bitmapWidth, bitmapHeight, newBitmapWidth, newBitmapHeight);
                if(!forceUseResizeInCenterCrop && (bitmapWidth <= newBitmapWidth || bitmapHeight <= newBitmapHeight)){
                    newBitmapWidth = srcRect.width();
                    newBitmapHeight = srcRect.height();
                }
            }
        }else if(scaleType == ImageView.ScaleType.CENTER_INSIDE || scaleType == ImageView.ScaleType.FIT_CENTER || scaleType == ImageView.ScaleType.FIT_END || scaleType == ImageView.ScaleType.FIT_START){
            if(newBitmapWidth >= bitmapWidth && newBitmapHeight >= bitmapHeight){
                srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
                newBitmapWidth = bitmapWidth;
                newBitmapHeight = bitmapHeight;
            }else{
                float widthScale = (float)bitmapWidth/newBitmapWidth;
                float heightScale = (float)bitmapHeight/newBitmapHeight;
                float finalScale = widthScale>heightScale?widthScale:heightScale;
                newBitmapWidth = (int)(bitmapWidth/finalScale);
                newBitmapHeight = (int)(bitmapHeight/finalScale);
                srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
            }
        }else if(scaleType == ImageView.ScaleType.FIT_XY || scaleType == ImageView.ScaleType.MATRIX){
            srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
            newBitmapWidth = bitmapWidth;
            newBitmapHeight = bitmapHeight;
        }

        if(srcRect == null){
            return bitmap;
        }else{
            Bitmap output = Bitmap.createBitmap(newBitmapWidth, newBitmapHeight, imagesOfLowQuality? Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(0xFFFF0000);

            // 绘制圆角的罩子
            canvas.drawRoundRect(new RectF(0, 0, newBitmapWidth, newBitmapHeight), roundPixels, roundPixels, paint);

            // 应用遮罩模式并绘制图片
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            Rect dstRect = new Rect(0, 0, output.getWidth(), output.getHeight());
            canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
            return output;
        }
	}

    public Bitmap fixedSize(Bitmap bitmap, FixedSize fixedSize, boolean imagesOfLowQuality){
        int newBitmapWidth = fixedSize.getWidth();
        int newBitmapHeight = fixedSize.getHeight();

        Rect srcRect = CutImageProcessor.findMappingRect(bitmap.getWidth(), bitmap.getHeight(), newBitmapWidth, newBitmapHeight);
        Rect dstRect = new Rect(0, 0, newBitmapWidth, newBitmapHeight);

        Bitmap output = Bitmap.createBitmap(newBitmapWidth, newBitmapHeight, imagesOfLowQuality? Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);

        canvas.drawRoundRect(new RectF(0, 0, newBitmapWidth, newBitmapHeight), roundPixels, roundPixels, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        return output;
    }

    public int getRoundPixels() {
		return roundPixels;
	}

	public RoundedCornerImageProcessor setRoundPixels(int roundPixels) {
		this.roundPixels = roundPixels;
        return this;
	}

    public RoundedCornerImageProcessor disableForceUseResizeInCenterCrop() {
        this.forceUseResizeInCenterCrop = false;
        return this;
    }

    public FixedSize getFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(FixedSize fixedSize) {
        this.fixedSize = fixedSize;
    }
}
