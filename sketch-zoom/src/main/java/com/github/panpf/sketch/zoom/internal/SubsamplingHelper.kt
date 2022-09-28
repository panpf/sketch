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
package com.github.panpf.sketch.zoom.internal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.OnMatrixChangeListener
import com.github.panpf.sketch.zoom.OnTileChangedListener
import com.github.panpf.sketch.zoom.Tile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SubsamplingHelper constructor(
    private val context: Context,
    private val sketch: Sketch,
    private val zoomerHelper: ZoomerHelper,
    private val imageUri: String,
    private val imageInfo: ImageInfo,
    private val memoryCachePolicy: CachePolicy,
    private val disallowReuseBitmap: Boolean,
    viewSize: Size,
) {

    companion object {
        internal const val MODULE = "SubsamplingHelper"
    }

    private val tempDrawMatrix = Matrix()
    private val tempDrawableVisibleRect = Rect()
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

    @Suppress("MemberVisibilityCanBePrivate")
    val destroyed: Boolean
        get() = _destroyed
    val tileList: List<Tile>?
        get() = tileManager?.tileList

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
                    logger.d(MODULE) { "pause. '$imageUri'" }
                    tileManager?.clean()
                } else {
                    logger.d(MODULE) { "resume. '$imageUri'" }
                    refreshTiles()
                }
            }
        }

    init {
        scope.launch(Dispatchers.Main) {
            val dataSource = withContext(Dispatchers.IO) {
                sketch.components.newFetcher(LoadRequest(context, imageUri)).fetch()
            }.dataSource
            val tileDecoder = TileDecoder(
                sketch = sketch,
                imageUri = imageUri,
                imageInfo = imageInfo,
                disallowReuseBitmap = disallowReuseBitmap,
                dataSource = dataSource
            )
            tileManager = TileManager(
                sketch = sketch,
                imageUri = imageUri,
                imageSize = Size(imageInfo.width, imageInfo.height),
                memoryCachePolicy = memoryCachePolicy,
                disallowReuseBitmap = disallowReuseBitmap,
                viewSize = viewSize,
                decoder = tileDecoder,
                subsamplingHelper = this@SubsamplingHelper
            )
            refreshTiles()
        }

        zoomerHelper.addOnMatrixChangeListener(onMatrixChangeListener)
    }

    @MainThread
    private fun refreshTiles() {
        requiredMainThread()

        if (destroyed) {
            logger.d(MODULE) { "refreshTiles. interrupted. destroyed. '$imageUri'" }
            return
        }
        if (paused) {
            logger.d(MODULE) { "refreshTiles. interrupted. paused. '$imageUri'" }
            return
        }
        val manager = tileManager
        if (manager == null) {
            logger.d(MODULE) { "refreshTiles. interrupted. initializing. '$imageUri'" }
            return
        }
        if (zoomerHelper.rotateDegrees % 90 != 0) {
            logger.d(MODULE) {
                "refreshTiles. interrupted. rotate degrees must be in multiples of 90. '$imageUri'"
            }
            return
        }

        val drawableSize = zoomerHelper.drawableSize
        val scaling = zoomerHelper.isScaling
        val drawMatrix = tempDrawMatrix.apply {
            zoomerHelper.getDrawMatrix(this)
        }
        val drawableVisibleRect = tempDrawableVisibleRect.apply {
            zoomerHelper.getVisibleRect(this)
        }

        if (drawableVisibleRect.isEmpty) {
            logger.d(MODULE) {
                "refreshTiles. interrupted. drawableVisibleRect is empty. drawableVisibleRect=${drawableVisibleRect}. '$imageUri'"
            }
            tileManager?.clean()
            return
        }

        if (scaling) {
            logger.d(MODULE) {
                "refreshTiles. interrupted. scaling. '$imageUri'"
            }
            return
        }

        if (zoomerHelper.scale.format(2) <= zoomerHelper.minScale.format(2)) {
            logger.d(MODULE) {
                "refreshTiles. interrupted. minScale. '$imageUri'"
            }
            tileManager?.clean()
            return
        }

        tileManager?.refreshTiles(drawableSize, drawableVisibleRect, drawMatrix)
    }

    @MainThread
    fun onDraw(canvas: Canvas) {
        requiredMainThread()

        if (destroyed) return
        val drawableSize = zoomerHelper.drawableSize
        val drawMatrix = tempDrawMatrix
        val drawableVisibleRect = tempDrawableVisibleRect
        tileManager?.onDraw(canvas, drawableSize, drawableVisibleRect, drawMatrix)
    }

    @MainThread
    internal fun invalidateView() {
        requiredMainThread()

        zoomerHelper.view.invalidate()
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
        val drawableSize = zoomerHelper.drawableSize.takeIf { !it.isEmpty } ?: return
        val drawableVisibleRect = tempDrawableVisibleRect.apply {
            zoomerHelper.getVisibleRect(this)
        }.takeIf { !it.isEmpty } ?: return
        tileManager?.eachTileList(drawableSize, drawableVisibleRect, action)
    }

    @MainThread
    fun destroy() {
        requiredMainThread()

        if (_destroyed) return
        logger.d(MODULE) {
            "destroy"
        }
        _destroyed = true
        zoomerHelper.removeOnMatrixChangeListener(onMatrixChangeListener)
        scope.cancel()
        tileManager?.destroy()
        tileManager = null
    }
}