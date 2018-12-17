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

import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import me.panpf.sketch.ErrorTracker;
import me.panpf.sketch.SLog;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.drawable.SketchGifFactory;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.LoadRequest;

public class GifDecodeHelper extends DecodeHelper {

    @Override
    public boolean match(@NonNull LoadRequest request, @NonNull DataSource dataSource, @Nullable ImageType imageType, @NonNull BitmapFactory.Options boundOptions) {
        if (imageType != null && imageType == ImageType.GIF && request.getOptions().isDecodeGifImage()) {
            if (SketchGifFactory.isExistGifLibrary()) {
                return true;
            } else {
                SLog.e("GifDecodeHelper", "Not found libpl_droidsonroids_gif.so. Please go to “https://github.com/panpf/sketch” find how to import the sketch-gif library");
            }
        }
        return false;
    }

    @NonNull
    @Override
    public DecodeResult decode(@NonNull LoadRequest request, @NonNull DataSource dataSource, @Nullable ImageType imageType, @NonNull BitmapFactory.Options boundOptions,
                               @NonNull BitmapFactory.Options decodeOptions, int exifOrientation) throws DecodeException {
        try {
            ImageAttrs imageAttrs = new ImageAttrs(boundOptions.outMimeType, boundOptions.outWidth, boundOptions.outHeight, exifOrientation);
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            SketchGifDrawable gifDrawable = dataSource.makeGifDrawable(request.getKey(), request.getUri(), imageAttrs, bitmapPool);

            return new GifDecodeResult(imageAttrs, gifDrawable).setBanProcess(true);
        } catch (IOException e) {
            throw new DecodeException(e, ErrorCause.DECODE_FILE_IO_EXCEPTION);
        } catch (NotFoundGifLibraryException e) {
            throw new DecodeException(e, ErrorCause.DECODE_NOT_FOUND_GIF_LIBRARY);
        } catch (UnsatisfiedLinkError | ExceptionInInitializerError e) {
            request.getConfiguration().getErrorTracker().onNotFoundGifSoError(e);
            throw new DecodeException(e, ErrorCause.DECODE_NO_MATCHING_GIF_SO);
        } catch (Throwable e) {
            ErrorTracker errorTracker = request.getConfiguration().getErrorTracker();
            errorTracker.onDecodeGifImageError(e, request, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
            throw new DecodeException(e, ErrorCause.DECODE_UNABLE_CREATE_GIF_DRAWABLE);
        }
    }
}
