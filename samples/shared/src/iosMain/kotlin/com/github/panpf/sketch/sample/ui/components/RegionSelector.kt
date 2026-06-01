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
     * 1. 缩放逻辑：拖拽四个角
     */
    fun handleDrag(handleIndex: Int, dragAmount: Offset) {
        val minSize = 100f
        var left = rect.left
        var top = rect.top
        var right = rect.right
        var bottom = rect.bottom

        when (handleIndex) {
            0 -> { // 左上
                left = (left + dragAmount.x).coerceIn(0f, right - minSize)
                top = (top + dragAmount.y).coerceIn(0f, bottom - minSize)
            }

            1 -> { // 右上
                right = (right + dragAmount.x).coerceIn(left + minSize, containerSize.width)
                top = (top + dragAmount.y).coerceIn(0f, bottom - minSize)
            }

            2 -> { // 右下
                right = (right + dragAmount.x).coerceIn(left + minSize, containerSize.width)
                bottom = (bottom + dragAmount.y).coerceIn(top + minSize, containerSize.height)
            }

            3 -> { // 左下
                left = (left + dragAmount.x).coerceIn(0f, right - minSize)
                bottom = (bottom + dragAmount.y).coerceIn(top + minSize, containerSize.height)
            }
        }
        updateRect(Rect(left, top, right, bottom))
    }

    /**
     * 2. 新增整体平移逻辑：拖拽矩形内部
     */
    fun handleMove(dragAmount: Offset) {
        val width = rect.width
        val height = rect.height

        // 计算平移后的新左上角坐标，并限制在容器范围内
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
            // -1: 未命中, 0~3: 命中了对应的角, -2: 命中了矩形内部（整体拖动）
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

                    // 1. 优先判断是否按中了四个角
                    val hitHandleIndex = handles.indexOfFirst { handleOffset ->
                        val distance =
                            sqrt((startOffset.x - handleOffset.x).pow2() + (startOffset.y - handleOffset.y).pow2())
                        distance <= handleRadius * 2f // 稍微放大感应区，提升触控体验
                    }

                    if (hitHandleIndex != -1) {
                        dragMode = hitHandleIndex
                    } else if (rect.contains(startOffset)) {
                        // 2. 如果没中角，但点在了矩形内部，触发整体拖动
                        dragMode = -2
                    } else {
                        dragMode = -1
                    }
                },
                onDrag = { change, dragAmount ->
                    if (dragMode != -1) {
                        change.consume()
                        if (dragMode == -2) {
                            // 整体平移
                            state.handleMove(dragAmount)
                        } else {
                            // 四角缩放
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
            if (rect != androidx.compose.ui.geometry.Rect.Zero) {
                // 画矩形边框
                drawRect(
                    color = rectColor,
                    topLeft = Offset(rect.left, rect.top),
                    size = rect.size,
                    style = Stroke(width = 4f)
                )

                // 画四个角
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

//class ResizableRectState(initialRect: Rect = Rect.Zero) {
//    // 内部持久化的矩形数据
//    private var _rect by mutableStateOf(initialRect)
//    val rect: Rect get() = _rect
//
//    // 向使用者暴露的实时 Flow
//    private val _rectFlow = MutableStateFlow(initialRect)
//    val rectFlow = _rectFlow.asStateFlow()
//
//    // 记录组件的实际大小，用于限制最大边界
//    var containerSize by mutableStateOf(Size.Zero)
//
//    // 初始化标记，确保只在获取到容器大小后初始化一次居中
//    private var isInitialized = false
//
//    fun updateContainerSize(size: Size) {
//        if (containerSize == size) return
//        containerSize = size
//
//        if (!isInitialized && size.width > 0 && size.height > 0) {
//            // 初始大小 300x300
//            val initWidth = 300f
//            val initHeight = 300f
//            // 居中计算
//            val left = (size.width - initWidth) / 2f
//            val top = (size.height - initHeight) / 2f
//
//            updateRect(Rect(left, top, left + initWidth, top + initHeight))
//            isInitialized = true
//        }
//    }
//
//    fun updateRect(newRect: Rect) {
//        _rect = newRect
//        _rectFlow.value = newRect
//    }
//
//    /**
//     * 根据拖拽的角更新矩形大小
//     * @param handleIndex 0: 左上, 1: 右上, 2: 右下, 3: 左下
//     */
//    fun handleDrag(handleIndex: Int, dragAmount: Offset) {
//        val minSize = 100f
//        var left = rect.left
//        var top = rect.top
//        var right = rect.right
//        var bottom = rect.bottom
//
//        when (handleIndex) {
//            0 -> { // 左上
//                left = (left + dragAmount.x).coerceIn(0f, right - minSize)
//                top = (top + dragAmount.y).coerceIn(0f, bottom - minSize)
//            }
//
//            1 -> { // 右上
//                right = (right + dragAmount.x).coerceIn(left + minSize, containerSize.width)
//                top = (top + dragAmount.y).coerceIn(0f, bottom - minSize)
//            }
//
//            2 -> { // 右下
//                right = (right + dragAmount.x).coerceIn(left + minSize, containerSize.width)
//                bottom = (bottom + dragAmount.y).coerceIn(top + minSize, containerSize.height)
//            }
//
//            3 -> { // 左下
//                left = (left + dragAmount.x).coerceIn(0f, right - minSize)
//                bottom = (bottom + dragAmount.y).coerceIn(top + minSize, containerSize.height)
//            }
//        }
//
//        updateRect(Rect(left, top, right, bottom))
//    }
//}
//
//fun Modifier.resizableRect(
//    state: ResizableRectState,
//    handleRadius: Float = 30f, // 四个控制角感应和绘制的半径
//    rectColor: Color = Color.Blue,
//    handleColor: Color = Color.Red
//): Modifier = composed {
//    this
//        // 1. 监听宿主组件的大小变化
//        .onSizeChanged { size ->
//            state.updateContainerSize(size.toSize())
//        }
//        // 2. 处理拖拽手势
//        .pointerInput(state) {
//            var activeHandleIndex = -1 // 记录当前抓取的是哪个角
//
//            detectDragGestures(
//                onDragStart = { startOffset ->
//                    val rect = state.rect
//                    // 定义四个角的位置
//                    val handles = listOf(
//                        Offset(rect.left, rect.top),     // 0: 左上
//                        Offset(rect.right, rect.top),    // 1: 右上
//                        Offset(rect.right, rect.bottom), // 2: 右下
//                        Offset(rect.left, rect.bottom)   // 3: 左下
//                    )
//                    // 判断触摸点距离哪个角最近，且在感应范围内（这里扩大 1.5 倍方便触控）
//                    activeHandleIndex = handles.indexOfFirst { handleOffset ->
//                        val distance =
//                            sqrt((startOffset.x - handleOffset.x).pow2() + (startOffset.y - handleOffset.y).pow2())
//                        distance <= handleRadius * 1.5f
//                    }
//                },
//                onDrag = { change, dragAmount ->
//                    if (activeHandleIndex != -1) {
//                        change.consume()
//                        state.handleDrag(activeHandleIndex, dragAmount)
//                    }
//                },
//                onDragEnd = { activeHandleIndex = -1 },
//                onDragCancel = { activeHandleIndex = -1 }
//            )
//        }
//        // 3. 绘制矩形和四个角
//        .drawWithContent {
//            drawContent() // 先绘制宿主组件原本的内容
//
//            val rect = state.rect
//            if (rect != androidx.compose.ui.geometry.Rect.Zero) {
//                // 画主矩形边框
//                drawRect(
//                    color = rectColor,
//                    topLeft = Offset(rect.left, rect.top),
//                    size = rect.size,
//                    style = Stroke(width = 4f)
//                )
//
//                // 画四个角的控制圆形
//                val handles = listOf(
//                    Offset(rect.left, rect.top),
//                    Offset(rect.right, rect.top),
//                    Offset(rect.right, rect.bottom),
//                    Offset(rect.left, rect.bottom)
//                )
//                handles.forEach { center ->
//                    drawCircle(
//                        color = handleColor,
//                        radius = handleRadius,
//                        center = center
//                    )
//                }
//            }
//        }
//}

// 辅助扩展函数
private fun Float.pow2() = this * this