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
package com.github.panpf.sketch.sample.ui.video

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.databinding.RecyclerFragmentBinding
import com.github.panpf.sketch.sample.model.DialogFragmentItemInfo
import com.github.panpf.sketch.sample.model.NavMenuItemInfo
import com.github.panpf.sketch.sample.model.SwitchMenuItemInfo
import com.github.panpf.sketch.sample.model.VideoInfo
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.common.menu.ListMenuViewModel
import com.github.panpf.tools4a.toast.ktx.showLongToast
import kotlinx.coroutines.launch
import java.io.File

class LocalVideoListFragment : ToolbarBindingFragment<RecyclerFragmentBinding>() {

    private val videoListViewModel by viewModels<LocalVideoListViewModel>()
    private val listMenuViewModel by viewModels<ListMenuViewModel> {
        ListMenuViewModel.Factory(
            requireActivity().application,
            showLayoutModeMenu = false,
            showPlayMenu = false
        )
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: RecyclerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.apply {
            title = "Local Video"
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

        val pagingAdapter = AssemblyPagingDataAdapter<VideoInfo>(listOf(
            LocalVideoItemFactory().setOnItemClickListener { _, _, _, _, data ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.fromFile(File(data.path)), data.mimeType)
                    })
                } catch (e: Throwable) {
                    e.printStackTrace()
                    showLongToast("Not found can play video app")
                }
            }
        )).apply {
            viewLifecycleOwner.lifecycleScope.launch {
                videoListViewModel.pagingFlow.collect {
                    submitData(it)
                }
            }
        }

        binding.recyclerRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = pagingAdapter.withLoadStateFooter(MyLoadStateAdapter().apply {
                noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
            })
        }

        binding.recyclerRefresh.setOnRefreshListener {
            pagingAdapter.refresh()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collect {
                when (val refreshState = it.refresh) {
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
                            binding.recyclerState.empty("No videos")
                        } else {
                            binding.recyclerState.gone()
                        }
                    }
                }
            }
        }
    }
}
