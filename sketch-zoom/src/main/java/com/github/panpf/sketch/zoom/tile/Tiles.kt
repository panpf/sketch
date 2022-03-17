package com.github.panpf.sketch.zoom.tile

import android.content.Context
import android.graphics.Canvas
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
    private val logger = context.sketch.logger
    private val scope: CoroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )
    private val lastVisibleRect = Rect()

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
                    refreshTiles()
                }
            }
        }

    init {
        scope.launch(Dispatchers.Main) {
            val tileDecoder = withContext(Dispatchers.IO) {
                TileDecoder.Factory(context, imageUri, disabledExifOrientation).create()
            } ?: return@launch
            this@Tiles.tileManager = TileManager(context, imageUri, viewSize, tileDecoder)
            refreshTiles()
        }

        zoomer.addOnMatrixChangeListener {
            if (zoomer.rotateDegrees % 90 != 0) {
                logger.w(MODULE, "rotate degrees must be in multiples of 90. $imageUri")
                return@addOnMatrixChangeListener
            }
            zoomer.getDrawMatrix(tempDrawMatrix)
            zoomer.getVisibleRect(tempVisibleRect)
            refreshTiles()
        }
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

    private fun refreshTiles() {
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
        val visibleRect = tempVisibleRect

        if (visibleRect.isEmpty || drawableSize.isEmpty || viewSize.isEmpty) {
            logger.w(MODULE) {
                "params is empty. visibleRect=${visibleRect}, drawableSize=$drawableSize, viewSize=$viewSize. $imageUri"
            }
            cleanMemory()
            return
        }

        if (zooming) {
            logger.d(MODULE) {
                "zooming. visibleRect=${visibleRect}. $imageUri"
            }
            return
        }

        if (visibleRect.width() == drawableSize.width && visibleRect.height() == drawableSize.height) {
            logger.d(MODULE) {
                "full display. visibleRect=${visibleRect}, drawableSize=$drawableSize. $imageUri"
            }
            cleanMemory()
            return
        }
        if (lastVisibleRect == visibleRect) {
            logger.d(MODULE) {
                "visible rect no changed. visibleRect=$visibleRect. $imageUri"
            }
            return
        } else {
            lastVisibleRect.set(visibleRect)
        }

        tileManager?.refreshTiles(visibleRect, drawableSize, drawMatrix)
    }

    fun onDraw(canvas: Canvas) {
        if (destroyed) return
        tileManager?.onDraw(canvas)
    }

    val destroyed: Boolean
        get() = _destroyed

    internal fun invalidateView() {
        zoomer.view.invalidate()
    }
}