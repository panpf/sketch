package com.github.panpf.sketch.request

import com.github.panpf.sketch.util.SketchException

const val PAUSE_LOAD_WHEN_SCROLLING_KEY = "sketch#PauseLoadWhenScrolling"
private const val ENABLED_KEY = "sketch#enabledPauseLoadWhenScrolling"
private const val IGNORE_KEY = "sketch#ignorePauseLoadWhenScrolling"

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun ImageRequest.Builder.pauseLoadWhenScrolling(enabled: Boolean = true): ImageRequest.Builder =
    apply {
        if (enabled) {
            setParameter(ENABLED_KEY, true, null)
        } else {
            removeParameter(ENABLED_KEY)
        }
    }

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun DisplayRequest.Builder.pauseLoadWhenScrolling(enabled: Boolean = true): DisplayRequest.Builder =
    apply {
        if (enabled) {
            setParameter(ENABLED_KEY, true, null)
        } else {
            removeParameter(ENABLED_KEY)
        }
    }

/**
 * Returns true if pause load when scrolling has been enabled
 */
val ImageRequest.isPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(ENABLED_KEY) == true

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun ImageOptions.Builder.pauseLoadWhenScrolling(enabled: Boolean = true): ImageOptions.Builder =
    apply {
        if (enabled) {
            setParameter(ENABLED_KEY, true, null)
        } else {
            removeParameter(ENABLED_KEY)
        }
    }

/**
 * Returns true if pause load when scrolling has been enabled
 */
val ImageOptions.isPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(ENABLED_KEY) == true


/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun ImageRequest.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true): ImageRequest.Builder =
    apply {
        if (ignore) {
            setParameter(IGNORE_KEY, true, null)
        } else {
            removeParameter(IGNORE_KEY)
        }
    }

/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun DisplayRequest.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true): DisplayRequest.Builder =
    apply {
        if (ignore) {
            setParameter(IGNORE_KEY, true, null)
        } else {
            removeParameter(IGNORE_KEY)
        }
    }

/**
 * Returns true if ignore pause load when scrolling has been enabled
 */
val ImageRequest.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(IGNORE_KEY) == true

/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun ImageOptions.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true): ImageOptions.Builder =
    apply {
        if (ignore) {
            setParameter(IGNORE_KEY, true, null)
        } else {
            removeParameter(IGNORE_KEY)
        }
    }

/**
 * Returns true if ignore pause load when scrolling has been enabled
 */
val ImageOptions.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(IGNORE_KEY) == true


/**
 * Returns true if Depth is from the pause load when scrolling feature
 */
val ImageRequest.isDepthFromPauseLoadWhenScrolling: Boolean
    get() = depthFrom == PAUSE_LOAD_WHEN_SCROLLING_KEY


/**
 * Returns true if Depth is from the pause load when scrolling feature
 */
val ImageOptions.isDepthFromPauseLoadWhenScrolling: Boolean
    get() = depthFrom == PAUSE_LOAD_WHEN_SCROLLING_KEY


/**
 * Returns true if the request is abnormal due to the pause load when scrolling feature
 */
fun isCausedByPauseLoadWhenScrolling(request: ImageRequest, exception: SketchException?): Boolean =
    exception is DepthException && request.depth == Depth.MEMORY && request.isDepthFromPauseLoadWhenScrolling