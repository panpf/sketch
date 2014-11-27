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

package me.xiaopan.android.spear.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.ImageView;

import me.xiaopan.android.spear.display.DefaultImageDisplayer;
import me.xiaopan.android.spear.display.ImageDisplayer;
import me.xiaopan.android.spear.process.ImageProcessor;

public class BaseDefaultProperty implements DefaultProperty {
    private ImageDisplayer defaultImageDisplayer;
    private ImageProcessor defaultCutImageProcessor;

    @Override
    public ImageDisplayer getDefaultImageDisplayer(Context context) {
        if(defaultImageDisplayer == null){
            synchronized (this){
                if(defaultImageDisplayer == null){
                    defaultImageDisplayer = new DefaultImageDisplayer();
                }
            }
        }
        return defaultImageDisplayer;
    }

    @Override
    public ImageProcessor getDefaultCutImageProcessor(Context context) {
        if(defaultCutImageProcessor == null){
            synchronized (this){
                if(defaultCutImageProcessor == null){
                    defaultCutImageProcessor = new CutImageProcessor();
                }
            }
        }
        return defaultCutImageProcessor;
    }

    /**
     * 裁剪位图处理器
     */
    private static class CutImageProcessor implements ImageProcessor {

        @Override
        public Bitmap process(Bitmap bitmap, ImageSize resize, ImageView.ScaleType scaleType) {
            if(bitmap == null){
                return null;
            }
            if(resize == null){
                return bitmap;
            }
            if(scaleType == null){
                scaleType = ImageView.ScaleType.FIT_CENTER;
            }

            // 如果新的尺寸大于等于原图的尺寸，就重新定义新的尺寸
            if((resize.getWidth() * resize.getHeight()) >= (bitmap.getWidth() * bitmap.getHeight())){
                Rect rect = ImageProcessUtils.computeSrcRect(new Point(bitmap.getWidth(), bitmap.getHeight()), new Point(resize.getWidth(), resize.getHeight()), scaleType);
                resize = new ImageSize(rect.width(), rect.height());
            }

            // 根据新的尺寸创建新的图片
            Bitmap newBitmap = Bitmap.createBitmap(resize.getWidth(), resize.getHeight(), Bitmap.Config.ARGB_8888);
            Rect srcRect = ImageProcessUtils.computeSrcRect(new Point(bitmap.getWidth(), bitmap.getHeight()), new Point(newBitmap.getWidth(), newBitmap.getHeight()), scaleType);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(bitmap, srcRect, new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight()), null);

            return newBitmap;
        }
    }
}
