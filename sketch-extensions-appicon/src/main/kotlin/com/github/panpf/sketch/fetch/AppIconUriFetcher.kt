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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.fetch

import android.content.Context
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.drawable.AppIconDrawableFetcher
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.util.Uri

/**
 * Adds App icon support
 *
 * @see com.github.panpf.sketch.extensions.appicon.test.fetch.AppIconUriFetcherTest.testSupportAppIcon
 */
fun ComponentRegistry.Builder.supportAppIcon(): ComponentRegistry.Builder = apply {
    addFetcher(AppIconUriFetcher.Factory())
}

/**
 * Sample: 'app.icon://com.github.panpf.sketch.sample/1120'
 *
 * @see com.github.panpf.sketch.extensions.appicon.test.fetch.AppIconUriFetcherTest.testNewAppIconUri
 */
fun newAppIconUri(packageName: String, versionCode: Int): String =
    "${AppIconUriFetcher.SCHEME}://$packageName/$versionCode"

/**
 * Check if the uri is an app icon uri
 *
 * Support 'app.icon://com.github.panpf.sketch.sample/1120' uri
 *
 * @see com.github.panpf.sketch.extensions.appicon.test.fetch.AppIconUriFetcherTest.testIsAppIconUri
 */
fun isAppIconUri(uri: Uri): Boolean = AppIconUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true)

/**
 * Extract the icon of the installed app
 *
 * Support 'app.icon://com.github.panpf.sketch.sample/1120' uri
 *
 * @see com.github.panpf.sketch.extensions.appicon.test.fetch.AppIconUriFetcherTest
 */
class AppIconUriFetcher constructor(
    val context: Context,
    val packageName: String,
    val versionCode: Int,
) : Fetcher {

    companion object {
        const val SCHEME = "app.icon"
        const val IMAGE_MIME_TYPE = "image/png"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = Result.success(
        FetchResult(
            dataSource = DrawableDataSource(
                context = context,
                dataFrom = DataFrom.LOCAL,
                drawableFetcher = AppIconDrawableFetcher(packageName, versionCode),
            ),
            mimeType = IMAGE_MIME_TYPE
        )
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AppIconUriFetcher
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
        return "AppIconUriFetcher(packageName='$packageName', versionCode=$versionCode)"
    }

    class Factory : Fetcher.Factory {

        override fun create(requestContext: RequestContext): AppIconUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isAppIconUri(uri)) return null
            val packageName = uri.authority
                ?.takeIf { it.isNotEmpty() && it.isNotBlank() }
                ?: throw UriInvalidException("App icon uri 'packageName' part invalid: '${uri}'")
            val versionCode = uri.pathSegments.firstOrNull()
                ?.takeIf { it.isNotEmpty() && it.isNotBlank() }
                ?.toIntOrNull()
                ?: throw UriInvalidException("App icon uri 'versionCode' part invalid: '${uri}'")
            return AppIconUriFetcher(request.context, packageName, versionCode)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "AppIconUriFetcher"
        }
    }
}