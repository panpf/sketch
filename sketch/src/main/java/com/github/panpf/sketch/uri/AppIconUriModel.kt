/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.uri

import android.content.Context
import com.github.panpf.sketch.uri.AbsBitmapDiskCacheUriModel
import com.github.panpf.sketch.uri.AppIconUriModel
import com.github.panpf.sketch.uri.GetDataSourceException
import android.graphics.Bitmap
import com.github.panpf.sketch.SLog
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.util.SketchUtils
import java.lang.NumberFormatException
import java.util.*

class AppIconUriModel : AbsBitmapDiskCacheUriModel() {

    companion object {
        const val SCHEME = "app.icon://"
        private const val NAME = "AppIconUriModel"

        @JvmStatic
        fun makeUri(packageName: String, versionCode: Int): String {
            return "$SCHEME$packageName/$versionCode"
        }
    }

    override fun match(uri: String): Boolean {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME)
    }

    @Throws(GetDataSourceException::class)
    override fun getContent(context: Context, uri: String): Bitmap {
        val imageUri = Uri.parse(uri)
        val packageName = imageUri.host
        var path = imageUri.path
        if (path != null && path.startsWith("/")) {
            path = path.substring(1)
        }
        val versionCode: Int = try {
            Integer.valueOf(path)
        } catch (e: NumberFormatException) {
            val cause = String.format("Conversion app versionCode failed. %s", uri)
            SLog.emt(NAME, e, cause)
            throw GetDataSourceException(cause, e)
        }
        val packageInfo: PackageInfo = try {
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            val cause = String.format("Not found PackageInfo by \"%s\". %s", packageName, uri)
            SLog.emt(NAME, e, cause)
            throw GetDataSourceException(cause, e)
        }
        if (packageInfo.versionCode != versionCode) {
            val cause = String.format(
                Locale.US,
                "App versionCode mismatch, %d != %d. %s",
                packageInfo.versionCode,
                versionCode,
                uri
            )
            SLog.em(NAME, cause)
            throw GetDataSourceException(cause)
        }
        val apkFilePath = packageInfo.applicationInfo.sourceDir
        val bitmapPool = Sketch.with(context).configuration.bitmapPool
        val iconBitmap = SketchUtils.readApkIcon(context, apkFilePath, false, NAME, bitmapPool)
        if (iconBitmap == null || iconBitmap.isRecycled) {
            val cause = String.format("App icon bitmap invalid. %s", uri)
            SLog.em(NAME, cause)
            throw GetDataSourceException(cause)
        }
        return iconBitmap
    }
}