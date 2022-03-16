package com.github.panpf.sketch.zoom.tile

import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.zoom.Zoomer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Tiles constructor(
    private val context: Context,
    private val zoomer: Zoomer,
    private val imageUri: String,
    viewSize: Size,
    private val disabledExifOrientation: Boolean = false,
) {

    companion object {
        private const val MODULE = "Tiles"
    }

    private val tempDrawMatrix = Matrix()
    private val tempVisibleRect = Rect()
    private val appContext = context.applicationContext
    private val logger = context.sketch.logger
    private val scope: CoroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    private var _destroyed: Boolean = false
    private var tileManager: TileManager? = null
    internal var onTileChangedListenerList: List<OnTileChangedListener>? = null

    var isShowTileBounds = false
        set(value) {
            field = value
            invalidateView()
        }
    var viewSize: Size = viewSize
        internal set(value) {
            if (field != value) {
                field = value
                scope.launch(Dispatchers.Main) {
                    tileManager?.viewSize = value
                }
            }
        }
    var paused = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    logger.v(MODULE) { "pause. $imageUri" }
                    cleanMemory()
                } else {
                    logger.v(MODULE) { "resume. $imageUri" }
                    refresh()
                }
            }
        }

    init {
        scope.launch(Dispatchers.Main) {
            val tileDecoder = withContext(Dispatchers.IO) {
                TileDecoder.Factory(context, imageUri, disabledExifOrientation).create()
            } ?: return@launch
            this@Tiles.tileManager = TileManager(context, viewSize, tileDecoder)
            refresh()
        }

//        zoomer.addonMatrixChangeListenerList
    }

    fun destroy() {
        _destroyed = true
        scope.cancel()
        tileManager?.destroy()
        tileManager = null
    }

    fun cleanMemory() {
        tileManager?.cleanMemory()
    }

    fun refresh() {
        requiredMainThread()
        if (destroyed) {
            logger.d(MODULE) { "Destroyed. $imageUri" }
            return
        }
        if (paused) {
            logger.d(MODULE) { "Paused. $imageUri" }
            return
        }
        val manager = tileManager
        if (manager == null) {
            logger.d(MODULE) { "Initializing. $imageUri" }
            return
        }

        val drawableSize = zoomer.drawableSize
        val zooming = zoomer.isZooming
        val drawMatrix = tempDrawMatrix
        val newVisibleRect = tempVisibleRect

//        // 传进来的参数不能用就什么也不显示
//        if (newVisibleRect.isEmpty || drawableSize.isEmpty || viewSize.isEmpty) {
//            logger.w(
//                MODULE,
//                "update params is empty. update. newVisibleRect=%s, drawableSize=%s, viewSize=%s. %s"
//                    .format(
//                        newVisibleRect.toShortString(),
//                        drawableSize.toString(),
//                        viewSize.toString(),
//                        imageUri
//                    )
//            )
//            clean("update param is empty")
//            return
//        }
//
//        // 如果当前完整显示预览图的话就清空什么也不显示
//        if (newVisibleRect.width() == drawableSize.width && newVisibleRect.height() == drawableSize.height) {
//            logger.v(MODULE) {
//                "full display. update. newVisibleRect=${newVisibleRect.toShortString()}. $imageUri"
//            }
//            clean("full display")
//            return
//        }
//
//        // 更新Matrix
//        lastZoomScale = zoomScale
//        matrix.set(drawMatrix)
//        zoomScale = matrix.getScale().format(2)
//        invalidateView()
//        tileManager.update(newVisibleRect, drawableSize, viewSize, imageSize!!, zooming)
    }

    //    /* -----------回调方法----------- */
//    fun onDraw(canvas: Canvas) {
//        if (destroyed) return
//        if (tileManager.blockList.size > 0) {
//            val saveCount = canvas.save()
//            canvas.concat(matrix)
//            for (block in tileManager.blockList) {
//                val bitmap = block.bitmap
//                if (!block.isEmpty && bitmap != null) {
//                    canvas.drawBitmap(
//                        bitmap,
//                        block.bitmapDrawSrcRect,
//                        block.drawRect,
//                        drawBlockPaint
//                    )
//                    if (isShowBlockBounds) {
//                        canvas.drawRect(block.drawRect, drawBlockRectPaint)
//                    }
//                } else if (!block.isDecodeParamEmpty) {
//                    if (isShowBlockBounds) {
//                        canvas.drawRect(block.drawRect, drawLoadingBlockRectPaint)
//                    }
//                }
//            }
//            canvas.restoreToCount(saveCount)
//        }
//    }

    fun onMatrixChanged() {
        if (zoomer.rotateDegrees % 90 != 0) {
            logger.w(MODULE, "rotate degrees must be in multiples of 90. $imageUri")
            return
        }
        zoomer.getDrawMatrix(tempDrawMatrix)
        zoomer.getVisibleRect(tempVisibleRect)
        refresh()
    }

    val destroyed: Boolean
        get() = _destroyed

    //    /** 当前缩放比例 */
//    var zoomScale = 0f
//        private set
//
//    /** 上次的缩放比例 */
//    var lastZoomScale = 0f
//        private set
//    private val drawBlockPaint: Paint
//    private val drawBlockRectPaint: Paint by lazy {
//        Paint().apply {
//            color = Color.parseColor("#88FF0000")
//        }
//    }
//    private val drawLoadingBlockRectPaint: Paint by lazy {
//        Paint().apply {
//            color = Color.parseColor("#880000FF")
//        }
//    }
//    private val matrix: Matrix
//    private var destroyed = false
//
    /* -----------其它方法----------- */
    fun invalidateView() {
        zoomer.view.invalidate()
    }
}