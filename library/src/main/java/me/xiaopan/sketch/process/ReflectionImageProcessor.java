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
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.widget.ImageView;

import me.xiaopan.sketch.Resize;

/**
 * 倒影位图处理器
 */
public class ReflectionImageProcessor implements ImageProcessor {
    private static final String NAME = "ReflectionImageProcessor";
	private int reflectionSpacing;
	private float reflectionScale;
    private boolean forceUseResizeInCenterCrop = true;

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
    public Bitmap process(Bitmap bitmap, Resize resize, boolean imagesOfLowQuality) {
        if(bitmap == null){
            return null;
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
            return process(bitmap, imagesOfLowQuality? Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888, bitmapWidth, bitmapHeight, newBitmapWidth, newBitmapHeight, srcRect);
        }
    }

    private Bitmap process(Bitmap bitmap, Bitmap.Config config, int bitmapWidth, int bitmapHeight, int newBitmapWidth, int newBitmapHeight, Rect srcRect){
        Bitmap srcBitmap;
        if(bitmapWidth == newBitmapWidth && bitmapHeight == newBitmapHeight){
            srcBitmap = bitmap;
        }else{
            srcBitmap = Bitmap.createBitmap(newBitmapWidth, newBitmapHeight, config);
            Canvas canvas = new Canvas(srcBitmap);
            canvas.drawBitmap(bitmap, srcRect, new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight()), null);
        }

        // 初始化画布
        Bitmap bitmapWithReflection = Bitmap.createBitmap(newBitmapWidth, (int) (newBitmapHeight+reflectionSpacing+(newBitmapHeight*reflectionScale)), config);
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
        canvas.drawBitmap(reflectionImage, 0, newBitmapHeight+reflectionSpacing, null);
        reflectionImage.recycle();

        // 在下半部分绘制半透明遮罩
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, newBitmapHeight+reflectionSpacing, 0, bitmapWithReflection.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, newBitmapHeight + reflectionSpacing, bitmapWithReflection.getWidth(), bitmapWithReflection.getHeight(), paint);

        return bitmapWithReflection;
    }

    public ReflectionImageProcessor disableForceUseResizeInCenterCrop() {
        this.forceUseResizeInCenterCrop = false;
        return this;
    }
}
