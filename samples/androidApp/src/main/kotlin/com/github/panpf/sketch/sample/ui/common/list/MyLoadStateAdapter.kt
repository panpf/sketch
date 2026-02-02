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

package com.github.panpf.sketch.sample.ui.common.list

import android.annotation.SuppressLint
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyLoadStateAdapter

class MyLoadStateAdapter(
    alwaysShowWhenEndOfPaginationReached: Boolean = true
) : AssemblyLoadStateAdapter(LoadStateItemFactory(), alwaysShowWhenEndOfPaginationReached) {

    var disableDisplay = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var pagingDataAdapter: PagingDataAdapter<*, *>? = null

    fun noDisplayLoadStateWhenPagingEmpty(pagingDataAdapter: PagingDataAdapter<*, *>) {
        this.pagingDataAdapter = pagingDataAdapter
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        if (disableDisplay) {
            return false
        }
        val pagingDataAdapter = pagingDataAdapter
        if (pagingDataAdapter != null && loadState is LoadState.NotLoading && pagingDataAdapter.itemCount == 0) {
            return false
        }
        return super.displayLoadStateAsItem(loadState)
    }
}