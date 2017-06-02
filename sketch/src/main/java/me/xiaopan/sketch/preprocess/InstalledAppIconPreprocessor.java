/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.preprocess;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.UriInfo;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

public class InstalledAppIconPreprocessor implements Preprocessor {

    public static final String INSTALLED_APP_URI_HOST = "installedApp";
    public static final String INSTALLED_APP_URI_PARAM_PACKAGE_NAME = "packageName";
    public static final String INSTALLED_APP_URI_PARAM_VERSION_CODE = "versionCode";

    private static final String LOG_NAME = "InstalledAppIconPreprocessor";

    @Override
    public boolean match(Context context, UriInfo uriInfo) {
        return uriInfo.getScheme() == UriScheme.FILE
                && uriInfo.getContent() != null
                && uriInfo.getContent().startsWith(INSTALLED_APP_URI_HOST);
    }

    @Override
    public PreProcessResult process(Context context, UriInfo uriInfo) {
        DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();

        DiskCache.Entry cacheEntry = diskCache.get(uriInfo.getDiskCacheKey());
        if (cacheEntry != null) {
            return new PreProcessResult(cacheEntry, ImageFrom.DISK_CACHE);
        }

        ReentrantLock diskCacheEditLock = diskCache.getEditLock(uriInfo.getDiskCacheKey());
        diskCacheEditLock.lock();

        PreProcessResult result;
        cacheEntry = diskCache.get(uriInfo.getDiskCacheKey());
        if (cacheEntry != null) {
            result = new PreProcessResult(cacheEntry, ImageFrom.DISK_CACHE);
        } else {
            result = readInstalledAppIcon(context, uriInfo, diskCache);
        }

        diskCacheEditLock.unlock();
        return result;
    }

    private PreProcessResult readInstalledAppIcon(Context context, UriInfo uriInfo, DiskCache diskCache) {
        Uri uri = Uri.parse(uriInfo.getUri());

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
        Bitmap iconBitmap = SketchUtils.readApkIcon(context, apkFilePath, false, LOG_NAME, bitmapPool);
        if (iconBitmap == null) {
            return null;
        }

        if (iconBitmap.isRecycled()) {
            if (SLogType.REQUEST.isEnabled()) {
                SLog.fw(SLogType.REQUEST, LOG_NAME, "apk icon bitmap recycled. %s", uriInfo.getUri());
            }
            return null;
        }

        DiskCache.Editor diskCacheEditor = diskCache.edit(uriInfo.getDiskCacheKey());
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
            DiskCache.Entry cacheEntry = diskCache.get(uriInfo.getDiskCacheKey());
            if (cacheEntry != null) {
                return new PreProcessResult(cacheEntry, ImageFrom.LOCAL);
            } else {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.fw(SLogType.REQUEST, LOG_NAME, "not found apk icon cache file. %s", uriInfo.getUri());
                }
                return null;
            }
        } else {
            return new PreProcessResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        }
    }
}
