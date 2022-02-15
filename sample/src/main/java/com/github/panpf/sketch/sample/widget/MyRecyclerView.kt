package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.util.observeFromView
import com.github.panpf.sketch.util.PauseLoadWhenScrollingMixedScrollListener

class MyRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private val mediatorLiveData = MediatorLiveData<Any>()

    init {
        val scrollListener = PauseLoadWhenScrollingMixedScrollListener()
        appSettingsService.pauseLoadWhenScrollInList.observeFromView(this) {
            if (it == true) {
                addOnScrollListener(scrollListener)
            } else {
                removeOnScrollListener(scrollListener)
            }
        }

        mediatorLiveData.apply {
            val observer = Observer<Any> {
                postValue(1)
            }
            addSource(appSettingsService.resizePrecision, observer)
            addSource(appSettingsService.resizeScale, observer)
        }

        mediatorLiveData.observeFromView(this) {
            adapter?.notifyDataSetChanged()
        }

        appSettingsService.ignoreExifOrientation.observeFromView(this) {
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