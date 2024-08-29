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

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Uri
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

/**
 * Adds compose resources support
 */
fun ComponentRegistry.Builder.supportComposeResources(): ComponentRegistry.Builder = apply {
    addFetcher(ComposeResourceUriFetcher.Factory())
}

/**
 * Build a uri that loads images from the compose resources folder
 *
 * @param resourcePath The path of the file to read in the compose resource's directory. For example:
 * * 'composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg'
 * * Res.getUri("files/huge_china.jpg") on android: 'jar:file:/data/app/com.github.panpf.sketch4.sample-1==/base.apk!/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg'
 * * Res.getUri("files/huge_china.jpg") on desktop: 'file:/Users/panpf/Workspace/sketch/sample/build/processedResources/desktop/main/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg'
 * * Res.getUri("files/huge_china.jpg") on js: 'http://localhost:8080/./composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg'
 * * Res.getUri("files/huge_china.jpg") on ios: 'file:///Users/panpf/Library/Developer/ CoreSimulator/Devices/F828C881-A750-432B-8210-93A84C45E/data/Containers/Bundle/Application/CBD75605-D35E-47A7-B56B-6C5690B062CC/SketchSample.app/compose-resources/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg'
 * @return 'file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg'
 */
fun newComposeResourceUri(resourcePath: String): String {
    if (resourcePath.startsWith("composeResources/")) {
        return "${ComposeResourceUriFetcher.SCHEME}:///${ComposeResourceUriFetcher.PATH_ROOT}/$resourcePath"
    }

    val index = resourcePath.indexOf("/composeResources/")
    if (index != -1) {
        val realResourcePath = resourcePath.substring(index + 1)
        return "${ComposeResourceUriFetcher.SCHEME}:///${ComposeResourceUriFetcher.PATH_ROOT}/$realResourcePath"
    }

    throw IllegalArgumentException("Unsupported compose resource path: $resourcePath")
}

/**
 * Check if the uri is a compose resource uri
 *
 * Support 'file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg'
 */
fun isComposeResourceUri(uri: Uri): Boolean =
    ComposeResourceUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true)
            && uri.authority?.takeIf { it.isNotEmpty() } == null
            && ComposeResourceUriFetcher.PATH_ROOT.equals(
        uri.pathSegments.firstOrNull(),
        ignoreCase = true
    )

class ComposeResourceUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val resourcePath: String,
) : Fetcher {

    companion object {
        const val SCHEME = "file"
        const val PATH_ROOT = "compose_resource"
    }

    @OptIn(InternalResourceApi::class)
    override suspend fun fetch(): Result<FetchResult> {
        val bytes = readResourceBytes(resourcePath)
        val mimeType = MimeTypeMap.getMimeTypeFromUrl(resourcePath)
        val dataSource = ByteArrayDataSource(data = bytes, dataFrom = LOCAL)
        return Result.success(FetchResult(dataSource, mimeType))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ComposeResourceUriFetcher
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (resourcePath != other.resourcePath) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + resourcePath.hashCode()
        return result
    }

    override fun toString(): String {
        return "ComposeResourceUriFetcher('$resourcePath')"
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): ComposeResourceUriFetcher? {
            val uri = request.uri
            if (!isComposeResourceUri(uri)) return null
            val resourcePath = uri.pathSegments.drop(1).joinToString("/")
            return ComposeResourceUriFetcher(sketch, request, resourcePath)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "ComposeResourceUriFetcher"
    }
}