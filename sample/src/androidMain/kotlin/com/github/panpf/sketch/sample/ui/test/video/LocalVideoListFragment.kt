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
package com.github.panpf.sketch.sample.ui.test.video

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerRefreshBinding
import com.github.panpf.sketch.sample.model.VideoInfo
import com.github.panpf.sketch.sample.ui.MainFragmentDirections
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.common.list.findPagingAdapter
import com.github.panpf.sketch.sample.ui.dialog.Page
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.tools4a.toast.ktx.showLongToast
import java.io.File

class LocalVideoListFragment : BaseToolbarBindingFragment<FragmentRecyclerRefreshBinding>() {

    private val videoListViewModel by viewModels<LocalVideoListViewModel>()

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
                        MainFragmentDirections.actionSettingsDialogFragment(
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
            appSettingsService.ignoreExifOrientation.ignoreFirst()
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
}
