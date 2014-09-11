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

package me.xiaopan.android.spear.decode;

/**
 * 默认的InSampleSize计算器
 */
public class DefaultInSampleSizeCalculator implements InSampleSizeCalculator{

    @Override
    public int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight) {
        if(targetWidth <= 0 && targetHeight <= 0){
            return 1;
        }

        if(targetWidth >= outWidth && targetHeight >= outHeight){
            return 1;
        }

        int inSampleSize = 1;
        do{
            inSampleSize *= 2;
        }while ((outWidth/inSampleSize) > targetWidth && (outHeight/inSampleSize) > targetHeight);
        return inSampleSize;
    }
}
