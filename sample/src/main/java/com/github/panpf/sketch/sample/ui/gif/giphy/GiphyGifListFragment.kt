package com.github.panpf.sketch.sample.ui.gif.giphy

import android.os.Bundle
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
import com.github.panpf.sketch.sample.util.observeWithFragmentView
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

    private fun startImageDetail(
        binding: RecyclerFragmentBinding,
        position: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val imageList = binding.recyclerRecycler
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
            NavMainDirections.actionGlobalImageViewerPagerFragment(
                Json.encodeToString(imageList),
                null,
                position,
            )
        )
    }
}
