package com.github.panpf.sketch.request

import com.github.panpf.sketch.util.SketchException

private const val KEY = "sketch#SaveCellularTraffic"
private const val ENABLED_KEY = "sketch#enabledSaveCellularTraffic"
private const val IGNORE_KEY = "sketch#ignoreSaveCellularTraffic"

/**
 * Set to enable or disable the function of saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun ImageRequest.Builder.saveCellularTraffic(enabled: Boolean = true): ImageRequest.Builder =
    apply {
        if (enabled) {
            setParameter(ENABLED_KEY, true, null)
        } else {
            removeParameter(ENABLED_KEY)
        }
    }

/**
 * Set to enable or disable the function of saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun DisplayRequest.Builder.saveCellularTraffic(enabled: Boolean = true): DisplayRequest.Builder =
    apply {
        if (enabled) {
            setParameter(ENABLED_KEY, true, null)
        } else {
            removeParameter(ENABLED_KEY)
        }
    }

/**
 * Returns true if cellular data saving has been enabled
 */
val ImageRequest.isSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(ENABLED_KEY) == true

/**
 * Set to enable or disable the function of saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun ImageOptions.Builder.saveCellularTraffic(enabled: Boolean = true): ImageOptions.Builder =
    apply {
        if (enabled) {
            setParameter(ENABLED_KEY, true, null)
        } else {
            removeParameter(ENABLED_KEY)
        }
    }

/**
 * Returns true if cellular data saving has been enabled
 */
val ImageOptions.isSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(ENABLED_KEY) == true


/**
 * Set to enable or disable the function of ignore saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun ImageRequest.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true): ImageRequest.Builder =
    apply {
        if (ignore) {
            setParameter(IGNORE_KEY, true, null)
        } else {
            removeParameter(IGNORE_KEY)
        }
    }

/**
 * Set to enable or disable the function of ignore saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun DisplayRequest.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true): DisplayRequest.Builder =
    apply {
        if (ignore) {
            setParameter(IGNORE_KEY, true, null)
        } else {
            removeParameter(IGNORE_KEY)
        }
    }

/**
 * Returns true if ignore cellular data saving has been enabled
 */
val ImageRequest.isIgnoredSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(IGNORE_KEY) == true

/**
 * Set to enable or disable the function of ignore saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun ImageOptions.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true): ImageOptions.Builder =
    apply {
        if (ignore) {
            setParameter(IGNORE_KEY, true, null)
        } else {
            removeParameter(IGNORE_KEY)
        }
    }

/**
 * Returns true if ignore cellular data saving has been enabled
 */
val ImageOptions.isIgnoredSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(IGNORE_KEY) == true


/**
 * Set Depth from cellular data saving feature
 */
internal fun ImageRequest.Builder.setDepthFromSaveCellularTraffic(enabled: Boolean = true): ImageRequest.Builder =
    apply {
        if (enabled) {
            depthFrom(KEY)
        } else {
            depthFrom(null)
        }
    }

/**
 * Set Depth from cellular data saving feature
 */
internal fun DisplayRequest.Builder.setDepthFromSaveCellularTraffic(enabled: Boolean = true): DisplayRequest.Builder =
    apply {
        if (enabled) {
            depthFrom(KEY)
        } else {
            depthFrom(null)
        }
    }

/**
 * Returns true if Depth is from the cellular saving feature
 */
val ImageRequest.isDepthFromSaveCellularTraffic: Boolean
    get() = depthFrom == KEY

/**
 * Set Depth from cellular data saving feature
 */
internal fun ImageOptions.Builder.setDepthFromSaveCellularTraffic(enabled: Boolean = true): ImageOptions.Builder =
    apply {
        if (enabled) {
            depthFrom(KEY)
        } else {
            depthFrom(null)
        }
    }

/**
 * Returns true if Depth is from the cellular saving feature
 */
val ImageOptions.isDepthFromSaveCellularTraffic: Boolean
    get() = depthFrom == KEY


/**
 * Returns true if the request is abnormal due to the cellular data saving feature
 */
fun isCausedBySaveCellularTraffic(request: ImageRequest, exception: SketchException?): Boolean =
    exception is DepthException && request.depth == Depth.LOCAL && request.depthFrom == KEY