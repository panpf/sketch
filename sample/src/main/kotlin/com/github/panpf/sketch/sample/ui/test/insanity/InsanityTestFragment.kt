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
package com.github.panpf.sketch.sample.ui.test.insanity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.addAssemblyGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.newAssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.RecyclerFragmentBinding
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LoadStateItemFactory
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.common.list.findPagingAdapter
import com.github.panpf.sketch.sample.ui.photo.ImageGridItemFactory
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle

class InsanityTestFragment : BaseToolbarBindingFragment<RecyclerFragmentBinding>() {

    private val localPhotoListViewModel by viewModels<InsanityTestViewModel>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(
        toolbar: Toolbar,
        binding: RecyclerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Insanity Test"

        binding.recyclerRecycler.apply {
            layoutManager =
                newAssemblyGridLayoutManager(3, GridLayoutManager.VERTICAL) {
                    itemSpanByItemFactory(
                        LoadStateItemFactory::class,
                        ItemSpan.fullSpan()
                    )
                }
            addAssemblyGridDividerItemDecoration {
                val gridDivider =
                    requireContext().resources.getDimensionPixelSize(R.dimen.grid_divider)
                divider(Divider.space(gridDivider))
                sideDivider(Divider.space(gridDivider))
                useDividerAsHeaderAndFooterDivider()
                useSideDividerAsSideHeaderAndFooterDivider()
            }

            val pagingAdapter = AssemblyPagingDataAdapter<Photo>(
                listOf(ImageGridItemFactory())
            ).apply {
                localPhotoListViewModel.pagingFlow
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) { pagingData ->
                        submitData(pagingData)
                    }
            }

            binding.recyclerRefresh.setOnRefreshListener {
                pagingAdapter.refresh()
            }

            pagingAdapter.loadStateFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) { loadStates ->
                    when (val refreshState = loadStates.refresh) {
                        is LoadState.Loading -> {
                            binding.recyclerState.gone()
                            binding.recyclerRefresh.isRefreshing = true
                        }

                        is LoadState.Error -> {
                            binding.recyclerRefresh.isRefreshing = false
                            binding.recyclerState.errorWithRetry(refreshState.error) {
                                pagingAdapter.refresh()
                            }
                        }

                        is LoadState.NotLoading -> {
                            binding.recyclerRefresh.isRefreshing = false
                            if (pagingAdapter.itemCount <= 0) {
                                binding.recyclerState.empty("No Photos")
                            } else {
                                binding.recyclerState.gone()
                            }
                        }
                    }
                }

            adapter = pagingAdapter.withLoadStateFooter(MyLoadStateAdapter().apply {
                noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
            })

            appSettingsService.listsCombinedFlow.ignoreFirst()
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    adapter?.notifyDataSetChanged()
                }
            appSettingsService.ignoreExifOrientation.sharedFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    adapter?.findPagingAdapter()?.refresh()
                }
        }
    }
}
