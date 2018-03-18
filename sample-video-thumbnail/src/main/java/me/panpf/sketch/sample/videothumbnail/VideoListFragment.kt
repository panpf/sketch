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

package me.panpf.sketch.sample.videothumbnail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.sketch.util.SketchUtils
import java.io.File

@BindContentView(R.layout.fragment_recycler)
class VideoListFragment : BaseFragment(), MyVideoItemFactory.MyVideoItemListener {

    private val refreshLayout: SwipeRefreshLayout by bindView(R.id.refresh_recyclerFragment)
    private val recyclerView: RecyclerView by bindView(R.id.recycler_recyclerFragment_content)
    private val hintView: TextView by bindView(R.id.hint_recyclerFragment)

    private val videoThumbViewModel: VideoThumbViewModel by bindViewModel(VideoThumbViewModel::class)

    private val adapter: AssemblyRecyclerAdapter by lazy {
        val adapter = AssemblyRecyclerAdapter(null as List<*>?)
        adapter.addItemFactory(MyVideoItemFactory(this))
        adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val padding = SketchUtils.dp2px(activity, 2)
        recyclerView.setPadding(padding, padding, padding, padding)
        recyclerView.clipToPadding = false

        recyclerView.adapter = adapter

        videoThumbViewModel.videoList.observe(this, android.arch.lifecycle.Observer {
            adapter.dataList = it
            refreshLayout.isRefreshing = false

            if (adapter.dataCount <= 0) {
                hintView.text = "No videos"
                hintView.visibility = View.VISIBLE
            } else {
                hintView.visibility = View.GONE
            }
        })

        refreshLayout.setOnRefreshListener {
            hintView.visibility = View.GONE
            videoThumbViewModel.refreshVideoList()
        }

        refreshLayout.isRefreshing = true
        hintView.visibility = View.VISIBLE

        // TODO 实现加载更多
    }

    override fun onClickVideo(position: Int, videoItem: VideoItem) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.fromFile(File(videoItem.path)), videoItem.mimeType)
        }

        try {
            startActivity(intent)
        } catch (e: Throwable) {
            e.printStackTrace()
            longToast("Not found can play video app")
        }
    }
}
