/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.drawable

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.internal.versionCodeCompat

/**
 * Get the icon of the installed app
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.drawable.AppIconDrawableFetcherTest
 */
class AppIconDrawableFetcher(
    private val packageName: String,
    private val versionCode: Int,
) : DrawableFetcher {

    override val key: String =
        "AppIconDrawableFetcher(packageName='$packageName',versionCode=$versionCode)"

    override fun getDrawable(context: Context): Drawable {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo = try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            throw Exception("Not found PackageInfo by '$packageName'", e)
        }
        val appVersionCode = packageInfo.versionCodeCompat
        if (appVersionCode != versionCode) {
            throw Exception("App versionCode mismatch, $appVersionCode != $versionCode")
        }
        return packageInfo.applicationInfo.loadIcon(packageManager)
            ?: throw Exception("loadIcon return null '$packageName'")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AppIconDrawableFetcher
        if (packageName != other.packageName) return false
        if (versionCode != other.versionCode) return false
        return true
    }

    override fun hashCode(): Int {
        var result = packageName.hashCode()
        result = 31 * result + versionCode
        return result
    }

    override fun toString(): String {
        return "AppIconDrawableFetcher(packageName='$packageName', versionCode=$versionCode)"
    }
}