package com.github.panpf.sketch.sample.ui.common.list

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter


fun Adapter<*>.findPagingAdapter(): PagingDataAdapter<*, *>? {
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