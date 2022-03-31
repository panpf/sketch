package com.github.panpf.sketch.zoom.tile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import androidx.annotation.MainThread
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.zoom.OnMatrixChangeListener
import com.github.panpf.sketch.zoom.Zoomer
import com.github.panpf.sketch.zoom.tile.internal.TileManager
import com.github.panpf.sketch.zoom.tile.internal.createTileDecoder
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
        internal const val MODULE = "Tiles"
    }

    private val tempDrawMatrix = Matrix()
    private val tempPreviewVisibleRect = Rect()
    private val logger: Logger = context.sketch.logger
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )
    private val onMatrixChangeListener = OnMatrixChangeListener {
        refreshTiles()
    }

    private var _destroyed: Boolean = false
    private var tileManager: TileManager? = null
    internal var onTileChangedListenerList: MutableSet<OnTileChangedListener>? = null

    val destroyed: Boolean
        get() = _destroyed
    val tileList: List<Tile>?
        get() = tileManager?.tileList
    val imageSize: Size?
        get() = tileManager?.imageSize

    var showTileBounds = false
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
                    tileManager?.freeAllTile()
                } else {
                    logger.v(MODULE) { "resume. $imageUri" }
                    refreshTiles()
                }
            }
        }

    init {
        scope.launch(Dispatchers.Main) {
            val tileDecoder = withContext(Dispatchers.IO) {
                createTileDecoder(context, imageUri, disabledExifOrientation)
            } ?: return@launch
            tileManager = TileManager(context, imageUri, viewSize, tileDecoder, this@Tiles)
            refreshTiles()
        }

        zoomer.addOnMatrixChangeListener(onMatrixChangeListener)
    }

    @MainThread
    private fun refreshTiles() {
        requiredMainThread()

        if (destroyed) {
            logger.d(MODULE) { "refreshTiles. destroyed. $imageUri" }
            return
        }
        if (paused) {
            logger.d(MODULE) { "refreshTiles. paused. $imageUri" }
            return
        }
        val manager = tileManager
        if (manager == null) {
            logger.d(MODULE) { "refreshTiles. initializing. $imageUri" }
            return
        }
        if (zoomer.rotateDegrees % 90 != 0) {
            logger.w(MODULE, "rotate degrees must be in multiples of 90. $imageUri")
            return
        }

        val previewSize = zoomer.drawableSize
        val zooming = zoomer.isZooming
        val drawMatrix = tempDrawMatrix.apply {
            zoomer.getDrawMatrix(this)
        }
        val previewVisibleRect = tempPreviewVisibleRect.apply {
            zoomer.getVisibleRect(this)
        }

        if (previewVisibleRect.isEmpty) {
            logger.w(MODULE) {
                "refreshTiles. previewVisibleRect is empty. previewVisibleRect=${previewVisibleRect}. $imageUri"
            }
            tileManager?.freeAllTile()
            return
        }

        if (zooming) {
            logger.d(MODULE) {
                "refreshTiles. zooming. $imageUri"
            }
            return
        }

        tileManager?.refreshTiles(previewSize, previewVisibleRect, drawMatrix)
    }

    @MainThread
    fun onDraw(canvas: Canvas) {
        requiredMainThread()

        if (destroyed) return
        val previewSize = zoomer.drawableSize
        val drawMatrix = tempDrawMatrix
        val previewVisibleRect = tempPreviewVisibleRect
        tileManager?.onDraw(canvas, previewSize, previewVisibleRect, drawMatrix)
    }

    @MainThread
    internal fun invalidateView() {
        requiredMainThread()

        zoomer.view.invalidate()
    }

    fun addOnTileChangedListener(listener: OnTileChangedListener) {
        this.onTileChangedListenerList = (onTileChangedListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
    }

    fun removeOnTileChangedListener(listener: OnTileChangedListener): Boolean {
        return onTileChangedListenerList?.remove(listener) == true
    }

    fun eachTileList(action: (tile: Tile, load: Boolean) -> Unit) {
        val previewSize = zoomer.drawableSize.takeIf { !it.isEmpty } ?: return
        val previewVisibleRect = tempPreviewVisibleRect.apply {
            zoomer.getVisibleRect(this)
        }.takeIf { !it.isEmpty } ?: return
        tileManager?.eachTileList(previewSize, previewVisibleRect, action)
    }

    @MainThread
    fun destroy() {
        requiredMainThread()

        if (_destroyed) return
        _destroyed = true
        zoomer.removeOnMatrixChangeListener(onMatrixChangeListener)
        scope.cancel()
        tileManager?.destroy()
        tileManager = null
    }
}