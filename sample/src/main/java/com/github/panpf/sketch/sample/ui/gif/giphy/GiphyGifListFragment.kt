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
package com.github.panpf.sketch.sample.ui.gif.giphy

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
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
import com.github.panpf.sketch.sample.databinding.RecyclerFragmentBinding
import com.github.panpf.sketch.sample.model.DialogFragmentItemInfo
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.model.LayoutMode
import com.github.panpf.sketch.sample.model.LayoutMode.GRID
import com.github.panpf.sketch.sample.model.LayoutMode.STAGGERED_GRID
import com.github.panpf.sketch.sample.model.NavMenuItemInfo
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.model.SwitchMenuItemInfo
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LoadStateItemFactory
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.common.menu.ListMenuViewModel
import com.github.panpf.sketch.sample.ui.photo.ImageGridItemFactory
import com.github.panpf.sketch.sample.util.intrinsicSize
import com.github.panpf.sketch.sample.util.observeWithFragmentView
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.findLastSketchDrawable
import com.github.panpf.sketch.util.isSameAspectRatio
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GiphyGifListFragment : ToolbarBindingFragment<RecyclerFragmentBinding>() {

    private val giphyGifListViewModel by viewModels<GiphyGifListViewModel>()
    private val listMenuViewModel by viewModels<ListMenuViewModel> {
        ListMenuViewModel.Factory(
            requireActivity().application,
            showLayoutModeMenu = true,
            showPlayMenu = true
        )
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: RecyclerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.apply {
            title = "Giphy GIF"
            listMenuViewModel.menuList.observe(viewLifecycleOwner) { list ->
                menu.clear()
                list?.forEachIndexed { groupIndex, group ->
                    group.items.forEachIndexed { index, menuItemInfo ->
                        menu.add(groupIndex, index, index, menuItemInfo.title).apply {
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

        binding.recyclerRecycler.apply {
            prefsService.photoListLayoutMode.stateFlow.observeWithFragmentView(this@GiphyGifListFragment) {
                (0 until itemDecorationCount).forEach { index ->
                    removeItemDecorationAt(index)
                }
                when (LayoutMode.valueOf(it)) {
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
                }


                val pagingAdapter = AssemblyPagingDataAdapter<Photo>(listOf(
                    ImageGridItemFactory().setOnViewClickListener(R.id.imageGridItemImage) { _, _, _, absoluteAdapterPosition, _ ->
                        startImageDetail(binding, absoluteAdapterPosition)
                    }
                )).apply {
                    viewLifecycleOwner.lifecycleScope.launch {
                        giphyGifListViewModel.pagingFlow.collect { pagingData ->
                            submitData(pagingData)
                        }
                    }
                }

                binding.recyclerRefresh.setOnRefreshListener {
                    pagingAdapter.refresh()
                }

                viewLifecycleOwner.lifecycleScope.launch {
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
                                    binding.recyclerState.empty("No gifs")
                                } else {
                                    binding.recyclerState.gone()
                                }
                            }
                        }
                    }
                }

                adapter = pagingAdapter.withLoadStateFooter(MyLoadStateAdapter().apply {
                    noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
                })
            }
        }
    }

    private fun startImageDetail(binding: RecyclerFragmentBinding, position: Int) {
        val currentViewCacheKeyMap = mutableMapOf<Int, String>().apply {
            binding.recyclerRecycler.forEach { view ->
                val sketchDrawable =
                    view.findViewById<ImageView>(R.id.imageGridItemImage)?.drawable?.findLastSketchDrawable()
                if (sketchDrawable != null) {
                    val drawable = sketchDrawable as Drawable
                    val drawableSize = drawable.intrinsicSize
                    val imageSize = sketchDrawable.imageInfo.let { Size(it.width, it.height) }
                    if (drawableSize.isSameAspectRatio(imageSize, delta = 0.1f)) {
                        val adapterPosition = binding.recyclerRecycler.getChildAdapterPosition(view)
                        val cacheKey = sketchDrawable.requestCacheKey
                        put(adapterPosition, cacheKey)
                    }
                }
            }
        }
        val imageList = binding.recyclerRecycler
            .adapter!!.asOrThrow<ConcatAdapter>()
            .adapters.first().asOrThrow<AssemblyPagingDataAdapter<Photo>>()
            .currentList.mapIndexedNotNull { index, photo ->
                if (index >= position - 50 && index <= position + 50) {
                    ImageDetail(
                        position = index,
                        originUrl = photo!!.originalUrl,
                        previewUrl = photo.detailPreviewUrl,
                        bgUrl = photo.listThumbnailUrl,
                        placeholderImageMemoryCacheKey = currentViewCacheKeyMap[index]
                    )
                } else {
                    null
                }
            }
        findNavController().navigate(
            NavMainDirections.actionGlobalImageViewerPagerFragment(
                Json.encodeToString(imageList),
                position,
            )
        )
    }
}
