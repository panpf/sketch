/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.util.ExifInterface;
import me.panpf.sketch.util.SketchUtils;

/**
 * 图片方向纠正器，可让原本被旋转了的图片以正常方向显示
 */
public class ImageOrientationCorrector {

    public static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;

    public static String toName(int exifOrientation) {
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return "ROTATE_90";
            case ExifInterface.ORIENTATION_TRANSPOSE:
                return "TRANSPOSE";
            case ExifInterface.ORIENTATION_ROTATE_180:
                return "ROTATE_180";
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return "FLIP_VERTICAL";
            case ExifInterface.ORIENTATION_ROTATE_270:
                return "ROTATE_270";
            case ExifInterface.ORIENTATION_TRANSVERSE:
                return "TRANSVERSE";
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return "FLIP_HORIZONTAL";
            case ExifInterface.ORIENTATION_UNDEFINED:
                return "UNDEFINED";
            case ExifInterface.ORIENTATION_NORMAL:
                return "NORMAL";
            default:
                return String.valueOf(exifOrientation);
        }
    }

    public static int getExifOrientationDegrees(int exifOrientation) {
        final int degreesToRotate;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_ROTATE_90:
                degreesToRotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                degreesToRotate = 180;
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_ROTATE_270:
                degreesToRotate = 270;
                break;
            default:
                degreesToRotate = 0;

        }
        return degreesToRotate;
    }

    @SuppressWarnings("unused")
    public static int getExifOrientationTranslation(int exifOrientation) {
        int translation;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                translation = -1;
                break;
            default:
                translation = 1;
        }
        return translation;
    }

    public static void initializeMatrixForExifRotation(int exifOrientation, Matrix matrix) {
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(270);
                matrix.postScale(-1, 1);
                break;
            default:
                // Do nothing.
        }
    }

    /**
     * 根据mimeType判断该类型的图片是否支持通过ExitInterface读取旋转角度
     *
     * @param mimeType 从图片文件中取出的图片的mimeTye
     */
    public boolean support(String mimeType) {
        return ImageType.JPEG.getMimeType().equalsIgnoreCase(mimeType);
    }

    /**
     * 根据exifOrientation判断图片是否被旋转了
     *
     * @param exifOrientation from exif info
     * @return true：已旋转
     */
    public boolean hasRotate(int exifOrientation) {
        return exifOrientation != ExifInterface.ORIENTATION_UNDEFINED && exifOrientation != ExifInterface.ORIENTATION_NORMAL;
    }

    /**
     * 读取图片方向
     *
     * @param inputStream 文件输入流
     * @return exif 保存的原始方向
     */
    public int readExifOrientation(InputStream inputStream) throws IOException {
        ExifInterface exifInterface = new ExifInterface(inputStream);
        return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    /**
     * 读取图片方向
     *
     * @param mimeType    图片的类型，某些类型不支持读取旋转角度，需要过滤掉，免得浪费精力
     * @param inputStream 输入流
     * @return exif 保存的原始方向
     */
    @SuppressWarnings("unused")
    public int readExifOrientation(String mimeType, InputStream inputStream) throws IOException {
        if (!support(mimeType)) {
            return ExifInterface.ORIENTATION_UNDEFINED;
        }

        return readExifOrientation(inputStream);
    }

    /**
     * 读取图片方向
     *
     * @param mimeType   图片的类型，某些类型不支持读取旋转角度，需要过滤掉，免得浪费精力
     * @param dataSource DataSource
     * @return exif 保存的原始方向
     */
    public int readExifOrientation(String mimeType, DataSource dataSource) {
        if (!support(mimeType)) {
            return ExifInterface.ORIENTATION_UNDEFINED;
        }

        InputStream inputStream = null;
        try {
            inputStream = dataSource.getInputStream();
            return readExifOrientation(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return ExifInterface.ORIENTATION_UNDEFINED;
        } finally {
            SketchUtils.close(inputStream);
        }
    }

    /**
     * 根据图片方向旋转图片
     *
     * @param exifOrientation 图片方向
     */
    public Bitmap rotate(Bitmap bitmap, int exifOrientation, BitmapPool bitmapPool) {
        if (!hasRotate(exifOrientation)) {
            return null;
        }

        Matrix matrix = new Matrix();
        initializeMatrixForExifRotation(exifOrientation, matrix);

        // 根据旋转角度计算新的图片的尺寸
        RectF newRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        matrix.mapRect(newRect);
        int newWidth = (int) newRect.width();
        int newHeight = (int) newRect.height();

        // 角度不能整除90°时新图片会是斜的，因此要支持透明度，这样倾斜导致露出的部分就不会是黑的
        int degrees = getExifOrientationDegrees(exifOrientation);
        Bitmap.Config config = bitmap.getConfig() != null ? bitmap.getConfig() : null;
        if (degrees % 90 != 0 && config != Bitmap.Config.ARGB_8888) {
            config = Bitmap.Config.ARGB_8888;
        }

        Bitmap result = bitmapPool.getOrMake(newWidth, newHeight, config);

        matrix.postTranslate(-newRect.left, -newRect.top);

        final Canvas canvas = new Canvas(result);
        final Paint paint = new Paint(PAINT_FLAGS);
        canvas.drawBitmap(bitmap, matrix, paint);

        return result;
    }

    /**
     * 根据旋转角度计算新图片旋转后的尺寸
     *
     * @param exifOrientation 图片方向
     */
    public void rotateSize(ImageAttrs imageAttrs, int exifOrientation) {
        if (!hasRotate(exifOrientation)) {
            return;
        }

        Matrix matrix = new Matrix();
        initializeMatrixForExifRotation(exifOrientation, matrix);
        RectF newRect = new RectF(0, 0, imageAttrs.getWidth(), imageAttrs.getHeight());
        matrix.mapRect(newRect);

        imageAttrs.resetSize((int) newRect.width(), (int) newRect.height());
    }

    /**
     * 根据旋转角度计算新图片旋转后的尺寸
     *
     * @param exifOrientation 图片方向
     */
    public void rotateSize(BitmapFactory.Options options, int exifOrientation) {
        if (!hasRotate(exifOrientation)) {
            return;
        }

        Matrix matrix = new Matrix();
        initializeMatrixForExifRotation(exifOrientation, matrix);
        RectF newRect = new RectF(0, 0, options.outWidth, options.outHeight);
        matrix.mapRect(newRect);

        options.outWidth = (int) newRect.width();
        options.outHeight = (int) newRect.height();
    }

    /**
     * 根据旋转角度计算新图片旋转后的尺寸
     *
     * @param exifOrientation 图片方向
     */
    public void rotateSize(Point size, int exifOrientation) {
        if (!hasRotate(exifOrientation)) {
            return;
        }

        Matrix matrix = new Matrix();
        initializeMatrixForExifRotation(exifOrientation, matrix);
        RectF newRect = new RectF(0, 0, size.x, size.y);
        matrix.mapRect(newRect);

        size.x = (int) newRect.width();
        size.y = (int) newRect.height();
    }

    /**
     * 根据图片方向恢复被旋转前的尺寸
     *
     * @param exifOrientation 图片方向
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public void reverseRotate(Rect srcRect, int imageWidth, int imageHeight, int exifOrientation) {
        if (!hasRotate(exifOrientation)) {
            return;
        }

        int rotateDegrees = 360 - getExifOrientationDegrees(exifOrientation);
        if (rotateDegrees == 90) {
            int top = srcRect.top;
            srcRect.top = srcRect.left;
            srcRect.left = imageHeight - srcRect.bottom;
            srcRect.bottom = srcRect.right;
            srcRect.right = imageHeight - top;
        } else if (rotateDegrees == 180) {
            int left = srcRect.left, top = srcRect.top;
            srcRect.left = imageWidth - srcRect.right;
            srcRect.right = imageWidth - left;
            srcRect.top = imageHeight - srcRect.bottom;
            srcRect.bottom = imageHeight - top;
        } else if (rotateDegrees == 270) {
            int left = srcRect.left;
            srcRect.left = srcRect.top;
            srcRect.top = imageWidth - srcRect.right;
            srcRect.right = srcRect.bottom;
            srcRect.bottom = imageWidth - left;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "ImageOrientationCorrector";
    }
}
