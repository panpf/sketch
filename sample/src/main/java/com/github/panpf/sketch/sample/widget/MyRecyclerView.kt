package com.github.panpf.sketch.sample.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.view.descendants
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.util.observeWithViewLifecycle
import com.github.panpf.sketch.util.PauseLoadWhenScrollingMixedScrollListener
import com.github.panpf.sketch.util.SketchUtils
import kotlinx.coroutines.flow.merge

@SuppressLint("NotifyDataSetChanged")
class MyRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private val scrollListener = PauseLoadWhenScrollingMixedScrollListener()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        prefsService.apply {
            merge(
                prefsService.resizePrecision.sharedFlow,
                prefsService.resizeScale.sharedFlow,
                prefsService.longImageResizeScale.sharedFlow,
                prefsService.otherImageResizeScale.sharedFlow,
            ).observeWithViewLifecycle(this@MyRecyclerView) {
                adapter?.notifyDataSetChanged()
            }

            merge(
                disallowAnimatedImageInList.sharedFlow,
                pauseLoadWhenScrollInList.sharedFlow,
                saveCellularTrafficInList.sharedFlow,
                disabledBitmapMemoryCache.sharedFlow,
                disabledDownloadCache.sharedFlow,
                disabledBitmapResultCache.sharedFlow,
                disallowReuseBitmap.sharedFlow,
                inPreferQualityOverSpeed.sharedFlow,
                bitmapQuality.sharedFlow,
            ).observeWithViewLifecycle(this@MyRecyclerView) {
                descendants.forEach {
                    SketchUtils.restart(it)
                }
            }

            pauseLoadWhenScrollInList.stateFlow.observeWithViewLifecycle(this@MyRecyclerView) {
                if (it) {
                    addOnScrollListener(scrollListener)
                } else {
                    removeOnScrollListener(scrollListener)
                }
            }

            showProgressIndicatorInList.sharedFlow.observeWithViewLifecycle(this@MyRecyclerView) {
                descendants.forEach { view ->
                    if (view is MyListImageView) {
                        view.setShowProgressIndicator(it)
                    }
                }
            }
            showMimeTypeLogoInLIst.sharedFlow.observeWithViewLifecycle(this@MyRecyclerView) {
                descendants.forEach { view ->
                    if (view is MyListImageView) {
                        view.setShowMimeTypeLogo(it)
                    }
                }
            }
            showDataFromLogo.sharedFlow.observeWithViewLifecycle(this@MyRecyclerView) {
                descendants.forEach { view ->
                    if (view is MyListImageView) {
                        view.setShowDataFromLogo(it)
                    }
                }
            }

            ignoreExifOrientation.sharedFlow.observeWithViewLifecycle(this@MyRecyclerView) {
                adapter?.findPagingAdapter()?.refresh()
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