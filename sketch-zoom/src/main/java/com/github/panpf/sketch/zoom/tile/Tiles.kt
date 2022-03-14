package com.github.panpf.sketch.zoom.tile

import android.content.Context
import android.graphics.Bitmap
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.zoom.Zoomer
import com.github.panpf.sketch.zoom.block.Blocks.OnBlockChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Tiles constructor(
    private val context: Context,
    private val zoomer: Zoomer,
    private val imageUri: String,
    private val imageSize: Size,
    viewSize: Size,
    private val exifOrientation: Int
) {

    companion object {
        private const val NAME = "Blocks"
    }

    //    private val tempDrawMatrix = Matrix()
//    private val tempVisibleRect = Rect()
    private var tileDecoder: TileDecoder? = null
    private val tileManager: TileManager = TileManager(context, imageSize, viewSize, ::decodeTile)
    private val appContext = context.applicationContext
    private val bitmapPool = context.sketch.bitmapPool
    private val logger = context.sketch.logger
    private val scope: CoroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )
    var viewSize: Size = viewSize
        internal set(value) {
            if (field != value) {
                field = value
                // todo
//                reset()
            }
        }

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
//    var paused = false
//        set(value) {
//            if (field != value) {
//                field = value
//                if (!destroyed) {
//                    if (value) {
//                        logger.v(NAME) { "pause. $imageUri" }
//                        if (destroyed) {
//                            clean("pause")
//                        }
//                    } else {
//                        logger.v(NAME) { "resume. $imageUri" }
//                        if (destroyed) {
//                            onMatrixChanged()
//                        }
//                    }
//                }
//            }
//        }

    var isShowBlockBounds = false
        set(value) {
            field = value
            invalidateView()
        }

    var onBlockChangedListener: OnBlockChangedListener? = null
    private var decoderInitializing: Job? = null

    private suspend fun decodeTile(tile: Tile): Bitmap? {
        requiredMainThread()

        val tileDecoder = tileDecoder
        return if (tileDecoder != null) {
            tileDecoder.decode(tile.key, tile)
        } else {
            if (decoderInitializing?.isActive != true) {
                decoderInitializing = scope.launch {
                    this@Tiles.tileDecoder = withContext(Dispatchers.IO) {
                        TileDecoder.Factory(context, imageUri, exifOrientation).create()
                    }
                }
            }
            null
        }
    }

    //    /**
//     * 回收资源，回收后就不能再用了
//     */
    fun destroy(why: String) {
//        destroyed = true
//        clean(why)
        tileDecoder?.destroy()
        tileDecoder = null
//        tileManager.cleanMemory()
//        blockDecoder.recycle(why)
//        decoderInitializing?.cancel()
//        scope.cancel()
    }

    //
//    /**
//     * 清理资源，不影响继续使用
//     */
//    private fun clean(why: String) {
//        tileDecoder.cleanDecode(why)
//        matrix.reset()
//        lastZoomScale = 0f
//        zoomScale = 0f
//        tileManager.clean(why)
//        invalidateView()
//    }
//
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
//
//    fun onMatrixChanged() {
//        if (destroyed) return
//        if (!isReady && !isInitializing) {
//            logger.v(NAME) { "Blocks not available. onMatrixChanged. $imageUri" }
//            return
//        }
//        if (zoomer.rotateDegrees % 90 != 0) {
//            logger.w(NAME, "rotate degrees must be in multiples of 90. $imageUri")
//            return
//        }
//        val drawMatrix = tempDrawMatrix.apply {
//            reset()
//            zoomer.getDrawMatrix(this)
//        }
//        val newVisibleRect = tempVisibleRect.apply {
//            setEmpty()
//            zoomer.getVisibleRect(this)
//        }
//
//        val drawableSize = zoomer.drawableSize
//        val viewSize = zoomer.viewSize
//        val zooming = zoomer.isZooming
//
//        // 没有准备好就不往下走了
//        if (!isReady) {
//            logger.v(NAME) { "not ready. $imageUri" }
//            return
//        }
//
//        // 暂停中也不走了
//        if (isPaused) {
//            logger.v(NAME) { "paused. $imageUri" }
//            return
//        }
//
//        // 传进来的参数不能用就什么也不显示
//        if (newVisibleRect.isEmpty || drawableSize.isEmpty || viewSize.isEmpty) {
//            logger.w(
//                NAME,
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
//            logger.v(NAME) {
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
//    }
//
    /* -----------其它方法----------- */
    fun invalidateView() {
        zoomer.view.invalidate()
    }
}