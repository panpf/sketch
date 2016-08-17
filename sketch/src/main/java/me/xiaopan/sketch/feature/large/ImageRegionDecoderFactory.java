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

package me.xiaopan.sketch.feature.large;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

public class ImageRegionDecoderFactory {

    private ImageRegionDecoderFactory() {

    }

    public static void create(Context context, final String imageUri, final CreateCallback createCallback) {
        final Context finalContext = context.getApplicationContext();
        new Thread() {
            @Override
            public void run() {
                try {
                    final ImageRegionDecoder decoder = createImpl(finalContext, imageUri);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            createCallback.onCreateCompleted(decoder);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            createCallback.onCreateFailed(e);
                        }
                    });
                }
            }
        }.start();
    }

    private static ImageRegionDecoder createImpl(Context context, final String imageUri) throws Exception {
        UriScheme uriScheme = UriScheme.valueOfUri(imageUri);
        if (uriScheme == UriScheme.NET) {
            return createDecoderFromHttp(context, imageUri);
        } else if (uriScheme == UriScheme.FILE) {
            return createDecoderFromFile(imageUri);
        } else if (uriScheme == UriScheme.CONTENT) {
            return createDecoderFromContent(context, imageUri);
        } else if (uriScheme == UriScheme.ASSET) {
            return createDecoderFromAsset(context, imageUri);
        } else if (uriScheme == UriScheme.DRAWABLE) {
            return createDecoderFromDrawable(context, imageUri);
        } else {
            throw new Exception("unknown scheme uri: " + imageUri);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private static ImageRegionDecoder createDecoderFromHttp(Context context, String imageUri) throws Exception {
        DiskCache.Entry diskCacheEntry = Sketch.with(context).getConfiguration().getDiskCache().get(imageUri);
        if (diskCacheEntry == null) {
            throw new Exception("not found disk cache: " + imageUri);
        }
        String diskCacheFilePath = diskCacheEntry.getFile().getPath();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(diskCacheFilePath, options);

        BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(diskCacheFilePath, false);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        ImageFormat imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        return new ImageRegionDecoder(imageUri, imageWidth, imageHeight, imageFormat, regionDecoder);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private static ImageRegionDecoder createDecoderFromFile(String imageUri) throws IOException {
        String filePath = UriScheme.FILE.crop(imageUri);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(filePath, false);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        ImageFormat imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        return new ImageRegionDecoder(imageUri, imageWidth, imageHeight, imageFormat, regionDecoder);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private static ImageRegionDecoder createDecoderFromContent(Context context, String imageUri) throws Exception {
        Uri uri = Uri.parse(UriScheme.CONTENT.crop(imageUri));

        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        SketchUtils.close(inputStream);

        inputStream = context.getContentResolver().openInputStream(uri);
        BitmapRegionDecoder regionDecoder;
        try {
            regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            SketchUtils.close(inputStream);
            throw e;
        }

        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        ImageFormat imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        return new ImageRegionDecoder(imageUri, imageWidth, imageHeight, imageFormat, regionDecoder, inputStream);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private static ImageRegionDecoder createDecoderFromAsset(Context context, String imageUri) throws IOException {
        String assetFileName = UriScheme.ASSET.crop(imageUri);

        InputStream inputStream = context.getAssets().open(assetFileName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        SketchUtils.close(inputStream);

        inputStream = context.getAssets().open(assetFileName);
        BitmapRegionDecoder regionDecoder;
        try {
            regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            SketchUtils.close(inputStream);
            throw e;
        }

        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        ImageFormat imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        return new ImageRegionDecoder(imageUri, imageWidth, imageHeight, imageFormat, regionDecoder, inputStream);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private static ImageRegionDecoder createDecoderFromDrawable(Context context, String imageUri) throws Exception {
        int drawableResId = Integer.valueOf(UriScheme.DRAWABLE.crop(imageUri));

        InputStream inputStream = context.getResources().openRawResource(drawableResId);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        SketchUtils.close(inputStream);

        inputStream = context.getResources().openRawResource(drawableResId);
        BitmapRegionDecoder regionDecoder;
        try {
            regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            SketchUtils.close(inputStream);
            throw e;
        }

        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        ImageFormat imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        return new ImageRegionDecoder(imageUri, imageWidth, imageHeight, imageFormat, regionDecoder, inputStream);
    }

    public interface CreateCallback {
        void onCreateCompleted(ImageRegionDecoder imageRegionDecoder);

        void onCreateFailed(Exception e);
    }
}
