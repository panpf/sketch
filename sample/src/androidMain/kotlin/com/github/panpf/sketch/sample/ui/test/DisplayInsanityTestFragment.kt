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
package com.github.panpf.sketch.sample.ui.test

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.GridLayoutManager
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.addAssemblyGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.newAssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerRefreshBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LoadStateItemFactory
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.common.list.findPagingAdapter
import com.github.panpf.sketch.sample.ui.gallery.PhotoGridItemFactory
import com.github.panpf.sketch.sample.ui.model.PhotoDiffCallback
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.sketch

class DisplayInsanityTestFragment : BaseToolbarBindingFragment<FragmentRecyclerRefreshBinding>() {

    private val localPhotoListViewModel by viewModels<InsanityTestViewModel>()

    override fun getNavigationBarInsetsView(binding: FragmentRecyclerRefreshBinding): View {
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentRecyclerRefreshBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "DisplayInsanityTest"

        binding.myRecycler.apply {
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

            val pagingAdapter = AssemblyPagingDataAdapter(
                itemFactoryList = listOf(PhotoGridItemFactory()),
                diffCallback = PhotoDiffCallback()
            ).apply {
                localPhotoListViewModel.pagingFlow
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) { pagingData ->
                        submitData(pagingData)
                    }
            }

            binding.swipeRefresh.setOnRefreshListener {
                pagingAdapter.refresh()
            }

            pagingAdapter.loadStateFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) { loadStates ->
                    when (val refreshState = loadStates.refresh) {
                        is LoadState.Loading -> {
                            binding.state.gone()
                            binding.swipeRefresh.isRefreshing = true
                        }

                        is LoadState.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            binding.state.error {
                                message(refreshState.error)
                                retryAction {
                                    pagingAdapter.refresh()
                                }
                            }
                        }

                        is LoadState.NotLoading -> {
                            binding.swipeRefresh.isRefreshing = false
                            if (pagingAdapter.itemCount <= 0) {
                                binding.state.empty {
                                    message("No Photos")
                                }
                            } else {
                                binding.state.gone()
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
            appSettingsService.exifOrientation.ignoreFirst()
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    adapter?.findPagingAdapter()?.refresh()
                }
        }
    }

    class InsanityTestViewModel(application: Application) : AndroidViewModel(application) {
        val pagingFlow = Pager(
            config = PagingConfig(
                pageSize = 80,
                enablePlaceholders = false,
            ),
            initialKey = 0,
            pagingSourceFactory = {
                DisplayInsanityTestPagingSource(application, application.sketch)
            }
        ).flow.cachedIn(viewModelScope)
    }
}
