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

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.tools4a.display.ktx.isOrientationPortrait
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.panpf.adapter.pager.AssemblyFragmentStatePagerAdapter
import me.panpf.sketch.sample.AppEvents
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.databinding.FragmentDetailBinding
import me.panpf.sketch.sample.item.ImageFragmentItemFactory
import me.panpf.sketch.sample.util.DeviceUtils
import me.panpf.sketch.sample.util.PageNumberSetter
import me.panpf.sketch.sample.util.ViewPagerPlayer
import me.panpf.sketch.sample.vm.ImageChangedViewModel
import me.panpf.sketch.zoom.ImageZoomer

class ImageDetailFragment : BaseFragment<FragmentDetailBinding>(), ImageZoomer.OnViewTapListener {

    private var handler: Handler? = null
    lateinit var viewPagerPlayer: ViewPagerPlayer
    private var recoverPlay: Boolean = false
    private var startPlay: StartPlay? = null

    private val imageChangedViewModel by viewModels<ImageChangedViewModel>()
    private val args by navArgs<ImageDetailFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        startPlay = StartPlay()
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentDetailBinding.inflate(inflater, parent, false)

    override fun onInitViews(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        super.onInitViews(binding, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.root.updatePadding(
                top = binding.root.paddingTop + DeviceUtils.getStatusBarHeight(
                    resources
                )
            )
        }

        //  + DeviceUtils.getNavigationBarHeightByUiVisibility(this) 是为了兼容 MIX 2
        binding.imageDetailAtBgImage.layoutParams?.let {
            it.width = resources.displayMetrics.widthPixels
            it.height = resources.displayMetrics.heightPixels
            if (isOrientationPortrait()) {
                it.height += DeviceUtils.getWindowHeightSupplement(requireActivity())
            } else {
                it.width += DeviceUtils.getWindowHeightSupplement(requireActivity())
            }
            binding.imageDetailAtBgImage.layoutParams = it
        }

        binding.imageDetailAtBgImage.setOptions(ImageOptions.WINDOW_BACKGROUND)
    }

    override fun onInitData(
        binding: FragmentDetailBinding,
        savedInstanceState: Bundle?
    ) {
        val context = context ?: return

        viewPagerPlayer = ViewPagerPlayer(binding.pagerDetailContent)
        PageNumberSetter(binding.textDetailCurrentItem, binding.pagerDetailContent)

        val imageList: List<Image> =
            Gson().fromJson(args.imageUrlJsonArray, object : TypeToken<List<Image>>() {}.type)
        val pagerAdapter = AssemblyFragmentStatePagerAdapter(childFragmentManager, imageList)
        pagerAdapter.addItemFactory(ImageFragmentItemFactory(context, args.loadingImageOptionsKey))
        binding.pagerDetailContent.adapter = pagerAdapter
        binding.pagerDetailContent.currentItem = args.defaultPosition
        binding.textDetailCurrentItem.text = String.format("%d", args.defaultPosition + 1)
        binding.textDetailCountItem.text = imageList.size.toString()

        AppEvents.playImageEvent.listen(viewLifecycleOwner) {
            viewPagerPlayer.start()
        }

        imageChangedViewModel.imageChangedData.observe(viewLifecycleOwner) {
            it?.let { binding.imageDetailAtBgImage.displayImage(it) }
        }
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
            findNavController().popBackStack()
        }
    }

    private inner class StartPlay : Runnable {
        override fun run() {
            viewPagerPlayer.start()
            recoverPlay = false
        }
    }
}
