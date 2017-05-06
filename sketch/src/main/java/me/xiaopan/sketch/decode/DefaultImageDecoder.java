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
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.feature.ImageOrientationCorrector;
import me.xiaopan.sketch.feature.ImagePreprocessor;
import me.xiaopan.sketch.feature.PreProcessResult;
import me.xiaopan.sketch.feature.ProcessedImageCache;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 图片解码器
 */
public class DefaultImageDecoder implements ImageDecoder {
    protected String logName = "DefaultImageDecoder";

    private DecodeTimeAnalyze timeAnalyze = new DecodeTimeAnalyze();
    private List<DecodeHelper> decodeHelperList;
    private List<ResultProcessor> resultProcessorList;

    public DefaultImageDecoder() {
        decodeHelperList = new LinkedList<>();
        resultProcessorList = new LinkedList<>();

        decodeHelperList.add(new ProcessedCacheDecodeHelper());
        decodeHelperList.add(new GifDecodeHelper());
        decodeHelperList.add(new ThumbnailModeDecodeHelper());
        decodeHelperList.add(new NormalDecodeHelper());

        resultProcessorList.add(new CorrectOrientationResultProcessor());
        resultProcessorList.add(new ProcessImageResultProcessor());
        resultProcessorList.add(new ProcessedCacheResultProcessor());
    }

    public static Bitmap decodeBitmap(DataSource dataSource, BitmapFactory.Options options) {
        InputStream inputStream;
        try {
            inputStream = dataSource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        SketchUtils.close(inputStream);
        return bitmap;
    }

    public static Bitmap decodeRegionBitmap(DataSource dataSource, Rect srcRect, BitmapFactory.Options options) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1) {
            return null;
        }

        InputStream inputStream;
        try {
            inputStream = dataSource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        BitmapRegionDecoder regionDecoder;
        try {
            regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            SketchUtils.close(inputStream);
        }

        Bitmap bitmap = regionDecoder.decodeRegion(srcRect, options);
        regionDecoder.recycle();
        SketchUtils.close(inputStream);
        return bitmap;
    }

    public static DataSource makeDataSource(LoadRequest request, boolean ignoreProcessedCache, String logName) throws DecodeException {
        // 缓存的处理过的图片，可直接读取
        if (!ignoreProcessedCache) {
            ProcessedImageCache processedImageCache = request.getConfiguration().getProcessedImageCache();
            if (processedImageCache.canUse(request.getOptions())) {
                DataSource dataSource = processedImageCache.checkProcessedImageDiskCache(request);
                if (dataSource != null) {
                    return dataSource;
                }
            }
        }

        // 特殊文件的预处理
        ImagePreprocessor imagePreprocessor = request.getConfiguration().getImagePreprocessor();
        if (imagePreprocessor.isSpecific(request)) {
            PreProcessResult prePrecessResult = request.doPreProcess();
            if (prePrecessResult != null && prePrecessResult.diskCacheEntry != null) {
                return new CacheFileDataSource(prePrecessResult.diskCacheEntry, request, prePrecessResult.imageFrom);
            }

            if (prePrecessResult != null && prePrecessResult.imageData != null) {
                return new ByteArrayDataSource(prePrecessResult.imageData, request, prePrecessResult.imageFrom);
            }

            SLog.w(SLogType.REQUEST, logName, "pre process result is null", request.getUri());
            throw new DecodeException("pre process result is null", ErrorCause.PRE_PROCESS_RESULT_IS_NULL);
        }

        UriScheme uriScheme = request.getUriScheme();

        if (uriScheme == UriScheme.NET) {
            DownloadResult downloadResult = request.getDownloadResult();
            DiskCache.Entry diskCacheEntry = downloadResult != null ? downloadResult.getDiskCacheEntry() : null;
            if (diskCacheEntry != null) {
                return new CacheFileDataSource(diskCacheEntry, request, downloadResult.getImageFrom());
            }

            byte[] imageDataArray = downloadResult != null ? downloadResult.getImageData() : null;
            if (imageDataArray != null && imageDataArray.length > 0) {
                return new ByteArrayDataSource(imageDataArray, request, downloadResult.getImageFrom());
            }

            SLog.w(SLogType.REQUEST, logName, "download result exception", request.getUri());
            throw new DecodeException("download result exception", ErrorCause.DOWNLOAD_RESULT_IS_NULL);
        }

        if (uriScheme == UriScheme.FILE) {
            return new FileDataSource(new File(request.getRealUri()), request);
        }

        if (uriScheme == UriScheme.CONTENT) {
            return new ContentDataSource(Uri.parse(request.getRealUri()), request);
        }

        if (uriScheme == UriScheme.ASSET) {
            return new AssetsDataSource(request.getRealUri(), request);
        }

        if (uriScheme == UriScheme.DRAWABLE) {
            return new DrawableDataSource(Integer.valueOf(request.getRealUri()), request);
        }

        SLog.w(SLogType.REQUEST, logName, "unknown uri is %s", request.getUri());
        throw new DecodeException(String.format("unknown uri is %s", request.getUri()), ErrorCause.NOT_FOUND_DATA_SOURCE_BY_UNKNOWN_URI);
    }

