package com.github.panpf.sketch.request

import com.github.panpf.sketch.util.SketchException

const val PAUSE_LOAD_WHEN_SCROLLING_KEY = "sketch#pause_load_when_scrolling"
private const val PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY = "sketch#pause_load_when_scrolling_enabled"
private const val PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY = "sketch#pause_load_when_scrolling_ignored"

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun ImageRequest.Builder.pauseLoadWhenScrolling(enabled: Boolean = true): ImageRequest.Builder =
    apply {
        if (enabled) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY)
        }
    }

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun DisplayRequest.Builder.pauseLoadWhenScrolling(enabled: Boolean = true): DisplayRequest.Builder =
    apply {
        if (enabled) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY)
        }
    }

/**
 * Returns true if pause load when scrolling has been enabled
 */
val ImageRequest.isPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY) == true

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun ImageOptions.Builder.pauseLoadWhenScrolling(enabled: Boolean = true): ImageOptions.Builder =
    apply {
        if (enabled) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY)
        }
    }

/**
 * Returns true if pause load when scrolling has been enabled
 */
val ImageOptions.isPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY) == true


/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun ImageRequest.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true): ImageRequest.Builder =
    apply {
        if (ignore) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY)
        }
    }

/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun DisplayRequest.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true): DisplayRequest.Builder =
    apply {
        if (ignore) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY)
        }
    }

/**
 * Returns true if ignore pause load when scrolling has been enabled
 */
val ImageRequest.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY) == true

/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDisplayInterceptor]
 */
fun ImageOptions.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true): ImageOptions.Builder =
    apply {
        if (ignore) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY)
        }
    }

/**
 * Returns true if ignore pause load when scrolling has been enabled
 */
val ImageOptions.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY) == true


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