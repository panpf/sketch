package com.github.panpf.sketch.sample.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.databinding.FragmentImageDetailBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.viewability.showRingProgressIndicator

// todo 增加上下滑动退出功能
// todo 增加进入和退出过渡动画
// todo 增加保存功能
// todo 增加设置壁纸功能
// todo 增加旋转功能
// todo 增加查看图片信息功能
class ImageZoomFragment : BindingFragment<FragmentImageDetailBinding>() {

    private val args by navArgs<ImageZoomFragmentArgs>()

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageDetailBinding.inflate(inflater, parent, false)

    override fun onInitViews(binding: FragmentImageDetailBinding, savedInstanceState: Bundle?) {
        super.onInitViews(binding, savedInstanceState)
        binding.imageFragmentZoomImageView.apply {
            showRingProgressIndicator()
            zoomAbility.readModeEnabled = true
        }
    }

    override fun onInitData(binding: FragmentImageDetailBinding, savedInstanceState: Bundle?) {
        binding.imageFragmentZoomImageView.apply {
            setOnClickListener {
                findNavController().popBackStack()
            }
            displayImage(args.imageUri) {
                lifecycle(viewLifecycleOwner.lifecycle)
            }
        }
    }
}