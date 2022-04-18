/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.addAssemblyGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.newAssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.base.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LoadStateItemFactory
import com.github.panpf.sketch.sample.ui.photo.PhotoItemFactory
import kotlinx.coroutines.launch

class InsanityTestFragment : ToolbarBindingFragment<FragmentRecyclerBinding>() {

    private val localPhotoListViewModel by viewModels<InsanityTestViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    @SuppressLint("NotifyDataSetChanged")
    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Insanity Test"

        binding.recyclerRecyclerFragmentContent.apply {
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
                listOf(PhotoItemFactory(disabledCache = true))
            )

            binding.refreshRecyclerFragment.setOnRefreshListener {
                pagingAdapter.refresh()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                pagingAdapter.loadStateFlow.collect { loadStates ->
                    when (val refreshState = loadStates.refresh) {
                        is LoadState.Loading -> {
                            binding.hintRecyclerFragment.hidden()
                            binding.refreshRecyclerFragment.isRefreshing = true
                        }
                        is LoadState.Error -> {
                            binding.refreshRecyclerFragment.isRefreshing = false
                            binding.hintRecyclerFragment.failed(refreshState.error) {
                                pagingAdapter.refresh()
                            }
                        }
                        is LoadState.NotLoading -> {
                            binding.refreshRecyclerFragment.isRefreshing = false
                            if (pagingAdapter.itemCount <= 0) {
                                binding.hintRecyclerFragment.empty("No Photos")
                            } else {
                                binding.hintRecyclerFragment.hidden()
                            }
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                localPhotoListViewModel.pagingFlow.collect { pagingData ->
                    pagingAdapter.submitData(pagingData)
                }
            }

            adapter = pagingAdapter.withLoadStateFooter(MyLoadStateAdapter().apply {
                noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
            })
        }
    }
}
