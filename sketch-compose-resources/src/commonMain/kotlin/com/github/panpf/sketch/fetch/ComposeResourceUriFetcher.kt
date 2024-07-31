package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.pathSegments
import com.github.panpf.sketch.util.toUri
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
 * @return 'file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg'
 */
fun newComposeResourceUri(resourcePath: String): String {
    if (resourcePath.startsWith("composeResources/")) {
        return "${ComposeResourceUriFetcher.SCHEME}://${ComposeResourceUriFetcher.AUTHORITY}/$resourcePath"
    }

    val index = resourcePath.indexOf("/composeResources/")
    if (index != -1) {
        val realResourcePath = resourcePath.substring(index + 1)
        return "${ComposeResourceUriFetcher.SCHEME}://${ComposeResourceUriFetcher.AUTHORITY}/$realResourcePath"
    }

    throw IllegalArgumentException("Unsupported compose resource path: $resourcePath")
}

fun isComposeResourceUri(uri: Uri): Boolean =
    ComposeResourceUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true)
            && ComposeResourceUriFetcher.AUTHORITY.equals(uri.authority, ignoreCase = true)

class ComposeResourceUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val resourcePath: String,
) : Fetcher {

    companion object {
        const val SCHEME = "file"
        const val AUTHORITY = "compose_resource"
    }

    @OptIn(InternalResourceApi::class)
    override suspend fun fetch(): Result<FetchResult> {
        val bytes = readResourceBytes(resourcePath) // TODO Just use 'files/huge_china.jpg' on js
        val mimeType = MimeTypeMap.getMimeTypeFromUrl(resourcePath)
        val dataSource = ByteArrayDataSource(sketch, request, LOCAL, bytes)
        return Result.success(FetchResult(dataSource, mimeType))
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): ComposeResourceUriFetcher? {
            val uri = request.uri.toUri()
            return if (isComposeResourceUri(uri)) {
                val resourcePath = uri.pathSegments.joinToString("/")
                ComposeResourceUriFetcher(sketch, request, resourcePath)
            } else {
                null
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "ComposeResourceUriFetcher"
    }
}