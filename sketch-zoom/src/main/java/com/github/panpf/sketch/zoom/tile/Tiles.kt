/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.zoom.tile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.OnMatrixChangeListener
import com.github.panpf.sketch.zoom.Zoomer
import com.github.panpf.sketch.zoom.internal.format
import com.github.panpf.sketch.zoom.internal.requiredMainThread
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
    private val sketch: Sketch,
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
    private val logger: Logger = sketch.logger
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
    var paused = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    logger.d(MODULE) { "pause. $imageUri" }
                    tileManager?.clean()
                } else {
                    logger.d(MODULE) { "resume. $imageUri" }
                    refreshTiles()
                }
            }
        }

    init {
        scope.launch(Dispatchers.Main) {
            val tileDecoder = withContext(Dispatchers.IO) {
                createTileDecoder(context, sketch, imageUri, disabledExifOrientation)
            } ?: return@launch
            tileManager = TileManager(sketch, imageUri, viewSize, tileDecoder, this@Tiles)
            refreshTiles()
        }

        zoomer.addOnMatrixChangeListener(onMatrixChangeListener)
    }

    @MainThread
    private fun refreshTiles() {
        requiredMainThread()

        if (destroyed) {
            logger.d(MODULE) { "refreshTiles. interrupted. destroyed. $imageUri" }
            return
        }
        if (paused) {
            logger.d(MODULE) { "refreshTiles. interrupted. paused. $imageUri" }
            return
        }
        val manager = tileManager
        if (manager == null) {
            logger.d(MODULE) { "refreshTiles. interrupted. initializing. $imageUri" }
            return
        }
        if (zoomer.rotateDegrees % 90 != 0) {
            logger.w(
                MODULE,
                "refreshTiles. interrupted. rotate degrees must be in multiples of 90. $imageUri"
            )
            return
        }

        val previewSize = zoomer.drawableSize
        val scaling = zoomer.isScaling
        val drawMatrix = tempDrawMatrix.apply {
            zoomer.getDrawMatrix(this)
        }
        val previewVisibleRect = tempPreviewVisibleRect.apply {
            zoomer.getVisibleRect(this)
        }

        if (previewVisibleRect.isEmpty) {
            logger.w(MODULE) {
                "refreshTiles. interrupted. previewVisibleRect is empty. previewVisibleRect=${previewVisibleRect}. $imageUri"
            }
            tileManager?.clean()
            return
        }

        if (scaling) {
            logger.d(MODULE) {
                "refreshTiles. interrupted. scaling. $imageUri"
            }
            return
        }

        if (zoomer.scale.format(2) <= zoomer.minScale.format(2)) {
            logger.d(MODULE) {
                "refreshTiles. interrupted. minScale. $imageUri"
            }
            tileManager?.clean()
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
        logger.w(MODULE, "destroy")
        _destroyed = true
        zoomer.removeOnMatrixChangeListener(onMatrixChangeListener)
        scope.cancel()
        tileManager?.destroy()
        tileManager = null
    }
}