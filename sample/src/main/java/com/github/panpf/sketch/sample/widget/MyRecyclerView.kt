package com.github.panpf.sketch.sample.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.util.observeWithViewLifecycle
import com.github.panpf.sketch.util.PauseLoadWhenScrollingMixedScrollListener
import kotlinx.coroutines.flow.merge

@SuppressLint("NotifyDataSetChanged")
class MyRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    init {
        val scrollListener = PauseLoadWhenScrollingMixedScrollListener()
        prefsService.pauseLoadWhenScrollInList.stateFlow.observeWithViewLifecycle(this) {
            if (it) {
                addOnScrollListener(scrollListener)
            } else {
                removeOnScrollListener(scrollListener)
            }
        }

        merge(
            prefsService.resizePrecision.sharedFlow,
            prefsService.resizeScale.sharedFlow,
            prefsService.longImageResizeScale.sharedFlow,
            prefsService.otherImageResizeScale.sharedFlow,
        ).observeWithViewLifecycle(this@MyRecyclerView) {
            adapter?.notifyDataSetChanged()
        }

        prefsService.ignoreExifOrientation.sharedFlow.observeWithViewLifecycle(this) {
            adapter?.findPagingAdapter()?.refresh()
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