/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.util.SketchUtils;

/**
 * {@link Sketch} 明确支持的图片格式
 */
// 转成一个 support Types 列表，支持根据 mimeType get
public enum ImageType {
    JPEG("image/jpeg", Bitmap.Config.RGB_565, Bitmap.Config.RGB_565),
    PNG("image/png", Bitmap.Config.ARGB_8888, SketchUtils.isDisabledARGB4444() ? Bitmap.Config.ARGB_8888 : Bitmap.Config.ARGB_4444),
    WEBP("image/webp", Bitmap.Config.ARGB_8888, SketchUtils.isDisabledARGB4444() ? Bitmap.Config.ARGB_8888 : Bitmap.Config.ARGB_4444),
    GIF("image/gif", Bitmap.Config.ARGB_8888, SketchUtils.isDisabledARGB4444() ? Bitmap.Config.ARGB_8888 : Bitmap.Config.ARGB_4444),
    BMP("image/bmp", Bitmap.Config.RGB_565, Bitmap.Config.RGB_565),
    ;

    @NonNull
    String mimeType;
    @NonNull
    Bitmap.Config bestConfig;
    @NonNull
    Bitmap.Config lowQualityConfig;

    ImageType(@NonNull String mimeType, @NonNull Bitmap.Config bestConfig, @NonNull Bitmap.Config lowQualityConfig) {
        this.mimeType = mimeType;
        this.bestConfig = bestConfig;
        this.lowQualityConfig = lowQualityConfig;
    }

    @Nullable
    public static ImageType valueOfMimeType(@Nullable String mimeType) {
        if (ImageType.JPEG.mimeType.equalsIgnoreCase(mimeType)) {
            return ImageType.JPEG;
        } else if (ImageType.PNG.mimeType.equalsIgnoreCase(mimeType)) {
            return ImageType.PNG;
        } else if (ImageType.WEBP.mimeType.equalsIgnoreCase(mimeType)) {
            return ImageType.WEBP;
        } else if (ImageType.GIF.mimeType.equalsIgnoreCase(mimeType)) {
            return ImageType.GIF;
        } else if (ImageType.BMP.mimeType.equalsIgnoreCase(mimeType)) {
            return ImageType.BMP;
        } else {
            return null;
        }
    }

    @NonNull
    public Bitmap.Config getConfig(boolean lowQualityImage) {
        return lowQualityImage ? lowQualityConfig : bestConfig;
    }

    @NonNull
    public String getMimeType() {
        return mimeType;
    }

    public void setBestConfig(@NonNull Bitmap.Config bestConfig) {
        //noinspection ConstantConditions
        if (bestConfig != null) {
            this.bestConfig = bestConfig;
        }
    }

    public void setLowQualityConfig(@NonNull Bitmap.Config lowQualityConfig) {
        //noinspection ConstantConditions
        if (lowQualityConfig != null) {
            if (lowQualityConfig == Bitmap.Config.ARGB_4444 && SketchUtils.isDisabledARGB4444()) {
                lowQualityConfig = Bitmap.Config.ARGB_8888;
            }
            this.lowQualityConfig = lowQualityConfig;
        }
    }

    public boolean equals(@Nullable String mimeType) {
        return this.mimeType.equalsIgnoreCase(mimeType);
    }
}