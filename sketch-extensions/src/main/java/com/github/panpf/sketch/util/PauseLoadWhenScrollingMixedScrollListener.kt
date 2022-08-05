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
import com.github.panpf.sketch.request.isCausedByPauseLoadWhenScrolling

class PauseLoadWhenScrollingMixedScrollListener(
    var absListScrollListenerWrapper: AbsListView.OnScrollListener? = null
) : RecyclerView.OnScrollListener(), AbsListView.OnScrollListener {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val adapter = recyclerView.adapter
        if (adapter != null) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                if (!PauseLoadWhenScrollingDisplayInterceptor.scrolling) {
                    PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
                }
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
            val result = SketchUtils.getResult(it)
            if (result is DisplayResult.Error
                && isCausedByPauseLoadWhenScrolling(result.request, result.exception)
            ) {
                SketchUtils.restart(it)
            }
        }
    }


    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        val listAdapter = view.adapter?.let { getFinalWrappedAdapter(it) }
        if (listAdapter is BaseAdapter) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                if (!PauseLoadWhenScrollingDisplayInterceptor.scrolling) {
                    PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
                }
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