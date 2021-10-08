package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.tools4a.dimen.ktx.dp2px
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.CircleImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentReflectionBinding

class CircleImageProcessorTestFragment : BaseFragment<FragmentReflectionBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentReflectionBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentReflectionBinding,
        savedInstanceState: Bundle?
    ) {
        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        binding.imageReflectionFragment.options.setMaxSize(
            metrics.widthPixels / 2,
            metrics.heightPixels / 2
        )

        val layoutParams =
            binding.imageReflectionFragment.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = 16.dp2px
        layoutParams.rightMargin = layoutParams.bottomMargin
        layoutParams.topMargin = layoutParams.rightMargin
        layoutParams.leftMargin = layoutParams.topMargin
        binding.imageReflectionFragment.layoutParams = layoutParams

        binding.imageReflectionFragment.options.processor = CircleImageProcessor.getInstance()
        binding.imageReflectionFragment.options.displayer = TransitionImageDisplayer()
        binding.imageReflectionFragment.displayImage(AssetImage.MEI_NV)
    }
}
