/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

package me.panpf.sketch.sample.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.addGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.AppEvents
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.bean.ImageInfo
import me.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import me.panpf.sketch.sample.item.LocalPhotoItemFactory
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.vm.LocalPhotoListViewModel
import me.panpf.sketch.sample.widget.SampleImageView

class LocalPhotosFragment : BaseToolbarFragment<FragmentRecyclerBinding>() {

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
            addOnScrollListener(
                ScrollingPauseLoadManager(requireContext())
            )
            layoutManager = GridLayoutManager(activity, 3)
            addGridDividerItemDecoration {
                val gridDivider =
                    requireContext().resources.getDimensionPixelSize(R.dimen.grid_divider)
                divider(Divider.space(gridDivider))
                useDividerAsHeaderAndFooterDivider()
                sideDivider(Divider.space(gridDivider))
                useSideDividerAsSideHeaderAndFooterDivider()
            }
            adapter = pagingAdapter
        }

        pagingAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    binding.hintRecyclerFragment.hidden()
                    binding.refreshRecyclerFragment.isRefreshing = true
                }
                else -> {
                    binding.refreshRecyclerFragment.isRefreshing = false
                    if (pagingAdapter.itemCount <= 0) {
                        binding.hintRecyclerFragment.empty("No photos")
                    }
                }
            }
        }

        lifecycleScope.launch {
            photoListViewModel.pagingFlow.collect {
                pagingAdapter.submitData(it)
            }
        }

        AppEvents.appConfigChangedEvent.listen(viewLifecycleOwner) {
            if (AppConfig.Key.SHOW_ROUND_RECT_IN_PHOTO_LIST == it) {
                pagingAdapter.notifyDataSetChanged()
            }
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

        val imageInfoList = binding.recyclerRecyclerFragmentContent
            .adapter!!.asOrThrow<AssemblyPagingDataAdapter<ImageInfo>>().currentList
        val imageArrayList = imageInfoList.map {
            Image(it!!.path, it.path)
        }

        ImageDetailActivity.launch(
            requireActivity(),
            dataTransferHelper.put("urlList", imageArrayList),
            finalOptionsKey,
            position
        )
    }
}
