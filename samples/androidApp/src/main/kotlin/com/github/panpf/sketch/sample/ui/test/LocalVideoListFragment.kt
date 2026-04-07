/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.paging.AssemblyPagingDataAdapter
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerRefreshBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LocalVideoItemFactory
import com.github.panpf.sketch.sample.ui.common.list.MyLoadStateAdapter
import com.github.panpf.sketch.sample.ui.model.VideoInfoDiffCallback
import com.github.panpf.sketch.sample.ui.setting.Page
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.tools4a.toast.ktx.showLongToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class LocalVideoListFragment : BaseToolbarBindingFragment<FragmentRecyclerRefreshBinding>() {

    override val permissions: Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    private val videoListViewModel by viewModel<LocalVideoListViewModel>()

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

        val pagingAdapter = AssemblyPagingDataAdapter(
            itemFactoryList = listOf(
                LocalVideoItemFactory().setOnItemClickListener { _, _, _, _, data ->
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.fromFile(File(data.uri.orEmpty())), data.mimeType)
                        })
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        showLongToast("Not found can play video app")
                    }
                }
            ),
            diffCallback = VideoInfoDiffCallback()
        ).apply {
            videoListViewModel.pagingFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                    submitData(it)
                }
        }

        binding.myRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = pagingAdapter.withLoadStateFooter(MyLoadStateAdapter().apply {
                noDisplayLoadStateWhenPagingEmpty(pagingAdapter)
            })
        }

        binding.swipeRefresh.setOnRefreshListener {
            pagingAdapter.refresh()
        }

        pagingAdapter.loadStateFlow
            .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
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