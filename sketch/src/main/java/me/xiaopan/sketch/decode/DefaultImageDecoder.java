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

package me.xiaopan.sketch.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.feature.ErrorCallback;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.request.DataSource;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.MaxSize;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 默认的图片解码器
 */
public class DefaultImageDecoder implements ImageDecoder {
    private volatile static long decodeCount;
    private volatile static long useTimeCount;
    private static DecimalFormat decimalFormat;
    protected String logName = "DefaultImageDecoder";

    public static DecodeResult decodeFromHelper(LoadRequest loadRequest, DecodeHelper decodeHelper, String logName) {
        Options boundsOptions = new Options();
        Options decodeOptions = new Options();

        // 读取图片的宽高以及格式信息
        boundsOptions.inJustDecodeBounds = true;
        decodeHelper.decode(boundsOptions);

        // 解析图片类型
        String mimeType = boundsOptions.outMimeType;
        ImageFormat imageFormat = ImageFormat.valueOfMimeType(mimeType);

        // 设置优先考虑质量还是速度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1
                && loadRequest.getOptions().isInPreferQualityOverSpeed()) {
            decodeOptions.inPreferQualityOverSpeed = true;
        }

        // setup bitmap config
        if (loadRequest.getOptions().getBitmapConfig() != null) {
            // by user
            decodeOptions.inPreferredConfig = loadRequest.getOptions().getBitmapConfig();
        } else if (imageFormat != null) {
            // best bitmap config by MimeType
            decodeOptions.inPreferredConfig = imageFormat.getConfig(loadRequest.getOptions().isLowQualityImage());
        }

        // decode gif image
        if (imageFormat != null && imageFormat == ImageFormat.GIF && loadRequest.getOptions().isDecodeGifImage()) {
            try {
                return new DecodeResult(mimeType, decodeHelper.getGifDrawable());
            } catch (Throwable e) {
                e.printStackTrace();
                ErrorCallback errorCallback = loadRequest.getSketch().getConfiguration().getErrorCallback();
                if (errorCallback != null) {
                    errorCallback.onDecodeGifImageFailed(e, loadRequest, boundsOptions);
                }
            }
        }

