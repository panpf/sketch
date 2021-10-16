package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.tools4k.lang.asOrThrow
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.panpf.sketch.sample.NavMainDirections
import me.panpf.sketch.sample.appSettingsService
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.base.MyLoadStateAdapter
import me.panpf.sketch.sample.bean.GiphyGif
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import me.panpf.sketch.sample.item.GiphyGifFlexboxItemFactory
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.vm.GiphyGifListViewModel
import me.panpf.sketch.sample.widget.SampleImageView

class OnlineGifFragment : BaseToolbarFragment<FragmentRecyclerBinding>() {

    private val giphyGifListViewModel by viewModels<GiphyGifListViewModel>()

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

        toolbar.title = "Online GIF - Giphy"

        val pagingAdapter = AssemblyPagingDataAdapter<GiphyGif>(listOf(
            GiphyGifFlexboxItemFactory { view, position, _ ->
                startImageDetail(view, binding, position)
            }
        ))

        binding.refreshRecyclerFragment.setOnRefreshListener {
            pagingAdapter.refresh()
        }

        binding.recyclerRecyclerFragmentContent.apply {
            layoutManager = FlexboxLayoutManager(context)
            adapter = pagingAdapter.withLoadStateFooter(MyLoadStateAdapter().apply {
                noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
            })
            addOnScrollListener(ScrollingPauseLoadManager(requireContext()))
            addItemDecoration(FlexboxItemDecoration(context))
        }

        pagingAdapter.addLoadStateListener {
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
                        binding.hintRecyclerFragment.empty("No gifs")
                    } else {
                        binding.hintRecyclerFragment.hidden()
                    }
                }
            }
        }

        lifecycleScope.launch {
            giphyGifListViewModel.pagingFlow.collect {
                pagingAdapter.submitData(it)
            }
        }
    }

    private fun showMenu(toolbar: Toolbar) {
        val playGifInListEnabled = appSettingsService.playGifInListEnabled
        toolbar.menu.add(
            0, 0, 0, if (playGifInListEnabled.value == true) {
                "Disable play gif in list"
            } else {
                "Enable play gif in list"
            }
        ).setOnMenuItemClickListener {
            val newValue = !(playGifInListEnabled.value ?: false)
            playGifInListEnabled.postValue(newValue)
            it.title = if (newValue) {
                "Disable play gif in list"
            } else {
                "Enable play gif in list"
            }
            true
        }

        val clickPlayGifEnabled = appSettingsService.clickPlayGifEnabled
        toolbar.menu.add(
            0, 1, 1, if (clickPlayGifEnabled.value == true) {
                "Disable click play gif in list"
            } else {
                "Enable click play gif in list"
            }
        ).setOnMenuItemClickListener {
            val newValue = !(clickPlayGifEnabled.value ?: false)
            clickPlayGifEnabled.postValue(newValue)
            it.title = if (newValue) {
                "Disable click play gif in list"
            } else {
                "Enable click play gif in list"
            }
            true
        }
    }

    private fun startImageDetail(
        view: SampleImageView,
        binding: FragmentRecyclerBinding,
        position: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val imageList = binding.recyclerRecyclerFragmentContent
            .adapter!!.asOrThrow<ConcatAdapter>()
            .adapters.first().asOrThrow<AssemblyPagingDataAdapter<GiphyGif>>()
            .currentList.map {
                Image(
                    it!!.images.original.downloadUrl,
                    it.images.original.downloadUrl,
                )
            }
        findNavController().navigate(
            NavMainDirections.actionGlobalImageViewerFragment(
                Json.encodeToString(imageList),
                position,
                view.optionsKey
            )
        )
    }
}
