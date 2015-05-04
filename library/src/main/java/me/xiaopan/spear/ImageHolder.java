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

package me.xiaopan.spear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import me.xiaopan.spear.process.ImageProcessor;

public class ImageHolder {
    private int resId;
    private Bitmap bitmap;
    private ImageProcessor imageProcessor;
    private boolean canRecycle;

    public ImageHolder(int resId) {
        this.resId = resId;
    }

    public ImageHolder(int resId, ImageProcessor imageProcessor) {
        this.resId = resId;
        this.imageProcessor = imageProcessor;
    }

    public Bitmap getBitmap(Context context){
        if(bitmap == null){
            Drawable drawable = context.getResources().getDrawable(resId);
            if(drawable != null && drawable instanceof BitmapDrawable){
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                if(imageProcessor != null){
                    Bitmap newBitmap = imageProcessor.process(bitmap, null, null);
                    if(newBitmap != bitmap){
                        bitmap = newBitmap;
                        canRecycle = true;
                    }
                }
            }
        }
        return bitmap;
    }

    public void recycle(){
        if(canRecycle && bitmap != null){
            bitmap.recycle();
            bitmap = null;
            canRecycle = false;
        }
    }
}