        // decode normal image
        Bitmap bitmap = null;
        Point originalSize = new Point(boundsOptions.outWidth, boundsOptions.outHeight);
        if (boundsOptions.outWidth != 1 && boundsOptions.outHeight != 1) {
            // calculate inSampleSize
            MaxSize maxSize = loadRequest.getOptions().getMaxSize();
            if (maxSize != null) {
                ImageSizeCalculator imageSizeCalculator = loadRequest.getSketch().getConfiguration().getImageSizeCalculator();
                decodeOptions.inSampleSize = imageSizeCalculator.calculateInSampleSize(boundsOptions.outWidth, boundsOptions.outHeight, maxSize.getWidth(), maxSize.getHeight());
            }

            // Decoding and exclude the width or height of 1 pixel image
            try {
                bitmap = decodeHelper.decode(decodeOptions);
            } catch (Throwable error) {
                error.printStackTrace();
                ErrorCallback errorCallback = loadRequest.getSketch().getConfiguration().getErrorCallback();
                if (errorCallback != null) {
                    errorCallback.onDecodeNormalImageFailed(error, loadRequest, boundsOptions);
                }
            }
            if (bitmap != null && (bitmap.getWidth() == 1 || bitmap.getHeight() == 1)) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(logName,
                            " - ", "bitmap width or height is 1px",
                            " - ", "ImageSize: ", originalSize.x, "x", originalSize.y,
                            " - ", "BitmapSize: ", bitmap.getWidth(), "x", bitmap.getHeight(),
                            " - ", loadRequest.getAttrs().getId()));
                }
                bitmap.recycle();
                bitmap = null;
            }
        } else {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "image width or height is 1px",
                        " - ", "ImageSize: ", originalSize.x, "x", originalSize.y,
                        " - ", loadRequest.getAttrs().getId()));
            }
        }

        // Results the callback
        if (bitmap != null && !bitmap.isRecycled()) {
            decodeHelper.onDecodeSuccess(bitmap, originalSize, decodeOptions.inSampleSize);
        } else {
            bitmap = null;
            decodeHelper.onDecodeFailed();
        }

        return bitmap != null ? new DecodeResult(mimeType, bitmap) : null;
    }

    @Override
    public DecodeResult decode(LoadRequest loadRequest) {
        long startTime = 0;
        if (Sketch.isDebugMode()) {
            startTime = System.currentTimeMillis();
        }

        DecodeResult result = null;
        try {
            if (loadRequest.getAttrs().getUriScheme() == UriScheme.NET) {
                result = decodeHttpOrHttps(loadRequest);
            } else if (loadRequest.getAttrs().getUriScheme() == UriScheme.FILE) {
                result = decodeFile(loadRequest);
            } else if (loadRequest.getAttrs().getUriScheme() == UriScheme.CONTENT) {
                result = decodeContent(loadRequest);
            } else if (loadRequest.getAttrs().getUriScheme() == UriScheme.ASSET) {
                result = decodeAsset(loadRequest);
            } else if (loadRequest.getAttrs().getUriScheme() == UriScheme.DRAWABLE) {
                result = decodeDrawable(loadRequest);
            } else {
                Log.w(Sketch.TAG, SketchUtils.concat(logName, " - ", "unknown uri is ", loadRequest.getAttrs().getUri()));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (Sketch.isDebugMode()) {
            long useTime = System.currentTimeMillis() - startTime;
            synchronized (DefaultImageDecoder.this) {
                if ((Long.MAX_VALUE - decodeCount) < 1 || (Long.MAX_VALUE - useTimeCount) < useTime) {
                    decodeCount = 0;
                    useTimeCount = 0;
                }
                decodeCount++;
                useTimeCount += useTime;
                if (decimalFormat == null) {
                    decimalFormat = new DecimalFormat("#.##");
                }
                Log.d(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "DecodeUseTime",
                        " - ", useTime, "ms", ", ",
                        "average", "=", decimalFormat.format((double) useTimeCount / decodeCount), "ms",
                        " - ", loadRequest.getAttrs().getId()));
            }
        }

        return result;
    }

    @Override
    public String getIdentifier() {
        return logName;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(logName);
    }

    public DecodeResult decodeFromDataSource(LoadRequest loadRequest, DataSource dataSource) {
        DecodeResult decodeResult = null;

        DiskCache.Entry diskCacheEntry = dataSource.getDiskCacheEntry();
        byte[] imageData = dataSource.getImageData();

        if (diskCacheEntry != null) {
            DecodeHelper decodeHelper = new CacheFileDecodeHelper(diskCacheEntry, loadRequest);
            decodeResult = decodeFromHelper(loadRequest, decodeHelper, logName);
        } else if (imageData != null && imageData.length > 0) {
            DecodeHelper decodeHelper = new ByteArrayDecodeHelper(imageData, loadRequest);
            decodeResult = decodeFromHelper(loadRequest, decodeHelper, logName);
        }

        if (decodeResult != null) {
            decodeResult.setImageFrom(dataSource.getImageFrom());
        }

        return decodeResult;
    }

    public DecodeResult decodeHttpOrHttps(LoadRequest loadRequest) {
        DecodeResult decodeResult = null;

        DataSource dataSource = loadRequest.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(loadRequest, dataSource);
        }

        return decodeResult;
    }

    public DecodeResult decodeFile(LoadRequest loadRequest) {
        DecodeResult decodeResult;

        DataSource dataSource = loadRequest.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(loadRequest, dataSource);
        } else {
            DecodeHelper decodeHelper = new FileDecodeHelper(new File(loadRequest.getAttrs().getRealUri()), loadRequest);
            decodeResult = decodeFromHelper(loadRequest, decodeHelper, logName);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.LOCAL);
            }
        }

        return decodeResult;
    }

    public DecodeResult decodeContent(LoadRequest loadRequest) {
        DecodeResult decodeResult;

        DataSource dataSource = loadRequest.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(loadRequest, dataSource);
        } else {
            DecodeHelper decodeHelper = new ContentDecodeHelper(Uri.parse(loadRequest.getAttrs().getRealUri()), loadRequest);
            decodeResult = decodeFromHelper(loadRequest, decodeHelper, logName);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.LOCAL);
            }
        }

        return decodeResult;
    }

    public DecodeResult decodeAsset(LoadRequest loadRequest) {
        DecodeResult decodeResult;

        DataSource dataSource = loadRequest.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(loadRequest, dataSource);
        } else {
            DecodeHelper decodeHelper = new AssetsDecodeHelper(loadRequest.getAttrs().getRealUri(), loadRequest);
            decodeResult = decodeFromHelper(loadRequest, decodeHelper, logName);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.LOCAL);
            }
        }

        return decodeResult;
    }

    public DecodeResult decodeDrawable(LoadRequest loadRequest) {
        DecodeResult decodeResult;

        DataSource dataSource = loadRequest.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(loadRequest, dataSource);
        } else {
            DecodeHelper decodeHelper = new DrawableDecodeHelper(Integer.valueOf(loadRequest.getAttrs().getRealUri()), loadRequest);
            decodeResult = decodeFromHelper(loadRequest, decodeHelper, logName);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.LOCAL);
            }
        }

        return decodeResult;
    }
}