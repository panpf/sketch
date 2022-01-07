package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import com.github.panpf.assemblyadapter.recycler.AssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.addGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.base.MyLoadStateAdapter
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.bean.Image
import com.github.panpf.sketch.sample.bean.ImageDetail
import com.github.panpf.sketch.sample.bean.PexelsPhoto
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.item.LoadStateItemFactory
import com.github.panpf.sketch.sample.item.PexelsImageItemFactory
import com.github.panpf.sketch.sample.vm.PexelsImageListViewModel
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class PexelsPhotosFragment : ToolbarBindingFragment<FragmentRecyclerBinding>() {

    private val pexelsImageListViewModel by viewModels<PexelsImageListViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        showMenu(toolbar)

        toolbar.title = "Pexels Photos"

        val pagingAdapter = AssemblyPagingDataAdapter<PexelsPhoto>(listOf(
            PexelsImageItemFactory(requireActivity()) { view, position, _ ->
                startImageDetail(binding, position)
            }
        ))

        binding.refreshRecyclerFragment.setOnRefreshListener {
            pagingAdapter.refresh()
        }

        binding.recyclerRecyclerFragmentContent.apply {
            layoutManager = AssemblyGridLayoutManager(
                requireActivity(),
                3,
                mapOf(LoadStateItemFactory::class to ItemSpan.fullSpan())
            )
            adapter = pagingAdapter.withLoadStateFooter(MyLoadStateAdapter().apply {
                noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
            })

            val gridDivider = requireContext().resources.getDimensionPixelSize(R.dimen.grid_divider)
            addGridDividerItemDecoration {
                divider(Divider.space(gridDivider))
                sideDivider(Divider.space(gridDivider))
                useDividerAsHeaderAndFooterDivider()
                useSideDividerAsSideHeaderAndFooterDivider()
            }

            // todo 支持滚动中停止加载
//            addOnScrollListener(ScrollingPauseLoadManager(requireContext()))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collect {
                when (val refreshState = it.refresh) {
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
                            binding.hintRecyclerFragment.empty("No photos")
                        } else {
                            binding.hintRecyclerFragment.hidden()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            pexelsImageListViewModel.pagingFlow.collect {
                pagingAdapter.submitData(it)
            }
        }
    }

    private fun showMenu(toolbar: Toolbar) {
        val showImageDownloadProgressEnabled = appSettingsService.showImageDownloadProgressEnabled
        toolbar.menu.add(
            0, 0, 0, if (showImageDownloadProgressEnabled.value == true) {
                "Hidden download progress"
            } else {
                "Show download progress"
            }
        ).setOnMenuItemClickListener {
            val newValue = !(showImageDownloadProgressEnabled.value ?: false)
            showImageDownloadProgressEnabled.postValue(newValue)
            it.title = if (newValue) {
                "Hidden download progress"
            } else {
                "Show download progress"
            }
            true
        }

        val mobileNetworkPauseDownloadEnabled = appSettingsService.mobileNetworkPauseDownloadEnabled
        toolbar.menu.add(
            0, 1, 1, if (mobileNetworkPauseDownloadEnabled.value == true) {
                "Disable mobile data pause download"
            } else {
                "Enable mobile data pause download"
            }
        ).setOnMenuItemClickListener {
            val newValue = !(mobileNetworkPauseDownloadEnabled.value ?: false)
            mobileNetworkPauseDownloadEnabled.postValue(newValue)
            it.title = if (newValue) {
                "Disable mobile data pause download"
            } else {
                "Enable mobile data pause download"
            }
            true
        }
    }

    private fun startImageDetail(binding: FragmentRecyclerBinding, position: Int) {
        val imageList = binding.recyclerRecyclerFragmentContent
            .adapter!!.asOrThrow<ConcatAdapter>()
            .adapters.first().asOrThrow<AssemblyPagingDataAdapter<PexelsPhoto>>()
            .currentList.map {
                ImageDetail(it!!.src.medium, it.url, null)
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
