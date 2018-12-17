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

package me.panpf.sketch.sample.ui

import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.GridLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_recycler.*
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.event.AppConfigChangedEvent
import me.panpf.sketch.sample.event.ChangeMainPageBgEvent
import me.panpf.sketch.sample.event.RegisterEvent
import me.panpf.sketch.sample.item.MyPhotoItemFactory
import me.panpf.sketch.sample.util.ImageOrientationCorrectTestFileGenerator
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.util.SketchUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference
import java.util.*

/**
 * 本地相册页面
 */
@RegisterEvent
@BindContentView(R.layout.fragment_recycler)
class MyPhotosFragment : BaseFragment(), MyPhotoItemFactory.OnImageClickListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {

    private var adapter: AssemblyRecyclerAdapter? = null

    private var backgroundImageUri: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh_recyclerFragment.setOnRefreshListener(this)
        recycler_recyclerFragment_content.addOnScrollListener(ScrollingPauseLoadManager(view.context))

        recycler_recyclerFragment_content.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 3)
        val padding = SketchUtils.dp2px(activity, 2)
        recycler_recyclerFragment_content.setPadding(padding, padding, padding, padding)
        recycler_recyclerFragment_content.clipToPadding = false

        if (adapter != null) {
            recycler_recyclerFragment_content.adapter = adapter
            recycler_recyclerFragment_content.scheduleLayoutAnimation()
        } else {
            refresh_recyclerFragment.post {
                refresh_recyclerFragment.isRefreshing = true
                onRefresh()
            }
        }
    }

    override fun onClickImage(position: Int, optionsKey: String) {
        val activity = activity ?: return
        var finalOptionsKey: String? = optionsKey
        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
        if (finalOptionsKey!!.contains("Resize")
                || finalOptionsKey.contains("ImageProcessor")
                || finalOptionsKey.contains("thumbnailMode")) {
            finalOptionsKey = null
        }

        val urlList = adapter!!.dataList
        val imageArrayList = ArrayList<Image>(urlList?.size ?: 0)
        urlList?.mapTo(imageArrayList) { Image(it as String, it) }

        ImageDetailActivity.launch(activity, dataTransferHelper.put("urlList", imageArrayList), finalOptionsKey, position)
    }

    override fun onRefresh() {
        if (activity != null) {
            LoadPhotoListTask(WeakReference(this)).execute()
        }
    }

    override fun onUserVisibleChanged(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            changeBackground(backgroundImageUri)
        }
    }

    private fun changeBackground(imageUri: String?) {
        this.backgroundImageUri = imageUri
        backgroundImageUri?.let { EventBus.getDefault().post(ChangeMainPageBgEvent(it)) }
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

            fragment.hint_recyclerFragment.hidden()
        }

        override fun doInBackground(params: Array<Void>): List<String>? {
            val fragment = fragmentWeakReference.get() ?: return null
            val context = fragment.context ?: return null

            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN), null, null,
                    MediaStore.Images.Media.DATE_TAKEN + " DESC")

            val generator = ImageOrientationCorrectTestFileGenerator.getInstance(context)
            val testFilePaths = generator.filePaths

            val allUris = AssetImage.getAll(context)
            val imageListSize = cursor?.count ?: 0+allUris.size+testFilePaths.size
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

            fragment.refresh_recyclerFragment.isRefreshing = false

            if (imageUriList == null || imageUriList.isEmpty()) {
                fragment.hint_recyclerFragment.empty("No photos")
                fragment.recycler_recyclerFragment_content.adapter = null
                return
            }

            val adapter = AssemblyRecyclerAdapter(imageUriList)
            adapter.addItemFactory(MyPhotoItemFactory(fragment))

            fragment.recycler_recyclerFragment_content.adapter = adapter
            fragment.recycler_recyclerFragment_content.scheduleLayoutAnimation()

            fragment.adapter = adapter

            fragment.changeBackground(imageUriList[0])
        }
    }
}
