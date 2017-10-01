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

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView
import me.xiaopan.assemblyadapter.AssemblyFragmentStatePagerAdapter
import me.xiaopan.sketch.util.SketchUtils
import me.xiaopan.sketch.viewfun.zoom.ImageZoomer
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.adapter.itemfactory.ImageFragmentItemFactory
import me.xiaopan.sketchsample.bean.Image
import me.xiaopan.sketchsample.util.PageNumberSetter
import me.xiaopan.sketchsample.util.ViewPagerPlayer
import me.xiaopan.sketchsample.widget.DepthPageTransformer
import me.xiaopan.sketchsample.widget.ZoomOutPageTransformer
import me.xiaopan.ssvt.bindView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@BindContentView(R.layout.fragment_detail)
class ImageDetailFragment : BaseFragment(), ImageZoomer.OnViewTapListener {

    val viewPager: ViewPager by bindView(R.id.pager_detail_content)
    val currentItemTextView: TextView by bindView(R.id.text_detail_currentItem)
    val countTextView: TextView by bindView(R.id.text_detail_countItem)

    private var imageList: List<Image>? = null
    private var loadingImageOptionsKey: String? = null
    private var position: Int = 0

    private var handler: Handler? = null
    private var viewPagerPlayer: ViewPagerPlayer? = null
    private var recoverPlay: Boolean = false
    private var startPlay: StartPlay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        startPlay = StartPlay()

        var dataTransferKey: String? = null
        arguments?.let {
            dataTransferKey = it.getString(PARAM_REQUIRED_STRING_DATA_TRANSFER_KEY)
            loadingImageOptionsKey = it.getString(PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY)
            position = it.getInt(PARAM_OPTIONAL_INT_DEFAULT_POSITION)
        }

        imageList = dataTransferHelper.get(dataTransferKey) as List<Image>?
        if (imageList == null) {
            throw IllegalArgumentException("Not found image list by dataTransferKey: " + dataTransferKey!!)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerPlayer = ViewPagerPlayer(viewPager)
        PageNumberSetter(currentItemTextView, viewPager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // 4.0上使用
                viewPager.setPageTransformer(true, DepthPageTransformer())
            } else {
                viewPager.setPageTransformer(true, ZoomOutPageTransformer())
            }
        } else {
            viewPager.pageMargin = SketchUtils.dp2px(activity, 8)
        }

        if (imageList != null) {
            val pagerAdapter = AssemblyFragmentStatePagerAdapter(childFragmentManager, imageList)
            pagerAdapter.addItemFactory(ImageFragmentItemFactory(activity, loadingImageOptionsKey))
            viewPager.adapter = pagerAdapter
            viewPager.currentItem = position
            currentItemTextView.text = String.format("%d", position + 1)
            countTextView.text = imageList!!.size.toString()
        }

        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()

        if (recoverPlay && !viewPagerPlayer!!.isPlaying) {
            handler!!.postDelayed(startPlay, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewPagerPlayer!!.isPlaying) {
            viewPagerPlayer!!.stop()
            recoverPlay = true
        }
        handler!!.removeCallbacks(startPlay)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun onViewTap(view: View, x: Float, y: Float) {
        // 如果正在播放就关闭自动播放
        if (viewPagerPlayer!!.isPlaying) {
            viewPagerPlayer!!.stop()
        } else {
            activity.finish()
        }
    }

    @Suppress("unused")
    @Subscribe
    fun onEvent(event: ImageFragment.PlayImageEvent) {
        viewPagerPlayer!!.start()
    }

    private inner class StartPlay : Runnable {
        override fun run() {
            viewPagerPlayer!!.start()
            recoverPlay = false
        }
    }

    companion object {
        val PARAM_REQUIRED_STRING_DATA_TRANSFER_KEY = "PARAM_REQUIRED_STRING_DATA_TRANSFER_KEY"
        val PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY = "PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY"
        val PARAM_OPTIONAL_INT_DEFAULT_POSITION = "PARAM_OPTIONAL_INT_DEFAULT_POSITION"
    }
}
