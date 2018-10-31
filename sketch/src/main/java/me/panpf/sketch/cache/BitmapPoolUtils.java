/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.cache;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;

import me.panpf.sketch.SLog;
import me.panpf.sketch.decode.ImageType;
import me.panpf.sketch.util.SketchUtils;

public class BitmapPoolUtils {
    private static final String NAME = "BitmapPoolUtils";

    /**
     * SDK版本是否支持inBitmap，适用于BitmapRegionDecoder
     */
    public static boolean sdkSupportInBitmapForRegionDecoder() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 从bitmap poo中取出可复用的Bitmap设置到inBitmap上，适用于BitmapFactory
     *
     * @param options     BitmapFactory.Options 需要用到inSampleSize以及inPreferredConfig属性
     * @param outWidth    图片原始宽
     * @param outHeight   图片原始高
     * @param outMimeType 图片类型
     * @param bitmapPool  BitmapPool 从这个池子里找可复用的Bitmap
     * @return true：找到了可复用的Bitmap
     */
    public static boolean setInBitmapFromPool(BitmapFactory.Options options, int outWidth, int outHeight, String outMimeType, BitmapPool bitmapPool) {
        if (outWidth == 0 || outHeight == 0) {
            SLog.e(NAME, "outWidth or ourHeight is 0");
            return false;
        }

        if (TextUtils.isEmpty(outMimeType)) {
            SLog.e(NAME, "outMimeType is empty");
            return false;
        }

        // 使用inBitmap时4.4以下inSampleSize不能为0，最小也得是1
        if (options.inSampleSize <= 0) {
            options.inSampleSize = 1;
        }

        int inSampleSize = options.inSampleSize;
        ImageType imageType = ImageType.valueOfMimeType(outMimeType);

        Bitmap inBitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int finalWidth = SketchUtils.calculateSamplingSize(outWidth, inSampleSize);
            int finalHeight = SketchUtils.calculateSamplingSize(outHeight, inSampleSize);
            inBitmap = bitmapPool.get(finalWidth, finalHeight, options.inPreferredConfig);
        } else if (inSampleSize == 1 && (imageType == ImageType.JPEG || imageType == ImageType.PNG)) {
            inBitmap = bitmapPool.get(outWidth, outHeight, options.inPreferredConfig);
        }

        if (inBitmap != null && SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
            int sizeInBytes = SketchUtils.computeByteCount(outWidth, outHeight, options.inPreferredConfig);
            SLog.d(NAME, "setInBitmapFromPool. options=%dx%d,%s,%d,%d. inBitmap=%s,%d",
                    outWidth, outHeight, options.inPreferredConfig, inSampleSize, sizeInBytes,
                    Integer.toHexString(inBitmap.hashCode()), SketchUtils.getByteCount(inBitmap));
        }

        options.inBitmap = inBitmap;
        options.inMutable = true;

        return inBitmap != null;
    }

    /**
     * 回收bitmap，首先尝试放入bitmap pool，放不进去就回收
     *
     * @param bitmap     要处理的bitmap
     * @param bitmapPool BitmapPool 尝试放入这个池子
     * @return true：成功放入bitmap pool
     */
    public static boolean freeBitmapToPool(Bitmap bitmap, BitmapPool bitmapPool) {
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }

        boolean success = bitmapPool.put(bitmap);
        if (success) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.d(NAME, "Put to bitmap pool. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), SketchUtils.toHexString(bitmap),
                        element.getClassName(), element.getMethodName(), element.getLineNumber());
            }
        } else {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.d(NAME, "Recycle bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), SketchUtils.toHexString(bitmap),
                        element.getClassName(), element.getMethodName(), element.getLineNumber());
            }
            bitmap.recycle();
        }
        return success;
    }

    /**
     * 从bitmap poo中取出可复用的Bitmap设置到inBitmap上，适用于BitmapRegionDecoder
     *
     * @param options    BitmapFactory.Options 需要用到options的inSampleSize以及inPreferredConfig属性
     * @param bitmapPool BitmapPool 从这个池子里找可复用的Bitmap
     * @return true：找到了可复用的Bitmap
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean setInBitmapFromPoolForRegionDecoder(BitmapFactory.Options options, Rect srcRect, BitmapPool bitmapPool) {
        if (!sdkSupportInBitmapForRegionDecoder()) {
            return false;
        }

        int inSampleSize = options.inSampleSize >= 1 ? options.inSampleSize : 1;
        Bitmap.Config config = options.inPreferredConfig;

        int finalWidth = SketchUtils.calculateSamplingSizeForRegion(srcRect.width(), inSampleSize);
        int finalHeight = SketchUtils.calculateSamplingSizeForRegion(srcRect.height(), inSampleSize);
        Bitmap inBitmap = bitmapPool.get(finalWidth, finalHeight, config);

        if (inBitmap != null) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                int sizeInBytes = SketchUtils.computeByteCount(finalWidth, finalHeight, config);
                SLog.d(NAME, "setInBitmapFromPoolForRegionDecoder. options=%dx%d,%s,%d,%d. inBitmap=%s,%d",
                        finalWidth, finalHeight, config, inSampleSize, sizeInBytes,
                        Integer.toHexString(inBitmap.hashCode()), SketchUtils.getByteCount(inBitmap));
            }
        } else {
            // 由于BitmapRegionDecoder不支持inMutable所以就自己创建Bitmap
            inBitmap = Bitmap.createBitmap(finalWidth, finalHeight, config);
        }

        options.inBitmap = inBitmap;

        return inBitmap != null;
    }

    /**
     * 处理bitmap，首先尝试放入bitmap pool，放不进去就回收
     *
     * @param bitmap     要处理的bitmap
     * @param bitmapPool BitmapPool 尝试放入这个池子
     * @return true：成功放入bitmap pool
     */
    public static boolean freeBitmapToPoolForRegionDecoder(Bitmap bitmap, BitmapPool bitmapPool) {
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }

        boolean success = sdkSupportInBitmapForRegionDecoder() && bitmapPool.put(bitmap);
        if (!success) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.d(NAME, "Recycle bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), SketchUtils.toHexString(bitmap),
                        element.getClassName(), element.getMethodName(), element.getLineNumber());
            }
            bitmap.recycle();
        } else {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.d(NAME, "Put to bitmap pool. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), SketchUtils.toHexString(bitmap),
                        element.getClassName(), element.getMethodName(), element.getLineNumber());
            }
        }
        return success;
    }
}
