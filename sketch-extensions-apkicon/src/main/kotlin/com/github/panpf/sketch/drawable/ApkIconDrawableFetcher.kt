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
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import java.io.File
import java.io.IOException

/**
 * Get the icon of the APK file
 *
 * @see com.github.panpf.sketch.extensions.apkicon.test.drawable.ApkIconDrawableFetcherTest
 */
class ApkIconDrawableFetcher(private val file: File) : DrawableFetcher {

    override val key: String = "ApkIconDrawable('$file')"

    override fun getDrawable(context: Context): Drawable {
        val packageManager = context.packageManager
        val packageInfo =
            packageManager.getPackageArchiveInfo(file.path, PackageManager.GET_ACTIVITIES)
                ?: throw IOException("getPackageArchiveInfo return null. ${file.path}")
        packageInfo.applicationInfo.sourceDir = file.path
        packageInfo.applicationInfo.publicSourceDir = file.path
        return packageManager.getApplicationIcon(packageInfo.applicationInfo)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ApkIconDrawableFetcher
        if (file != other.file) return false
        return true
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }

    override fun toString(): String = "ApkIconDrawableFetcher(file='$file')"
}