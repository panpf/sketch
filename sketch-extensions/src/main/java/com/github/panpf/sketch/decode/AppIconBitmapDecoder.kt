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
package com.github.panpf.sketch.decode

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.AppIconUriFetcher.AppIconDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.toNewBitmap

/**
 * Extract the icon of the installed app and convert it to Bitmap
 */
class AppIconBitmapDecoder(
    private val sketch: Sketch,
    private val requestContext: RequestContext,
    private val packageName: String,
    private val versionCode: Int,
) : BitmapDecoder {

    companion object {
        const val MODULE = "AppIconBitmapDecoder"
    }

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val request = requestContext.request
        val packageManager = request.context.packageManager
        val packageInfo: PackageInfo = try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            throw Exception("Not found PackageInfo by '$packageName'", e)
        }
        @Suppress("DEPRECATION")
        if (packageInfo.versionCode != versionCode) {
            throw Exception("App versionCode mismatch, ${packageInfo.versionCode} != $versionCode")
        }
        val iconDrawable = packageInfo.applicationInfo.loadIcon(packageManager)
            ?: throw Exception("loadIcon return null '$packageName'")
        val bitmap = iconDrawable.toNewBitmap(
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = request.disallowReuseBitmap,
            preferredConfig = request.bitmapConfig?.getConfig(AppIconUriFetcher.MIME_TYPE)
        )
        val imageInfo = ImageInfo(
            width = bitmap.width,
            height = bitmap.height,
            mimeType = AppIconUriFetcher.MIME_TYPE,
            exifOrientation = ExifInterface.ORIENTATION_UNDEFINED
        )
        sketch.logger.d(MODULE) {
            "decode. successful. ${bitmap.logString}. ${imageInfo}. '${requestContext.key}'"
        }
        return BitmapDecodeResult(bitmap, imageInfo, LOCAL, null, null)
            .appliedResize(sketch, requestContext)
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): BitmapDecoder? {
            val dataSource = fetchResult.dataSource
            return if (
                AppIconUriFetcher.MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)
                && dataSource is AppIconDataSource
            ) {
                AppIconBitmapDecoder(
                    sketch = sketch,
                    requestContext = requestContext,
                    packageName = dataSource.packageName,
                    versionCode = dataSource.versionCode
                )
            } else {
                null
            }
        }

        override fun toString(): String = "AppIconBitmapDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}