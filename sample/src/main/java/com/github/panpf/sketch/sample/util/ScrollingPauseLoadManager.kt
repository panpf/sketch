package com.github.panpf.sketch.sample.util

import android.content.Context
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.WrapperListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.appSettingsService

/**
 * 滚动中暂停暂停加载新图片管理器支持RecyclerView和AbsListView
 */
class ScrollingPauseLoadManager(context: Context?) : RecyclerView.OnScrollListener(),
    AbsListView.OnScrollListener {

    private val sketch: Sketch = Sketch.with(context!!)
    private var absListScrollListener: AbsListView.OnScrollListener? = null
    private var recyclerScrollListener: RecyclerView.OnScrollListener? = null

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        recyclerScrollListener?.onScrolled(recyclerView, dx, dy)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (recyclerView.appSettingsService.scrollingPauseLoadEnabled.value == true && recyclerView.adapter != null) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                sketch.configuration.isPauseLoadEnabled = true
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (sketch.configuration.isPauseLoadEnabled) {
                    sketch.configuration.isPauseLoadEnabled = false
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
            }
        }
        recyclerScrollListener?.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        if (view.appSettingsService.scrollingPauseLoadEnabled.value == true && view.adapter != null) {
            var listAdapter = view.adapter
            if (listAdapter is WrapperListAdapter) {
                listAdapter = listAdapter.wrappedAdapter
            }
            if (listAdapter is BaseAdapter) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    if (!sketch.configuration.isPauseLoadEnabled) {
                        sketch.configuration.isPauseLoadEnabled = true
                    }
                } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (sketch.configuration.isPauseLoadEnabled) {
                        sketch.configuration.isPauseLoadEnabled = false
                        listAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        absListScrollListener?.onScrollStateChanged(view, scrollState)
    }

    override fun onScroll(
        view: AbsListView,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
        absListScrollListener?.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
    }

    fun setOnScrollListener(absListViewScrollListener: AbsListView.OnScrollListener?) {
        absListScrollListener = absListViewScrollListener
    }

    fun setOnScrollListener(recyclerScrollListener: RecyclerView.OnScrollListener?) {
        this.recyclerScrollListener = recyclerScrollListener
    }

}