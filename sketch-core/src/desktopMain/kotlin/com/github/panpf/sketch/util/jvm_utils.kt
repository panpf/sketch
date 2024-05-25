package com.github.panpf.sketch.util

import java.net.URI
import java.nio.file.Paths


/**
 * Returns the path to the app's compose resources directory. Only works in release mode
 *
 * Example:
 * macOs: '/Applications/hellokmp.app/Contents/app/resources'
 * Windows: 'C:\Program Files\hellokmp\app\resources'
 * Linux: '/opt/hellokmp/lib/app/resources'
 * dev: null
 */
fun getComposeResourcesPath(): String? {
    return System.getProperty("compose.application.resources.dir")
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
}

/**
 * Returns the path to the jar file where the specified class is located
 *
 * Example:
 * macOs: '/Applications/hellokmp.app/Contents/app/composeApp-desktop-f6c789dab561fea8ab3a9533b659d11a.jar'
 * Windows: 'C:\Program Files\hellokmp\app\composeApp-desktop-55c1f2d3ee1433be9f95b1912fbd.jar'
 * Linux: '/opt/hellokmp/lib/app/composeApp-desktop-e1e452276759301f909baa97e6a11ff4.jar'
 * dev: '/Users/panpf/Workspace/KotlinProjectDesktop/composeApp/build/classes/kotlin/desktop/main'
 */
fun getJarPath(aclass: Class<*>): String? {
    try {
        val codeSource = aclass.protectionDomain.codeSource
        if (codeSource != null) {
            val location: URI = codeSource.location.toURI()
            return Paths.get(location).toString()
        } else {
            return null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}