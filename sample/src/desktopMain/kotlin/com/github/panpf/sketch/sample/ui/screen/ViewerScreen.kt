//package com.github.panpf.sketch.sample.ui.screen
//
//import androidx.compose.desktop.ui.tooling.preview.Preview
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.derivedStateOf
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.input.key.Key
//import androidx.compose.ui.input.key.KeyEventType
//import androidx.compose.ui.input.key.isMetaPressed
//import androidx.compose.ui.input.key.key
//import androidx.compose.ui.input.key.type
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import com.github.panpf.sketch.ZoomImage
//import com.github.panpf.sketch.compose.rememberZoomImageLogger
//import com.github.panpf.sketch.compose.rememberZoomState
//import com.github.panpf.sketch.compose.subsampling.fromResource
//import com.github.panpf.sketch.compose.zoom.ScrollBarSpec
//import com.github.panpf.sketch.compose.zoom.ZoomAnimationSpec
//import com.github.panpf.sketch.compose.zoom.ZoomableState
//import com.github.panpf.sketch.sample.compose.widget.ZoomImageMinimap
//import com.github.panpf.sketch.sample.compose.widget.ZoomImageTool
//import com.github.panpf.sketch.sample.compose.widget.rememberMyDialogState
//import com.github.panpf.sketch.sample.ui.MySettings
//import com.github.panpf.sketch.sample.ui.model.ImageResource
//import com.github.panpf.sketch.sample.ui.navigation.Navigation
//import com.github.panpf.sketch.sample.ui.util.EventBus
//import com.github.panpf.sketch.sample.ui.util.valueOf
//import com.github.panpf.sketch.subsampling.ImageSource
//import com.github.panpf.sketch.subsampling.TileAnimationSpec
//import com.github.panpf.sketch.util.Logger
//import com.github.panpf.sketch.zoom.ReadMode
//import com.github.panpf.sketch.zoom.ScalesCalculator
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//@Preview
//fun ViewerScreen(
//    @Suppress("UNUSED_PARAMETER") navigation: Navigation,
//    imageResource: ImageResource
//) {
//    val supportIgnoreExifOrientation = true
//    Box(Modifier.fillMaxSize()) {
//        val contentScaleName by MySettings.contentScaleName.collectAsState()
//        val alignmentName by MySettings.alignmentName.collectAsState()
//        val threeStepScale by MySettings.threeStepScale.collectAsState()
//        val rubberBandScale by MySettings.rubberBandScale.collectAsState()
//        val readModeEnabled by MySettings.readModeEnabled.collectAsState()
//        val readModeAcceptedBoth by MySettings.readModeAcceptedBoth.collectAsState()
//        val scrollBarEnabled by MySettings.scrollBarEnabled.collectAsState()
//        val logLevelName by MySettings.logLevel.collectAsState()
//        val animateScale by MySettings.animateScale.collectAsState()
//        val slowerScaleAnimation by MySettings.slowerScaleAnimation.collectAsState()
//        val limitOffsetWithinBaseVisibleRect by MySettings.limitOffsetWithinBaseVisibleRect.collectAsState()
//        val scalesCalculatorName by MySettings.scalesCalculator.collectAsState()
//        val scalesMultipleString by MySettings.scalesMultiple.collectAsState()
//        val pausedContinuousTransformType by MySettings.pausedContinuousTransformType.collectAsState()
//        val disabledGestureType by MySettings.disabledGestureType.collectAsState()
//        val disabledBackgroundTiles by MySettings.disabledBackgroundTiles.collectAsState()
//        val ignoreExifOrientation by MySettings.ignoreExifOrientation.collectAsState()
//        val showTileBounds by MySettings.showTileBounds.collectAsState()
//        val tileAnimation by MySettings.tileAnimation.collectAsState()
////        val horizontalLayout by MySettings.horizontalPagerLayout.collectAsState(initial = true)
//
//        val scalesCalculator by remember {
//            derivedStateOf {
//                val scalesMultiple = scalesMultipleString.toFloat()
//                if (scalesCalculatorName == "Dynamic") {
//                    ScalesCalculator.dynamic(scalesMultiple)
//                } else {
//                    ScalesCalculator.fixed(scalesMultiple)
//                }
//            }
//        }
//        val contentScale by remember { derivedStateOf { ContentScale.valueOf(contentScaleName) } }
//        val alignment by remember { derivedStateOf { Alignment.valueOf(alignmentName) } }
//        val zoomAnimationSpec by remember {
//            derivedStateOf {
//                val durationMillis =
//                    if (animateScale) (if (slowerScaleAnimation) 3000 else 300) else 0
//                ZoomAnimationSpec.Default.copy(durationMillis = durationMillis)
//            }
//        }
//        val readMode by remember {
//            derivedStateOf {
//                val sizeType = when {
//                    readModeAcceptedBoth -> ReadMode.SIZE_TYPE_HORIZONTAL or ReadMode.SIZE_TYPE_VERTICAL
//                    else -> ReadMode.SIZE_TYPE_VERTICAL
////                    horizontalLayout -> ReadMode.SIZE_TYPE_VERTICAL
////                    else -> ReadMode.SIZE_TYPE_HORIZONTAL
//                }
//                if (readModeEnabled) ReadMode.Default.copy(sizeType = sizeType) else null
//            }
//        }
//        val logLevel by remember { derivedStateOf { Logger.level(logLevelName) } }
//        val zoomState = rememberZoomState(rememberZoomImageLogger(level = logLevel)).apply {
//            LaunchedEffect(threeStepScale) {
//                zoomable.threeStepScale = threeStepScale
//            }
//            LaunchedEffect(rubberBandScale) {
//                zoomable.rubberBandScale = rubberBandScale
//            }
//            LaunchedEffect(zoomAnimationSpec) {
//                zoomable.animationSpec = zoomAnimationSpec
//            }
//            LaunchedEffect(scalesCalculator) {
//                zoomable.scalesCalculator = scalesCalculator
//            }
//            LaunchedEffect(limitOffsetWithinBaseVisibleRect) {
//                zoomable.limitOffsetWithinBaseVisibleRect = limitOffsetWithinBaseVisibleRect
//            }
//            LaunchedEffect(readMode) {
//                zoomable.readMode = readMode
//            }
//            LaunchedEffect(disabledGestureType) {
//                zoomable.disabledGestureType = disabledGestureType.toInt()
//            }
//            LaunchedEffect(pausedContinuousTransformType) {
//                subsampling.pausedContinuousTransformType = pausedContinuousTransformType.toInt()
//            }
//            LaunchedEffect(disabledBackgroundTiles) {
//                subsampling.disabledBackgroundTiles = disabledBackgroundTiles
//            }
//            if (supportIgnoreExifOrientation) {
//                LaunchedEffect(ignoreExifOrientation) {
//                    subsampling.ignoreExifOrientation = ignoreExifOrientation
//                }
//            }
//            LaunchedEffect(showTileBounds) {
//                subsampling.showTileBounds = showTileBounds
//            }
//            LaunchedEffect(tileAnimation) {
//                subsampling.tileAnimationSpec =
//                    if (tileAnimation) TileAnimationSpec.Default else TileAnimationSpec.None
//            }
//        }
//        LaunchedEffect(Unit) {
//            val imageSource = ImageSource.fromResource(imageResource.resourcePath)
//            zoomState.subsampling.setImageSource(imageSource)
//        }
//
//        var lastMoveJob by remember { mutableStateOf<Job?>(null) }
//        LaunchedEffect(Unit) {
//            EventBus.keyEvent.collect { keyEvent ->
//                if (keyEvent.type == KeyEventType.KeyUp) {
//                    val zoomIn = when {
//                        keyEvent.key == Key.Equals && keyEvent.isMetaPressed -> true
//                        keyEvent.key == Key.Minus && keyEvent.isMetaPressed -> false
//                        keyEvent.key == Key.DirectionUp && !keyEvent.isMetaPressed -> true
//                        keyEvent.key == Key.DirectionDown && !keyEvent.isMetaPressed -> false
//                        else -> null
//                    }
//                    if (zoomIn != null) {
//                        zoomState.zoomable.scale(
//                            targetScale = zoomState.zoomable.transform.scaleX * if (zoomIn) 2f else 0.5f,
//                            animated = true,
//                        )
//                    }
//                }
//
//                lastMoveJob?.cancel()
//                if (keyEvent.type == KeyEventType.KeyDown && keyEvent.isMetaPressed) {
//                    val direction = when (keyEvent.key) {
//                        Key.DirectionLeft -> 1
//                        Key.DirectionUp -> 2
//                        Key.DirectionRight -> 3
//                        Key.DirectionDown -> 4
//                        else -> null
//                    }
//                    if (direction != null) {
//                        lastMoveJob = launch {
//                            move(zoomState.zoomable, direction)
//                        }
//                    }
//                }
//            }
//        }
//
//        ZoomImage(
//            modifier = Modifier.fillMaxSize(),
//            painter = painterResource(imageResource.thumbnailResourcePath),
//            contentScale = contentScale,
//            alignment = alignment,
//            contentDescription = "Viewer",
//            state = zoomState,
//            scrollBar = if (scrollBarEnabled) ScrollBarSpec.Default else null
//        )
//
//        ZoomImageMinimap(
//            imageUri = imageResource.thumbnailResourcePath,
//            zoomableState = zoomState.zoomable,
//            subsamplingState = zoomState.subsampling,
//        )
//
//        val infoDialogState = rememberMyDialogState()
//        ZoomImageTool(
//            imageUri = imageResource.thumbnailResourcePath,
//            zoomableState = zoomState.zoomable,
//            subsamplingState = zoomState.subsampling,
//            infoDialogState = infoDialogState,
//        )
//    }
//}
//
//private suspend fun CoroutineScope.move(zoomable: ZoomableState, direction: Int) {
//    val startTime = System.currentTimeMillis()
//    while (isActive) {
//        val containerSize = zoomable.containerSize
//        val maxStep = Offset(
//            containerSize.width / 20f,
//            containerSize.height / 20f
//        )
//        val offset = zoomable.transform.offset
//        val currentTime = System.currentTimeMillis()
//        val time = currentTime - startTime
//        val scale = when {
//            time < 2000 -> 1f
//            time < 4000 -> 2f
//            time < 8000 -> 4f
//            else -> 8f
//        }
//        val newOffset = when (direction) {
//            1 -> offset.copy(x = offset.x + maxStep.x * scale)  // left
//            2 -> offset.copy(y = offset.y + maxStep.y * scale)  // up
//            3 -> offset.copy(x = offset.x - maxStep.x * scale)  // right
//            4 -> offset.copy(y = offset.y - maxStep.y * scale)  // down
//            else -> offset
//        }
//        zoomable.offset(
//            targetOffset = newOffset,
//            animated = false
//        )
//        delay(8)
//    }
//}