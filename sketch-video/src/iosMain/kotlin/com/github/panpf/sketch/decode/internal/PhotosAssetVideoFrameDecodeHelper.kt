/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright (C) 2026 Kuki93
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

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.PhotosAssetDataSource
import com.github.panpf.sketch.util.pixelSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAsset
import platform.Photos.PHImageManager
import platform.Photos.PHVideoRequestOptions
import kotlin.coroutines.resumeWithException

/**
 * Help decode video frames from PhotosAssetDataSource
 *
 * @see com.github.panpf.sketch.video.ios.test.decode.internal.PhotosAssetVideoFrameDecodeHelperTest
 */
@OptIn(ExperimentalForeignApi::class)
class PhotosAssetVideoFrameDecodeHelper(
    request: ImageRequest,
    val dataSource: PhotosAssetDataSource,
    mimeType: String,
) : BaseAvAssetVideoFrameDecodeHelper(request, mimeType) {

    override fun readImageInfo(): ImageInfo {
        return ImageInfo(size = dataSource.asset.pixelSize(), mimeType = mimeType)
    }

    override fun requestVideoAsset(): AVAsset = runBlocking {
        suspendCancellableCoroutine { continuation ->
            val allowNetworkAccess = dataSource.allowNetworkAccess
            PHImageManager.defaultManager().requestAVAssetForVideo(
                asset = dataSource.asset,
                options = PHVideoRequestOptions().apply {
                    this.networkAccessAllowed = allowNetworkAccess
                },
                resultHandler = { result, _, info ->
                    if (result != null) {
                        continuation.resumeWith(Result.success(result))
                    } else {
                        val message =
                            "requestAVAssetForVideo return null. allowNetworkAccess='$allowNetworkAccess', info='${info?.toString()}'"
                        continuation.resumeWithException(DecodeException(message))
                    }
                },
            )
        }
    }
}