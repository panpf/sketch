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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.DiskLruCache;
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
    public String getKey() {
        return logName;
    }

    private boolean isApkFile(LoadRequest loadRequest) {
        return loadRequest.getUriScheme() == UriScheme.FILE
                && SketchUtils.checkSuffix(loadRequest.getRealUri(), ".apk");
    }

    private boolean isInstalledApp(LoadRequest loadRequest) {
        return loadRequest.getUriScheme() == UriScheme.FILE
                && loadRequest.getRealUri().startsWith(INSTALLED_APP_URI_HOST);
    }

    /**
     * 获取APK图标的缓存
     */
    private PreProcessResult getApkIconDiskCache(LoadRequest loadRequest) {
        String realUri = loadRequest.getRealUri();
        Configuration configuration = loadRequest.getConfiguration();

        File apkFile = new File(realUri);
        if (!apkFile.exists()) {
            return null;
        }
        long lastModifyTime = apkFile.lastModified();
        String diskCacheKey = realUri + "." + lastModifyTime;

        DiskCache diskCache = configuration.getDiskCache();
        ReentrantLock diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
        diskCacheEditLock.lock();

        PreProcessResult result = readApkIcon(configuration.getContext(), diskCache, loadRequest, diskCacheKey, realUri);

        diskCacheEditLock.unlock();
        return result;
    }

    private PreProcessResult readApkIcon(Context context, DiskCache diskCache, LoadRequest loadRequest, String diskCacheKey, String realUri) {
        DiskCache.Entry apkIconDiskCacheEntry = diskCache.get(diskCacheKey);
        if (apkIconDiskCacheEntry != null) {
            return new PreProcessResult(apkIconDiskCacheEntry, ImageFrom.DISK_CACHE);
        }

        BitmapPool bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();
        boolean lowQualityImage = loadRequest.getOptions().isLowQualityImage();
        Bitmap iconBitmap = SketchUtils.readApkIcon(context, realUri, lowQualityImage, logName, bitmapPool);
        if (iconBitmap == null) {
            return null;
        }
        if (iconBitmap.isRecycled()) {
            if (SLogType.REQUEST.isEnabled()) {
                SLog.w(SLogType.REQUEST, logName, "apk icon bitmap recycled. %s", loadRequest.getKey());
            }
            return null;
        }

        DiskCache.Editor diskCacheEditor = diskCache.edit(diskCacheKey);
        OutputStream outputStream;
        if (diskCacheEditor != null) {
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
                BitmapPoolUtils.freeBitmapToPool(iconBitmap, bitmapPool);
                diskCacheEditor.abort();
                return null;
            }
        } else {
            outputStream = new ByteArrayOutputStream();
        }

        try {
            iconBitmap.compress(SketchUtils.bitmapConfigToCompressFormat(iconBitmap.getConfig()), 100, outputStream);

            if (diskCacheEditor != null) {
                diskCacheEditor.commit();
            }
        } catch (DiskLruCache.EditorChangedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (DiskLruCache.ClosedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (DiskLruCache.FileNotExistException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } finally {
            BitmapPoolUtils.freeBitmapToPool(iconBitmap, bitmapPool);
            SketchUtils.close(outputStream);
        }

        if (diskCacheEditor != null) {
            apkIconDiskCacheEntry = diskCache.get(diskCacheKey);
            if (apkIconDiskCacheEntry != null) {
                return new PreProcessResult(apkIconDiskCacheEntry, ImageFrom.LOCAL);
            } else {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.w(SLogType.REQUEST, logName, "not found apk icon cache file. %s", loadRequest.getKey());
                }
                return null;
            }
        } else {
            return new PreProcessResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        }
    }

    /**
     * 获取已安装APP图标的缓存
     */
    private PreProcessResult getInstalledAppIconDiskCache(LoadRequest loadRequest) {
        String diskCacheKey = loadRequest.getUri();
        Configuration configuration = loadRequest.getConfiguration();

        DiskCache diskCache = configuration.getDiskCache();
        ReentrantLock diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
        diskCacheEditLock.lock();

        PreProcessResult result = readInstalledAppIcon(configuration.getContext(), diskCache, loadRequest, diskCacheKey);

        diskCacheEditLock.unlock();
        return result;
    }

    private PreProcessResult readInstalledAppIcon(Context context, DiskCache diskCache, LoadRequest loadRequest, String diskCacheKey) {
        DiskCache.Entry appIconDiskCacheEntry = diskCache.get(diskCacheKey);
        if (appIconDiskCacheEntry != null) {
            return new PreProcessResult(appIconDiskCacheEntry, ImageFrom.DISK_CACHE);
        }

        Uri uri = Uri.parse(loadRequest.getUri());

        String packageName = uri.getQueryParameter(INSTALLED_APP_URI_PARAM_PACKAGE_NAME);
        int versionCode = Integer.valueOf(uri.getQueryParameter(INSTALLED_APP_URI_PARAM_VERSION_CODE));

        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (packageInfo.versionCode != versionCode) {
            return null;
        }

        String apkFilePath = packageInfo.applicationInfo.sourceDir;
        BitmapPool bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();
        boolean lowQualityImage = loadRequest.getOptions().isLowQualityImage();
        Bitmap iconBitmap = SketchUtils.readApkIcon(context, apkFilePath, lowQualityImage, logName, bitmapPool);
        if (iconBitmap == null) {
            return null;
        }

        if (iconBitmap.isRecycled()) {
            if (SLogType.REQUEST.isEnabled()) {
                SLog.w(SLogType.REQUEST, logName, "apk icon bitmap recycled. %s", loadRequest.getKey());
            }
            return null;
        }

        DiskCache.Editor diskCacheEditor = diskCache.edit(diskCacheKey);
        OutputStream outputStream;
        if (diskCacheEditor != null) {
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
                BitmapPoolUtils.freeBitmapToPool(iconBitmap, bitmapPool);
                diskCacheEditor.abort();
                return null;
            }
        } else {
            outputStream = new ByteArrayOutputStream();
        }

        try {
            iconBitmap.compress(SketchUtils.bitmapConfigToCompressFormat(iconBitmap.getConfig()), 100, outputStream);

            if (diskCacheEditor != null) {
                diskCacheEditor.commit();
            }
        } catch (DiskLruCache.EditorChangedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (DiskLruCache.ClosedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (DiskLruCache.FileNotExistException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } finally {
            BitmapPoolUtils.freeBitmapToPool(iconBitmap, bitmapPool);
            SketchUtils.close(outputStream);
        }

        if (diskCacheEditor != null) {
            appIconDiskCacheEntry = diskCache.get(diskCacheKey);
            if (appIconDiskCacheEntry != null) {
                return new PreProcessResult(appIconDiskCacheEntry, ImageFrom.LOCAL);
            } else {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.w(SLogType.REQUEST, logName, "not found apk icon cache file. %s", loadRequest.getKey());
                }
                return null;
            }
        } else {
            return new PreProcessResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        }
    }
}
