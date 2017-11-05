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
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter
import me.panpf.sketch.util.SketchUtils
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

@BindContentView(R.layout.fragment_recycler)
class VideoListFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, MyVideoItemFactory.MyVideoItemListener {

    val refreshLayout: SwipeRefreshLayout by bindView(R.id.refresh_recyclerFragment)
    val recyclerView: RecyclerView by bindView(R.id.recycler_recyclerFragment_content)
    val hintView: TextView by bindView(R.id.hint_recyclerFragment)

    var adapter: AssemblyRecyclerAdapter? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout.setOnRefreshListener(this)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        val padding = SketchUtils.dp2px(activity, 2)
        recyclerView.setPadding(padding, padding, padding, padding)
        recyclerView.clipToPadding = false

        if (adapter != null) {
            recyclerView.adapter = adapter
            recyclerView.scheduleLayoutAnimation()
        } else {
            refreshLayout.post {
                refreshLayout.isRefreshing = true
                onRefresh()
            }
        }
    }

    override fun onRefresh() {
        if (activity != null) {
            LoadVideoListTask(this).execute()
        }
    }

    override fun onClickVideo(position: Int, videoItem: VideoItem) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.fromFile(File(videoItem.path))
        intent.type = videoItem.mimeType
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivity(intent)
        } catch (e: Throwable) {
            e.printStackTrace()

            Toast.makeText(context, "Not found can play video app", Toast.LENGTH_LONG).show()
        }
    }
}

private class LoadVideoListTask constructor(fragment: VideoListFragment) : AsyncTask<Void, Int, List<VideoItem>>() {
    private val fragmentWeakReference: WeakReference<VideoListFragment> = WeakReference(fragment)

    override fun onPreExecute() {
        super.onPreExecute()

        val fragment = fragmentWeakReference.get()
        if (fragment == null || fragment.context == null) {
            return
        }

        fragment.hintView.visibility = View.GONE
    }

    override fun doInBackground(params: Array<Void>): List<VideoItem>? {
        val fragment = fragmentWeakReference.get()
        if (fragment == null || fragment.context == null) {
            return null
        }

        val cursor = fragment.context.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.Media.MIME_TYPE), null, null,
                MediaStore.Video.Media.DATE_TAKEN + " DESC") ?: return null

        val imagePathList = ArrayList<VideoItem>(cursor.count)
        while (cursor.moveToNext()) {
            val video = VideoItem()
            video.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
            video.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
            video.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
            video.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
            video.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)).toLong()
            video.date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN))
            imagePathList.add(video)
        }
        cursor.close()
        return imagePathList
    }

    override fun onPostExecute(imageUriList: List<VideoItem>?) {
        val fragment = fragmentWeakReference.get()
        if (fragment == null || fragment.context == null) {
            return
        }

        fragment.refreshLayout.isRefreshing = false

        if (imageUriList == null || imageUriList.isEmpty()) {
            fragment.hintView.text = "No videos"
            fragment.hintView.visibility = View.VISIBLE
            fragment.recyclerView.adapter = null
            return
        }

        val adapter = AssemblyRecyclerAdapter(imageUriList)
        adapter.addItemFactory(MyVideoItemFactory(fragment))

        fragment.recyclerView.adapter = adapter
        fragment.recyclerView.scheduleLayoutAnimation()

        fragment.adapter = adapter
    }
}
