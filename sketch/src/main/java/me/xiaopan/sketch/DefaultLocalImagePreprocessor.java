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

package me.xiaopan.sketch;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 默认的本地图片预处理器，可解析APK文件的图标
 */
public class DefaultLocalImagePreprocessor implements LocalImagePreprocessor {
    private static final String NAME = "DefaultLocalImagePreprocessor";

    @Override
    public boolean isSpecific(LoadRequest loadRequest) {
        return isApkFile(loadRequest);
    }

    @Override
    public DiskCache.Entry getDiskCacheEntry(LoadRequest loadRequest) {
        if(isApkFile(loadRequest)){
            return getApkIconCacheFile(loadRequest);
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }

    private boolean isApkFile(LoadRequest loadRequest){
        return loadRequest.getAttrs().getUriScheme() == UriScheme.FILE && SketchUtils.checkSuffix(loadRequest.getAttrs().getUri(), ".apk");
    }

    /**
     * 获取APK图标的缓存文件
     *
     * @return APK图标的缓存文件
     */
    private DiskCache.Entry getApkIconCacheFile(LoadRequest loadRequest) {
        String uri = loadRequest.getAttrs().getUri();
        Configuration configuration = loadRequest.getAttrs().getSketch().getConfiguration();

        File apkFile = new File(uri);
        if (!apkFile.exists()) {
            return null;
        }
        long lastModifyTime = apkFile.lastModified();
        String diskCacheKey = uri + "." + lastModifyTime;

        DiskCache.Entry apkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (apkIconDiskCacheEntry != null) {
            return apkIconDiskCacheEntry;
        }

        Bitmap iconBitmap = SketchUtils.decodeIconFromApk(configuration.getContext(), uri, loadRequest.getOptions().isLowQualityImage(), NAME);
        if (iconBitmap == null) {
            return null;
        }

        if (iconBitmap.isRecycled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "apk icon bitmap recycled", " - ", uri));
            }
            return null;
        }

        DiskLruCache.Editor editor = configuration.getDiskCache().edit(diskCacheKey);
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(editor.newOutputStream(0), 8 * 1024);
            iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            SketchUtils.close(outputStream);
        }

        apkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (apkIconDiskCacheEntry == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "not found apk icon cache file", " - ", uri));
            }
        }

        return apkIconDiskCacheEntry;
    }
}
