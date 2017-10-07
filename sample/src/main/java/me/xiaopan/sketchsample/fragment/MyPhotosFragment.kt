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

package me.xiaopan.sketchsample.fragment

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
import me.xiaopan.sketchsample.AssetImage
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.activity.ImageDetailActivity
import me.xiaopan.sketchsample.activity.PageBackgApplyCallback
import me.xiaopan.sketchsample.adapter.itemfactory.MyPhotoItemFactory
import me.xiaopan.sketchsample.bean.Image
import me.xiaopan.sketchsample.event.AppConfigChangedEvent
import me.xiaopan.sketchsample.util.AppConfig
import me.xiaopan.sketchsample.util.ImageOrientationCorrectTestFileGenerator
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager
import me.xiaopan.sketchsample.widget.HintView
import me.xiaopan.ssvt.bindView
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

            val cursor = fragment.context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN), null, null,
                    MediaStore.Images.Media.DATE_TAKEN + " DESC") ?: return null

            val generator = ImageOrientationCorrectTestFileGenerator.getInstance(fragment.context)
            val testFilePaths = generator.filePaths

            val allUris = AssetImage.getAll(fragment.context)
            val imagePathList = ArrayList<String>(cursor.count + allUris.size + testFilePaths.size)
            Collections.addAll(imagePathList, *allUris)
            Collections.addAll(imagePathList, *testFilePaths)
            while (cursor.moveToNext()) {
                imagePathList.add(String.format("file://%s", cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))))
            }
            cursor.close()
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
