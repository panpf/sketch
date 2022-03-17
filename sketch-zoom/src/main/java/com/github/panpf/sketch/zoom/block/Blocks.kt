package com.github.panpf.sketch.zoom.block

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.zoom.Zoomer
import com.github.panpf.sketch.zoom.block.internal.BlockDecoder
import com.github.panpf.sketch.zoom.block.internal.BlockExecutor
import com.github.panpf.sketch.zoom.block.internal.BlockManager
import com.github.panpf.sketch.zoom.block.internal.DecodeHandler.DecodeErrorException
import com.github.panpf.sketch.zoom.block.internal.ImageRegionDecoder
import com.github.panpf.sketch.zoom.internal.getScale

class Blocks constructor(
    context: Context,
    private val zoomer: Zoomer,
    private val imageUri: String,
    exifOrientation: Int
) {

    companion object {
        private const val NAME = "Blocks"
    }

    private val tempDrawMatrix = Matrix()
    private val tempVisibleRect = Rect()
    internal val blockExecutor: BlockExecutor
    internal val blockDecoder: BlockDecoder
    private val blockManager: BlockManager
    private val appContext = context.applicationContext
    private val bitmapPool = context.sketch.bitmapPool
    private val logger = context.sketch.logger

    /** 当前缩放比例 */
    var zoomScale = 0f
        private set

    /** 上次的缩放比例 */
    var lastZoomScale = 0f
        private set
    private val drawBlockPaint: Paint
    private val drawBlockRectPaint: Paint by lazy {
        Paint().apply {
            color = Color.parseColor("#88FF0000")
        }
    }
    private val drawLoadingBlockRectPaint: Paint by lazy {
        Paint().apply {
            color = Color.parseColor("#880000FF")
        }
    }
    private val matrix: Matrix
    private var running = true
    var isPaused = false
        private set

    /**
     * 是否显示碎片的范围（红色表示已加载，蓝色表示正在加载）
     */
    var isShowBlockBounds = false
        set(value) {
            field = value
            invalidateView()
        }

    /**
     * 碎片变化监听器
     */
    var onBlockChangedListener: OnBlockChangedListener? = null

    init {
        blockExecutor = BlockExecutor(context, ExecutorCallback())
        blockManager = BlockManager(context, this)
        blockDecoder = BlockDecoder(context, this)
        matrix = Matrix()
        drawBlockPaint = Paint()

        blockDecoder.setImage(imageUri, exifOrientation)

        zoomer.addOnMatrixChangeListener{
            onMatrixChanged()
        }
    }

    /**
     * 回收资源，回收后就不能再用了
     */
    fun recycle(why: String) {
        running = false
        clean(why)
        blockExecutor.recycle(why)
        blockManager.recycle(why)
        blockDecoder.recycle(why)
    }

    /**
     * 清理资源，不影响继续使用
     */
    private fun clean(why: String) {
        blockExecutor.cleanDecode(why)
        matrix.reset()
        lastZoomScale = 0f
        zoomScale = 0f
        blockManager.clean(why)
        invalidateView()
    }

    /* -----------回调方法----------- */
    fun onDraw(canvas: Canvas) {
        if (blockManager.blockList.size > 0) {
            val saveCount = canvas.save()
            canvas.concat(matrix)
            for (block in blockManager.blockList) {
                val bitmap = block.bitmap
                if (!block.isEmpty && bitmap != null) {
                    canvas.drawBitmap(
                        bitmap,
                        block.bitmapDrawSrcRect,
                        block.drawRect,
                        drawBlockPaint
                    )
                    if (isShowBlockBounds) {
                        canvas.drawRect(block.drawRect, drawBlockRectPaint)
                    }
                } else if (!block.isDecodeParamEmpty) {
                    if (isShowBlockBounds) {
                        canvas.drawRect(block.drawRect, drawLoadingBlockRectPaint)
                    }
                }
            }
            canvas.restoreToCount(saveCount)
        }
    }

    private fun onMatrixChanged() {
        if (!isReady && !isInitializing) {
            logger.v(NAME) { "Blocks not available. onMatrixChanged. $imageUri" }
            return
        }
        if (zoomer.rotateDegrees % 90 != 0) {
            logger.w(NAME, "rotate degrees must be in multiples of 90. $imageUri")
            return
        }
        val drawMatrix = tempDrawMatrix.apply {
            reset()
            zoomer.getDrawMatrix(this)
        }
        val newVisibleRect = tempVisibleRect.apply {
            setEmpty()
            zoomer.getVisibleRect(this)
        }

        val drawableSize = zoomer.drawableSize
        val viewSize = zoomer.viewSize
        val zooming = zoomer.isZooming

        // 没有准备好就不往下走了
        if (!isReady) {
            logger.v(NAME) { "not ready. $imageUri" }
            return
        }

        // 暂停中也不走了
        if (isPaused) {
            logger.v(NAME) { "paused. $imageUri" }
            return
        }

        // 传进来的参数不能用就什么也不显示
        if (newVisibleRect.isEmpty || drawableSize.isEmpty || viewSize.isEmpty) {
            logger.w(
                NAME,
                "update params is empty. update. newVisibleRect=%s, drawableSize=%s, viewSize=%s. %s"
                    .format(
                        newVisibleRect.toShortString(),
                        drawableSize.toString(),
                        viewSize.toString(),
                        imageUri
                    )
            )
            clean("update param is empty")
            return
        }

        // 如果当前完整显示预览图的话就清空什么也不显示
        if (newVisibleRect.width() == drawableSize.width && newVisibleRect.height() == drawableSize.height) {
            logger.v(NAME) {
                "full display. update. newVisibleRect=${newVisibleRect.toShortString()}. $imageUri"
            }
            clean("full display")
            return
        }

        // 更新Matrix
        lastZoomScale = zoomScale
        matrix.set(drawMatrix)
        zoomScale = matrix.getScale().format(2)
        invalidateView()
        blockManager.update(newVisibleRect, drawableSize, viewSize, imageSize!!, zooming)
    }

    /* -----------其它方法----------- */
    fun invalidateView() {
        zoomer.view.invalidate()
    }

    /**
     * 设置是否暂停，暂停后会清除所有的碎片，并不会再解码新的碎片
     */
    fun setPause(pause: Boolean) {
        if (pause == isPaused) {
            return
        }
        isPaused = pause
        if (isPaused) {
            logger.v(NAME) { "pause. $imageUri" }
            if (running) {
                clean("pause")
            }
        } else {
            logger.v(NAME) { "resume. $imageUri" }
            if (running) {
                onMatrixChanged()
            }
        }
    }

    /**
     * 工作中？
     */
    val isWorking: Boolean
        get() = !TextUtils.isEmpty(imageUri)

    /**
     * 准备好了？
     */
    val isReady: Boolean
        get() = running && blockDecoder.isReady

    /**
     * 初始化中？
     */
    val isInitializing: Boolean
        get() = running && blockDecoder.isInitializing

    /**
     * 获取图片的尺寸
     */
    val imageSize: Size?
        get() = if (blockDecoder.isReady) blockDecoder.decoder!!.imageSize else null

    /**
     * 获取图片的类型
     */
    val imageType: ImageFormat?
        get() = if (blockDecoder.isReady) blockDecoder.decoder!!.imageFormat else null

    /**
     * 获取绘制区域
     */
    val drawRect: Rect
        get() = blockManager.drawRect

    /**
     * 获取绘制区域在原图中对应的位置
     */
    val drawSrcRect: Rect
        get() = blockManager.drawSrcRect

    /**
     * 获取解码区域
     */
    val decodeRect: Rect
        get() = blockManager.decodeRect

    /**
     * 获取解码区域在原图中对应的位置
     */
    val decodeSrcRect: Rect
        get() = blockManager.decodeSrcRect

    /**
     * 获取碎片列表
     */
    val blockList: List<Block>
        get() = blockManager.blockList

    /**
     * 获取碎片数量
     */
    val blockSize: Int
        get() = blockManager.blockList.size

    /**
     * 获取碎片占用的内存，单位字节
     */
    val allocationByteCount: Long
        get() = blockManager.allocationByteCount

    /**
     * 获取碎片基数，例如碎片基数是3时，就将绘制区域分割成一个 (3+1)x(3+1)=16 个方块
     */
    val blockBaseNumber: Int
        get() = blockManager.blockBaseNumber

    fun getBlockByDrawablePoint(drawableX: Int, drawableY: Int): Block? {
        for (block in blockManager.blockList) {
            if (block.drawRect.contains(drawableX, drawableY)) {
                return block
            }
        }
        return null
    }

    fun getBlockByImagePoint(imageX: Int, imageY: Int): Block? {
        for (block in blockManager.blockList) {
            if (block.srcRect.contains(imageX, imageY)) {
                return block
            }
        }
        return null
    }

    fun interface OnBlockChangedListener {
        fun onBlockChanged(blockDisplayer: Blocks)
    }

    private inner class ExecutorCallback : BlockExecutor.Callback {

        override val context = appContext

        override fun onInitCompleted(imageUri: String, decoder: ImageRegionDecoder) {
            if (!running) {
                logger.w(NAME, "stop running. initCompleted. $imageUri")
                return
            }
            blockDecoder.initCompleted(imageUri, decoder)
            onMatrixChanged()
        }

        override fun onInitError(imageUri: String, e: Exception) {
            if (!running) {
                logger.w(NAME, "stop running. initError. $imageUri")
                return
            }
            blockDecoder.initError(imageUri, e)
        }

        override fun onDecodeCompleted(block: Block, bitmap: Bitmap, useTime: Int) {
            if (!running) {
                logger.w(NAME, "stop running. decodeCompleted. block=${block.info}")
                bitmapPool.free(bitmap)
                return
            }
            blockManager.decodeCompleted(block, bitmap, useTime)
        }

        override fun onDecodeError(block: Block, exception: DecodeErrorException) {
            if (!running) {
                logger.w(NAME, "stop running. decodeError. block=${block.info}")
                return
            }
            blockManager.decodeError(block, exception)
        }
    }
}