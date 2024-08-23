package com.github.panpf.zoomimage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.github.panpf.zoomimage.compose.ZoomState
import com.github.panpf.zoomimage.compose.rememberZoomImageLogger
import com.github.panpf.zoomimage.compose.subsampling.SubsamplingState
import com.github.panpf.zoomimage.compose.subsampling.rememberSubsamplingState
import com.github.panpf.zoomimage.compose.zoom.ZoomableState
import com.github.panpf.zoomimage.compose.zoom.rememberZoomableState
import com.github.panpf.zoomimage.util.Logger
import com.github.panpf.zoomimage.util.Logger.Level

/**
 * Creates and remember a [SketchZoomState]
 *
 * @see com.github.panpf.zoomimage.compose.sketch.core.test.SketchZoomStateTest.testRememberSketchZoomState
 */
@Composable
fun rememberSketchZoomState(logLevel: Level? = null): SketchZoomState {
    val logger: Logger = rememberZoomImageLogger(tag = "SketchZoomAsyncImage", level = logLevel)
    val zoomableState = rememberZoomableState(logger)
    val subsamplingState = rememberSubsamplingState(zoomableState)
    return remember(logger, zoomableState, subsamplingState) {
        SketchZoomState(logger, zoomableState, subsamplingState)
    }
}

/**
 * [ZoomState] implementation for Sketch
 *
 * @see com.github.panpf.zoomimage.compose.sketch.core.test.SketchZoomStateTest
 */
@Stable
class SketchZoomState(
    logger: Logger,
    zoomable: ZoomableState,
    subsampling: SubsamplingState
) : ZoomState(logger, zoomable, subsampling)