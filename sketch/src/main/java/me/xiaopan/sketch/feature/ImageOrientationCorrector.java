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

import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.decode.DecodeHelper;
import me.xiaopan.sketch.decode.ImageType;
import me.xiaopan.sketch.request.LoadOptions;
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
     * 获取图片方向
     *
     * @param inputStream 文件输入流
     * @return 图片旋转度数
     */
    public int getImageOrientation(InputStream inputStream) throws IOException {
        ExifInterface exifInterface = new ExifInterface(inputStream);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_UNDEFINED:
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                return 0;
        }
    }

    /**
     * 获取图片方向
     *
     * @param correctImageOrientation 是否纠正图片方向，通常由{@link LoadOptions}提供
     * @param mimeType                图片的类型，某些类型不支持读取旋转角度，需要过滤掉，免得浪费精力
     * @param inputStream             输入流
     * @return 图片旋转角度
     */
    @SuppressWarnings("unused")
    public int getImageOrientation(boolean correctImageOrientation, String mimeType, InputStream inputStream) throws IOException {
        if (!correctImageOrientation || !support(mimeType)) {
            return 0;
        }

        return getImageOrientation(inputStream);
    }

    /**
     * 获取图片方向
     *
     * @param correctImageOrientation 是否纠正图片方向，通常由{@link LoadOptions}提供
     * @param mimeType                图片的类型，某些类型不支持读取旋转角度，需要过滤掉，免得浪费精力
     * @param decodeHelper            DecodeHelper
     * @return 图片旋转角度
     */
    public int getImageOrientation(boolean correctImageOrientation, String mimeType, DecodeHelper decodeHelper) {
        if (!correctImageOrientation || !support(mimeType)) {
            return 0;
        }

        InputStream inputStream = null;
        try {
            inputStream = decodeHelper.getInputStream();
            return getImageOrientation(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            SketchUtils.close(inputStream);
        }
    }

    @Override
    public String getKey() {
        return "ImageOrientationCorrector";
    }
}
