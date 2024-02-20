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
package com.github.panpf.sketch.sample.ui.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
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
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerRefreshBinding
import com.github.panpf.sketch.sample.databinding.FragmentSamplesBinding
import com.github.panpf.sketch.sample.ui.MainFragmentDirections
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LoadStateItemFactory
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.common.list.findPagingAdapter
import com.github.panpf.sketch.sample.ui.dialog.Page
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.model.PhotoGridMode
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ViewHomeFragment : BaseHomeFragment() {

    override val fragmentMap = mapOf(
        "Local" to LocalPhotoListViewFragment(),
        "Pexels" to PexelsPhotoListViewFragment(),
        "Giphy" to GiphyPhotoListViewFragment()
    )

    override fun onViewCreated(binding: FragmentSamplesBinding, savedInstanceState: Bundle?) {
        super.onViewCreated(binding, savedInstanceState)
        binding.toolbar.subtitle = "View"

        binding.settingsImage.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionSettingsDialogFragment(Page.LIST.name))
        }
    }

    class LocalPhotoListViewFragment : BasePhotoListViewFragment() {

        private val localPhotoListViewModel by viewModels<LocalPhotoListViewModel>()

        override val animatedPlaceholder: Boolean
            get() = false

        override val photoPagingFlow: Flow<PagingData<Photo>>
            get() = localPhotoListViewModel.pagingFlow

        private val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
                super.onViewCreated(binding!!, null)
            }

        override fun onViewCreated(
            binding: FragmentRecyclerRefreshBinding,
            savedInstanceState: Bundle?
        ) {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    class PexelsPhotoListViewFragment : BasePhotoListViewFragment() {

        private val pexelsImageListViewModel by viewModels<PexelsPhotoListViewModel>()

        override val animatedPlaceholder: Boolean
            get() = false

        override val photoPagingFlow: Flow<PagingData<Photo>>
            get() = pexelsImageListViewModel.pagingFlow
    }

    class GiphyPhotoListViewFragment : BasePhotoListViewFragment() {

        private val giphyPhotoListViewModel by viewModels<GiphyPhotoListViewModel>()

        override val animatedPlaceholder: Boolean
            get() = true

        override val photoPagingFlow: Flow<PagingData<Photo>>
            get() = giphyPhotoListViewModel.pagingFlow
    }

    abstract class BasePhotoListViewFragment :
        BaseBindingFragment<FragmentRecyclerRefreshBinding>() {

        abstract val animatedPlaceholder: Boolean
        abstract val photoPagingFlow: Flow<PagingData<Photo>>

        private var pagingFlowCollectJob: Job? = null
        private var loadStateFlowCollectJob: Job? = null

        @SuppressLint("NotifyDataSetChanged")
        override fun onViewCreated(
            binding: FragmentRecyclerRefreshBinding,
            savedInstanceState: Bundle?
        ) {
            binding.myRecycler.apply {
                appSettingsService.photoGridMode
                    .repeatCollectWithLifecycle(
                        viewLifecycleOwner,
                        State.STARTED
                    ) { photoGridMode ->
                        val (layoutManager1, itemDecoration) =
                            newLayoutManagerAndItemDecoration(photoGridMode)
                        layoutManager = layoutManager1
                        (0 until itemDecorationCount).forEach { index ->
                            removeItemDecorationAt(index)
                        }
                        addItemDecoration(itemDecoration)

                        val pagingAdapter = newPagingAdapter(binding)
                        val loadStateAdapter = MyLoadStateAdapter().apply {
                            noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
                        }
                        adapter = pagingAdapter.withLoadStateFooter(loadStateAdapter)

                        bindRefreshAndAdapter(binding, pagingAdapter)
                    }

                appSettingsService.listsCombinedFlow.ignoreFirst()
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        adapter?.notifyDataSetChanged()
                    }
                appSettingsService.ignoreExifOrientation.ignoreFirst()
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        adapter?.findPagingAdapter()?.refresh()
                    }
            }
        }

        private fun newLayoutManagerAndItemDecoration(photoGridMode: PhotoGridMode): Pair<LayoutManager, ItemDecoration> {
            val layoutManager: LayoutManager
            val itemDecoration: ItemDecoration
            when (photoGridMode) {
                PhotoGridMode.SQUARE -> {
                    layoutManager =
                        requireContext().newAssemblyGridLayoutManager(
                            3,
                            GridLayoutManager.VERTICAL
                        ) {
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

                PhotoGridMode.STAGGERED -> {
                    layoutManager = newAssemblyStaggeredGridLayoutManager(
                        3,
                        StaggeredGridLayoutManager.VERTICAL
                    ) {
                        fullSpanByItemFactory(LoadStateItemFactory::class)
                    }
                    itemDecoration =
                        requireContext().newAssemblyStaggeredGridDividerItemDecoration {
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

        private fun newPagingAdapter(binding: FragmentRecyclerRefreshBinding): PagingDataAdapter<*, *> {
            return AssemblyPagingDataAdapter(
                itemFactoryList = listOf(
                    PhotoGridItemFactory(animatedPlaceholder = animatedPlaceholder)
                        .setOnViewClickListener(R.id.myListImage) { _, _, _, absoluteAdapterPosition, _ ->
                            startPhotoPager(binding, absoluteAdapterPosition)
                        }
                ),
                diffCallback = PhotoDiffCallback()
            ).apply {
                pagingFlowCollectJob?.cancel()
                pagingFlowCollectJob = viewLifecycleOwner.lifecycleScope.launch {
                    photoPagingFlow.collect {
                        submitData(it)
                    }
                }
            }
        }

        private fun bindRefreshAndAdapter(
            binding: FragmentRecyclerRefreshBinding,
            pagingAdapter: PagingDataAdapter<*, *>
        ) {
            binding.swipeRefresh.setOnRefreshListener {
                pagingAdapter.refresh()
            }
            loadStateFlowCollectJob?.cancel()
            loadStateFlowCollectJob =
                viewLifecycleOwner.lifecycleScope.launch {
                    pagingAdapter.loadStateFlow.collect { loadStates ->
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
                }
        }

        private fun startPhotoPager(binding: FragmentRecyclerRefreshBinding, position: Int) {
            val items = binding.myRecycler
                .adapter!!.asOrThrow<ConcatAdapter>()
                .adapters.first().asOrThrow<AssemblyPagingDataAdapter<Photo>>()
                .currentList
            val totalCount = items.size
            val startPosition = (position - 50).coerceAtLeast(0)
            val endPosition = (position + 50).coerceAtMost(totalCount - 1)
            val imageList = items.asSequence()
                .filterNotNull()
                .filterIndexed { index, _ -> index in startPosition..endPosition }
                .map {
                    ImageDetail(
                        originUrl = it.originalUrl,
                        mediumUrl = it.detailPreviewUrl,
                        thumbnailUrl = it.listThumbnailUrl,
                    )
                }.toList()
            findNavController().navigate(
                NavMainDirections.actionPhotoPagerViewFragment(
                    imageDetailJsonArray = Json.encodeToString(imageList),
                    totalCount = totalCount,
                    startPosition = startPosition,
                    initialPosition = position
                ),
            )
        }
    }
}