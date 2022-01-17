package com.github.panpf.sketch.util

import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.WrapperListAdapter
import androidx.core.view.descendants
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDisplayInterceptor
import com.github.panpf.sketch.request.RequestManagerUtils
import com.github.panpf.sketch.request.isCausedByPauseLoadWhenScrolling

class PauseLoadWhenScrollingMixedScrollListener : RecyclerView.OnScrollListener(),
    AbsListView.OnScrollListener {

    companion object {
        fun attach(recyclerView: RecyclerView) {
            recyclerView.addOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())
        }

        fun attach(view: AbsListView) {
            view.setOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())
        }
    }

    var absListScrollListenerWrapper: AbsListView.OnScrollListener? = null

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val adapter = recyclerView.adapter
        if (adapter != null) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (PauseLoadWhenScrollingDisplayInterceptor.scrolling) {
                    PauseLoadWhenScrollingDisplayInterceptor.scrolling = false
                    restartAllChildViewRequest(recyclerView)
                }
            }
        }
    }

    private fun restartAllChildViewRequest(view: ViewGroup) {
        view.descendants.forEach {
            val requestManager = RequestManagerUtils.requestManagerOrNull(it)
            if (requestManager != null) {
                val result = requestManager.getResult()
                if (result is DisplayResult.Error && result.exception.isCausedByPauseLoadWhenScrolling) {
                    requestManager.restart()
                }
            }
        }
    }


    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        val listAdapter = view.adapter?.let { getFinalWrappedAdapter(it) }
        if (listAdapter is BaseAdapter) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
            } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (PauseLoadWhenScrollingDisplayInterceptor.scrolling) {
                    PauseLoadWhenScrollingDisplayInterceptor.scrolling = false
                    restartAllChildViewRequest(view)
                }
            }
        }
        absListScrollListenerWrapper?.onScrollStateChanged(view, scrollState)
    }

    override fun onScroll(
        view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int
    ) {
        absListScrollListenerWrapper
            ?.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
    }

    private fun getFinalWrappedAdapter(adapter: ListAdapter): ListAdapter =
        if (adapter is WrapperListAdapter) {
            getFinalWrappedAdapter(adapter.wrappedAdapter)
        } else {
            adapter
        }
}