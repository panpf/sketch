package com.github.panpf.sketch.request

const val DEPTH_FROM_KEY = "sketch#depthFrom"

/**
 * Get depth source
 */
val ImageOptions.depthFrom: String?
    get() = parameters?.value(DEPTH_FROM_KEY)

/**
 * Set the source of the request depth
 */
fun ImageOptions.Builder.depthFrom(from: String?): ImageOptions.Builder = apply {
    if (from != null) {
        setParameter(DEPTH_FROM_KEY, from, null)
    } else {
        removeParameter(DEPTH_FROM_KEY)
    }
}


/**
 * Get depth source
 */
val ImageRequest.depthFrom: String?
    get() = parameters?.value(DEPTH_FROM_KEY)

/**
 * Set the source of the request depth
 */
fun ImageRequest.Builder.depthFrom(from: String?): ImageRequest.Builder = apply {
    if (from != null) {
        setParameter(DEPTH_FROM_KEY, from, null)
    } else {
        removeParameter(DEPTH_FROM_KEY)
    }
}

/**
 * Set the source of the request depth
 */
fun DisplayRequest.Builder.depthFrom(from: String?): DisplayRequest.Builder = apply {
    if (from != null) {
        setParameter(DEPTH_FROM_KEY, from, null)
    } else {
        removeParameter(DEPTH_FROM_KEY)
    }
}

/**
 * Set the source of the request depth
 */
fun LoadRequest.Builder.depthFrom(from: String?): LoadRequest.Builder = apply {
    if (from != null) {
        setParameter(DEPTH_FROM_KEY, from, null)
    } else {
        removeParameter(DEPTH_FROM_KEY)
    }
}

/**
 * Set the source of the request depth
 */
fun DownloadRequest.Builder.depthFrom(from: String?): DownloadRequest.Builder = apply {
    if (from != null) {
        setParameter(DEPTH_FROM_KEY, from, null)
    } else {
        removeParameter(DEPTH_FROM_KEY)
    }
}