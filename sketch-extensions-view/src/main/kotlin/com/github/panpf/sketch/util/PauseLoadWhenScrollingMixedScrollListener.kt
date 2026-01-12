/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.sketch.request.PauseLoadWhenScrollingRequestInterceptor

/**
 * Listen to the scrolling events of RecyclerView and AbsListView, pause loading of images when scrolling starts, and resume loading of images when scrolling ends.
 *
 * @see com.github.panpf.sketch.extensions.view.test.util.PauseLoadWhenScrollingMixedScrollListenerTest
 */
class PauseLoadWhenScrollingMixedScrollListener(
    var absListScrollListenerWrapper: AbsListView.OnScrollListener? = null
) : RecyclerView.OnScrollListener(), AbsListView.OnScrollListener {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (recyclerView.adapter != null) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                if (!PauseLoadWhenScrollingRequestInterceptor.scrolling) {
                    PauseLoadWhenScrollingRequestInterceptor.scrolling = true
                }
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (PauseLoadWhenScrollingRequestInterceptor.scrolling) {
                    PauseLoadWhenScrollingRequestInterceptor.scrolling = false
                }
            }
        }
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        if (view.adapter != null) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                if (!PauseLoadWhenScrollingRequestInterceptor.scrolling) {
                    PauseLoadWhenScrollingRequestInterceptor.scrolling = true
                }
            } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (PauseLoadWhenScrollingRequestInterceptor.scrolling) {
                    PauseLoadWhenScrollingRequestInterceptor.scrolling = false
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
}