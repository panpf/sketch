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
import android.arch.paging.AssemblyRecyclerPageListAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import me.panpf.adapter.AssemblyAdapter
import me.panpf.adapter.more.OnLoadMoreListener
import me.panpf.sketch.sample.vt.BaseFragment
import me.panpf.sketch.sample.vt.BindContentView
import me.panpf.sketch.sample.vt.R
import me.panpf.sketch.sample.vt.bean.BoundaryStatus
import me.panpf.sketch.sample.vt.bean.VideoInfo
import me.panpf.sketch.sample.vt.ext.bindView
import me.panpf.sketch.sample.vt.ext.bindViewModel
import me.panpf.sketch.sample.vt.ext.longToast
import me.panpf.sketch.sample.vt.item.LoadMoreItemFactory
import me.panpf.sketch.sample.vt.item.VideoInfoItemFactory
import me.panpf.sketch.sample.vt.vm.VideoListViewModel
import me.panpf.sketch.util.SketchUtils
import java.io.File

@BindContentView(R.layout.fragment_recycler)
class VideoListFragment : BaseFragment(), VideoInfoItemFactory.VideoInfoItemListener, OnLoadMoreListener {

    private val refreshLayout: SwipeRefreshLayout by bindView(R.id.refresh_recyclerFragment)
    private val recyclerView: RecyclerView by bindView(R.id.recycler_recyclerFragment_content)
    private val hintTextView: TextView by bindView(R.id.hint_recyclerFragment)

    private val videoListViewModel: VideoListViewModel by bindViewModel(VideoListViewModel::class)

    private val adapter by lazy {
        AssemblyRecyclerPageListAdapter<VideoInfo>(VideoInfo.DiffCallback()).apply {
            addItemFactory(VideoInfoItemFactory(this@VideoListFragment))
            setLoadMoreItem(LoadMoreItemFactory(this@VideoListFragment))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val padding = SketchUtils.dp2px(activity, 2)
        recyclerView.setPadding(padding, padding, padding, padding)
        recyclerView.clipToPadding = false

        recyclerView.adapter = adapter

        refreshLayout.setOnRefreshListener {
            videoListViewModel.refresh()
        }

        refreshLayout.isEnabled = false

        videoListViewModel.videoListing.observe(this, Observer { pageList ->
            adapter.submitList(pageList)
        })

        videoListViewModel.initStatus.observe(this, Observer { initStatus ->
            initStatus ?: return@Observer

            if (refreshLayout.isRefreshing) {
                when {
                    initStatus.isLoading() -> {
                        hintTextView.visibility = View.GONE
                    }
                    initStatus.isError() -> {
                        refreshLayout.isRefreshing = false

                        hintTextView.text = getString(R.string.hint_loadFailed, initStatus.message)
                        hintTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_error, 0, 0)
                        hintTextView.visibility = View.VISIBLE
                    }
                    initStatus.isSuccess() -> {
                        refreshLayout.isRefreshing = false

                        if (videoListViewModel.boundaryStatus.value == BoundaryStatus.ZERO_ITEMS_LOADED) {
                            hintTextView.text = getString(R.string.hint_empty_list, "Video")
                            hintTextView.visibility = View.VISIBLE
                        } else {
                            hintTextView.visibility = View.GONE
                        }
                    }
                }
            } else {
                when {
                    initStatus.isLoading() -> {
                        hintTextView.setText(R.string.hint_loading)
                        hintTextView.setCompoundDrawables(null, null, null, null)
                        hintTextView.visibility = View.VISIBLE
                    }
                    initStatus.isError() -> {
                        hintTextView.text = getString(R.string.hint_loadFailed, initStatus.message)
                        hintTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_error, 0, 0)
                        hintTextView.visibility = View.VISIBLE
                    }
                    initStatus.isSuccess() -> {
                        if (videoListViewModel.boundaryStatus.value == BoundaryStatus.ZERO_ITEMS_LOADED) {
                            hintTextView.text = getString(R.string.hint_empty_list, "Video")
                            hintTextView.visibility = View.VISIBLE
                        } else {
                            hintTextView.visibility = View.GONE
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
