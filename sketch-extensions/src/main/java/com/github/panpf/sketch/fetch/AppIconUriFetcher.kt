/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.fetch

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DrawableDataSource
import com.github.panpf.sketch.fetch.AppIconUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.util.DrawableFetcher
import com.github.panpf.sketch.util.ifOrNull

/**
 * Adds App icon support
 */
fun ComponentRegistry.Builder.supportAppIcon(): ComponentRegistry.Builder = apply {
    addFetcher(AppIconUriFetcher.Factory())
}

/**
 * Sample: 'app.icon://com.github.panpf.sketch.sample/1120'
 */
fun newAppIconUri(packageName: String, versionCode: Int): String =
    "$SCHEME://$packageName/$versionCode"

/**
 * Extract the icon of the installed app
 *
 * Support 'app.icon://com.github.panpf.sketch.sample/1120' uri
 */
class AppIconUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val packageName: String,
    val versionCode: Int,
) : Fetcher {

    companion object {
        const val SCHEME = "app.icon"
        const val MIME_TYPE = "application/vnd.android.app-icon"
    }

    @WorkerThread
    override suspend fun fetch(): FetchResult = FetchResult(
        DrawableDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.LOCAL,
            drawableFetcher = AppIconDrawableFetcher(packageName, versionCode)
        ),
        MIME_TYPE
    )

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): AppIconUriFetcher? {
            val uri = request.uriString.toUri()
            return ifOrNull(SCHEME.equals(uri.scheme, ignoreCase = true)) {
                val packageName = uri.authority
                    ?.takeIf { it.isNotEmpty() && it.isNotBlank() }
                    ?: throw UriInvalidException("App icon uri 'packageName' part invalid: ${request.uriString}")
                val versionCode = uri.lastPathSegment
                    ?.takeIf { it.isNotEmpty() && it.isNotBlank() }
                    ?.toIntOrNull()
                    ?: throw UriInvalidException("App icon uri 'versionCode' part invalid: ${request.uriString}")
                AppIconUriFetcher(sketch, request, packageName, versionCode)
            }
        }

        override fun toString(): String = "AppIconUriFetcher"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class AppIconDrawableFetcher(
        private val packageName: String,
        private val versionCode: Int,
    ) : DrawableFetcher {

        override fun getDrawable(context: Context): Drawable {
            val packageManager = context.packageManager
            val packageInfo: PackageInfo = try {
                packageManager.getPackageInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                throw Exception("Not found PackageInfo by '$packageName'", e)
            }
            @Suppress("DEPRECATION")
            if (packageInfo.versionCode != versionCode) {
                throw Exception("App versionCode mismatch, ${packageInfo.versionCode} != $versionCode")
            }
            return packageInfo.applicationInfo.loadIcon(packageManager)
                ?: throw Exception("loadIcon return null '$packageName'")
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
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
            return "AppIconDrawableFetcher(packageName='$packageName',versionCode=$versionCode)"
        }
    }
}