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

package me.xiaopan.sketch.uri;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.decode.ByteArrayDataSource;
import me.xiaopan.sketch.decode.DataSource;
import me.xiaopan.sketch.decode.DiskCacheDataSource;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

public class AppIconUriModel extends UriModel {

    public static final String SCHEME = "app.icon://";
    private static final String NAME = "AppIconUriModel";

    public static String makeUri(String packageName, int versionCode) {
        return SCHEME + packageName + "/" + versionCode;
    }

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    /**
     * 获取 uri 所真正包含的内容部分，对于 "app.icon://me.xiaopan.sketchsmaple/240" 格式的 uri 来说，就是返回它自己
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，对于 "app.icon://me.xiaopan.sketchsmaple/240" 格式的 uri 来说，就是返回它自己
     */
    @Override
    public String getUriContent(@NonNull String uri) {
        return uri;
    }

    @Override
    public DataSource getDataSource(@NonNull Context context, @NonNull String uri, DownloadResult downloadResult) {
        DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();

        DiskCache.Entry cacheEntry = diskCache.get(getDiskCacheKey(uri));
        if (cacheEntry != null) {
            return new DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE);
        }

        ReentrantLock diskCacheEditLock = diskCache.getEditLock(getDiskCacheKey(uri));
        diskCacheEditLock.lock();

        DataSource dataSource;
        try {
            cacheEntry = diskCache.get(getDiskCacheKey(uri));
            if (cacheEntry != null) {
                dataSource = new DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE);
            } else {
                dataSource = readAppIcon(context, uri, diskCache);
            }
        } finally {
            diskCacheEditLock.unlock();
        }

        return dataSource;
    }

    private DataSource readAppIcon(Context context, String uri, DiskCache diskCache) {
        Uri imageUri = Uri.parse(uri);

        String packageName = imageUri.getHost();

        String path = imageUri.getPath();
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        int versionCode;
        try {
            versionCode = Integer.valueOf(path);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            SLog.e(NAME, "Conversion app versionCode failed. %s", uri);
            return null;
        }

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
        Bitmap iconBitmap = SketchUtils.readApkIcon(context, apkFilePath, false, NAME, bitmapPool);
        if (iconBitmap == null) {
            return null;
        }

        if (iconBitmap.isRecycled()) {
            SLog.e(NAME, "App icon bitmap recycled. %s", uri);
            return null;
        }

        DiskCache.Editor diskCacheEditor = diskCache.edit(getDiskCacheKey(uri));
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
            DiskCache.Entry cacheEntry = diskCache.get(getDiskCacheKey(uri));
            if (cacheEntry != null) {
                return new DiskCacheDataSource(cacheEntry, ImageFrom.LOCAL);
            } else {
                SLog.e(NAME, "Not found app icon cache file. %s", uri);
                return null;
            }
        } else {
            return new ByteArrayDataSource(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        }
    }
}
