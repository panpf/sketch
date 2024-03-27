package com.github.panpf.sketch.util

import java.io.InputStream

/**
 * Resource loader based on JVM current context class loader.
 *
 * Copy from compose Resources.kt
 */
internal class ClassLoaderResourceLoader {

    companion object {
        val Default = ClassLoaderResourceLoader()
    }

    fun load(resourcePath: String): InputStream {
        // TODO(https://github.com/JetBrains/compose-jb/issues/618): probably we shouldn't use
        //  contextClassLoader here, as it is not defined in threads created by non-JVM
        val contextClassLoader = Thread.currentThread().contextClassLoader!!
        val resource = contextClassLoader.getResourceAsStream(resourcePath)
            ?: (::ClassLoaderResourceLoader.javaClass).getResourceAsStream(resourcePath)
        return requireNotNull(resource) { "Resource $resourcePath not found" }
    }
}