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
package com.github.panpf.sketch.sample.ui.test

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.request.updateImageOptions
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.data.paging.LocalVideoListPagingSource
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerRefreshBinding
import com.github.panpf.sketch.sample.databinding.ListItemVideoBinding
import com.github.panpf.sketch.sample.model.VideoInfo
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.common.list.findPagingAdapter
import com.github.panpf.sketch.sample.ui.setting.Page
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import com.github.panpf.tools4a.toast.ktx.showLongToast
import java.io.File

class LocalVideoListFragment : BaseToolbarBindingFragment<FragmentRecyclerRefreshBinding>() {

    private val videoListViewModel by viewModels<LocalVideoListViewModel>()

    override fun getNavigationBarInsetsView(binding: FragmentRecyclerRefreshBinding): View {
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentRecyclerRefreshBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.apply {
            title = "Local Video"
            menu.add(0, 0, 0, "Settings").apply {
                setIcon(R.drawable.ic_settings)
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                setOnMenuItemClickListener {
                    this@LocalVideoListFragment.findNavController().navigate(
                        NavMainDirections.actionSettingsDialogFragment(
                            Page.LIST.name
                        )
                    )
                    true
                }
            }
        }

        val pagingAdapter = AssemblyPagingDataAdapter<VideoInfo>(listOf(
            LocalVideoItemFactory().setOnItemClickListener { _, _, _, _, data ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.fromFile(File(data.path.orEmpty())), data.mimeType)
                    })
                } catch (e: Throwable) {
                    e.printStackTrace()
                    showLongToast("Not found can play video app")
                }
            }
        )).apply {
            videoListViewModel.pagingFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    submitData(it)
                }
        }

        binding.myRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = pagingAdapter.withLoadStateFooter(MyLoadStateAdapter().apply {
                noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
            })

            appSettingsService.listsCombinedFlow.ignoreFirst()
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    adapter?.notifyDataSetChanged()
                }
            appSettingsService.exifOrientation.ignoreFirst()
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    adapter?.findPagingAdapter()?.refresh()
                }
        }

        binding.swipeRefresh.setOnRefreshListener {
            pagingAdapter.refresh()
        }

        pagingAdapter.loadStateFlow
            .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                when (val refreshState = it.refresh) {
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
                                message("No Videos")
                            }
                        } else {
                            binding.state.gone()
                        }
                    }
                }
            }
    }

    class LocalVideoListViewModel(application: Application) : AndroidViewModel(application) {
        val pagingFlow = Pager(
            config = PagingConfig(
                pageSize = 80,
                prefetchDistance = 10,
                enablePlaceholders = false,
                initialLoadSize = 40
            ),
            initialKey = 0,
            pagingSourceFactory = {
                LocalVideoListPagingSource(application)
            }
        ).flow.cachedIn(viewModelScope)
    }

    class LocalVideoItemFactory :
        BaseBindingItemFactory<VideoInfo, ListItemVideoBinding>(VideoInfo::class) {

        override fun initItem(
            context: Context,
            binding: ListItemVideoBinding,
            item: BindingItem<VideoInfo, ListItemVideoBinding>
        ) {
            binding.thumbnailImage.updateImageOptions {
                placeholder(
                    IconStateImage(
                        icon = R.drawable.ic_image_outline,
                        background = R.color.placeholder_bg
                    )
                )
                error(
                    IconStateImage(
                        icon = R.drawable.ic_error_baseline,
                        background = R.color.placeholder_bg
                    )
                ) {
                    saveCellularTrafficError(
                        IconStateImage(
                            icon = R.drawable.im_save_cellular_traffic,
                            background = R.color.placeholder_bg
                        )
                    )
                }
                crossfade()
                videoFramePercent(0.5f)
            }
        }

        override fun bindItemData(
            context: Context,
            binding: ListItemVideoBinding,
            item: BindingItem<VideoInfo, ListItemVideoBinding>,
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: VideoInfo
        ) {
            binding.thumbnailImage.displayImage(data.path) {
                merge(context.appSettingsService.buildListImageOptions())
            }
            binding.nameText.text = data.title
            binding.sizeText.text = data.getTempFormattedSize(context)
            binding.dateText.text = data.tempFormattedDate
            binding.durationText.text = data.tempFormattedDuration
        }
    }
}