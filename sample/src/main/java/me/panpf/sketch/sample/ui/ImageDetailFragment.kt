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

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import me.panpf.adapter.pager.AssemblyFragmentStatePagerAdapter
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.databinding.FragmentDetailBinding
import me.panpf.sketch.sample.event.RegisterEvent
import me.panpf.sketch.sample.item.ImageFragmentItemFactory
import me.panpf.sketch.sample.util.PageNumberSetter
import me.panpf.sketch.sample.util.ViewPagerPlayer
import me.panpf.sketch.zoom.ImageZoomer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@RegisterEvent
class ImageDetailFragment : BaseBindingFragment<FragmentDetailBinding>(),
    ImageZoomer.OnViewTapListener {

    private var imageList: List<Image>? = null
    private var loadingImageOptionsKey: String? = null
    private var position: Int = 0

    private var handler: Handler? = null
    lateinit var viewPagerPlayer: ViewPagerPlayer
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

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentDetailBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentDetailBinding,
        savedInstanceState: Bundle?
    ) {
        val context = context ?: return

        viewPagerPlayer = ViewPagerPlayer(binding.pagerDetailContent)
        PageNumberSetter(binding.textDetailCurrentItem, binding.pagerDetailContent)

        if (imageList != null) {
            val pagerAdapter = AssemblyFragmentStatePagerAdapter(childFragmentManager, imageList!!)
            pagerAdapter.addItemFactory(ImageFragmentItemFactory(context, loadingImageOptionsKey))
            binding.pagerDetailContent.adapter = pagerAdapter
            binding.pagerDetailContent.currentItem = position
            binding.textDetailCurrentItem.text = String.format("%d", position + 1)
            binding.textDetailCountItem.text = imageList!!.size.toString()
        }

        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()

        if (recoverPlay && !viewPagerPlayer.isPlaying) {
            handler!!.postDelayed(startPlay, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewPagerPlayer.isPlaying) {
            viewPagerPlayer.stop()
            recoverPlay = true
        }
        handler!!.removeCallbacks(startPlay)
    }

    override fun onViewTap(view: View, x: Float, y: Float) {
        // 如果正在播放就关闭自动播放
        if (viewPagerPlayer.isPlaying) {
            viewPagerPlayer.stop()

            Toast.makeText(activity, "Stop auto play", Toast.LENGTH_SHORT).show()
        } else {
            activity?.finish()
        }
    }

    @Suppress("unused")
    @Subscribe
    fun onEvent(event: ImageFragment.PlayImageEvent) {
        viewPagerPlayer.start()
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    private inner class StartPlay : Runnable {
        override fun run() {
            viewPagerPlayer.start()
            recoverPlay = false
        }
    }

    companion object {
        val PARAM_REQUIRED_STRING_DATA_TRANSFER_KEY = "PARAM_REQUIRED_STRING_DATA_TRANSFER_KEY"
        val PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY =
            "PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY"
        val PARAM_OPTIONAL_INT_DEFAULT_POSITION = "PARAM_OPTIONAL_INT_DEFAULT_POSITION"
    }
}
