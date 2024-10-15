package com.github.panpf.sketch.sample.ui.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerRefreshBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LoadStateItemFactory
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.model.PhotoDiffCallback
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.tools4a.dimen.ktx.dp2px
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

abstract class BasePhotoListFragment :
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
            setPadding(0, 0, 0, 80.dp2px)
            clipToPadding = false

            appSettings.staggeredGridMode
                .repeatCollectWithLifecycle(
                    viewLifecycleOwner,
                    Lifecycle.State.CREATED
                ) { staggeredGridMode ->
                    val (layoutManager1, itemDecoration) =
                        newLayoutManagerAndItemDecoration(staggeredGridMode)
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
        }
    }

    private fun newLayoutManagerAndItemDecoration(staggeredGridMode: Boolean): Pair<RecyclerView.LayoutManager, RecyclerView.ItemDecoration> {
        val layoutManager: RecyclerView.LayoutManager
        val itemDecoration: RecyclerView.ItemDecoration
        if (staggeredGridMode) {
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
        } else {
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
        val startPosition = (position - 100).coerceAtLeast(0)
        val endPosition = (position + 100).coerceAtMost(items.size - 1)
        val photos = items.asSequence()
            .filterNotNull()
            .filterIndexed { index, _ -> index in startPosition..endPosition }
            .toList()
        val photosJsonString = Json.encodeToString(photos)
        findNavController().navigate(
            NavMainDirections.actionPhotoPagerViewFragment(
                photos = photosJsonString,
                totalCount = totalCount,
                startPosition = startPosition,
                initialPosition = position
            ),
        )
    }
}