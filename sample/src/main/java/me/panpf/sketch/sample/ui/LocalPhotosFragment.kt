/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.panpf.tools4a.dimen.ktx.dp2px
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import me.panpf.sketch.sample.event.AppConfigChangedEvent
import me.panpf.sketch.sample.event.RegisterEvent
import me.panpf.sketch.sample.item.MyPhotoItem
import me.panpf.sketch.sample.util.ImageOrientationCorrectTestFileGenerator
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference
import java.util.*

@RegisterEvent
class LocalPhotosFragment : BaseToolbarFragment<FragmentRecyclerBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    private var adapter: AssemblyRecyclerAdapter? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Local Photos"

        binding.refreshRecyclerFragment.setOnRefreshListener(this)
        binding.recyclerRecyclerFragmentContent.addOnScrollListener(
            ScrollingPauseLoadManager(
                requireContext()
            )
        )

        binding.recyclerRecyclerFragmentContent.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(activity, 3)
        val padding = 2.dp2px
        binding.recyclerRecyclerFragmentContent.setPadding(padding, padding, padding, padding)
        binding.recyclerRecyclerFragmentContent.clipToPadding = false

        if (adapter != null) {
            binding.recyclerRecyclerFragmentContent.adapter = adapter
            binding.recyclerRecyclerFragmentContent.scheduleLayoutAnimation()
        } else {
            binding.refreshRecyclerFragment.post {
                binding.refreshRecyclerFragment.isRefreshing = true
                onRefresh()
            }
        }

        EventBus.getDefault().register(this)
    }

    override fun onRefresh() {
        if (activity != null) {
            LoadPhotoListTask(WeakReference(this)).execute()
        }
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

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    private class LoadPhotoListTask constructor(private val fragmentWeakReference: WeakReference<LocalPhotosFragment>) :
        AsyncTask<Void, Int, List<String>>() {

        override fun onPreExecute() {
            super.onPreExecute()

            val fragment = fragmentWeakReference.get() ?: return

            fragment.binding?.hintRecyclerFragment?.hidden()
        }

        override fun doInBackground(params: Array<Void>): List<String>? {
            val fragment = fragmentWeakReference.get() ?: return null
            val context = fragment.context ?: return null

            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN),
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC"
            )

            val generator = ImageOrientationCorrectTestFileGenerator.getInstance(context)
            val testFilePaths = generator.filePaths

            val allUris = AssetImage.getAll(context)
            val imageListSize = cursor?.count ?: 0 + allUris.size + testFilePaths.size
            val imagePathList = ArrayList<String>(imageListSize)

            Collections.addAll(imagePathList, *allUris)
            Collections.addAll(imagePathList, *testFilePaths)
            cursor?.let {
                while (cursor.moveToNext()) {
                    imagePathList.add(
                        String.format(
                            "file://%s",
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        )
                    )
                }
                cursor.close()
            }
            return imagePathList
        }

        override fun onPostExecute(imageUriList: List<String>?) {
            val fragment = fragmentWeakReference.get() ?: return

            fragment.binding?.refreshRecyclerFragment?.isRefreshing = false

            if (imageUriList == null || imageUriList.isEmpty()) {
                fragment.binding?.hintRecyclerFragment?.empty("No photos")
                fragment.binding?.recyclerRecyclerFragmentContent?.adapter = null
                return
            }

            val adapter = AssemblyRecyclerAdapter(imageUriList)
            adapter.addItemFactory(
                MyPhotoItem.Factory()
                    .setOnViewClickListener(R.id.image_myPhotoItem) { _, view, position, _, _ ->
                        val activity = fragment.activity ?: return@setOnViewClickListener
                        var finalOptionsKey: String? = (view as SketchImageView).optionsKey
                        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
                        if (finalOptionsKey!!.contains("Resize")
                            || finalOptionsKey.contains("ImageProcessor")
                            || finalOptionsKey.contains("thumbnailMode")
                        ) {
                            finalOptionsKey = null
                        }

                        val urlList = adapter.dataList
                        val imageArrayList = ArrayList<Image>(urlList?.size ?: 0)
                        urlList?.mapTo(imageArrayList) { Image(it as String, it) }

                        ImageDetailActivity.launch(
                            activity,
                            fragment.dataTransferHelper.put("urlList", imageArrayList),
                            finalOptionsKey,
                            position
                        )
                    })

            fragment.binding?.recyclerRecyclerFragmentContent?.adapter = adapter
            fragment.binding?.recyclerRecyclerFragmentContent?.scheduleLayoutAnimation()

            fragment.adapter = adapter
        }
    }
}
