package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.util.MimeTypeMap
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
 * Sample: 'compose.resource://composeResources/sketch_root.sample.generated.resources/drawable/sample.png'
 *
 * @param resourcePath The path of the file to read in the compose resource's directory. For example:
 * * 'composeResources/sketch_root.sample.generated.resources/drawable/sample.png'
 * * Res.getUri("drawable/sample.png")
 */
fun newComposeResourceUri(resourcePath: String): String {
    // "composeResources/sketch_root.sample.generated.resources/drawable/sample.png"
    if (resourcePath.startsWith("composeResources/")) {
        return "${ComposeResourceUriFetcher.SCHEME}://$resourcePath"
    }

    // file:/Users/panpf/Workspace/sketch/sample/build/processedResources/desktop/main/composeResources/sketch_root.sample.generated.resources/drawable/sample.png
    // jar:file:/data/app/com.github.panpf.sketch4.sample-kz2o4eobaLdvBww0SkguMw==/base.apk!/composeResources/sketch_root.sample.generated.resources/drawable/sample.png
    val index = resourcePath.indexOf("/composeResources/")
    if (index != -1) {
        val realResourcePath = resourcePath.substring(index + 1)
        return "${ComposeResourceUriFetcher.SCHEME}://$realResourcePath"
    }

    throw IllegalArgumentException("Unsupported compose resource path: $resourcePath")
}

class ComposeResourceUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val resourcePath: String,
) : Fetcher {

    companion object {
        const val SCHEME = "compose.resource"
    }

    @OptIn(InternalResourceApi::class)
    override suspend fun fetch(): Result<FetchResult> {
        val bytes = readResourceBytes(resourcePath)
        val mimeType = MimeTypeMap.getMimeTypeFromUrl(resourcePath)
        val dataSource = ByteArrayDataSource(sketch, request, LOCAL, bytes)
        return Result.success(FetchResult(dataSource, mimeType))
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): ComposeResourceUriFetcher? {
            val uri = request.uri.toUri()
            return if (SCHEME.equals(uri.scheme, ignoreCase = true)) {
                val resourcePath = "${uri.authority.orEmpty()}${uri.path.orEmpty()}"
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