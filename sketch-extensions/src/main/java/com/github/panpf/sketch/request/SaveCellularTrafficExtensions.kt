package com.github.panpf.sketch.request

import com.github.panpf.sketch.util.SketchException

const val SAVE_CELLULAR_TRAFFIC_KEY = "sketch#save_cellular_traffic"
private const val SAVE_CELLULAR_TRAFFIC_ENABLED_KEY = "sketch#save_cellular_traffic_enabled"
private const val SAVE_CELLULAR_TRAFFIC_IGNORED_KEY = "sketch#save_cellular_traffic_ignored"

/**
 * Set to enable or disable the function of saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun ImageRequest.Builder.saveCellularTraffic(enabled: Boolean = true): ImageRequest.Builder =
    apply {
        if (enabled) {
            setParameter(SAVE_CELLULAR_TRAFFIC_ENABLED_KEY, true, null)
        } else {
            removeParameter(SAVE_CELLULAR_TRAFFIC_ENABLED_KEY)
        }
    }

/**
 * Set to enable or disable the function of saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun DisplayRequest.Builder.saveCellularTraffic(enabled: Boolean = true): DisplayRequest.Builder =
    apply {
        if (enabled) {
            setParameter(SAVE_CELLULAR_TRAFFIC_ENABLED_KEY, true, null)
        } else {
            removeParameter(SAVE_CELLULAR_TRAFFIC_ENABLED_KEY)
        }
    }

/**
 * Returns true if cellular data saving has been enabled
 */
val ImageRequest.isSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(SAVE_CELLULAR_TRAFFIC_ENABLED_KEY) == true

/**
 * Set to enable or disable the function of saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun ImageOptions.Builder.saveCellularTraffic(enabled: Boolean = true): ImageOptions.Builder =
    apply {
        if (enabled) {
            setParameter(SAVE_CELLULAR_TRAFFIC_ENABLED_KEY, true, null)
        } else {
            removeParameter(SAVE_CELLULAR_TRAFFIC_ENABLED_KEY)
        }
    }

/**
 * Returns true if cellular data saving has been enabled
 */
val ImageOptions.isSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(SAVE_CELLULAR_TRAFFIC_ENABLED_KEY) == true


/**
 * Set to enable or disable the function of ignore saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun ImageRequest.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true): ImageRequest.Builder =
    apply {
        if (ignore) {
            setParameter(SAVE_CELLULAR_TRAFFIC_IGNORED_KEY, true, null)
        } else {
            removeParameter(SAVE_CELLULAR_TRAFFIC_IGNORED_KEY)
        }
    }

/**
 * Set to enable or disable the function of ignore saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun DisplayRequest.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true): DisplayRequest.Builder =
    apply {
        if (ignore) {
            setParameter(SAVE_CELLULAR_TRAFFIC_IGNORED_KEY, true, null)
        } else {
            removeParameter(SAVE_CELLULAR_TRAFFIC_IGNORED_KEY)
        }
    }

/**
 * Returns true if ignore cellular data saving has been enabled
 */
val ImageRequest.isIgnoredSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(SAVE_CELLULAR_TRAFFIC_IGNORED_KEY) == true

/**
 * Set to enable or disable the function of ignore saving cellular data, it needs to be used together with [SaveCellularTrafficDisplayInterceptor]
 */
fun ImageOptions.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true): ImageOptions.Builder =
    apply {
        if (ignore) {
            setParameter(SAVE_CELLULAR_TRAFFIC_IGNORED_KEY, true, null)
        } else {
            removeParameter(SAVE_CELLULAR_TRAFFIC_IGNORED_KEY)
        }
    }

/**
 * Returns true if ignore cellular data saving has been enabled
 */
val ImageOptions.isIgnoredSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(SAVE_CELLULAR_TRAFFIC_IGNORED_KEY) == true

/**
 * Returns true if Depth is from the cellular saving feature
 */
val ImageRequest.isDepthFromSaveCellularTraffic: Boolean
    get() = depthFrom == SAVE_CELLULAR_TRAFFIC_KEY

/**
 * Returns true if Depth is from the cellular saving feature
 */
val ImageOptions.isDepthFromSaveCellularTraffic: Boolean
    get() = depthFrom == SAVE_CELLULAR_TRAFFIC_KEY


/**
 * Returns true if the request is abnormal due to the cellular data saving feature
 */
fun isCausedBySaveCellularTraffic(request: ImageRequest, exception: SketchException?): Boolean =
    exception is DepthException && request.depth == Depth.LOCAL && request.isDepthFromSaveCellularTraffic