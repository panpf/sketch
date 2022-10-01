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
package com.github.panpf.sketch.sample.ui.photo.local

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.newAssemblyGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.divider.newAssemblyStaggeredGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.newAssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.newAssemblyStaggeredGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.RecyclerFragmentBinding
import com.github.panpf.sketch.sample.image.SettingsEventViewModel
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.model.LayoutMode
import com.github.panpf.sketch.sample.model.LayoutMode.GRID
import com.github.panpf.sketch.sample.model.LayoutMode.STAGGERED_GRID
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LoadStateItemFactory
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.common.menu.ListMenuViewModel
import com.github.panpf.sketch.sample.ui.photo.ImageGridItemFactory
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalPhotoListFragment : ToolbarBindingFragment<RecyclerFragmentBinding>() {

    private val settingsEventViewModel by viewModels<SettingsEventViewModel>()
    private val localPhotoListViewModel by viewModels<LocalPhotoListViewModel>()
    private val listMenuViewModel by viewModels<ListMenuViewModel> {
        ListMenuViewModel.Factory(
            requireActivity().application,
            showLayoutModeMenu = true,
            showPlayMenu = true
        )
    }
    private var pagingFlowCollectJob: Job? = null
    private var loadStateFlowCollectJob: Job? = null

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: RecyclerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.apply {
            title = "Local Photos"

            viewLifecycleOwner.lifecycleScope.launch {
                listMenuViewModel.menuFlow.collect { list ->
                    menu.clear()
                    list.forEachIndexed { groupIndex, group ->
                        group.items.forEachIndexed { index, menuItemInfo ->
                            menu.add(groupIndex, index, index, menuItemInfo.title).apply {
                                menuItemInfo.iconResId?.let { iconResId ->
                                    setIcon(iconResId)
                                }
                                setOnMenuItemClickListener {
                                    menuItemInfo.onClick(this@LocalPhotoListFragment)
                                    true
                                }
                                setShowAsAction(menuItemInfo.showAsAction)
                            }
                        }
                    }
                }
            }
        }

        binding.recyclerRecycler.apply {
            settingsEventViewModel.observeListSettings(this)
            viewLifecycleOwner.lifecycleScope.launch {
                prefsService.photoListLayoutMode.stateFlow.collect {
                    (0 until itemDecorationCount).forEach { index ->
                        removeItemDecorationAt(index)
                    }
                    val (layoutManager1, itemDecoration) =
                        newLayoutManagerAndItemDecoration(LayoutMode.valueOf(it))
                    layoutManager = layoutManager1
                    addItemDecoration(itemDecoration)
                    this@apply.adapter = newAdapter(binding)
                }
            }
        }
    }

    private fun newLayoutManagerAndItemDecoration(layoutMode: LayoutMode): Pair<LayoutManager, ItemDecoration> {
        val layoutManager: LayoutManager
        val itemDecoration: ItemDecoration
        when (layoutMode) {
            GRID -> {
                layoutManager =
                    requireContext().newAssemblyGridLayoutManager(3, GridLayoutManager.VERTICAL) {
                        itemSpanByItemFactory(LoadStateItemFactory::class, ItemSpan.fullSpan())
                    }
                itemDecoration = requireContext().newAssemblyGridDividerItemDecoration {
                    val gridDivider =
                        requireContext().resources.getDimensionPixelSize(R.dimen.grid_divider)
                    divider(Divider.space(gridDivider))
                    sideDivider(Divider.space(gridDivider))
                    useDividerAsHeaderAndFooterDivider()
                    useSideDividerAsSideHeaderAndFooterDivider()
                }
            }
            STAGGERED_GRID -> {
                layoutManager = newAssemblyStaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                ) {
                    fullSpanByItemFactory(LoadStateItemFactory::class)
                }
                itemDecoration = requireContext().newAssemblyStaggeredGridDividerItemDecoration {
                    val gridDivider =
                        requireContext().resources.getDimensionPixelSize(R.dimen.grid_divider)
                    divider(Divider.space(gridDivider))
                    sideDivider(Divider.space(gridDivider))
                    useDividerAsHeaderAndFooterDivider()
                    useSideDividerAsSideHeaderAndFooterDivider()
                }
            }
        }
        return layoutManager to itemDecoration
    }

    private fun newAdapter(binding: RecyclerFragmentBinding): RecyclerView.Adapter<*> {
        val pagingAdapter = AssemblyPagingDataAdapter<Photo>(listOf(
            ImageGridItemFactory().setOnViewClickListener(R.id.imageGridItemImage) { _, _, _, absoluteAdapterPosition, _ ->
                startImageDetail(binding, absoluteAdapterPosition)
            }
        )).apply {
            pagingFlowCollectJob?.cancel()
            pagingFlowCollectJob = viewLifecycleOwner.lifecycleScope.launch {
                localPhotoListViewModel.pagingFlow.collect {
                    submitData(it)
                }
            }
        }

        binding.recyclerRefresh.setOnRefreshListener {
            pagingAdapter.refresh()
        }
        loadStateFlowCollectJob?.cancel()
        loadStateFlowCollectJob = viewLifecycleOwner.lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collect { loadStates ->
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
        }

        val loadStateAdapter = MyLoadStateAdapter().apply {
            noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
        }
        return pagingAdapter.withLoadStateFooter(loadStateAdapter)
    }

    private fun startImageDetail(binding: RecyclerFragmentBinding, position: Int) {
        val imageList = binding.recyclerRecycler
            .adapter!!.asOrThrow<ConcatAdapter>()
            .adapters.first().asOrThrow<AssemblyPagingDataAdapter<Photo>>()
            .currentList.mapIndexedNotNull { index, photo ->
                if (index >= position - 50 && index <= position + 50) {
                    ImageDetail(
                        position = index,
                        originUrl = photo!!.originalUrl,
                        mediumUrl = photo.detailPreviewUrl,
                        thumbnailUrl = photo.listThumbnailUrl,
                    )
                } else {
                    null
                }
            }
        findNavController().navigate(
            NavMainDirections.actionGlobalImageViewerPagerFragment(
                Json.encodeToString(imageList),
                position,
            ),
        )
    }
}
