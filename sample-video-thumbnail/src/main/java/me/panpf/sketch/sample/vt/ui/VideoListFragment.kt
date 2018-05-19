/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.sample.vt.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_recycler.*
import me.panpf.adapter.AssemblyAdapter
import me.panpf.adapter.more.OnLoadMoreListener
import me.panpf.adapter.paged.AssemblyPagedListAdapter
import me.panpf.sketch.sample.vt.BaseFragment
import me.panpf.sketch.sample.vt.BindContentView
import me.panpf.sketch.sample.vt.R
import me.panpf.sketch.sample.vt.bean.BoundaryStatus
import me.panpf.sketch.sample.vt.bean.VideoInfo
import me.panpf.sketch.sample.vt.item.LoadMoreItemFactory
import me.panpf.sketch.sample.vt.item.VideoInfoItemFactory
import me.panpf.sketch.sample.vt.kxt.bindViewModel
import me.panpf.sketch.sample.vt.kxt.longToast
import me.panpf.sketch.sample.vt.vm.VideoListViewModel
import me.panpf.sketch.util.SketchUtils
import java.io.File

@BindContentView(R.layout.fragment_recycler)
class VideoListFragment : BaseFragment(), VideoInfoItemFactory.VideoInfoItemListener, OnLoadMoreListener {

    private val videoListViewModel: VideoListViewModel by bindViewModel(VideoListViewModel::class)

    private val adapter by lazy {
        AssemblyPagedListAdapter<VideoInfo>(VideoInfo.DiffCallback()).apply {
            addItemFactory(VideoInfoItemFactory(this@VideoListFragment))
            setMoreItem(LoadMoreItemFactory(this@VideoListFragment))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_recyclerFragment_content.layoutManager = LinearLayoutManager(activity)

        val padding = SketchUtils.dp2px(activity, 2)
        recycler_recyclerFragment_content.setPadding(padding, padding, padding, padding)
        recycler_recyclerFragment_content.clipToPadding = false

        recycler_recyclerFragment_content.adapter = adapter

        refresh_recyclerFragment.setOnRefreshListener {
            videoListViewModel.refresh()
        }

        refresh_recyclerFragment.isEnabled = false

        videoListViewModel.videoListing.observe(this, Observer { pageList ->
            adapter.submitList(pageList)
        })

        videoListViewModel.initStatus.observe(this, Observer { initStatus ->
            initStatus ?: return@Observer

            if (refresh_recyclerFragment.isRefreshing) {
                when {
                    initStatus.isLoading() -> {
                        hint_recyclerFragment.visibility = View.GONE
                    }
                    initStatus.isError() -> {
                        refresh_recyclerFragment.isRefreshing = false

                        hint_recyclerFragment.text = getString(R.string.hint_loadFailed, initStatus.message)
                        hint_recyclerFragment.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_error, 0, 0)
                        hint_recyclerFragment.visibility = View.VISIBLE
                    }
                    initStatus.isSuccess() -> {
                        refresh_recyclerFragment.isRefreshing = false

                        if (videoListViewModel.boundaryStatus.value == BoundaryStatus.ZERO_ITEMS_LOADED) {
                            hint_recyclerFragment.text = getString(R.string.hint_empty_list, "Video")
                            hint_recyclerFragment.visibility = View.VISIBLE
                        } else {
                            hint_recyclerFragment.visibility = View.GONE
                        }
                    }
                }
            } else {
                when {
                    initStatus.isLoading() -> {
                        hint_recyclerFragment.setText(R.string.hint_loading)
                        hint_recyclerFragment.setCompoundDrawables(null, null, null, null)
                        hint_recyclerFragment.visibility = View.VISIBLE
                    }
                    initStatus.isError() -> {
                        hint_recyclerFragment.text = getString(R.string.hint_loadFailed, initStatus.message)
                        hint_recyclerFragment.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_error, 0, 0)
                        hint_recyclerFragment.visibility = View.VISIBLE
                    }
                    initStatus.isSuccess() -> {
                        if (videoListViewModel.boundaryStatus.value == BoundaryStatus.ZERO_ITEMS_LOADED) {
                            hint_recyclerFragment.text = getString(R.string.hint_empty_list, "Video")
                            hint_recyclerFragment.visibility = View.VISIBLE
                        } else {
                            hint_recyclerFragment.visibility = View.GONE
                        }
                    }
                }
            }
        })

        videoListViewModel.pagingStatus.observe(this, Observer { pagingStatus ->
            pagingStatus ?: return@Observer

            when {
                pagingStatus.isLoading() -> {
                    adapter.loadMoreFinished(false)
                }
                pagingStatus.isError() -> {
                    adapter.loadMoreFailed()
                }
            }
        })

        videoListViewModel.boundaryStatus.observe(this, Observer {
            if (it == BoundaryStatus.ITEM_AT_END_LOADED) {
                adapter.loadMoreFinished(true)
            }
        })
    }

    override fun onLoadMore(adapter: AssemblyAdapter) {

    }

    override fun onClickVideo(position: Int, videoInfo: VideoInfo) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.fromFile(File(videoInfo.path)), videoInfo.mimeType)
        }

        try {
            startActivity(intent)
        } catch (e: Throwable) {
            e.printStackTrace()
            longToast("Not found can play video app")
        }
    }
}