    @Override
    public DecodeResult decode(LoadRequest request) throws DecodeException {
        long startTime = 0;
        if (SLogType.TIME.isEnabled()) {
            startTime = timeAnalyze.decodeStart();
        }

        DecodeResult result = null;
        try {
            result = doDecode(request);
        } catch (DecodeException e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (SLogType.TIME.isEnabled()) {
            timeAnalyze.decodeEnd(startTime, logName, request.getKey());
        }

        if (result != null) {
            try {
                doProcess(request, result);
            } catch (DecodeException e) {
                result.recycle(request.getConfiguration().getBitmapPool());
                throw e;
            } catch (Throwable e) {
                e.printStackTrace();
                result.recycle(request.getConfiguration().getBitmapPool());
                result = null;
            }
        }

        return result;
    }

    private DecodeResult doDecode(LoadRequest request) throws DecodeException {
        // Make date source
        DataSource dataSource = makeDataSource(request, false, logName);

        // Decode bounds and mime info
        Options boundOptions = new Options();
        boundOptions.inJustDecodeBounds = true;
        decodeBitmap(dataSource, boundOptions);

        // Exclude images with a width of less than or equal to 1
        if (boundOptions.outWidth <= 1 || boundOptions.outHeight <= 1) {
            SLog.e(SLogType.REQUEST, logName, "image width or height less than or equal to 1px. imageSize: %dx%d. %s",
                    boundOptions.outWidth, boundOptions.outHeight, request.getKey());
            dataSource.onDecodeError();
            return null;
        }

        // Read image orientation
        int imageOrientation = 0;
        if (request.getOptions().isCorrectImageOrientation()) {
            ImageOrientationCorrector imageOrientationCorrector = request.getConfiguration().getImageOrientationCorrector();
            imageOrientation = imageOrientationCorrector.readImageRotateDegrees(boundOptions.outMimeType, dataSource);
        }

        ImageType imageType = ImageType.valueOfMimeType(boundOptions.outMimeType);

        // Set whether priority is given to quality or speed
        Options decodeOptions = new Options();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1
                && request.getOptions().isInPreferQualityOverSpeed()) {
            decodeOptions.inPreferQualityOverSpeed = true;
        }

        // Setup preferred bitmap config
        Bitmap.Config newConfig = request.getOptions().getBitmapConfig();
        if (newConfig == null && imageType != null) {
            newConfig = imageType.getConfig(request.getOptions().isLowQualityImage());
        }
        if (newConfig != null) {
            decodeOptions.inPreferredConfig = newConfig;
        }

        DecodeResult decodeResult = null;
        for (DecodeHelper decodeHelper : decodeHelperList) {
            if (decodeHelper.match(request, dataSource, imageType, boundOptions)) {
                decodeResult = decodeHelper.decode(request, dataSource, imageType, boundOptions, decodeOptions, imageOrientation);
                break;
            }
        }

        if (decodeResult != null) {
            decodeResult.setImageFrom(dataSource.getImageFrom());
        }

        return decodeResult;
    }

    private void doProcess(LoadRequest request, DecodeResult result) throws DecodeException{
        for(ResultProcessor resultProcessor : resultProcessorList){
            resultProcessor.process(request, result);
        }
    }

    @Override
    public String getKey() {
        return logName;
    }
}