package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

@Composable
fun rememberRegionSelectorState(initialSizeOfContainer: Float = 0.5f): RegionSelectorState {
    return remember(initialSizeOfContainer) {
        RegionSelectorState(initialSizeOfContainer)
    }
}

class RegionSelectorState(val initialSizeOfContainer: Float) {
    private var _rect by mutableStateOf(Rect.Zero)
    val rect: Rect get() = _rect

    private val _rectFlow = MutableStateFlow(Rect.Zero)
    val rectFlow = _rectFlow.asStateFlow()

    var containerSize by mutableStateOf(Size.Zero)
    private var isInitialized = false

    init {
        require(initialSizeOfContainer in 0.1f..1f) {
            "initialSizeOfContainer must be between 0.1 and 1"
        }
    }

    fun updateContainerSize(size: Size) {
        if (containerSize == size) return
        containerSize = size

        if (!isInitialized && size.width > 0 && size.height > 0) {
            val initialWith = size.width * initialSizeOfContainer
            val initialHeight = size.height * initialSizeOfContainer
            val left = (size.width - initialWith) / 2f
            val top = (size.height - initialHeight) / 2f

            updateRect(
                Rect(
                    left = left,
                    top = top,
                    right = left + initialWith,
                    bottom = top + initialHeight
                )
            )
            isInitialized = true
        }
    }

    fun updateRect(newRect: Rect) {
        _rect = newRect
        _rectFlow.value = newRect
    }

    /**
     * 1. Zoom logic: drag and drop the four corners
     */
    fun handleDrag(handleIndex: Int, dragAmount: Offset) {
        val minSize = 100f
        var left = rect.left
        var top = rect.top
        var right = rect.right
        var bottom = rect.bottom

        when (handleIndex) {
            0 -> { // Top left
                left = (left + dragAmount.x).coerceIn(0f, right - minSize)
                top = (top + dragAmount.y).coerceIn(0f, bottom - minSize)
            }

            1 -> { // Top right
                right = (right + dragAmount.x).coerceIn(left + minSize, containerSize.width)
                top = (top + dragAmount.y).coerceIn(0f, bottom - minSize)
            }

            2 -> { // Bottom right
                right = (right + dragAmount.x).coerceIn(left + minSize, containerSize.width)
                bottom = (bottom + dragAmount.y).coerceIn(top + minSize, containerSize.height)
            }

            3 -> { // Lower left
                left = (left + dragAmount.x).coerceIn(0f, right - minSize)
                bottom = (bottom + dragAmount.y).coerceIn(top + minSize, containerSize.height)
            }
        }
        updateRect(Rect(left, top, right, bottom))
    }

    /**
     * 2. Added overall translation logic: drag inside the rectangle
     */
    fun handleMove(dragAmount: Offset) {
        val width = rect.width
        val height = rect.height

        // Calculate the new top-left coordinates after translation and limit them within the container range
        val maxLeft = containerSize.width - width
        val maxTop = containerSize.height - height

        val newLeft = (rect.left + dragAmount.x).coerceIn(0f, maxLeft)
        val newTop = (rect.top + dragAmount.y).coerceIn(0f, maxTop)

        updateRect(Rect(newLeft, newTop, newLeft + width, newTop + height))
    }
}

fun Modifier.regionSelector(
    state: RegionSelectorState,
    handleRadius: Float = 30f,
    rectColor: Color = Color.Blue,
    handleColor: Color = Color.Red
): Modifier = composed {
    this.onSizeChanged { state.updateContainerSize(it.toSize()) }
        .pointerInput(state) {
            // -1: Missed, 0~3: Hit the corresponding angle, -2: Hit inside the rectangle (overall drag)
            var dragMode = -1

            detectDragGestures(
                onDragStart = { startOffset ->
                    val rect = state.rect
                    val handles = listOf(
                        Offset(rect.left, rect.top),     // 0: 左上
                        Offset(rect.right, rect.top),    // 1: 右上
                        Offset(rect.right, rect.bottom), // 2: 右下
                        Offset(rect.left, rect.bottom)   // 3: 左下
                    )

                    // 1. First, check whether you have pressed the four corners
                    val hitHandleIndex = handles.indexOfFirst { handleOffset ->
                        val distance =
                            sqrt((startOffset.x - handleOffset.x).pow2() + (startOffset.y - handleOffset.y).pow2())
                        distance <= handleRadius * 2f // Slightly enlarging the sensing area to enhance the touch experience
                    }

                    dragMode = if (hitHandleIndex != -1) {
                        hitHandleIndex
                    } else if (rect.contains(startOffset)) {
                        // 2. If there's no center angle but the point is inside the rectangle, it triggers overall drag
                        -2
                    } else {
                        -1
                    }
                },
                onDrag = { change, dragAmount ->
                    if (dragMode != -1) {
                        change.consume()
                        if (dragMode == -2) {
                            // Overall translation
                            state.handleMove(dragAmount)
                        } else {
                            // Square Corner Zoom
                            state.handleDrag(dragMode, dragAmount)
                        }
                    }
                },
                onDragEnd = { dragMode = -1 },
                onDragCancel = { dragMode = -1 }
            )
        }
        .drawWithContent {
            drawContent()
            val rect = state.rect
            if (rect != Rect.Zero) {
                // Draw a rectangular border
                drawRect(
                    color = rectColor,
                    topLeft = Offset(rect.left, rect.top),
                    size = rect.size,
                    style = Stroke(width = 4f)
                )

                // Draw the four corners
                val handles = listOf(
                    Offset(rect.left, rect.top),
                    Offset(rect.right, rect.top),
                    Offset(rect.right, rect.bottom),
                    Offset(rect.left, rect.bottom)
                )
                handles.forEach { center ->
                    drawCircle(
                        color = handleColor,
                        radius = handleRadius,
                        center = center
                    )
                }
            }
        }
}

private fun Float.pow2() = this * this