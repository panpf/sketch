/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.feature;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.decode.DataSource;
import me.xiaopan.sketch.decode.DecodeResult;
import me.xiaopan.sketch.decode.ImageType;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.process.RotateImageProcessor;
import me.xiaopan.sketch.util.ExifInterface;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 图片方向纠正器，可让原本被旋转了的图片以正常方向显示
 */
public class ImageOrientationCorrector implements Identifier {

    /**
     * 根据mimeType判断该类型的图片是否支持通过ExitInterface读取旋转角度
     *
     * @param mimeType 从图片文件中取出的图片的mimeTye
     */
    public boolean support(String mimeType) {
        return ImageType.JPEG.getMimeType().equalsIgnoreCase(mimeType);
    }

    /**
     * 读取图片旋转角度
     *
     * @param inputStream 文件输入流
     * @return 图片旋转度数
     */
    public int readImageRotateDegrees(InputStream inputStream) throws IOException {
        ExifInterface exifInterface = new ExifInterface(inputStream);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_UNDEFINED:
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                return 0;
        }
    }

    /**
     * 读取图片旋转角度
     *
     * @param mimeType    图片的类型，某些类型不支持读取旋转角度，需要过滤掉，免得浪费精力
     * @param inputStream 输入流
     * @return 图片旋转角度
     */
    @SuppressWarnings("unused")
    public int readImageRotateDegrees(String mimeType, InputStream inputStream) throws IOException {
        if (!support(mimeType)) {
            return 0;
        }

        return readImageRotateDegrees(inputStream);
    }

    /**
     * 读取图片旋转角度
     *
     * @param mimeType   图片的类型，某些类型不支持读取旋转角度，需要过滤掉，免得浪费精力
     * @param dataSource DataSource
     * @return 图片旋转角度
     */
    public int readImageRotateDegrees(String mimeType, DataSource dataSource) {
        if (!support(mimeType)) {
            return 0;
        }

        InputStream inputStream = null;
        try {
            inputStream = dataSource.getInputStream();
            return readImageRotateDegrees(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            SketchUtils.close(inputStream);
        }
    }

    public Bitmap rotate(Bitmap bitmap, int rotateDegrees, BitmapPool bitmapPool) {
        return RotateImageProcessor.rotate(bitmap, rotateDegrees, bitmapPool);
    }

    public void rotateSize(DecodeResult result, int rotateDegrees) {
        ImageAttrs imageAttrs = result.getImageAttrs();

        Matrix matrix = new Matrix();
        matrix.setRotate(rotateDegrees);

        RectF dstR = new RectF(0, 0, imageAttrs.getOriginWidth(), imageAttrs.getOriginHeight());
        RectF deviceR = new RectF();
        matrix.mapRect(deviceR, dstR);

        imageAttrs.resetSize((int) deviceR.width(), (int) deviceR.height());
    }

    public void rotateSize(Point size, int rotateDegrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(rotateDegrees);

        RectF dstR = new RectF(0, 0, size.x, size.y);
        RectF deviceR = new RectF();
        matrix.mapRect(deviceR, dstR);

        size.x = (int) deviceR.width();
        size.y = (int) deviceR.height();
    }

    /**
     * @param rotateDegrees 顺时针方向将图片旋转多少度能回正
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public void reverseRotate(Rect srcRect, Point imageSize, int rotateDegrees) {
        rotateDegrees = 360 - rotateDegrees;

        if (rotateDegrees == 90) {
            int top = srcRect.top;
            srcRect.top = srcRect.left;
            srcRect.left = imageSize.y - srcRect.bottom;
            srcRect.bottom = srcRect.right;
            srcRect.right = imageSize.y - top;
        } else if (rotateDegrees == 180) {
            int left = srcRect.left, top = srcRect.top;
            srcRect.left = imageSize.x - srcRect.right;
            srcRect.right = imageSize.x - left;
            srcRect.top = imageSize.y - srcRect.bottom;
            srcRect.bottom = imageSize.y - top;
        } else if (rotateDegrees == 270) {
            int left = srcRect.left;
            srcRect.left = srcRect.top;
            srcRect.top = imageSize.x - srcRect.right;
            srcRect.right = srcRect.bottom;
            srcRect.bottom = imageSize.x - left;
        }
    }

    @Override
    public String getKey() {
        return "ImageOrientationCorrector";
    }
}
