package com.github.panpf.sketch.sample.image

import android.app.Application
import android.widget.ImageView.ScaleType
import androidx.core.view.descendants
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.util.lifecycleOwner
import com.github.panpf.sketch.sample.widget.MyRecyclerView
import com.github.panpf.sketch.sample.widget.MyZoomImageView
import com.github.panpf.sketch.util.SketchUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class SettingsEventViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsService = application.prefsService

    private val flow: Flow<Any> = merge(
        prefsService.saveCellularTrafficInList.sharedFlow,

        prefsService.resizePrecision.sharedFlow,
        prefsService.resizeScale.sharedFlow,
        prefsService.longImageResizeScale.sharedFlow,
        prefsService.otherImageResizeScale.sharedFlow,

        prefsService.bitmapQuality.sharedFlow,
        prefsService.colorSpace.sharedFlow,
        prefsService.inPreferQualityOverSpeed.sharedFlow,

        prefsService.disabledMemoryCache.sharedFlow,
        prefsService.disabledResultCache.sharedFlow,
        prefsService.disabledDownloadCache.sharedFlow,
        prefsService.disallowReuseBitmap.sharedFlow,

        prefsService.disallowAnimatedImageInList.sharedFlow,
    )

    fun observeListSettings(recyclerView: MyRecyclerView) {
        recyclerView.lifecycleOwner.lifecycleScope.launch {
            flow.collect {
                recyclerView.descendants.forEach {
                    SketchUtils.restart(it)
                }
            }
        }

        recyclerView.lifecycleOwner.lifecycleScope.launch {
            prefsService.ignoreExifOrientation.sharedFlow.collect {
                recyclerView.adapter?.findPagingAdapter()?.refresh()
            }
        }
    }

    fun observeZoomSettings(zoomImageView: MyZoomImageView) {
        zoomImageView.lifecycleOwner.lifecycleScope.launch {
            prefsService.scrollBarEnabled.stateFlow.collect {
                zoomImageView.scrollBarEnabled = it
            }
        }
        zoomImageView.lifecycleOwner.lifecycleScope.launch {
            prefsService.readModeEnabled.stateFlow.collect {
                zoomImageView.readModeEnabled = it
            }
        }
        zoomImageView.lifecycleOwner.lifecycleScope.launch {
            prefsService.showTileBoundsInHugeImagePage.stateFlow.collect {
                zoomImageView.showTileBounds = it
            }
        }
        zoomImageView.lifecycleOwner.lifecycleScope.launch {
            prefsService.scaleType.stateFlow.collect {
                zoomImageView.scaleType = ScaleType.valueOf(it)
            }
        }
    }

    private fun Adapter<*>.findPagingAdapter(): PagingDataAdapter<*, *>? {
        when (this) {
            is PagingDataAdapter<*, *> -> {
                return this
            }
            is ConcatAdapter -> {
                this.adapters.forEach {
                    it.findPagingAdapter()?.let { pagingDataAdapter ->
                        return pagingDataAdapter
                    }
                }
                return null
            }
            else -> {
                return null
            }
        }
    }
}