/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.panpf.sketch.sample.fragment

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter
import me.xiaopan.sketch.util.SketchUtils
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.activity.ImageDetailActivity
import me.panpf.sketch.sample.activity.PageBackgApplyCallback
import me.panpf.sketch.sample.adapter.itemfactory.MyPhotoItemFactory
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.event.AppConfigChangedEvent
import me.panpf.sketch.sample.util.AppConfig
import me.panpf.sketch.sample.util.ImageOrientationCorrectTestFileGenerator
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.widget.HintView
import me.panpf.sketch.sample.bindView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference
import java.util.*

/**
 * 本地相册页面
 */
@BindContentView(R.layout.fragment_recycler)
class MyPhotosFragment : BaseFragment(), MyPhotoItemFactory.OnImageClickListener, SwipeRefreshLayout.OnRefreshListener {

    val refreshLayout: SwipeRefreshLayout by bindView(R.id.refresh_recyclerFragment)
    val recyclerView: RecyclerView by bindView(R.id.recycler_recyclerFragment_content)
    val hintView: HintView by bindView(R.id.hint_recyclerFragment)

    private var adapter: AssemblyRecyclerAdapter? = null

    private var pageBackgApplyCallback: PageBackgApplyCallback? = null
    private var backgroundImageUri: String? = null

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (activity is PageBackgApplyCallback) {
            pageBackgApplyCallback = activity
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout.setOnRefreshListener(this)
        recyclerView.addOnScrollListener(ScrollingPauseLoadManager(view!!.context))

        recyclerView.layoutManager = GridLayoutManager(activity, 3)
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

        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun onClickImage(position: Int, optionsKey: String) {
        var finalOptionsKey: String? = optionsKey
        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
        if (finalOptionsKey!!.contains("Resize")
                || finalOptionsKey.contains("ImageProcessor")
                || finalOptionsKey.contains("thumbnailMode")) {
            finalOptionsKey = null
        }

        val urlList = adapter!!.dataList
        val imageArrayList = ArrayList<Image>(urlList.size)
        urlList.mapTo(imageArrayList) { Image(it as String, it) }

        ImageDetailActivity.launch(activity, dataTransferHelper.put("urlList", imageArrayList), finalOptionsKey, position)
    }

    override fun onRefresh() {
        if (activity != null) {
            LoadPhotoListTask(WeakReference(this)).execute()
        }
    }

    override fun onUserVisibleChanged(isVisibleToUser: Boolean) {
        if (pageBackgApplyCallback != null && isVisibleToUser) {
            changeBackground(backgroundImageUri)
        }
    }

    private fun changeBackground(imageUri: String?) {
        this.backgroundImageUri = imageUri
        pageBackgApplyCallback?.onApplyBackground(backgroundImageUri)
    }

    @Suppress("unused")
    @Subscribe
    fun onEvent(event: AppConfigChangedEvent) {
        if (AppConfig.Key.SHOW_ROUND_RECT_IN_PHOTO_LIST == event.key) {
            if (adapter != null) {
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    private class LoadPhotoListTask constructor(private val fragmentWeakReference: WeakReference<MyPhotosFragment>) : AsyncTask<Void, Int, List<String>>() {

        override fun onPreExecute() {
            super.onPreExecute()

            val fragment = fragmentWeakReference.get() ?: return

            fragment.hintView.hidden()
        }

        override fun doInBackground(params: Array<Void>): List<String>? {
            val fragment = fragmentWeakReference.get() ?: return null

            var cursor = fragment.context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN), null, null,
                    MediaStore.Images.Media.DATE_TAKEN + " DESC")

            val generator = ImageOrientationCorrectTestFileGenerator.getInstance(fragment.context)
            val testFilePaths = generator.filePaths

            val allUris = AssetImage.getAll(fragment.context)
            val imageListSize = cursor?.count ?: 0 + allUris.size + testFilePaths.size
            val imagePathList = ArrayList<String>(imageListSize)

            Collections.addAll(imagePathList, *allUris)
            Collections.addAll(imagePathList, *testFilePaths)
            cursor?.let {
                while (cursor.moveToNext()) {
                    imagePathList.add(String.format("file://%s", cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))))
                }
                cursor.close()
            }
            return imagePathList
        }

        override fun onPostExecute(imageUriList: List<String>?) {
            val fragment = fragmentWeakReference.get() ?: return

            fragment.refreshLayout.isRefreshing = false

            if (imageUriList == null || imageUriList.isEmpty()) {
                fragment.hintView.empty("No photos")
                fragment.recyclerView.adapter = null
                return
            }

            val adapter = AssemblyRecyclerAdapter(imageUriList)
            adapter.addItemFactory(MyPhotoItemFactory(fragment))

            fragment.recyclerView.adapter = adapter
            fragment.recyclerView.scheduleLayoutAnimation()

            fragment.adapter = adapter

            fragment.changeBackground(imageUriList[0])
        }
    }
}
