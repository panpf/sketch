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

package me.panpf.sketch.uri;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.util.SketchUtils;

public class AppIconUriModel extends AbsBitmapDiskCacheUriModel {

    public static final String SCHEME = "app.icon://";
    private static final String NAME = "AppIconUriModel";

    @NonNull
    public static String makeUri(@NonNull String packageName, int versionCode) {
        return SCHEME + packageName + "/" + versionCode;
    }

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    @NonNull
    @Override
    protected Bitmap getContent(@NonNull Context context, @NonNull String uri) throws GetDataSourceException {
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
            String cause = String.format("Conversion app versionCode failed. %s", uri);
            SLog.e(NAME, e, cause);
            throw new GetDataSourceException(cause, e);
        }

        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            String cause = String.format("Not found PackageInfo by \"%s\". %s", packageName, uri);
            SLog.e(NAME, e, cause);
            throw new GetDataSourceException(cause, e);
        }

        if (packageInfo.versionCode != versionCode) {
            String cause = String.format("App versionCode mismatch, %d != %d. %s", packageInfo.versionCode, versionCode, uri);
            SLog.e(NAME, cause);
            throw new GetDataSourceException(cause);
        }

        String apkFilePath = packageInfo.applicationInfo.sourceDir;
        BitmapPool bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();
        Bitmap iconBitmap = SketchUtils.readApkIcon(context, apkFilePath, false, NAME, bitmapPool);

        if (iconBitmap == null || iconBitmap.isRecycled()) {
            String cause = String.format("App icon bitmap invalid. %s", uri);
            SLog.e(NAME, cause);
            throw new GetDataSourceException(cause);
        }

        return iconBitmap;
    }
}
