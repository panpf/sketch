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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.base.MyLoadStateAdapter
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.bean.DialogFragmentItemInfo
import com.github.panpf.sketch.sample.bean.NavMenuItemInfo
import com.github.panpf.sketch.sample.bean.SwitchMenuItemInfo
import com.github.panpf.sketch.sample.bean.VideoInfo
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.item.LocalVideoItemFactory
import com.github.panpf.sketch.sample.vm.ListMenuViewModel
import com.github.panpf.sketch.sample.vm.LocalVideoListViewModel
import com.github.panpf.tools4a.toast.ktx.showLongToast
import kotlinx.coroutines.launch
import java.io.File

class LocalVideoListFragment : ToolbarBindingFragment<FragmentRecyclerBinding>() {

    private val videoListViewModel by viewModels<LocalVideoListViewModel>()
    private val listMenuViewModel by viewModels<ListMenuViewModel> {
        ListMenuViewModel.Factory(
            requireActivity().application,
            showLayoutModeMenu = false,
            showPlayMenu = false
        )
    }

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
        listMenuViewModel.menuList.observe(viewLifecycleOwner) { list ->
            toolbar.menu.clear()
            list?.forEachIndexed { groupIndex, group ->
                group.items.forEachIndexed { index, menuItemInfo ->
                    toolbar.menu.add(groupIndex, index, index, menuItemInfo.title).apply {
                        menuItemInfo.iconResId?.let { iconResId ->
                            setIcon(iconResId)
                        }
                        setOnMenuItemClickListener {
                            when (menuItemInfo) {
                                is SwitchMenuItemInfo<*> -> menuItemInfo.click()
                                is NavMenuItemInfo -> findNavController().navigate(menuItemInfo.navDirections)
                                is DialogFragmentItemInfo -> menuItemInfo.fragment
                                    .show(childFragmentManager, null)
                            }
                            true
                        }
                        setShowAsAction(menuItemInfo.showAsAction)
                    }
                }
            }
        }
    }

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Local Video"

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
        ))

        binding.recyclerRecyclerFragmentContent.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = pagingAdapter.withLoadStateFooter(MyLoadStateAdapter().apply {
                noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
            })
        }

        binding.refreshRecyclerFragment.setOnRefreshListener {
            pagingAdapter.refresh()
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
                            binding.hintRecyclerFragment.empty("No videos")
                        } else {
                            binding.hintRecyclerFragment.hidden()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            videoListViewModel.pagingFlow.collect {
                pagingAdapter.submitData(it)
            }
        }
    }
}
