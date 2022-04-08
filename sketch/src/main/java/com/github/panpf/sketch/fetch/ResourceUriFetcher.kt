package com.github.panpf.sketch.fetch

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Resources
import android.net.Uri
import android.util.TypedValue
import android.webkit.MimeTypeMap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.fetch.ResourceUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.getMimeTypeFromUrl
import java.io.FileNotFoundException

/**
 * Sample: 'android.resource://com.github.panpf.sketch.sample/drawable/ic_launcher'
 */
fun newResourceUri(packageName: String, resType: String, drawableResName: String): String =
    "$SCHEME://$packageName/$resType/$drawableResName"

/**
 * Sample: 'android.resource://com.github.panpf.sketch.sample/1031232'
 */
fun newResourceUri(packageName: String, drawableResId: Int): String =
    "$SCHEME://$packageName/$drawableResId"

/**
 * Sample: 'android.resource://com.github.panpf.sketch.sample/drawable/ic_launcher'
 */
fun Context.newResourceUri(resType: String, drawableResName: String): String =
    newResourceUri(packageName, resType, drawableResName)

/**
 * Sample: 'android.resource://com.github.panpf.sketch.sample/1031232'
 */
fun Context.newResourceUri(drawableResId: Int): String = newResourceUri(packageName, drawableResId)

/**
 * Support 'android.resource://com.github.panpf.sketch.sample/mipmap/ic_launch' uri
 */
class ResourceUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val contentUri: Uri,
) : Fetcher {

    companion object {
        const val SCHEME = "android.resource"
    }

    override suspend fun fetch(): FetchResult {
        val (resources, id) = getResourceId(contentUri)
        val path = TypedValue().apply { resources.getValue(id, this, true) }.string ?: ""
        val entryName = path.lastIndexOf('/')
            .takeIf { it != -1 }
            ?.let { path.substring(it + 1) }
            ?: path.toString()
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromUrl(entryName)
        return FetchResult(ResourceDataSource(sketch, request, resources, id), mimeType)
    }

    @Throws(FileNotFoundException::class)
    private fun getResourceId(uri: Uri): Pair<Resources, Int> {
        val authority = uri.authority?.takeIf { it.isNotEmpty() }
            ?: throw FileNotFoundException("No authority: $uri")
        val r: Resources = try {
            sketch.context.packageManager.getResourcesForApplication(authority)
        } catch (ex: NameNotFoundException) {
            throw FileNotFoundException("No package found for authority: $uri")
        }
        val path = uri.pathSegments ?: throw FileNotFoundException("No path: $uri")
        val id: Int = when (path.size) {
            1 -> {
                try {
                    path[0].toInt()
                } catch (e: NumberFormatException) {
                    throw FileNotFoundException("Single path segment is not a resource ID: $uri")
                }
            }
            2 -> {
                r.getIdentifier(path[1], path[0], authority)
            }
            else -> {
                throw FileNotFoundException("More than two path segments: $uri")
            }
        }
        if (id == 0) {
            throw FileNotFoundException("No resource found for: $uri")
        }
        return r to id
    }

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): ResourceUriFetcher? =
            if (SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                ResourceUriFetcher(sketch, request, request.uri)
            } else {
                null
            }

        override fun toString(): String = "ResourceUriFetcher"
    }
}