package com.github.panpf.sketch.sample.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.github.panpf.sketch.sample.base.MyLoadStateAdapter
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.bean.ImageDetail
import com.github.panpf.sketch.sample.bean.LayoutMode.GRID
import com.github.panpf.sketch.sample.bean.LayoutMode.STAGGERED_GRID
import com.github.panpf.sketch.sample.bean.Photo
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.item.LoadStateItemFactory
import com.github.panpf.sketch.sample.item.PhotoItemFactory
import com.github.panpf.sketch.sample.vm.PexelsImageListViewModel
import com.github.panpf.sketch.sample.vm.SampleMenuListViewModel
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PexelsPhotosFragment : ToolbarBindingFragment<FragmentRecyclerBinding>() {

    private val pexelsImageListViewModel by viewModels<PexelsImageListViewModel>()
    private val sampleMenuListViewModel by viewModels<SampleMenuListViewModel>()

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
        sampleMenuListViewModel.menuList.observe(viewLifecycleOwner) {
            toolbar.menu.clear()
            it?.forEachIndexed { index, menuItemInfo ->
                toolbar.menu.add(menuItemInfo.groupId, index, index, menuItemInfo.title).apply {
                    menuItemInfo.iconResId?.let { iconResId ->
                        setIcon(iconResId)
                    }
                    setOnMenuItemClickListener {
                        menuItemInfo.click()
                        true
                    }
                    setShowAsAction(menuItemInfo.showAsAction)
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
        toolbar.title = "Pexels Photos"

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
                        layoutManager = LinearLayoutManager(requireContext())
                    }
                }


                val pagingAdapter = AssemblyPagingDataAdapter<Photo>(listOf(
                    PhotoItemFactory().setOnViewClickListener(R.id.imageItemImageView) { _, _, _, absoluteAdapterPosition, _ ->
                        startImageDetail(binding, absoluteAdapterPosition)
                    }
                ))

                appSettingsService.disabledAnimatableDrawableInList.observe(viewLifecycleOwner) {
                    pagingAdapter.notifyDataSetChanged()
                }

                appSettingsService.saveCellularTrafficInList.observe(viewLifecycleOwner) {
                    pagingAdapter.notifyDataSetChanged()
                }

                appSettingsService.pauseLoadWhenScrollInList.observe(viewLifecycleOwner) {
                    pagingAdapter.notifyDataSetChanged()
                }

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
                    pexelsImageListViewModel.pagingFlow.collect { pagingData ->
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
