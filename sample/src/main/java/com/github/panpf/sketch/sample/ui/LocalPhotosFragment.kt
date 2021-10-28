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
import com.github.panpf.assemblyadapter.recycler.AssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.addGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.base.MyLoadStateAdapter
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.bean.Image
import com.github.panpf.sketch.sample.bean.ImageInfo
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.item.LoadStateItemFactory
import com.github.panpf.sketch.sample.item.LocalPhotoItemFactory
import com.github.panpf.sketch.sample.util.ScrollingPauseLoadManager
import com.github.panpf.sketch.sample.vm.LocalPhotoListViewModel
import com.github.panpf.sketch.sample.widget.SampleImageView

class LocalPhotosFragment : ToolbarBindingFragment<FragmentRecyclerBinding>() {

    private val photoListViewModel by viewModels<LocalPhotoListViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    @SuppressLint("NotifyDataSetChanged")
    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        showMenu(toolbar)

        toolbar.title = "Local Photos"

        val pagingAdapter = AssemblyPagingDataAdapter<ImageInfo>(listOf(
            LocalPhotoItemFactory { view, position, _ ->
                startImageDetail(view, binding, position)
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
            addOnScrollListener(ScrollingPauseLoadManager(requireContext()))

            val gridDivider = requireContext().resources.getDimensionPixelSize(R.dimen.grid_divider)
            addGridDividerItemDecoration {
                divider(Divider.space(gridDivider))
                sideDivider(Divider.space(gridDivider))
                useDividerAsHeaderAndFooterDivider()
                useSideDividerAsSideHeaderAndFooterDivider()
            }
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
            photoListViewModel.pagingFlow.collect {
                pagingAdapter.submitData(it)
            }
        }
    }

    private fun showMenu(toolbar: Toolbar) {
        val showRoundedInPhotoListEnabled = appSettingsService.showRoundedInPhotoListEnabled
        toolbar.menu.add(
            0, 0, 0, if (showRoundedInPhotoListEnabled.value == true) {
                "Hidden rounded"
            } else {
                "Show rounded"
            }
        ).setOnMenuItemClickListener {
            val newValue = !(showRoundedInPhotoListEnabled.value ?: false)
            showRoundedInPhotoListEnabled.postValue(newValue)
            it.title = if (newValue) {
                "Hidden rounded"
            } else {
                "Show rounded"
            }
            true
        }

        val showPressedStatusInListEnabled = appSettingsService.showPressedStatusInListEnabled
        toolbar.menu.add(
            0, 1, 1, if (showPressedStatusInListEnabled.value == true) {
                "Hidden press status"
            } else {
                "Show press status"
            }
        ).setOnMenuItemClickListener {
            val newValue = !(showPressedStatusInListEnabled.value ?: false)
            showPressedStatusInListEnabled.postValue(newValue)
            it.title = if (newValue) {
                "Hidden press status"
            } else {
                "Show press status"
            }
            true
        }

        val thumbnailModeEnabled = appSettingsService.thumbnailModeEnabled
        toolbar.menu.add(
            0, 2, 2, if (thumbnailModeEnabled.value == true) {
                "Disable thumbnail mode"
            } else {
                "Enable thumbnail mode"
            }
        ).setOnMenuItemClickListener {
            val newValue = !(thumbnailModeEnabled.value ?: false)
            thumbnailModeEnabled.postValue(newValue)
            it.title = if (newValue) {
                "Disable thumbnail mode"
            } else {
                "Enable thumbnail mode"
            }
            true
        }
    }

    private fun startImageDetail(
        view: SampleImageView,
        binding: FragmentRecyclerBinding,
        position: Int
    ) {
        var finalOptionsKey: String? = view.optionsKey
        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
        if (finalOptionsKey!!.contains("Resize")
            || finalOptionsKey.contains("ImageProcessor")
            || finalOptionsKey.contains("thumbnailMode")
        ) {
            finalOptionsKey = null
        }

        val imageList = binding.recyclerRecyclerFragmentContent
            .adapter!!.asOrThrow<ConcatAdapter>()
            .adapters.first().asOrThrow<AssemblyPagingDataAdapter<ImageInfo>>()
            .currentList.map {
                Image(it!!.path, it.path)
            }
        findNavController().navigate(
            NavMainDirections.actionGlobalImageViewerFragment(
                Json.encodeToString(imageList),
                position,
                finalOptionsKey
            )
        )
    }
}
