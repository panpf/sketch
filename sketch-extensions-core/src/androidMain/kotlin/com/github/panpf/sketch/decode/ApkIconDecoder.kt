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

package com.github.panpf.sketch.decode

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.drawable.DrawableFetcher
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DrawableDataSource
import java.io.File
import java.io.IOException

/**
 * Adds Apk icon support
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.decode.ApkIconDecoderTest.testSupportApkIcon
 */
fun ComponentRegistry.Builder.supportApkIcon(): ComponentRegistry.Builder = apply {
    addDecoder(ApkIconDecoder.Factory())
}

/**
 * Extract the icon of the Apk file and convert it to Bitmap
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.decode.ApkIconDecoderTest
 */
class ApkIconDecoder(
    requestContext: RequestContext,
    dataFrom: DataFrom,
    file: File
) : DrawableDecoder(
    requestContext = requestContext,
    drawableDataSource = DrawableDataSource(
        context = requestContext.sketch.context,
        dataFrom = dataFrom,
        drawableFetcher = ApkIconDrawableFetcher(file),
    ),
    mimeType = IMAGE_MIME_TYPE
) {

    companion object {
        const val MIME_TYPE = "application/vnd.android.package-archive"
        const val IMAGE_MIME_TYPE = "image/png"
    }

    class Factory : Decoder.Factory {

        override val key: String = "ApkIconDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            val dataSource = fetchResult.dataSource
            return if (MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)) {
                ApkIconDecoder(
                    requestContext = requestContext,
                    dataFrom = fetchResult.dataFrom,
                    file = dataSource.getFile(requestContext.sketch).toFile()
                )
            } else {
                null
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "ApkIconDecoder"
    }

    class ApkIconDrawableFetcher(private val file: File) : DrawableFetcher {

        override val key: String = "ApkIconDrawableFetcher($file)"

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

        override fun toString(): String {
            return "ApkIconDrawableFetcher(file=$file)"
        }
    }
}