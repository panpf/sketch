/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.zoom.block;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import com.github.panpf.sketch.Configuration;
import com.github.panpf.sketch.Sketch;
import com.github.panpf.sketch.datasource.DataSource;
import com.github.panpf.sketch.decode.ImageDecodeUtils;
import com.github.panpf.sketch.decode.ImageOrientationCorrector;
import com.github.panpf.sketch.decode.ImageType;
import com.github.panpf.sketch.uri.GetDataSourceException;
import com.github.panpf.sketch.uri.UriModel;
import com.github.panpf.sketch.util.ExifInterface;
import com.github.panpf.sketch.util.SketchUtils;

/**
 * 图片碎片解码器，支持纠正图片方向
 */
@SuppressWarnings("WeakerAccess")
public class ImageRegionDecoder {

    private final int exifOrientation;
    @NonNull
    private Point imageSize;
    @NonNull
    private String imageUri;
    @Nullable
    private ImageType imageType;
    @Nullable
    private BitmapRegionDecoder regionDecoder;

    public ImageRegionDecoder(@NonNull String imageUri, @NonNull Point imageSize, @Nullable ImageType imageType,
                               int exifOrientation, @NonNull BitmapRegionDecoder regionDecoder) {
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

    @NonNull
    public Point getImageSize() {
        return imageSize;
    }

    @Nullable
    public ImageType getImageType() {
        return imageType;
    }

    @NonNull
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
        if (regionDecoder != null && isReady()) {
            regionDecoder.recycle();
            regionDecoder = null;
        }
    }

    public Bitmap decodeRegion(Rect srcRect, BitmapFactory.Options options) {
        if (regionDecoder != null && isReady()) {
            return regionDecoder.decodeRegion(srcRect, options);
        } else {
            return null;
        }
    }
}
