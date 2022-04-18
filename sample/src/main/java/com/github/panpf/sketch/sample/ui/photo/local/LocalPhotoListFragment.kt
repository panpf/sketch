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

package com.github.panpf.sketch.sample.ui.photo.local

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.addAssemblyGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.divider.addAssemblyStaggeredGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.newAssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.newAssemblyStaggeredGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.model.DialogFragmentItemInfo
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.model.LayoutMode.GRID
import com.github.panpf.sketch.sample.model.LayoutMode.STAGGERED_GRID
import com.github.panpf.sketch.sample.model.NavMenuItemInfo
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.model.SwitchMenuItemInfo
import com.github.panpf.sketch.sample.ui.base.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LoadStateItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.ListMenuViewModel
import com.github.panpf.sketch.sample.ui.photo.PhotoItemFactory
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalPhotoListFragment : ToolbarBindingFragment<FragmentRecyclerBinding>() {

    private val localPhotoListViewModel by viewModels<LocalPhotoListViewModel>()
    private val listMenuViewModel by viewModels<ListMenuViewModel> {
        ListMenuViewModel.Factory(
            requireActivity().application,
            showLayoutModeMenu = true,
            showPlayMenu = true
        )
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitViews(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        super.onInitViews(toolbar, binding, savedInstanceState)
        listMenuViewModel.menuList.observe(viewLifecycleOwner) { list ->
            toolbar.menu.clear()
            list?.forEachIndexed { groupIndex, group ->
                group.items.forEachIndexed { index, menuItemInfo ->
                    toolbar.menu.add(groupIndex, index, index, menuItemInfo.title).apply {
                        menuItemInfo.iconResId?.let { iconResId ->
                            setIcon(iconResId)
                        }
                        setOnMenuItemClickListener {
                            when (menuItemInfo) {
                                is SwitchMenuItemInfo<*> -> menuItemInfo.click()
                                is NavMenuItemInfo -> findNavController().navigate(menuItemInfo.navDirections)
                                is DialogFragmentItemInfo -> menuItemInfo.fragment
                                    .javaClass.newInstance().apply {
                                        arguments = menuItemInfo.fragment.arguments
                                    }.show(childFragmentManager, null)
                            }
                            true
                        }
                        setShowAsAction(menuItemInfo.showAsAction)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Local Photos"

        binding.recyclerRecyclerFragmentContent.apply {
            appSettingsService.photoListLayoutMode.observe(viewLifecycleOwner) {
                (0 until itemDecorationCount).forEach { index ->
                    removeItemDecorationAt(index)
                }
                when (it) {
                    GRID -> {
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
                    }
                    STAGGERED_GRID -> {
                        layoutManager = newAssemblyStaggeredGridLayoutManager(
                            2,
                            StaggeredGridLayoutManager.VERTICAL
                        ) {
                            fullSpanByItemFactory(LoadStateItemFactory::class)
                        }
                        addAssemblyStaggeredGridDividerItemDecoration {
                            val gridDivider =
                                requireContext().resources.getDimensionPixelSize(R.dimen.grid_divider)
                            divider(Divider.space(gridDivider))
                            sideDivider(Divider.space(gridDivider))
                            useDividerAsHeaderAndFooterDivider()
                            useSideDividerAsSideHeaderAndFooterDivider()
                        }
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported layout mode: $it")
                    }
                }


                val pagingAdapter = AssemblyPagingDataAdapter<Photo>(listOf(
                    PhotoItemFactory().setOnViewClickListener(R.id.imageItemImageView) { _, _, _, absoluteAdapterPosition, _ ->
                        startImageDetail(binding, absoluteAdapterPosition)
                    }
                ))

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

    private fun startImageDetail(binding: FragmentRecyclerBinding, position: Int) {
        val imageList = binding.recyclerRecyclerFragmentContent
            .adapter!!.asOrThrow<ConcatAdapter>()
            .adapters.first().asOrThrow<AssemblyPagingDataAdapter<Photo>>()
            .currentList.map {
                ImageDetail(
                    url = it!!.originalUrl,
                    middenUrl = it.middenUrl,
                    placeholderImageMemoryKey = null
                )
            }
        findNavController().navigate(
            NavMainDirections.actionGlobalImageViewerFragment(
                Json.encodeToString(imageList),
                null,
                position,
            )
        )
    }
}
