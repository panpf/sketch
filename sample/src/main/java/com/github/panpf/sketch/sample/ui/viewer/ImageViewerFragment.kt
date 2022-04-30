package com.github.panpf.sketch.sample.ui.viewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.databinding.ImageViewerFragmentBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.viewability.showRingProgressIndicator

// todo 增加上下滑动退出功能
// todo 增加进入和退出过渡动画
// todo 增加保存功能
// todo 增加设置壁纸功能
// todo 增加旋转功能
// todo 增加查看图片信息功能
class ImageViewerFragment : BindingFragment<ImageViewerFragmentBinding>() {

    private val args by navArgs<ImageViewerFragmentArgs>()

    override fun onViewCreated(binding: ImageViewerFragmentBinding, savedInstanceState: Bundle?) {
        binding.imageViewerZoomImage.apply {
            showRingProgressIndicator()
            zoomAbility.readModeEnabled = true
            setOnClickListener {
                findNavController().popBackStack()
            }
            displayImage(args.imageUri) {
                lifecycle(viewLifecycleOwner.lifecycle)
            }
        }
    }

    class ItemFactory : FragmentItemFactory<ImageDetail>(ImageDetail::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: ImageDetail
        ): Fragment = ImageViewerFragment().apply {
            arguments = ImageViewerFragmentArgs(data.url).toBundle()
        }
    }
}