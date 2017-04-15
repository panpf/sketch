/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.cache;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SketchMonitor;
import me.xiaopan.sketch.decode.ImageType;
import me.xiaopan.sketch.util.SketchUtils;

public class BitmapPoolUtils {
    /**
     * SDK版本是否支持inBitmap，适用于BitmapFactory
     */
    public static boolean sdkSupportInBitmap() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * SDK版本是否支持inBitmap，适用于BitmapRegionDecoder
     */
    public static boolean sdkSupportInBitmapForRegionDecoder() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 从bitmap poo中取出可复用的Bitmap设置到inBitmap上，适用于BitmapFactory
     *
     * @param options    BitmapFactory.Options 需要用到options的outWidth、outHeight、inSampleSize以及inPreferredConfig属性
     * @param bitmapPool BitmapPool 从这个池子里找可复用的Bitmap
     * @return true：找到了可复用的Bitmap
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean setInBitmapFromPool(BitmapFactory.Options options, BitmapPool bitmapPool) {
        if (!sdkSupportInBitmap()) {
            return false;
        }

        if (options.outWidth == 0 || options.outHeight == 0) {
            SLog.e(SLogType.REQUEST, "outWidth or ourHeight is 0");
            return false;
        }

        if (TextUtils.isEmpty(options.outMimeType)) {
            SLog.e(SLogType.REQUEST, "outMimeType is empty");
            return false;
        }

        // 使用inBitmap时4.4以下inSampleSize不能为0，最小也得是1
        if (options.inSampleSize <= 0) {
            options.inSampleSize = 1;
        }

        int inSampleSize = options.inSampleSize;
        ImageType imageType = ImageType.valueOfMimeType(options.outMimeType);

        Bitmap inBitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int finalWidth = SketchUtils.ceil(options.outWidth, inSampleSize);
            int finalHeight = SketchUtils.ceil(options.outHeight, inSampleSize);
            inBitmap = bitmapPool.get(finalWidth, finalHeight, options.inPreferredConfig);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && inSampleSize == 1
                && (imageType == ImageType.JPEG || imageType == ImageType.PNG)) {
            inBitmap = bitmapPool.get(options.outWidth, options.outHeight, options.inPreferredConfig);
        }

        if (inBitmap != null && SLogType.CACHE.isEnabled()) {
            int sizeInBytes = SketchUtils.computeByteCount(options.outWidth, options.outHeight, options.inPreferredConfig);
            SLog.d(SLogType.CACHE, "setInBitmapFromPool. options=%dx%d,%s,%d,%d. inBitmap=%s,%d",
                    options.outWidth, options.outHeight, options.inPreferredConfig, inSampleSize, sizeInBytes,
                    Integer.toHexString(inBitmap.hashCode()), SketchUtils.getByteCount(inBitmap));
        }

        options.inBitmap = inBitmap;
        options.inMutable = true;

        return inBitmap != null;
    }

    public static boolean inBitmapThrow(Throwable throwable, BitmapFactory.Options options,
                                        SketchMonitor monitor, BitmapPool bitmapPool, String imageUri, int imageWidth, int imageHeight) {
        if (throwable instanceof IllegalArgumentException) {
            if (sdkSupportInBitmap()) {
                if (options.inBitmap != null) {
                    monitor.onInBitmapException(imageUri, imageWidth, imageHeight, options.inSampleSize, options.inBitmap);
                    freeBitmapToPool(options.inBitmap, bitmapPool);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 处理bitmap，首先尝试放入bitmap pool，放不进去就回收
     *
     * @param bitmap     要处理的bitmap
     * @param bitmapPool BitmapPool 尝试放入这个池子
     * @return ture：成功放入bitmap pool
     */
    public static boolean freeBitmapToPool(Bitmap bitmap, BitmapPool bitmapPool) {
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }

        boolean success = bitmapPool.put(bitmap);
        if (success) {
            if (SLogType.CACHE.isEnabled()) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.d(SLogType.CACHE, String.format("Put to bitmap pool. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), SketchUtils.toHexString(bitmap),
                        element.getClassName(), element.getMethodName(), element.getLineNumber()));
            }
        } else {
            if (SLogType.CACHE.isEnabled()) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.w(SLogType.CACHE, String.format("Recycle bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), SketchUtils.toHexString(bitmap),
                        element.getClassName(), element.getMethodName(), element.getLineNumber()));
            }
            bitmap.recycle();
        }
        return success;
    }

    /**
     * 从bitmap poo中取出可复用的Bitmap设置到inBitmap上，适用于BitmapRegionDecoder
     *
     * @param options    BitmapFactory.Options 需要用到options的outWidth、outHeight、inSampleSize以及inPreferredConfig属性
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

        int finalWidth = SketchUtils.ceil(srcRect.width(), inSampleSize);
        int finalHeight = SketchUtils.ceil(srcRect.height(), inSampleSize);
        Bitmap inBitmap = bitmapPool.get(finalWidth, finalHeight, config);

        if (inBitmap != null) {
            if (SLogType.CACHE.isEnabled()) {
                int sizeInBytes = SketchUtils.computeByteCount(finalWidth, finalHeight, config);
                SLog.d(SLogType.CACHE, "setInBitmapFromPoolForRegionDecoder. options=%dx%d,%s,%d,%d. inBitmap=%s,%d",
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

    public static boolean inBitmapThrowForRegionDecoder(Throwable throwable, BitmapFactory.Options options,
                                                        SketchMonitor monitor, BitmapPool bitmapPool, String imageUri,
                                                        int imageWidth, int imageHeight, Rect srcRect) {
        if (throwable instanceof IllegalArgumentException) {
            if (sdkSupportInBitmapForRegionDecoder()) {
                if (options.inBitmap != null) {
                    monitor.onInBitmapExceptionForRegionDecoder(imageUri, imageWidth,
                            imageHeight, srcRect, options.inSampleSize, options.inBitmap);
                    freeBitmapToPoolForRegionDecoder(options.inBitmap, bitmapPool);
                    return true;
                }
            }
        }

        return false;
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
            if (SLogType.CACHE.isEnabled()) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.w(SLogType.CACHE, String.format("Recycle bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), SketchUtils.toHexString(bitmap),
                        element.getClassName(), element.getMethodName(), element.getLineNumber()));
            }
            bitmap.recycle();
        } else {
            if (SLogType.CACHE.isEnabled()) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.d(SLogType.CACHE, String.format("Put to bitmap pool. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), SketchUtils.toHexString(bitmap),
                        element.getClassName(), element.getMethodName(), element.getLineNumber()));
            }
        }
        return success;
    }
}
