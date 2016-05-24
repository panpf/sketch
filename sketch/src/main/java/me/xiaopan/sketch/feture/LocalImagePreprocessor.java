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

package me.xiaopan.sketch.feture;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 本地图片预处理器，可读取APK文件的图标以及根据包名和版本号读取已安装APP的图标
 */
public class LocalImagePreprocessor implements Identifier {
    private static final String NAME = "LocalImagePreprocessor";

    private static final String INSTALLED_APP_URI_HOST = "installedApp";
    private static final String INSTALLED_APP_URI_PARAM_PACKAGE_NAME = "packageName";
    private static final String INSTALLED_APP_URI_PARAM_VERSION_CODE = "versionCode";

    public boolean isSpecific(LoadRequest loadRequest) {
        return isApkFile(loadRequest) || isInstalledApp(loadRequest);
    }

    public DiskCache.Entry getDiskCacheEntry(LoadRequest loadRequest) {
        if (isApkFile(loadRequest)) {
            return getApkIconDiskCache(loadRequest);
        }

        if (isInstalledApp(loadRequest)) {
            return getInstalledAppIconDiskCache(loadRequest);
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

    private boolean isApkFile(LoadRequest loadRequest) {
        return loadRequest.getRequestAttrs().getUriScheme() == UriScheme.FILE && SketchUtils.checkSuffix(loadRequest.getRequestAttrs().getRealUri(), ".apk");
    }

    private boolean isInstalledApp(LoadRequest loadRequest) {
        return loadRequest.getRequestAttrs().getUriScheme() == UriScheme.FILE && loadRequest.getRequestAttrs().getRealUri().startsWith(INSTALLED_APP_URI_HOST);
    }

    /**
     * 获取APK图标的缓存文件
     *
     * @return APK图标的缓存文件
     */
    private DiskCache.Entry getApkIconDiskCache(LoadRequest loadRequest) {
        String realUri = loadRequest.getRequestAttrs().getRealUri();
        Configuration configuration = loadRequest.getSketch().getConfiguration();

        File apkFile = new File(realUri);
        if (!apkFile.exists()) {
            return null;
        }
        long lastModifyTime = apkFile.lastModified();
        String diskCacheKey = realUri + "." + lastModifyTime;

        DiskCache.Entry apkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (apkIconDiskCacheEntry != null) {
            return apkIconDiskCacheEntry;
        }

        Bitmap iconBitmap = SketchUtils.decodeIconFromApk(configuration.getContext(), realUri, loadRequest.getOptions().isLowQualityImage(), NAME);
        if (iconBitmap == null) {
            return null;
        }

        if (iconBitmap.isRecycled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "apk icon bitmap recycled", " - ", realUri));
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
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            SketchUtils.close(outputStream);
        }

        apkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (apkIconDiskCacheEntry == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "not found apk icon cache file", " - ", realUri));
            }
        }

        return apkIconDiskCacheEntry;
    }

    private DiskCache.Entry getInstalledAppIconDiskCache(LoadRequest loadRequest) {
        String realUri = loadRequest.getRequestAttrs().getRealUri();
        Configuration configuration = loadRequest.getSketch().getConfiguration();

        String diskCacheKey = loadRequest.getRequestAttrs().getDiskCacheKey();

        DiskCache.Entry appIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (appIconDiskCacheEntry != null) {
            return appIconDiskCacheEntry;
        }

        Uri uri = Uri.parse(loadRequest.getRequestAttrs().getUri());

        String packageName = uri.getQueryParameter(INSTALLED_APP_URI_PARAM_PACKAGE_NAME);
        int versionCode = Integer.valueOf(uri.getQueryParameter(INSTALLED_APP_URI_PARAM_VERSION_CODE));

        PackageInfo packageInfo;
        try {
            packageInfo = loadRequest.getSketch().getConfiguration().getContext().getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (packageInfo.versionCode != versionCode) {
            return null;
        }

        Bitmap iconBitmap = SketchUtils.decodeIconFromApk(configuration.getContext(), packageInfo.applicationInfo.sourceDir, loadRequest.getOptions().isLowQualityImage(), NAME);
        if (iconBitmap == null) {
            return null;
        }

        if (iconBitmap.isRecycled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "apk icon bitmap recycled", " - ", realUri));
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
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            SketchUtils.close(outputStream);
        }

        appIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (appIconDiskCacheEntry == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "not found apk icon cache file", " - ", realUri));
            }
        }

        return appIconDiskCacheEntry;
    }

    public static String createInstalledAppIconUri(String packageName, int versionCode) {
        return SketchUtils.concat(UriScheme.FILE.getSecondaryUriPrefix(), INSTALLED_APP_URI_HOST, "?", INSTALLED_APP_URI_PARAM_PACKAGE_NAME, "=", packageName, "&", INSTALLED_APP_URI_PARAM_VERSION_CODE, "=", versionCode);
    }
}
