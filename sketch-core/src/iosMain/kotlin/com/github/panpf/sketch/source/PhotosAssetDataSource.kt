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

package com.github.panpf.sketch.source

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.newPhotosAssetUri
import okio.IOException
import okio.Path
import okio.Source
import platform.Photos.PHAsset
import platform.Photos.PHAssetResource

/**
 * PhotosAssetDataSource is a DataSource that represents a photo asset in the iOS Photos library. It is used to load images from the Photos library using their local identifiers.
 *
 * @see com.github.panpf.sketch.core.ios.test.source.PhotosAssetDataSourceTest
 */
class PhotosAssetDataSource constructor(
    val localIdentifier: String,
    val preferredThumbnail: Boolean,
    val networkAccessAllowed: Boolean,
    val asset: PHAsset,
    val resource: PHAssetResource,
) : DataSource {

    override val dataFrom: DataFrom = DataFrom.LOCAL

    override val key: String by lazy {
        buildString {
            append(newPhotosAssetUri(localIdentifier))
            if (preferredThumbnail) append("#thumb")
            if (!networkAccessAllowed) append("#onlyLocal")
        }
    }

    @Throws(IOException::class)
    override fun openSource(): Source =
        throw UnsupportedOperationException("PhotosAssetDataSource does not support openSource")

    @Throws(IOException::class)
    override fun getFile(sketch: Sketch): Path =
        throw UnsupportedOperationException("PhotosAssetDataSource does not support getFile")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as PhotosAssetDataSource
        if (localIdentifier != other.localIdentifier) return false
        if (preferredThumbnail != other.preferredThumbnail) return false
        if (networkAccessAllowed != other.networkAccessAllowed) return false
        return true
    }

    override fun hashCode(): Int {
        var result = localIdentifier.hashCode()
        result = 31 * result + preferredThumbnail.hashCode()
        result = 31 * result + networkAccessAllowed.hashCode()
        return result
    }

    override fun toString(): String {
        return "PhotosAssetDataSource(" +
                "localIdentifier='$localIdentifier', " +
                "preferredThumbnail=$preferredThumbnail, " +
                "networkAccessAllowed=$networkAccessAllowed" +
                ")"
    }
}