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
import android.os.Build;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.util.ExifInterface;

/**
 * 图片解码器，读取bitmap之前执行计算采样比例、选择合适的config、读取方向、寻找可复用的bitmap等操作，之后进行方向纠正、处理、缓存等操作
 */
public class ImageDecoder implements Identifier {
    private static final String LOG_NAME = "ImageDecoder";

    private DecodeTimeAnalyze timeAnalyze = new DecodeTimeAnalyze();
    private List<DecodeHelper> decodeHelperList;
    private List<ResultProcessor> resultProcessorList;

    public ImageDecoder() {
        decodeHelperList = new LinkedList<>();
        resultProcessorList = new LinkedList<>();

        decodeHelperList.add(new ProcessedCacheDecodeHelper());
        decodeHelperList.add(new GifDecodeHelper());
        decodeHelperList.add(new ThumbnailModeDecodeHelper());
        decodeHelperList.add(new NormalDecodeHelper());

        resultProcessorList.add(new ProcessImageResultProcessor());
        resultProcessorList.add(new ProcessedResultCacheProcessor());
    }

    /**
     * 解码入口方法，统计解码时间、调用解码方法以及后续处理
     *
     * @param request LoadRequest
     * @return DecodeResult
     * @throws DecodeException 解码失败了
     */
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
            timeAnalyze.decodeEnd(startTime, LOG_NAME, request.getKey());
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

    /**
     * 执行具体解码，这个方法里值读取出解码所需的一些属性，然后再交给具体的DecodeHelper去解码
     *
     * @param request LoadRequest
     * @return DecodeResult
     * @throws DecodeException 解码失败了
     */
    private DecodeResult doDecode(LoadRequest request) throws DecodeException {
        // Make date source
        DataSource dataSource = DataSourceFactory.processedCacheFirstMakeDataSource(request.getContext(), request.getUriInfo(),
                request.getDownloadResult(), request.getOptions(), request.getProcessedImageDiskCacheKey());

        // Decode bounds and mime info
        BitmapFactory.Options boundOptions = new BitmapFactory.Options();
        boundOptions.inJustDecodeBounds = true;
        try {
            ImageDecodeUtils.decodeBitmap(dataSource, boundOptions);
        } catch (IOException e) {
            e.printStackTrace();
            SLog.fe(SLogType.REQUEST, LOG_NAME, "decode bounds failed %s", request.getKey());
            ImageDecodeUtils.decodeError(request, dataSource, LOG_NAME);
            return null;
        }

        // Exclude images with a width of less than or equal to 1
        if (boundOptions.outWidth <= 1 || boundOptions.outHeight <= 1) {
            SLog.fe(SLogType.REQUEST, LOG_NAME, "image width or height less than or equal to 1px. imageSize: %dx%d. %s",
                    boundOptions.outWidth, boundOptions.outHeight, request.getKey());
            ImageDecodeUtils.decodeError(request, dataSource, LOG_NAME);
            return null;
        }

        // Read image orientation
        int exifOrientation = ExifInterface.ORIENTATION_UNDEFINED;
        if (!request.getOptions().isCorrectImageOrientationDisabled()) {
            ImageOrientationCorrector imageOrientationCorrector = request.getConfiguration().getImageOrientationCorrector();
            exifOrientation = imageOrientationCorrector.readExifOrientation(boundOptions.outMimeType, dataSource);
        }

        ImageType imageType = ImageType.valueOfMimeType(boundOptions.outMimeType);

        // Set whether priority is given to quality or speed
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
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
                decodeResult = decodeHelper.decode(request, dataSource, imageType, boundOptions, decodeOptions, exifOrientation);
                break;
            }
        }

        if (decodeResult != null) {
            decodeResult.setImageFrom(dataSource.getImageFrom());
        }

        return decodeResult;
    }

    /**
     * 执行后续的处理，包括转换、缓存
     *
     * @param request LoadRequest
     * @param result  DecodeResult
     * @throws DecodeException 处理失败了
     */
    private void doProcess(LoadRequest request, DecodeResult result) throws DecodeException {
        if (result == null || result.isBanProcess()) {
            return;
        }

        for (ResultProcessor resultProcessor : resultProcessorList) {
            resultProcessor.process(request, result);
        }
    }

    @Override
    public String getKey() {
        return LOG_NAME;
    }
}
