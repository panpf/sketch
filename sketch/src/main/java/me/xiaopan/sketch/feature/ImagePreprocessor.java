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

package me.xiaopan.sketch.feature;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 图片预处理器，可读取APK文件的图标以及根据包名和版本号读取已安装APP的图标
 */
public class ImagePreprocessor implements Identifier {

    private static final String INSTALLED_APP_URI_HOST = "installedApp";
    private static final String INSTALLED_APP_URI_PARAM_PACKAGE_NAME = "packageName";
    private static final String INSTALLED_APP_URI_PARAM_VERSION_CODE = "versionCode";

    protected String logName = "ImagePreprocessor";

    public static String createInstalledAppIconUri(String packageName, int versionCode) {
        return SketchUtils.concat(
                UriScheme.FILE.getSecondaryUriPrefix(),
                INSTALLED_APP_URI_HOST,
                "?",
                INSTALLED_APP_URI_PARAM_PACKAGE_NAME, "=", packageName,
                "&",
                INSTALLED_APP_URI_PARAM_VERSION_CODE, "=", versionCode);
    }

    public boolean isSpecific(LoadRequest loadRequest) {
        return isApkFile(loadRequest) || isInstalledApp(loadRequest);
    }

    public PreProcessResult process(LoadRequest loadRequest) {
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
        return logName;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(logName);
    }

    private boolean isApkFile(LoadRequest loadRequest) {
        return loadRequest.getAttrs().getUriScheme() == UriScheme.FILE
                && SketchUtils.checkSuffix(loadRequest.getAttrs().getRealUri(), ".apk");
    }

    private boolean isInstalledApp(LoadRequest loadRequest) {
        return loadRequest.getAttrs().getUriScheme() == UriScheme.FILE
                && loadRequest.getAttrs().getRealUri().startsWith(INSTALLED_APP_URI_HOST);
    }

    /**
     * 获取APK图标的缓存
     */
    private PreProcessResult getApkIconDiskCache(LoadRequest loadRequest) {
        String realUri = loadRequest.getAttrs().getRealUri();
        Configuration configuration = loadRequest.getSketch().getConfiguration();

        File apkFile = new File(realUri);
        if (!apkFile.exists()) {
            return null;
        }
        long lastModifyTime = apkFile.lastModified();
        String diskCacheKey = realUri + "." + lastModifyTime;

        ReentrantLock diskCacheEditLock = configuration.getDiskCache().getEditLock(diskCacheKey);
        if (diskCacheEditLock != null) {
            diskCacheEditLock.lock();
        }
        PreProcessResult result = readApkIcon(configuration, loadRequest, diskCacheKey, realUri);
        if (diskCacheEditLock != null) {
            diskCacheEditLock.unlock();
        }
        return result;
    }

    private PreProcessResult readApkIcon(Configuration configuration, LoadRequest loadRequest, String diskCacheKey, String realUri) {
        DiskCache.Entry apkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (apkIconDiskCacheEntry != null) {
            return new PreProcessResult(apkIconDiskCacheEntry, ImageFrom.DISK_CACHE);
        }

        Bitmap iconBitmap = SketchUtils.readApkIcon(
                configuration.getContext(),
                realUri,
                loadRequest.getOptions().isLowQualityImage(),
                logName);
        if (iconBitmap == null) {
            return null;
        }
        if (iconBitmap.isRecycled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "apk icon bitmap recycled",
                        " - ", loadRequest.getAttrs().getId()));
            }
            return null;
        }

        DiskCache.Editor diskCacheEditor = configuration.getDiskCache().edit(diskCacheKey);
        OutputStream outputStream = null;
        try {
            if (diskCacheEditor != null) {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } else {
                outputStream = new ByteArrayOutputStream();
            }
            iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            if (diskCacheEditor != null) {
                diskCacheEditor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (diskCacheEditor != null) {
                diskCacheEditor.abort();
            }
        } finally {
            iconBitmap.recycle();
            SketchUtils.close(outputStream);
        }

        if (diskCacheEditor != null) {
            apkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
            if (apkIconDiskCacheEntry == null) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(logName,
                            " - ", "not found apk icon cache file",
                            " - ", loadRequest.getAttrs().getId()));
                }
                return null;
            }
            return new PreProcessResult(apkIconDiskCacheEntry, ImageFrom.LOCAL);
        } else if (outputStream != null) {
            return new PreProcessResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        } else {
            return null;
        }
    }

    /**
     * 获取已安装APP图标的缓存
     */
    private PreProcessResult getInstalledAppIconDiskCache(LoadRequest loadRequest) {
        String diskCacheKey = loadRequest.getAttrs().getUri();
        Configuration configuration = loadRequest.getSketch().getConfiguration();

        ReentrantLock diskCacheEditLock = configuration.getDiskCache().getEditLock(diskCacheKey);
        if (diskCacheEditLock != null) {
            diskCacheEditLock.lock();
        }
        PreProcessResult result = readInstalledAppIcon(configuration, loadRequest, diskCacheKey);
        if (diskCacheEditLock != null) {
            diskCacheEditLock.unlock();
        }
        return result;
    }

    private PreProcessResult readInstalledAppIcon(Configuration configuration, LoadRequest loadRequest, String diskCacheKey) {
        DiskCache.Entry appIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (appIconDiskCacheEntry != null) {
            return new PreProcessResult(appIconDiskCacheEntry, ImageFrom.DISK_CACHE);
        }

        Uri uri = Uri.parse(loadRequest.getAttrs().getUri());

        String packageName = uri.getQueryParameter(INSTALLED_APP_URI_PARAM_PACKAGE_NAME);
        int versionCode = Integer.valueOf(uri.getQueryParameter(INSTALLED_APP_URI_PARAM_VERSION_CODE));

        PackageInfo packageInfo;
        try {
            packageInfo = configuration.getContext().getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (packageInfo.versionCode != versionCode) {
            return null;
        }

        Bitmap iconBitmap = SketchUtils.readApkIcon(
                configuration.getContext(),
                packageInfo.applicationInfo.sourceDir,
                loadRequest.getOptions().isLowQualityImage(),
                logName);
        if (iconBitmap == null) {
            return null;
        }

        if (iconBitmap.isRecycled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "apk icon bitmap recycled",
                        " - ", loadRequest.getAttrs().getId()));
            }
            return null;
        }

        DiskCache.Editor diskCacheEditor = configuration.getDiskCache().edit(diskCacheKey);
        OutputStream outputStream = null;
        try {
            if (diskCacheEditor != null) {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } else {
                outputStream = new ByteArrayOutputStream();
            }
            iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            if (diskCacheEditor != null) {
                diskCacheEditor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (diskCacheEditor != null) {
                diskCacheEditor.abort();
            }
        } finally {
            iconBitmap.recycle();
            SketchUtils.close(outputStream);
        }

        if (diskCacheEditor != null) {
            appIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
            if (appIconDiskCacheEntry == null) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(logName,
                            " - ", "not found apk icon cache file",
                            " - ", loadRequest.getAttrs().getId()));
                }
                return null;
            }
            return new PreProcessResult(appIconDiskCacheEntry, ImageFrom.LOCAL);
        } else if (outputStream != null) {
            return new PreProcessResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        } else {
            return null;
        }
    }
}
