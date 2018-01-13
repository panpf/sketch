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

package me.panpf.sketch.zoom.block;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;

import java.io.IOException;
import java.io.InputStream;

import me.panpf.sketch.Configuration;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.decode.ImageDecodeUtils;
import me.panpf.sketch.decode.ImageOrientationCorrector;
import me.panpf.sketch.decode.ImageType;
import me.panpf.sketch.uri.GetDataSourceException;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.ExifInterface;
import me.panpf.sketch.util.SketchUtils;

/**
 * 图片碎片解码器，支持纠正图片方向
 */
public class ImageRegionDecoder {

    private final int exifOrientation;
    private Point imageSize;
    private String imageUri;
    private ImageType imageType;
    private BitmapRegionDecoder regionDecoder;

    ImageRegionDecoder(String imageUri, Point imageSize, ImageType imageType,
                       int exifOrientation, BitmapRegionDecoder regionDecoder) {
        this.imageUri = imageUri;
        this.imageSize = imageSize;
        this.imageType = imageType;
        this.exifOrientation = exifOrientation;
        this.regionDecoder = regionDecoder;
    }

    public static ImageRegionDecoder build(Context context, final String imageUri,
                                           final boolean correctImageOrientationDisabled) throws IOException {
        UriModel uriModel = UriModel.match(context, imageUri);
        if (uriModel == null) {
            throw new IllegalArgumentException("Unknown scheme uri. " + imageUri);
        }

        DataSource dataSource;
        try {
            dataSource = uriModel.getDataSource(context, imageUri, null);
        } catch (GetDataSourceException e) {
            throw new IllegalArgumentException("Can not be generated DataSource.  " + imageUri, e);
        }

        // 读取图片尺寸和类型
        BitmapFactory.Options boundOptions = new BitmapFactory.Options();
        boundOptions.inJustDecodeBounds = true;
        ImageDecodeUtils.decodeBitmap(dataSource, boundOptions);
        Point imageSize = new Point(boundOptions.outWidth, boundOptions.outHeight);

        // 读取图片方向并根据方向改变尺寸
        Configuration configuration = Sketch.with(context).getConfiguration();
        ImageOrientationCorrector orientationCorrector = configuration.getOrientationCorrector();
        int exifOrientation = ExifInterface.ORIENTATION_UNDEFINED;
        if (!correctImageOrientationDisabled) {
            exifOrientation = orientationCorrector.readExifOrientation(boundOptions.outMimeType, dataSource);
        }
        orientationCorrector.rotateSize(imageSize, exifOrientation);

        InputStream inputStream = null;
        BitmapRegionDecoder regionDecoder;
        try {
            inputStream = dataSource.getInputStream();
            regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } finally {
            SketchUtils.close(inputStream);
        }

        ImageType imageType = ImageType.valueOfMimeType(boundOptions.outMimeType);

        return new ImageRegionDecoder(imageUri, imageSize, imageType, exifOrientation, regionDecoder);
    }

    @SuppressWarnings("unused")
    public Point getImageSize() {
        return imageSize;
    }

    public ImageType getImageType() {
        return imageType;
    }

    @SuppressWarnings("unused")
    public String getImageUri() {
        return imageUri;
    }

    public int getExifOrientation() {
        return exifOrientation;
    }

    public boolean isReady() {
        return regionDecoder != null && !regionDecoder.isRecycled();
    }

    public void recycle() {
        if (isReady()) {
            regionDecoder.recycle();
            regionDecoder = null;
        }
    }

    public Bitmap decodeRegion(Rect srcRect, BitmapFactory.Options options) {
        if (isReady()) {
            return regionDecoder.decodeRegion(srcRect, options);
        } else {
            return null;
        }
    }
}
