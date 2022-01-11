package com.github.panpf.sketch.extensions

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.WrapperListAdapter
import androidx.core.view.descendants
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.internal.requestManagerOrNull

class PauseLoadScrollListener : RecyclerView.OnScrollListener(), AbsListView.OnScrollListener {

    companion object {
        fun attach(recyclerView: RecyclerView) {
            recyclerView.addOnScrollListener(PauseLoadScrollListener())
        }

        fun attach(view: AbsListView) {
            view.setOnScrollListener(PauseLoadScrollListener())
        }
    }

    var absListScrollListenerWrapper: AbsListView.OnScrollListener? = null

    private fun restart(view: ViewGroup) {
        view.descendants.forEach {
            val requestManager = it.requestManagerOrNull
            if (requestManager != null) {
                val result = requestManager.getResult()
                if (result is DisplayResult.Error && result.exception.isCausedByPauseLoadWhenScroll) {
                    requestManager.restart()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val adapter = recyclerView.adapter
        if (adapter != null) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                PauseLoadWhenScrollDisplayInterceptor.scrolling = true
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (PauseLoadWhenScrollDisplayInterceptor.scrolling) {
                    PauseLoadWhenScrollDisplayInterceptor.scrolling = false
                    restart(recyclerView)
                }
            }
        }
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        var listAdapter = view.adapter
        if (listAdapter != null) {
            if (listAdapter is WrapperListAdapter) {
                listAdapter = listAdapter.wrappedAdapter
            }
            if (listAdapter is BaseAdapter) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    PauseLoadWhenScrollDisplayInterceptor.scrolling = true
                } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (PauseLoadWhenScrollDisplayInterceptor.scrolling) {
                        PauseLoadWhenScrollDisplayInterceptor.scrolling = false
                        restart(view)
                    }
                }
            }
        }
        absListScrollListenerWrapper?.onScrollStateChanged(view, scrollState)
    }

    override fun onScroll(
        view: AbsListView,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
        absListScrollListenerWrapper?.onScroll(
            view,
            firstVisibleItem,
            visibleItemCount,
            totalItemCount
        )
    }
}